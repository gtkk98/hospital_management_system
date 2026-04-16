package com.hospitalmanagementsystem.hospital.service;

import com.hospitalmanagementsystem.hospital.dto.response.DoctorAvailabilityResponse;
import com.hospitalmanagementsystem.hospital.dto.response.DoctorResponse;
import com.hospitalmanagementsystem.hospital.model.Doctor;
import com.hospitalmanagementsystem.hospital.model.Doctor.Specialization;
import com.hospitalmanagementsystem.hospital.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;

    // Read Operations
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));
        return toResponse(doctor);
    }

    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DoctorResponse> getDoctorsBySpecialization(Specialization spec) {
        return doctorRepository.findBySpecialization(spec)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Availability check
    public List<DoctorAvailabilityResponse> findAvailableDoctors(
            Specialization spec,
            LocalDateTime startTime,
            int durationMinutes) {
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        log.info("Checking available {} doctors from {} to {}", spec, startTime, endTime);

        return doctorRepository
                .findAvailableDoctors(spec, startTime, endTime)
                .stream()
                .map(doc -> DoctorAvailabilityResponse.builder()
                        .doctorId(doc.getId())
                        .fullName(doc.getFullName())
                        .specialization(doc.getSpecialization())
                        .consultationFee(doc.getConsultationFee())
                        .requestedSlot(startTime)
                        .available(true)
                        .build())
                .toList();
    }

    // Write Operations

}
