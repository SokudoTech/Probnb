package org.codevoke.probnb.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationExceptionTest {

    @Test
    void reservationNotFound_ShouldCreateExceptionWithCorrectMessageAndStatus() {
        // when
        ReservationException exception = ReservationException.ReservationNotFound(123L);

        // then
        assertThat(exception.getMessage()).isEqualTo("Reservation with id 123 not found");
        assertThat(exception.getHTTPStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void reservationNotFound_WithDifferentId_ShouldCreateExceptionWithCorrectMessage() {
        // when
        ReservationException exception = ReservationException.ReservationNotFound(456L);

        // then
        assertThat(exception.getMessage()).isEqualTo("Reservation with id 456 not found");
        assertThat(exception.getHTTPStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
} 