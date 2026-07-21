package org.siste.mix.seller.usecase;

import org.siste.mix.seller.dto.SellerResponse;
import org.siste.mix.seller.dto.UpdateSellerRequest;
import org.siste.mix.seller.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateSellerUseCase {

    @Autowired
    private SellerRepository repository;

    @Transactional
    public SellerResponse update(UpdateSellerRequest data) {
        var seller = repository.getReferenceById(data.id());
        seller.update(data);
        return new SellerResponse(seller);
    }
}
