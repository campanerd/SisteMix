package org.example.vendedor;

public record SellerSummary(Long id, String nome, String cpf, String telefone) {
    public SellerSummary(Vendedor vendedor) {
        this(vendedor.getId(), vendedor.getNome(), vendedor.getCpf(), vendedor.getTelefone());
    }
}
