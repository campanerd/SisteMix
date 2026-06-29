package org.example.cliente;

public record DadosListagemCliente(
        Long id,
        String nome,
        String telefone,
        String cpfCnpj
) {
    public DadosListagemCliente(Cliente cliente) {
        this(cliente.getId(), cliente.getNome(), cliente.getTelefone(), cliente.getCpfCnpj());
    }
}
