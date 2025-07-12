package org.codevoke.probnb.exceptions;

import org.springframework.http.HttpStatus;

public class ServerException extends HTTPException {
    public ServerException(String message, HttpStatus httpStatusCode) {
        super(message, httpStatusCode);
    }

    public static ServerException DatabaseError(String message) {
        return new ServerException(
                String.format("Server error: database query failed. details: %s", message),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    public static ServerException DatabaseError() {
        return DatabaseError("empty");
    }
}
