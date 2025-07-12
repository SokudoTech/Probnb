package org.codevoke.probnb.exceptions;

import org.springframework.http.HttpStatus;

public class HostReservationException extends HTTPException {
    public HostReservationException(String message, HttpStatus code) {
        super(message, code);
    }

    public static HostReservationException HostReservationNotFound(Long id) {
        return new HostReservationException(String.format("Host reservation with id %d not found", id), HttpStatus.NOT_FOUND);
    }
} 