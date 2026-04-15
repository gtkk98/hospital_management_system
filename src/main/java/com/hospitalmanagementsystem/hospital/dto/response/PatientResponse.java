package com.hospitalmanagementsystem.hospital.dto.response;

import com.hospitalmanagementsystem.hospital.model.Patient.Gender;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PatientResponse {

    private Long id;
    private String fullName;
    private LocalDate dateOfBirth;  // ISO format: "YYYY-MM-DD"
    private Gender gender;
    private String phone;
    private String bloodGroup;
    private String address;
}
