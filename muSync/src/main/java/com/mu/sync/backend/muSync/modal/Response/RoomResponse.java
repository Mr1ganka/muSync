package com.mu.sync.backend.muSync.modal.Response;

import java.util.List;

import com.mu.sync.backend.muSync.modal.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private String roomId;
    private boolean created;
    private String masterId;
    private List<String> clientIds;

    public RoomResponse(Room room, boolean created) {
        this.roomId = room.getRoomId();
        this.created = created;
        this.masterId = room.getMasterClientId();
        this.clientIds = room.getClientIds().stream().toList();
    }
}
