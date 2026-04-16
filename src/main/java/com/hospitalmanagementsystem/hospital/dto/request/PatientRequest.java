package com.hospitalmanagementsystem.hospital.dto.request;

import com.hospitalmanagementsystem.hospital.model.Patient.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PatientRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;

    private LocalDate dateOfBirth;
    private Gender gender;

    @Size(max = 20)
    private String phone;

    private String bloodGroup;
    private String address;

}
