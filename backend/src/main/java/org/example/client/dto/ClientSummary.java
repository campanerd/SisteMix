package org.example.client.dto;

import org.example.client.model.Client;

public record ClientSummary(Long id, String name, String phone, String cpfCnpj) {
    public ClientSummary(Client client) {
        this(client.getId(), client.getName(), client.getPhone(), client.getCpfCnpj());
    }
}
