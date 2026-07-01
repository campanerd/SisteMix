package org.example.cliente.dto;

import org.example.cliente.model.Client;

public record ClientSummary(Long id, String nome, String telefone, String cpfCnpj) {
    public ClientSummary(Client client) {
        this(client.getId(), client.getNome(), client.getTelefone(), client.getCpfCnpj());
    }
}
