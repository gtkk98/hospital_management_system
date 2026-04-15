package com.hospitalmanagementsystem.hospital.repository;

import com.hospitalmanagementsystem.hospital.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Spring Data generates the SQL for this automatically
    // SELECT * FROM patients WHERE user_id = ?
    Optional<Patient> findByUserId(Long userId);

    // SELECT * FROM patients WHERE full_name ILIKE %name%
    java.util.List<Patient> findByFullNameContainingIgnoreCase(String name);
}
