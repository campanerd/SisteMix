package org.example.vendedor;

public record DadosDetalhamentoVendedor(
        Long id,
        String nome,
        String cpf,
        String telefone
) {
    public DadosDetalhamentoVendedor(Vendedor vendedor) {
        this(vendedor.getId(), vendedor.getNome(), vendedor.getCpf(), vendedor.getTelefone());
    }
}
