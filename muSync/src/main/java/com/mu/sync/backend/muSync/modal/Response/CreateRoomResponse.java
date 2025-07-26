package com.mu.sync.backend.muSync.modal.Response;

import com.mu.sync.backend.muSync.modal.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomResponse {
    private String roomId;
    private boolean created;

    public CreateRoomResponse(Room room, boolean created) {
        this.roomId = room.getRoomId();
        this.created = created;
    }
}
