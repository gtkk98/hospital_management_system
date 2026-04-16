package com.hospitalmanagementsystem.hospital.dto.response;

import com.hospitalmanagementsystem.hospital.model.Doctor.Specialization;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoctorResponse {

    private Long id;
    private String fullName;
    private Specialization specialization;
    private String licenseNumber;
    private String phone;
    private Integer consultationFee;
    private String consultationFeeFormatted;  // "LKR 500.00"
}
