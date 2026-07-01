package org.example.cliente;

public record ClientSummary(Long id, String nome, String telefone, String cpfCnpj) {
    public ClientSummary(Cliente cliente) {
        this(cliente.getId(), cliente.getNome(), cliente.getTelefone(), cliente.getCpfCnpj());
    }
}
