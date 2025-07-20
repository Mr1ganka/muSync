package ws

import (
	"encoding/json"
	"fmt"
	"log"
	"sync"

	"github.com/gorilla/websocket"
)

type Hub struct {
	Rooms        map[string]map[*Client]bool
	ClientToRoom map[string]string
	mutex        sync.Mutex
}

var hub = &Hub{
	Rooms:        make(map[string]map[*Client]bool),
	ClientToRoom: make(map[string]string),
}

func (h *Hub) AddClient(roomId string, client *Client) {
	h.mutex.Lock()
	defer h.mutex.Unlock()

	if existingRoom, exists := h.ClientToRoom[client.id]; exists {
		log.Printf("client: {%s} already exists in Room: {%s}", client.id, existingRoom)
		errorDto := MessageDto{
			Type:    "SERVER_ERROR",
			From:    "SERVER",
			Message: fmt.Sprintf("Client {%s} already exists in Room: {%s} ", client.id, roomId),
		}
		jsonMsg, _ := json.Marshal(errorDto)
		client.conn.WriteMessage(websocket.TextMessage, jsonMsg)
		return
	}

	if h.Rooms[roomId] == nil {
		h.Rooms[roomId] = make(map[*Client]bool)
		h.ClientToRoom[client.id] = roomId
	}
	h.Rooms[roomId][client] = true
	successDto := MessageDto{
		Type:    "SERVER_INFO",
		From:    "SERVER",
		Message: fmt.Sprintf("Client {%s} connected to Room: {%s} ", client.id, roomId),
	}

	jsonMsg, _ := json.Marshal(successDto)
	client.conn.WriteMessage(websocket.TextMessage, jsonMsg)
	log.Printf("Client (Id: %s) joined room %s ", client.id, roomId)
}

func (h *Hub) RemoveClient(client *Client) {
	h.mutex.Lock()
	defer h.mutex.Unlock()

	clients, ok := h.Rooms[client.roomId]
	if !ok {
		return
	}

	delete(clients, client)
	delete(h.ClientToRoom, client.id)

	if len(clients) == 0 {
		delete(h.Rooms, client.roomId)
	}

	log.Printf("Client:{%s} left room:{%s}", client.id, client.roomId)
}

func (h *Hub) Broadcast(sender *Client, rawMsg []byte) {
	h.mutex.Lock()
	defer h.mutex.Unlock()

	msg := MessageDto{Type: "Broadcast", From: sender.id, Message: string(rawMsg)}
	jsonMsg, err := json.Marshal(msg)
	if err != nil {
		log.Println("Failed to marshal message: " + string(jsonMsg) + " error: " + err.Error())
	}

	for client := range h.Rooms[sender.roomId] {
		if err := client.conn.WriteMessage(websocket.TextMessage, jsonMsg); err != nil {
			log.Println("Write error:", err)
		}
	}
}
