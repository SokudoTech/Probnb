package org.codevoke.probnb.exceptions;

import org.springframework.http.HttpStatus;

public class RoomException extends HTTPException {
    public RoomException(String message, HttpStatus code) {
        super(message, code);
    }

    public static RoomException RoomNotFound(Long id) {
        return new RoomException(String.format("Room with id %d not found", id), HttpStatus.NOT_FOUND);
    }
}
