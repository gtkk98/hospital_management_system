package com.hospitalmanagementsystem.hospital.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class AppointmentRequest {
    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Appointment must be in the future")
    private LocalDateTime scheduledAt;

    @Min(value = 15, message = "Minimum appointment duration is 15 minutes")
    @Max(value = 120, message = "Maximum appointment duration is 120 minutes")
    private Integer durationMinutes = 30;

    @Size(max = 500)
    private String notes;
}
