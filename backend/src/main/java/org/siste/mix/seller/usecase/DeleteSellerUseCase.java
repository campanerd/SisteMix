package org.siste.mix.seller.usecase;

import org.siste.mix.seller.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteSellerUseCase {

    @Autowired
    private SellerRepository repository;

    @Transactional
    public void delete(Long id) {
        repository.getReferenceById(id).deactivate();
    }
}
