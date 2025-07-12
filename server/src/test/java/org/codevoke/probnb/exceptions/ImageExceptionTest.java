package org.codevoke.probnb.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ImageExceptionTest {

    @Test
    void imageNotFound_ShouldCreateExceptionWithCorrectMessageAndStatus() {
        // when
        ImageException exception = ImageException.ImageNotFound(123L);

        // then
        assertThat(exception.getMessage()).isEqualTo("Image with id 123 not found");
        assertThat(exception.getHTTPStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void imageNotFound_WithDifferentId_ShouldCreateExceptionWithCorrectMessage() {
        // when
        ImageException exception = ImageException.ImageNotFound(456L);

        // then
        assertThat(exception.getMessage()).isEqualTo("Image with id 456 not found");
        assertThat(exception.getHTTPStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void invalidFormat_ShouldCreateExceptionWithCorrectMessageAndStatus() {
        // when
        ImageException exception = ImageException.InvalidFormat();

        // then
        assertThat(exception.getMessage()).isEqualTo("Invalid image format");
        assertThat(exception.getHTTPStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void ioException_ShouldCreateExceptionWithCorrectMessageAndStatus() {
        // when
        ImageException exception = ImageException.IOException();

        // then
        assertThat(exception.getMessage()).isEqualTo("Error reading image file");
        assertThat(exception.getHTTPStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 