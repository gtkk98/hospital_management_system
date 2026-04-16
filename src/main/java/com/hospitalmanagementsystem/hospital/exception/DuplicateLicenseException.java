package com.hospitalmanagementsystem.hospital.exception;

public class DuplicateLicenseException extends RuntimeException {
    public DuplicateLicenseException(String license) {

        super("A doctor with license number '" + license + "' already exists");
    }
}
