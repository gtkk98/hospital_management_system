package com.hospitalmanagementsystem.hospital.controller;

import com.hospitalmanagementsystem.hospital.dto.request.AppointmentRequest;
import com.hospitalmanagementsystem.hospital.dto.response.AppointmentResponse;
import com.hospitalmanagementsystem.hospital.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // GET /api/appointments?patientId=1
    // GET /api/appointments?doctorId=2
    // GET /api/appointments?doctorId=2&date=2025-06-01
    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAppointments(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date) {

        if (patientId != null) {
            return ResponseEntity.ok(appointmentService.getForPatient(patientId));
        }
        if (doctorId != null && date != null) {
            return ResponseEntity.ok(appointmentService.getDoctorScheduleForDay(doctorId, date));
        }
        if (doctorId != null) {
            return ResponseEntity.ok(appointmentService.getForDoctor(doctorId));
        }
        return ResponseEntity.badRequest().build();
    }

    // POST /api/appointments
    @PostMapping
    public ResponseEntity<AppointmentResponse> book(
            @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.bookAppointment(request));
    }

    // PATCH /api/appointments/{id}/cancel
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(
            @PathVariable Long id,
            @RequestParam(defaultValue = "") String reason) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, reason));
    }

    // PATCH /api/appointments/{id}/complete
    @PatchMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }
}
