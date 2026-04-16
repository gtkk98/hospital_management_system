package com.hospitalmanagementsystem.hospital.exception;

public class CancellationWindowException extends RuntimeException {
    public CancellationWindowException() {

        super("Appointments cannot be cancelled less than 1 hour before the scheduled time");
    }
}
