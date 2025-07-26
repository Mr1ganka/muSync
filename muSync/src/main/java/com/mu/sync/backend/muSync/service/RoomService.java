package com.mu.sync.backend.muSync.service;

import com.mu.sync.backend.muSync.modal.Client;
import com.mu.sync.backend.muSync.modal.Room;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface RoomService {

    Room createOrFetchRoom(String roomId);

    void joinRoom(String roomId, Client client);

    void leaveRoom(String client);

    Optional<Room> fetchRoom(String roomId);

    Optional<Room> fetchRoomUsingClientId(String clientId);

    String generateRoomIdBasedOnTime();
}
