package org.example.seller.service;

import org.example.seller.dto.*;
import org.example.seller.model.Seller;
import org.example.seller.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerService {

    @Autowired
    private SellerRepository repository;

    @Transactional
    public Seller create(CreateSellerRequest data) {
        return repository.save(new Seller(data));
    }

    public Page<SellerSummary> list(Pageable pageable) {
        return repository.findAllByActiveTrue(pageable).map(SellerSummary::new);
    }

    @Transactional
    public SellerResponse update(UpdateSellerRequest data) {
        var seller = repository.getReferenceById(data.id());
        seller.update(data);
        return new SellerResponse(seller);
    }

    @Transactional
    public void delete(Long id) {
        repository.getReferenceById(id).deactivate();
    }

    public SellerResponse findById(Long id) {
        return new SellerResponse(repository.getReferenceById(id));
    }
}
