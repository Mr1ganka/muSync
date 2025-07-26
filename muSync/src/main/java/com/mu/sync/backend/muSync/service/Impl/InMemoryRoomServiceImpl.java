package com.mu.sync.backend.muSync.service.Impl;

import com.mu.sync.backend.muSync.modal.Client;
import com.mu.sync.backend.muSync.modal.Room;
import com.mu.sync.backend.muSync.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class InMemoryRoomServiceImpl implements RoomService {

    private static final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> clientToRoom = new ConcurrentHashMap<>();

    @Override
    public Optional<Room> fetchRoom(String roomId) {
        if (rooms.containsKey(roomId))
            return Optional.ofNullable(rooms.get(roomId));
        else
            return Optional.empty();
    }

    @Override
    public Optional<Room> fetchRoomUsingClientId(String clientId) {
        if (clientToRoom.containsKey(clientId)) {
            String roomId = clientToRoom.get(clientId);
            return Optional.of(rooms.get(roomId));
        } else
            return Optional.empty();
    }

    @Override
    public Room createOrFetchRoom(String roomId) {
        Room room;
        if (!rooms.containsKey(roomId)) {
            room = new Room(roomId, new ConcurrentHashMap<String, Client>(), null);
            rooms.put(roomId, room);
            log.info("Created new room: {} ", roomId);
        } else {
            room = rooms.get(roomId);
            log.debug("Fetched existing room: {} ", roomId);

        }
        
        return room;
    }

    @Override
    public void joinRoom(String roomId, Client client) {
       if (clientToRoom.containsKey(client.getClientId())) {
           String currentRoomId = clientToRoom.get(client.getClientId());

           if (!currentRoomId.equalsIgnoreCase(roomId)) {
               rooms.get(currentRoomId).getClients().remove(client.getClientId());
               log.info("Client: {} left room: {} ", client.getClientId(), currentRoomId);
           } else {
               log.info("Client: {} already exists in room: {} ", client.getClientId(), roomId);
           }
       }
       Room room = createOrFetchRoom(roomId);
       room.setClient(client);
       clientToRoom.put(client.getClientId(), roomId);
       log.info("Client: {} added to room: {} ", client.getClientId(), roomId);
    }

    @Override
    public void leaveRoom(String clientId) {
        if(clientToRoom.containsKey(clientId)) {
            String roomId = clientToRoom.get(clientId);
            Room currentRoom = rooms.get(roomId);

            //we check if the last member is standing
            if(currentRoom.getClients().size() == 1) {
                rooms.remove(roomId);
                log.info("Deleted room: {} since it had only 1 client ", roomId);
            } else {
                currentRoom.getClients().remove(clientId);
                log.info("Removed client: {} from room: {}", clientId, roomId);
            }
            clientToRoom.remove(clientId);
        } else {
            log.warn("Tried removing client: {} but client was not in clientList ", clientId);
        }
    }
}
