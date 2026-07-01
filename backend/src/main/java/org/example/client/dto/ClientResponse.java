package org.example.client.dto;

import org.example.client.model.Client;

public record ClientResponse(Long id, String name, String phone, String cpfCnpj, String email) {
    public ClientResponse(Client client) {
        this(client.getId(), client.getName(), client.getPhone(), client.getCpfCnpj(), client.getEmail());
    }
}
