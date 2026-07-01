package org.example.cliente;

public record ClientResponse(Long id, String nome, String telefone, String cpfCnpj, String email) {
    public ClientResponse(Cliente cliente) {
        this(cliente.getId(), cliente.getNome(), cliente.getTelefone(), cliente.getCpfCnpj(), cliente.getEmail());
    }
}
