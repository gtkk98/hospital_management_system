package com.hospitalmanagementsystem.hospital.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private Specialization specialization;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(length = 20)
    private String phone;

    @Column(name = "consultation_fee", nullable = false)
    private Integer consultationFee;

    // Specializations
    public enum Specialization {
        GENERAL_PRACTICE,
        CARDIOLOGY,
        DERMATOLOGY,
        NEUROLOGY,
        ORTHOPEDICS,
        PEDIATRICS,
        PSYCHIATRY,
        RADIOLOGY,
        SURGERY,
        GYNECOLOGY
    }
}
