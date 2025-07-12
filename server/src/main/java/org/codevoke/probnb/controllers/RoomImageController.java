package org.codevoke.probnb.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codevoke.probnb.annotations.JwtRequired;
import org.codevoke.probnb.dto.RoomImageDTO;
import org.codevoke.probnb.service.RoomImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("rooms/{roomId}/images")
@RequiredArgsConstructor
public class RoomImageController {
    private final RoomImageService roomImageService;

    @GetMapping(value = "/{imageId}")
    public ResponseEntity<RoomImageDTO> getRoomImageById(
            @PathVariable Long imageId, 
            @PathVariable Long roomId) {
        return ResponseEntity.ok(roomImageService.findRoomImageById(imageId));
    }

    @GetMapping
    public ResponseEntity<List<RoomImageDTO>> getRoomImagesByRoomId(
            @PathVariable Long roomId) {
        return ResponseEntity.ok(roomImageService.findRoomImagesByRoomId(roomId));
    }

    @JwtRequired
    @PostMapping
    public ResponseEntity<RoomImageDTO> createRoomImage(
            @Valid @RequestBody RoomImageDTO roomImageDTO, 
            @PathVariable Long roomId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(roomImageService.createRoomImage(roomImageDTO));
    }

    @JwtRequired
    @PatchMapping(value = "/{imageId}")
    public ResponseEntity<RoomImageDTO> updateRoomImage(
            @PathVariable Long roomId, 
            @PathVariable Long imageId, 
            @RequestBody RoomImageDTO roomImageDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(roomImageService.updateRoomImage(imageId, roomImageDTO));
    }

    @JwtRequired
    @DeleteMapping(value = "/{imageId}")
    public ResponseEntity<RoomImageDTO> deleteRoomImage(
            @PathVariable Long roomId, 
            @PathVariable Long imageId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(roomImageService.deleteRoomImage(imageId));
    }
} 