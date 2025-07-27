package com.mu.sync.backend.muSync.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mu.sync.backend.muSync.modal.Response.ClientResponse;
import com.mu.sync.backend.muSync.service.ClientService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private ClientService clientService;

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClient(@PathVariable String id) {
        return ResponseEntity.ok(clientService.getClientDetails(id));
    }
    
    @PostMapping("/{id}")
    public ResponseEntity<ClientResponse> makeMaster(@RequestBody String id) {
        return ResponseEntity.ok(clientService.makeMasterClient(id));
    }
    
    
}
