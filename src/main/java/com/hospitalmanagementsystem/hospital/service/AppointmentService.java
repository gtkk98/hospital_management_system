package com.hospitalmanagementsystem.hospital.service;

import com.hospitalmanagementsystem.hospital.dto.request.AppointmentRequest;
import com.hospitalmanagementsystem.hospital.dto.response.AppointmentResponse;
import com.hospitalmanagementsystem.hospital.exception.DoctorNotFoundException;
import com.hospitalmanagementsystem.hospital.exception.PatientNotFoundException;
import com.hospitalmanagementsystem.hospital.model.Appointment;
import com.hospitalmanagementsystem.hospital.model.Appointment.Status;
import com.hospitalmanagementsystem.hospital.model.Doctor;
import com.hospitalmanagementsystem.hospital.model.Invoice;
import com.hospitalmanagementsystem.hospital.model.Patient;
import com.hospitalmanagementsystem.hospital.repository.AppointmentRepository;
import com.hospitalmanagementsystem.hospital.repository.DoctorRepository;
import com.hospitalmanagementsystem.hospital.repository.InvoiceRepository;
import com.hospitalmanagementsystem.hospital.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository  appointmentRepository;
    private final PatientRepository  patientRepository;
    private final DoctorRepository doctorRepository;
    private final InvoiceRepository invoiceRepository;

    // Read
    public AppointmentResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public List<AppointmentResponse> getForPatient(Long patientId) {
        return appointmentRepository
                .findByPatientIdOrderByScheduledAtDesc(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AppointmentResponse> getForDoctor(Long doctorId) {
        return appointmentRepository
                .findByDoctorIdOrderByScheduledAtDesc(doctorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Booking
    // appointment + invoice created together — both succeed or both fail
    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {

        // Load patient and doctor (throws 404 if not found)
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException(request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException(request.getDoctorId()));

        // Validate appointment time is during business hours
        LocalDateTime start = request.getScheduledAt();
        validateBusinessHours(start);

        // THE CRITICAL CHECK — does this doctor already have a booking here?
        LocalDateTime end = start.plusMinutes(request.getDurationMinutes());

        boolean conflict = appointmentRepository.existsOverlappingAppointment(
                doctor.getId(), start, end);

        if (conflict) {
            throw new DoubleBookingException(
                    doctor.getFullName(), start, end);
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .scheduledAt(start)
                .durationMinutes(request.getDurationMinutes())
                .status(Status.SCHEDULED)
                .notes(request.getNotes())
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment {} booked: patient={} doctor={} at={}",
                saved.getId(), patient.getFullName(), doctor.getFullName(), start);

        // 5. Automatically create an invoice (same transaction — if this fails, booking rolls back)
        Invoice invoice = Invoice.builder()
                .appointment(saved)
                .amount(doctor.getConsultationFee())
                .status(Invoice.Status.UNPAID)
                .build();

        invoiceRepository.save(invoice);
        log.info("Invoice created for appointment {}: amount={}", saved.getId(), invoice.getAmount());

        return toResponse(saved);
    }

    // Cancel
    @Transactional
    public AppointmentResponse cancelAppointment(Long id, String reason) {
        Appointment appointment = findOrThrow(id);

        // can only cancel SCHEDULED appointments
        if (appointment.getStatus() != Status.SCHEDULED) {
            throw new InvalidStatusTransitionException(
                    appointment.getStatus(), Status.CANCELLED);
        }

        // cannot cancel less than 1 hour before
        if (appointment.getScheduledAt().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new CancellationWindowException();
        }

        appointment.setStatus(Status.CANCELLED);
        appointment.setNotes(reason);

        // Also mark the invoice as waived
        invoiceRepository.findByAppointmentId(id).ifPresent(invoice -> {
            invoice.setStatus(Invoice.Status.WAIVED);
        });

        log.info("Appointment {} cancelled. Reason: {}", id, reason);
        return toResponse(appointment);
    }

    // Complete
    @Transactional
    public AppointmentResponse completeAppointment(Long id) {
        Appointment appointment = findOrThrow(id);

        if (appointment.getStatus() != Status.SCHEDULED) {
            throw new InvalidStatusTransitionException(
                    appointment.getStatus(), Status.COMPLETED);
        }

        appointment.setStatus(Status.COMPLETED);
        log.info("Appointment {} marked as completed", id);
        return toResponse(appointment);
    }

    // Helper

    private Appointment findOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    private void validateBusinessHours(LocalDateTime time) {
        int hour = time.getHour();
        // Business rule: appointments only 8 AM – 6 PM, no weekends
        if (hour < 8 || hour >= 18) {
            throw new BusinessHoursException("Appointments are only available 8 AM – 6 PM");
        }
        var day = time.getDayOfWeek();
        if (day == java.time.DayOfWeek.SATURDAY || day == java.time.DayOfWeek.SUNDAY) {
            throw new BusinessHoursException("Appointments are not available on weekends");
        }
    }

    private AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .patientId(a.getPatient().getId())
                .patientName(a.getPatient().getFullName())
                .doctorId(a.getDoctor().getId())
                .doctorName(a.getDoctor().getFullName())
                .specialization(a.getDoctor().getSpecialization().name())
                .scheduledAt(a.getScheduledAt())
                .endTime(a.getEndTime())
                .durationMinutes(a.getDurationMinutes())
                .status(a.getStatus())
                .notes(a.getNotes())
                .consultationFee(a.getDoctor().getConsultationFee())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
