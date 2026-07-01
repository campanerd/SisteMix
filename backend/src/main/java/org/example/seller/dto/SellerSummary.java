package org.example.vendedor.dto;

import org.example.vendedor.model.Seller;

public record SellerSummary(Long id, String nome, String cpf) {
    public SellerSummary(Seller seller) {
        this(seller.getId(), seller.getNome(), seller.getCpf());
    }
}