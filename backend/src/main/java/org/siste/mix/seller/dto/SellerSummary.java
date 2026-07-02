package org.siste.mix.seller.dto;

import org.siste.mix.seller.model.Seller;

public record SellerSummary(Long id, String name, String cpf) {
    public SellerSummary(Seller seller) {
        this(seller.getId(), seller.getName(), seller.getCpf());
    }
}
