package org.siste.mix.seller.usecase;

import org.siste.mix.seller.dto.CreateSellerRequest;
import org.siste.mix.seller.model.Seller;
import org.siste.mix.seller.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateSellerUseCase {

    @Autowired
    private SellerRepository repository;

    @Transactional
    public Seller create(CreateSellerRequest data) {
        return repository.save(new Seller(data));
    }
}
