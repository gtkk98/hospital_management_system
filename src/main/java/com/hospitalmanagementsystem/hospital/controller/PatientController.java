package com.hospitalmanagementsystem.hospital.controller;

import com.hospitalmanagementsystem.hospital.dto.request.PatientRequest;
import com.hospitalmanagementsystem.hospital.dto.response.PatientResponse;
import com.hospitalmanagementsystem.hospital.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // GET /api/patients?name=
    @GetMapping
    public ResponseEntity<List<PatientResponse>> searchPatients(
            @RequestParam(defaultValue = "") String name) {
        return ResponseEntity.ok(patientService.searchPatient(name));
    }

    // POST /api/patients
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody PatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.createPatient(request));
    }

    // PUT /api/patients/{id}
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(id, request));
    }

    // DELETE /api/patinets/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
