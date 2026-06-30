package org.example.vendedor;

public record DadosListagemVendedor(
        Long id,
        String nome,
        String cpf,
        String telefone
) {
    public DadosListagemVendedor(Vendedor vendedor) {
        this(vendedor.getId(), vendedor.getNome(), vendedor.getCpf(), vendedor.getTelefone());
    }
}
