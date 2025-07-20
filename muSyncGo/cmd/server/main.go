package main

import (
	"fmt"
	"log"
	"mySyncGo/internal/ws"
	"net/http"
)

func main() {
	http.HandleFunc("/ws", ws.HandleWebSocket)

	http.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte("OK"))
	})

	fmt.Println("Server running on http://localhost:8080")
	log.Fatal(http.ListenAndServe(":8080", nil))

}
