package org.codevoke.probnb.controllers;

import lombok.RequiredArgsConstructor;
import org.codevoke.probnb.dto.RoomDTO;
import org.codevoke.probnb.dto.RoomFilterDTO;
import org.codevoke.probnb.dto.RoomSearchDTO;
import org.codevoke.probnb.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class AllRoomsController {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomSearchDTO>> getFilteredRooms(RoomFilterDTO filter) {
        return ResponseEntity.ok(roomService.searchRooms(filter));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.findRoomById(roomId, null));
    }

    @PostMapping("/search")
    public ResponseEntity<List<RoomSearchDTO>> searchRooms(@RequestBody RoomFilterDTO filter) {
        return ResponseEntity.ok(roomService.searchRooms(filter));
    }
} 