package com.mu.sync.backend.muSync.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mu.sync.backend.muSync.modal.Client;
import com.mu.sync.backend.muSync.modal.DTO.MessageDto;
import com.mu.sync.backend.muSync.modal.Room;
import com.mu.sync.backend.muSync.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
public class RoomWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private RoomService roomService;

    @Autowired
    private ObjectMapper mapper;

    enum serverMessages {
        JOINED_ROOM ("JOINED_ROOM"),
        LEFT_ROOM ("LEFT_ROOM");

        private final String stringMsg;

        serverMessages(String stringMsg) {
            this.stringMsg = stringMsg;
        }

        @Override
        public String toString() {
            return stringMsg;
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String clientId = (String) session.getAttributes().get("clientId");
        String roomId = (String) session.getAttributes().get("roomId");

        log.info("Payload message {} received from session {} ", payload, session.getId());

        if (clientId == null || roomId == null ) {
            log.warn("Either client id: {} or roomId: {} is empty ", clientId, roomId);
            return;
        }

        MessageDto incoming = mapper.readValue(payload, MessageDto.class);

        MessageDto outGoing = new MessageDto(incoming.getType(), clientId, incoming.getMessage());

        Optional<Room> room = roomService.fetchRoom(roomId);

        if(room.isPresent())
            broadcastToRoom(room.get(), outGoing, false, clientId );
        else
            log.warn("Room wasn't found for roomId: {}", roomId);
    }

    public void broadcastToRoom(Room room, MessageDto messageDto, boolean includeSender, String senderId) throws IOException {
        Collection<Client> clients = room.getClients();
        String json = mapper.writeValueAsString(messageDto);

        for(Client client: clients) {
            if(!includeSender && client.getClientId().equalsIgnoreCase(senderId)) continue;
            try {
                client.getSocketSession().sendMessage(new TextMessage(json));
                log.debug("Broadcasted to client: {}", client.getClientId());
            } catch (Exception e) {
                log.warn("Failed to send message to client: {} skipping", client.getClientId(), e);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String clientId = (String) session.getAttributes().get("clientId");

        log.info("Client: {} closing connection for session: {} ", clientId, session.getId());
        Optional<Room> room = roomService.fetchRoomUsingClientId(clientId);

        if(room.isPresent()) {
            String message = "Client: " + clientId + " left room: " + room.get().getRoomId();
            MessageDto leftRoomDto = new MessageDto(serverMessages.LEFT_ROOM.toString(), "SERVER", message);
            broadcastToRoom(room.get(), leftRoomDto, true, clientId );
            roomService.leaveRoom(clientId);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = getParam(session, "roomId");
        String clientId = getParam(session, "clientId");

        if (clientId == null || roomId == null) {
            log.warn("Missing clientId or roomId in connection params");
            session.close();
            return;
        }

        session.getAttributes().put("clientId", clientId);
        session.getAttributes().put("roomId", roomId);

        log.info("New connection for client: {} to room: {} ", clientId, roomId);
        Client client = new Client(clientId, session);

        Room room = roomService.createOrFetchRoom(roomId);
        roomService.joinRoom(roomId, client);

        String message = "Client: " + clientId + " joined room: " + roomId;
        MessageDto joinedRoomDto = new MessageDto(serverMessages.JOINED_ROOM.toString(), "SERVER", message);
        broadcastToRoom(room, joinedRoomDto, false, clientId );
    }

    private String getParam(WebSocketSession session, String key) {
        return UriComponentsBuilder.fromUri(session.getUri()).build()
                .getQueryParams().getFirst(key);
    }
}
