package org.example.seller.repository;

import org.example.seller.model.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Page<Seller> findAllByActiveTrue(Pageable pageable);
}
