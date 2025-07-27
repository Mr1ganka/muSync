package com.mu.sync.backend.muSync.service;

import com.mu.sync.backend.muSync.modal.Client;
import com.mu.sync.backend.muSync.modal.Response.ClientResponse;

public interface ClientService {

    ClientResponse getClientDetails (String clientId);

    ClientResponse makeMasterClient (String clientId);
}