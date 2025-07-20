package com.mu.sync.backend.muSync.websocket;

import com.mu.sync.backend.muSync.modal.Client;
import com.mu.sync.backend.muSync.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class RoomWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private RoomService roomService;

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = getParam(session, "roomId");
        String clientId = getParam(session, "clientId");

        log.info("New connection for client: {} to room: {} ", clientId, roomId);
        Client client = new Client(clientId, session);
        roomService.joinRoom(roomId, client);
    }

    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {}

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String clientId = getParam(session, "clientId");
        log.info("Client: {} closing connection for session: {} ", clientId, session.getId());
        roomService.leaveRoom(clientId);
    }

    private String getParam(WebSocketSession session, String key) {
        return session.getUri().getQuery().split(key + "=")[1].split("&")[0];
    }
}
