package com.hospitalmanagementsystem.hospital.dto.response;

import com.hospitalmanagementsystem.hospital.model.Appointment.Status;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AppointmentResponse {

    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String specialization;
    private LocalDateTime scheduledAt;
    private LocalDateTime endTime;        // computed: scheduledAt + duration
    private Integer durationMinutes;
    private Status status;
    private String notes;
    private Integer consultationFee;
    private LocalDateTime createdAt;
}
