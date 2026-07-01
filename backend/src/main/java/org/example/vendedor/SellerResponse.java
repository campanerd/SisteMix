package org.example.vendedor;

public record SellerResponse(Long id, String nome, String cpf, String telefone) {
    public SellerResponse(Vendedor vendedor) {
        this(vendedor.getId(), vendedor.getNome(), vendedor.getCpf(), vendedor.getTelefone());
    }
}
