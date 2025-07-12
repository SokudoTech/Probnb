package org.codevoke.probnb.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codevoke.probnb.annotations.JwtRequired;
import org.codevoke.probnb.dto.HostReservationDTO;
import org.codevoke.probnb.exceptions.AuthException;
import org.codevoke.probnb.service.HostReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("users/{hostId}/host-reservations")
@RequiredArgsConstructor
public class HostReservationController {
    private final HostReservationService hostReservationService;

    @GetMapping(value = "/{reservationId}")
    public ResponseEntity<HostReservationDTO> getHostReservationById(
            @PathVariable Long reservationId, 
            @PathVariable Long hostId) {
        return ResponseEntity.ok(hostReservationService.findHostReservationById(reservationId));
    }

    @GetMapping
    public ResponseEntity<List<HostReservationDTO>> getHostReservationsByHostId(
            @PathVariable Long hostId) {
        return ResponseEntity.ok(hostReservationService.findHostReservationsByHostId(hostId));
    }

    @JwtRequired
    @PostMapping
    public ResponseEntity<HostReservationDTO> createHostReservation(
            @Valid @RequestBody HostReservationDTO hostReservationDTO, 
            @PathVariable Long hostId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(hostReservationService.createHostReservation(hostReservationDTO, hostId));
    }

    @JwtRequired
    @PatchMapping(value = "/{reservationId}")
    public ResponseEntity<HostReservationDTO> updateHostReservation(
            @RequestAttribute Long userId, 
            @PathVariable Long hostId, 
            @PathVariable Long reservationId, 
            @RequestBody HostReservationDTO hostReservationDTO) {
        if (!Objects.equals(userId, hostId))
            throw AuthException.AuthorizationFailed();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(hostReservationService.updateHostReservation(reservationId, hostReservationDTO));
    }

    @JwtRequired
    @DeleteMapping(value = "/{reservationId}")
    public ResponseEntity<HostReservationDTO> deleteHostReservation(
            @RequestAttribute Long userId, 
            @PathVariable Long hostId, 
            @PathVariable Long reservationId) {
        if (!Objects.equals(userId, hostId))
            throw AuthException.AuthorizationFailed();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(hostReservationService.deleteHostReservation(reservationId));
    }
} 