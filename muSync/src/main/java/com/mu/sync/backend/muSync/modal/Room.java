package com.mu.sync.backend.muSync.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private String roomId;
    private ConcurrentHashMap<String, Client> clients;
    private String masterClientId;

    public void setClient(Client client) {
        if(!clients.containsKey(client.getClientId()))
            clients.put(client.getClientId(), client);
    }

    public Optional<Client> getClient(String clientId) {
        return Optional.ofNullable(clients.get(clientId));
    }

    public Collection<Client> getClients () {
        return clients.values();
    }

    public Collection<String> getClientIds() {
        return clients.keySet();
    }
}
