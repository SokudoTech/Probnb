package org.codevoke.probnb.exceptions;

import org.springframework.http.HttpStatus;

public class UserException extends HTTPException {
    public UserException(String message, HttpStatus httpStatusCode) {
        super(message, httpStatusCode);
    }

    public static UserException UserNotFound(Long id) {
        return new UserException(String.format("User with id %d not found", id), HttpStatus.NOT_FOUND);
    }

    public static UserException UsernameAlreadyExist(String username) {
        return new UserException(String.format("User with username %s already exists", username), HttpStatus.CONFLICT);
    }

    public static UserException EmailAlreadyExists(String email) {
        return new UserException(String.format("User with email %s already exists", email), HttpStatus.CONFLICT);
    }

    public static UserException LoginOrEmailAreIncorrect() {
        return new UserException("Login or password is incorrect", HttpStatus.BAD_REQUEST);
    }

    public static UserException NotFoundByEmail() {
        return LoginOrEmailAreIncorrect();
    }

    public static UserException PasswordIncorrect() {
        return LoginOrEmailAreIncorrect();
    }
}
