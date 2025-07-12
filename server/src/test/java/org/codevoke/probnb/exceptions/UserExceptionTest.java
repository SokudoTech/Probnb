package org.codevoke.probnb.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class UserExceptionTest {

    @Test
    void userNotFound_ShouldCreateExceptionWithCorrectMessageAndStatus() {
        // when
        UserException exception = UserException.UserNotFound(123L);

        // then
        assertThat(exception.getMessage()).isEqualTo("User with id 123 not found");
        assertThat(exception.getHTTPStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void usernameAlreadyExist_ShouldCreateExceptionWithCorrectMessageAndStatus() {
        // when
        UserException exception = UserException.UsernameAlreadyExist("testuser");

        // then
        assertThat(exception.getMessage()).isEqualTo("User with username testuser already exists");
        assertThat(exception.getHTTPStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void emailAlreadyExists_ShouldCreateExceptionWithCorrectMessageAndStatus() {
        // when
        UserException exception = UserException.EmailAlreadyExists("test@example.com");

        // then
        assertThat(exception.getMessage()).isEqualTo("User with email test@example.com already exists");
        assertThat(exception.getHTTPStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void loginOrEmailAreIncorrect_ShouldCreateExceptionWithCorrectMessageAndStatus() {
        // when
        UserException exception = UserException.LoginOrEmailAreIncorrect();

        // then
        assertThat(exception.getMessage()).isEqualTo("Login or password is incorrect");
        assertThat(exception.getHTTPStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void notFoundByEmail_ShouldReturnSameExceptionAsLoginOrEmailAreIncorrect() {
        // when
        UserException exception1 = UserException.NotFoundByEmail();
        UserException exception2 = UserException.LoginOrEmailAreIncorrect();

        // then
        assertThat(exception1.getMessage()).isEqualTo(exception2.getMessage());
        assertThat(exception1.getHTTPStatusCode()).isEqualTo(exception2.getHTTPStatusCode());
    }

    @Test
    void passwordIncorrect_ShouldReturnSameExceptionAsLoginOrEmailAreIncorrect() {
        // when
        UserException exception1 = UserException.PasswordIncorrect();
        UserException exception2 = UserException.LoginOrEmailAreIncorrect();

        // then
        assertThat(exception1.getMessage()).isEqualTo(exception2.getMessage());
        assertThat(exception1.getHTTPStatusCode()).isEqualTo(exception2.getHTTPStatusCode());
    }
} 