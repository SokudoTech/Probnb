package org.codevoke.probnb.exceptions;

import org.springframework.http.HttpStatus;

public class RoomImageException extends HTTPException {
    public RoomImageException(String message, HttpStatus code) {
        super(message, code);
    }

    public static RoomImageException RoomImageNotFound(Long id) {
        return new RoomImageException(String.format("Room image with id %d not found", id), HttpStatus.NOT_FOUND);
    }
} 