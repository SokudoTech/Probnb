package org.codevoke.probnb.exceptions;


import org.springframework.http.HttpStatus;

public abstract class HTTPException extends RuntimeException {
    private final HttpStatus httpStatusCode;

    public HTTPException(String message, HttpStatus status) {
        super(message);
        this.httpStatusCode = status;
    }

    public HttpStatus getHTTPStatusCode() {
        return httpStatusCode;
    }
}
