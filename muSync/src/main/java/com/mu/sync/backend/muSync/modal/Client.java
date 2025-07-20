package com.mu.sync.backend.muSync.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private String clientId;
    private WebSocketSession socketSession;
}
