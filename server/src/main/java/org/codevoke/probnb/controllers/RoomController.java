package org.codevoke.probnb.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codevoke.probnb.annotations.JwtRequired;
import org.codevoke.probnb.dto.RoomDTO;
import org.codevoke.probnb.exceptions.AuthException;
import org.codevoke.probnb.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("users/{hostId}/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping(value = "/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long roomId, @PathVariable Long hostId) {
        return ResponseEntity.ok(roomService.findRoomById(roomId, hostId));
    }

    @JwtRequired
    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody RoomDTO roomDTO, @PathVariable Long hostId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(roomService.createRoom(roomDTO, hostId));
    }

    @JwtRequired
    @PatchMapping(value = "/{roomId}")
    public ResponseEntity<RoomDTO> updateRoom(@RequestAttribute Long userId, @PathVariable Long hostId, @PathVariable Long roomId, @RequestBody RoomDTO roomDTO) {
        if (!Objects.equals(userId, hostId))
            throw AuthException.AuthorizationFailed();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(roomService.patchRoom(roomId, roomDTO));
    }

    @JwtRequired
    @DeleteMapping(value = "/{roomId}")
    public ResponseEntity<RoomDTO> deleteRoom(@RequestAttribute Long userId, @PathVariable Long hostId, @PathVariable Long roomId) {
        if (!Objects.equals(userId, hostId))
            throw AuthException.AuthorizationFailed();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(roomService.deleteRoom(roomId));
    }
}
