package org.example.seller.dto;

import org.example.seller.model.Seller;

public record SellerResponse(Long id, String name, String cpf, String phone) {
    public SellerResponse(Seller seller) {
        this(seller.getId(), seller.getName(), seller.getCpf(), seller.getPhone());
    }
}
