package org.siste.mix.seller.usecase;

import org.siste.mix.seller.dto.SellerResponse;
import org.siste.mix.seller.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindSellerByIdUseCase {

    @Autowired
    private SellerRepository repository;

    public SellerResponse findById(Long id) {
        return new SellerResponse(repository.getReferenceById(id));
    }
}
