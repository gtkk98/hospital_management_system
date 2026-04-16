package com.hospitalmanagementsystem.hospital.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DoubleBookingException extends RuntimeException {
    public DoubleBookingException(String doctorName, LocalDateTime start,  LocalDateTime end) {
        super(String.format(
                "Dr. %s already has an appointment from %s to %s",
                doctorName,
                start.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")),
                end.format(DateTimeFormatter.ofPattern("HH:mm"))
        ));
    }
}
