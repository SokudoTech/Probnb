package org.codevoke.probnb.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        this::getDefaultMessage,
                        (existing, replacement) -> existing));

        Map<String, Object> response = new HashMap<>();
        response.put("status", "failed");
        response.put("fields", errors);
        return response;
    }

    private String getDefaultMessage(FieldError error) {
        String message = error.getDefaultMessage();
        return (message != null) ? message : "Validation failed";
    }

    @ExceptionHandler(HTTPException.class)
    public ResponseEntity<Map<String, String>> handleHTTPException(HTTPException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("status", "failed");
        errors.put("error", exception.getMessage());
        return ResponseEntity
                .status(exception.getHTTPStatusCode())
                .body(errors);
    }
}
