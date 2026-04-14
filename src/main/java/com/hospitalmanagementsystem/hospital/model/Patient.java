package com.hospitalmanagementsystem.hospital.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One patient, one user
    @OneToOne(fetch = FetchType.LAZY)
    // FK column in the patient table
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)   // store "MALE"/"FEMALE"
    private Gender gender;

    @Column(length = 20)
    private String phone;

    @Column(name = "blood_group", length = 5)
    private String bloodGroup;

    @Column(columnDefinition = "TEXT")
    private String address;

    // ── Enum nested inside the entity
    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
