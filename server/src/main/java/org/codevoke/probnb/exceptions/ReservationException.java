package org.codevoke.probnb.exceptions;

import org.springframework.http.HttpStatus;

public class ReservationException extends HTTPException {
    public ReservationException(String message, HttpStatus code) {
        super(message, code);
    }

    public static ReservationException ReservationNotFound(Long id) {
        return new ReservationException(String.format("Reservation with id %d not found", id), HttpStatus.NOT_FOUND);
    }
} 