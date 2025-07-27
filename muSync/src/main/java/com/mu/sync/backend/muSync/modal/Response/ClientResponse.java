package com.mu.sync.backend.muSync.modal.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    String clientId;
    boolean isMasterClient;
    String roomId;
    boolean doesExist;

    public ClientResponse(boolean doesExist, String clientId, String roomId, boolean isMasterClient) {
        this.doesExist = doesExist;
        this.clientId = clientId;
        this.roomId = roomId;
        this.isMasterClient = isMasterClient;
    }
}
