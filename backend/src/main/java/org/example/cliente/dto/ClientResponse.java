package org.example.cliente.dto;

import org.example.cliente.model.Client;

public record ClientResponse(Long id, String nome, String telefone, String cpfCnpj, String email) {
    public ClientResponse(Client client) {
        this(client.getId(), client.getNome(), client.getTelefone(), client.getCpfCnpj(), client.getEmail());
    }
}
