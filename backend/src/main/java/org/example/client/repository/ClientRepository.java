package org.example.cliente.repository;

import org.example.cliente.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Page<Client> findAllByAtivoTrue(Pageable pageable);
}
