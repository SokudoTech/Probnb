package org.codevoke.probnb.exceptions;

import org.springframework.http.HttpStatus;

public class AuthException extends HTTPException {
    public AuthException(String message, HttpStatus status) {
        super(message, status);
    }

    public static AuthException AuthorizationFailed() {
        return new AuthException("You haven't access for this resource", HttpStatus.FORBIDDEN);
    }

    public static AuthException AuthenticationFailed() {
        return new AuthException("You must authenticate for take access to this resource", HttpStatus.UNAUTHORIZED);
    }
}
