package com.hospitalmanagementsystem.hospital.repository;

import com.hospitalmanagementsystem.hospital.model.Doctor;
import com.hospitalmanagementsystem.hospital.model.Doctor.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Find all doctors with a given specialization
    // Spring generates: SELECT * FROM doctors WHERE specialization = ?
    List<Doctor> findBySpecialization(Specialization specialization);

    // Search by name
    List<Doctor> findByFullnameContainingIgnoreCase(String name);

    // Check license number uniqueness
    boolean existsByLicenseNumber(String licenseNumber);

    // Find a doctor by user ID
    Optional<Doctor> findByUserId(Long userId);

    // Custom JPQL query — find doctors who have NO appointment overlapping a time slot
    // This is used when showing available doctors for booking
    @Query("""
        SELECT d FROM Doctor d
        WHERE d.specialization = :spec
        AND d.id NOT IN (
            SELECT a.doctor.id FROM Appointment a
            WHERE a.status = 'SCHEDULED'
            AND a.scheduledAt < :endTime
            AND (a.scheduledAt + a.durationMinutes * 1 MINUTE) > :startTime
        )
    """)
    List<Doctor> findAvailableDoctors(
            @Param("spec")       Specialization spec,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime")    LocalDateTime endTime
    );
}
