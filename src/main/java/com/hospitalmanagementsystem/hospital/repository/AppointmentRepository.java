package com.hospitalmanagementsystem.hospital.repository;

import com.hospitalmanagementsystem.hospital.model.Appointment;
import com.hospitalmanagementsystem.hospital.model.Appointment.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // All appointments for a specific patient
    List<Appointment> findByPatientIdOrderByScheduledAtDesc(Long patientId);

    // All appointments for a specific docto
    List<Appointment> findByDoctorIdOrderByScheduledAtDesc(Long doctorId);

    // All appointments for a doctor on a specific day
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.doctor.id = :doctorId
        AND a.scheduledAt >= :dayStart
        AND a.scheduledAt < :dayEnd
        AND a.status = 'SCHEDULED'
        ORDER BY a.scheduledAt
    """)
    List<Appointment> findDoctorScheduleForDay(
            @Param("doctorId")  Long doctorId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd")    LocalDateTime dayEnd
    );

    // THIS IS THE CORE DOUBLE-BOOKING CHECK
    // Returns true if the doctor already has a SCHEDULED appointment
    // that overlaps the requested time window [startTime, endTime)
    @Query("""
        SELECT COUNT(a) > 0 FROM Appointment a
        WHERE a.doctor.id = :doctorId
        AND a.status = 'SCHEDULED'
        AND a.scheduledAt < :endTime
        AND (a.scheduledAt + a.durationMinutes * 1 MINUTE) > :startTime
    """)
    boolean existsOverlappingAppointment(
            @Param("doctorId")  Long doctorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime")   LocalDateTime endTime
    );

    // Count upcoming appointments for a patient (useful for limits)
    long countByPatientIdAndStatus(Long patientId, Status status);
}