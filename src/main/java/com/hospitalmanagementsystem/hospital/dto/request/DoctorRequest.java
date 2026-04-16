package com.hospitalmanagementsystem.hospital.dto.request;

import com.hospitalmanagementsystem.hospital.model.Doctor.Specialization;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class DoctorRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Specialization is required")
    private Specialization specialization;

    @NotBlank(message = "License number is required")
    @Size(max = 100)
    private String licenseNumber;

    @Size(max = 20)
    private String phone;

    @NotNull(message = "Consultation fee is required")
    @Min(value = 0, message = "Consultation fee must be non-negative")
    private Integer consultationFee;
}
