package com.hospitalmanagementsystem.hospital.dto.response;

import com.hospitalmanagementsystem.hospital.model.Doctor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DoctorAvailabilityResponse {

    private Long doctorId;
    private String fullName;
    private Doctor.Specialization specialization;
    private Integer consultationFee;
    private LocalDateTime requestedSlot;
    private boolean available;
}
