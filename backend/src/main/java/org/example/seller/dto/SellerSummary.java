package org.example.seller.dto;

import org.example.seller.model.Seller;

public record SellerSummary(Long id, String name, String cpf) {
    public SellerSummary(Seller seller) {
        this(seller.getId(), seller.getName(), seller.getCpf());
    }
}
