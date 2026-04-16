package com.hospitalmanagementsystem.hospital.controller;

import com.hospitalmanagementsystem.hospital.dto.request.DoctorRequest;
import com.hospitalmanagementsystem.hospital.dto.response.DoctorAvailabilityResponse;
import com.hospitalmanagementsystem.hospital.dto.response.DoctorResponse;
import com.hospitalmanagementsystem.hospital.model.Doctor.Specialization;
import com.hospitalmanagementsystem.hospital.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // GET /api/doctors
    // GET /api/doctors?name=silva
    // GET /api/doctors?specialization=CARDIOLOGY
    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Specialization specialization) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(doctorService.searchDoctors(name));
        }

        if (specialization != null ) {
            return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specialization));
        }
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    // GET /api/doctors/{id}
    public ResponseEntity<DoctorResponse> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    // GET /api/doctors/available?specialization=CARDIOLOGY&startTime=2025-06-01T10:00&duration=30
    @GetMapping("/available")
    public ResponseEntity<List<DoctorAvailabilityResponse>> getAvailableDoctors(
        @RequestParam Specialization specialization,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime startTime,
        @RequestParam(defaultValue = "30") int duration) {
                return ResponseEntity.ok(
                        doctorService.findAvailableDoctors(specialization, startTime, duration));
    }

    // POST /api/doctors
    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doctorService.createDoctor(request));
    }

    // PUT /api/doctors/{id}
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(
            @PathVariable Long id, @RequestBody DoctorRequest request) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, request));
    }

    // DELETE /api/doctors/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
