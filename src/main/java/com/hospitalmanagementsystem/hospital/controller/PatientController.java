package com.hospitalmanagementsystem.hospital.controller;

import com.hospitalmanagementsystem.hospital.dto.response.PatientResponse;
import com.hospitalmanagementsystem.hospital.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    // GET /api/patients/{id}
    public ResponseEntity<PatientResponse> getPatient(
            @PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    // GET /api/patients?name=kamal
    public ResponseEntity<List<PatientResponse>> searchPatients(
            @RequestParam(defaultValue = "") String name) {
        return ResponseEntity.ok(patientService.searchPatient(name));
    }
}
