package org.example.cliente;

public record DadosDetalhamentoCliente(
        Long id,
        String nome,
        String telefone,
        String cpfCnpj,
        String email
) {
    public DadosDetalhamentoCliente(Cliente cliente) {
        this(cliente.getId(), cliente.getNome(), cliente.getTelefone(), cliente.getCpfCnpj(), cliente.getEmail());
    }
}
