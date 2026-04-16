package com.hospitalmanagementsystem.hospital.exception;

import com.hospitalmanagementsystem.hospital.model.Appointment.Status;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(Status current, Status target) {
        super("Cannot transition appointment from " + current + " to " + target);
    }
}
