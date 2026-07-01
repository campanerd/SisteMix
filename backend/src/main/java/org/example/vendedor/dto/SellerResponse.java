package org.example.vendedor.dto;

import org.example.vendedor.model.Seller;

public record SellerResponse(Long id, String nome, String cpf, String telefone) {
    public SellerResponse(Seller seller) {
        this(seller.getId(), seller.getNome(), seller.getCpf(), seller.getTelefone());
    }
}