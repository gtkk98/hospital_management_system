package com.hospitalmanagementsystem.hospital.service;

import com.hospitalmanagementsystem.hospital.dto.request.PatientRequest;
import com.hospitalmanagementsystem.hospital.dto.response.PatientResponse;
import com.hospitalmanagementsystem.hospital.model.Patient;
import com.hospitalmanagementsystem.hospital.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // constructor injection
@Slf4j // log.info(), log.error() etc.
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientResponse getPatientById(Long id) {
        log.info("Fetching Patient by ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
        return toResponse(patient);
    }

    public List<PatientResponse> searchPatient(String name) {
        return patientRepository
                .findByFullNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PatientResponse createPatient(PatientRequest request) {
        Patient patient = Patient.builder()
                .fullName(request.getFullName())
                .dateOfBirth(request.getBirthDate())
                .gender(request.getGender())
                .phone(request.getPhone())
                .bloodGroup(request.getBloodGroup())
                .address(request.getAddress())
                .build();

        Patient saved = patientRepository.save(patient);
        log.info("Saved Patient with ID: {}", saved.getId());
        return toResponse(saved);
    }
}
