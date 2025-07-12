package org.codevoke.probnb.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codevoke.probnb.annotations.JwtRequired;
import org.codevoke.probnb.dto.ReservationDTO;
import org.codevoke.probnb.exceptions.AuthException;
import org.codevoke.probnb.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("users/{userId}/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping(value = "/{reservationId}")
    public ResponseEntity<ReservationDTO> getReservationById(
            @PathVariable Long reservationId, 
            @PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.findReservationById(reservationId));
    }

    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getReservationsByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.findReservationsByUserId(userId));
    }

    @JwtRequired
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody ReservationDTO reservationDTO, 
            @PathVariable Long userId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservationService.createReservation(reservationDTO, userId));
    }

    @JwtRequired
    @PatchMapping(value = "/{reservationId}")
    public ResponseEntity<ReservationDTO> updateReservation(
            @RequestAttribute Long authenticatedUserId, 
            @PathVariable Long userId, 
            @PathVariable Long reservationId, 
            @RequestBody ReservationDTO reservationDTO) {
        if (!Objects.equals(authenticatedUserId, userId))
            throw AuthException.AuthorizationFailed();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reservationService.updateReservation(reservationId, reservationDTO));
    }

    @JwtRequired
    @DeleteMapping(value = "/{reservationId}")
    public ResponseEntity<ReservationDTO> deleteReservation(
            @RequestAttribute Long authenticatedUserId, 
            @PathVariable Long userId, 
            @PathVariable Long reservationId) {
        if (!Objects.equals(authenticatedUserId, userId))
            throw AuthException.AuthorizationFailed();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reservationService.deleteReservation(reservationId));
    }
} 