package ws

import (
	"log"
	"net/http"

	"github.com/gorilla/websocket"
)

var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool { return true },
}

func readMessages(client *Client) {
	defer func() {
		client.conn.Close()
		hub.RemoveClient(client)
	}()

	for {
		_, msg, err := client.conn.ReadMessage()
		if err != nil {
			log.Println("Read Error:", err)
			break
		}

		log.Printf("Message By {%s} to room {%s}: %s", client.id, client.roomId, msg)
		hub.Broadcast(client, msg)
	}

}

func HandleWebSocket(w http.ResponseWriter, r *http.Request) {
	roomId := r.URL.Query().Get("roomId")
	clientId := r.URL.Query().Get("clientId")
	if roomId == "" || clientId == "" {
		http.Error(w, "Missing room or client Id ", http.StatusBadRequest)
		return
	}

	conn, err := upgrader.Upgrade(w, r, nil)

	if err != nil {
		log.Println("Upgrade error:", err)
		return
	}

	client := &Client{conn: conn, roomId: roomId, id: clientId}
	hub.AddClient(roomId, client)
	go readMessages(client)
}
