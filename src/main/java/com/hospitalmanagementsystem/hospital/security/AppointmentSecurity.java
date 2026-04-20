package com.hospitalmanagementsystem.hospital.security;

import com.hospitalmanagementsystem.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("appointmentSecurity")
@RequiredArgsConstructor
public class AppointmentSecurity {

    private final PatientRepository patientRepository;

    // Returns true if the authenticated user IS this patient
    public boolean isOwner(Authentication auth, Long patientId) {
        String email = (String) auth.getPrincipal();
        return patientRepository.findById(patientId)
                .map(p -> p.getUser().getEmail().equals(email))
                .orElse(false);
    }
}
