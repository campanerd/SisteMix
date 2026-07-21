package org.siste.mix.order.usecase;

import org.siste.mix.client.repository.ClientRepository;
import org.siste.mix.order.dto.CreateOrderRequest;
import org.siste.mix.order.dto.OrderResponse;
import org.siste.mix.order.model.Order;
import org.siste.mix.order.repository.OrderRepository;
import org.siste.mix.seller.repository.SellerRepository;
import org.siste.mix.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateOrderUseCase {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private InstallmentGenerator installmentGenerator;

    @Transactional
    public OrderResponse create(CreateOrderRequest data) {
        var client = clientRepository.getReferenceById(data.clientId());
        var seller = sellerRepository.getReferenceById(data.sellerId());
        var currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var order = orderRepository.save(new Order(data, client, seller, currentUser));
        installmentGenerator.generate(order);
        return new OrderResponse(order);
    }
}
