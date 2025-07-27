package com.mu.sync.backend.muSync.controller;

import com.mu.sync.backend.muSync.modal.Response.RoomResponse;
import com.mu.sync.backend.muSync.modal.Room;
import com.mu.sync.backend.muSync.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/createOrFetchRoom")
    public ResponseEntity<RoomResponse> createRoom(@RequestParam(required = false) String roomId) {
        String createRoomId = (roomId == null)? roomService.generateRoomIdBasedOnTime() : roomId;
        Room room = roomService.createOrFetchRoom(createRoomId);
        return ResponseEntity.ok(new RoomResponse(room,true));
    }
    
}
