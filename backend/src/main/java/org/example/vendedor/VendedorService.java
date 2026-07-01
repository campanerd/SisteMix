package org.example.vendedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VendedorService {

    @Autowired
    private VendedorRepository repository;

    @Transactional
    public Vendedor create(CreateSellerRequest data) {
        return repository.save(new Vendedor(data));
    }

    public Page<SellerSummary> list(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable).map(SellerSummary::new);
    }

    @Transactional
    public SellerResponse update(UpdateSellerRequest data) {
        var vendedor = repository.getReferenceById(data.id());
        vendedor.update(data);
        return new SellerResponse(vendedor);
    }

    @Transactional
    public void delete(Long id) {
        var vendedor = repository.getReferenceById(id);
        vendedor.deactivate();
    }

    public SellerResponse findById(Long id) {
        var vendedor = repository.getReferenceById(id);
        return new SellerResponse(vendedor);
    }
}
