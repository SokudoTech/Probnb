package org.codevoke.probnb.exceptions;

import org.springframework.http.HttpStatus;

public class ImageException extends HTTPException {
    public ImageException(String message, HttpStatus code) {
        super(message, code);
    }

    public static ImageException ImageNotFound(Long id) {
        return new ImageException(String.format("Image with id %d not found", id), HttpStatus.NOT_FOUND);
    }

    public static ImageException InvalidFormat() {
        return new ImageException("Image format must be base64.", HttpStatus.NOT_ACCEPTABLE);
    }

    public static ImageException IOException() {
        return new ImageException("Error of working with file system. Try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
