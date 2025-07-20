const express = require("express")
const http = require("http")
const { Server } = require("socket.io")
const cors = require("cors");
const { Socket } = require("dgram");

const app = express()
app.use(cors())
app.use(express.json())

const server = http.createServer(app)
const io = new Server(server, {
    cors: {
        origin: "*",
    },
});

const rooms = new Map()

app.post("/rooms", (req, res) => {
    console.log(`/rooms is being hit`)
    const roomId = `room-${Date.now()}`
    rooms.set(roomId, new Set())
    res.json({roomId})
    console.log(`Room Id creeaded: ${roomId}`)
})

io.on("connection", (socket) => {
    console.log(`Socket connected: ${socket.id}`)
    
    socket.on("join-room", (roomId) => {
        socket.join(roomId);
        
        if (!rooms.get(roomId))
            rooms.set(roomId, new Set())

        rooms.get(roomId).add(socket.id)
        console.log(`Socket: ${socket.id} has joined room: ${roomId}`)
    });

    socket.on("disconnect", () => {
        for (const [roomId, sockets] of roomId.entries()) {
            sockets.delete(socket.id)
            if (sockets.size == 0) rooms.delete(roomId)
        }
    });

    socket.on("play", ({roomId}) => {
        socket.to(roomId).emit("play")
    });

    socket.on("pause", ({roomId}) => {
        socket.to(roomId).emit("pause")
    });

})


const PORT = 4000;
server.listen(PORT, ()=>{
    console.log(`Server listening on Port: ${PORT}`)
})