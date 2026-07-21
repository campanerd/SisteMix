package org.siste.mix.seller.usecase;

import org.siste.mix.seller.dto.SellerSummary;
import org.siste.mix.seller.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListSellersUseCase {

    @Autowired
    private SellerRepository repository;

    public Page<SellerSummary> list(Pageable pageable) {
        return repository.findAllByActiveTrue(pageable).map(SellerSummary::new);
    }
}
