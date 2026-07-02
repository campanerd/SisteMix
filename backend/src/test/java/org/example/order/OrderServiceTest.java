package org.example.order;

import org.example.client.dto.CreateClientRequest;
import org.example.client.model.Client;
import org.example.client.repository.ClientRepository;
import org.example.installment.model.Installment;
import org.example.installment.repository.InstallmentRepository;
import org.example.order.dto.CreateOrderRequest;
import org.example.order.model.Order;
import org.example.order.repository.OrderRepository;
import org.example.order.service.OrderService;
import org.example.seller.dto.CreateSellerRequest;
import org.example.seller.model.Seller;
import org.example.seller.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private InstallmentRepository installmentRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private OrderService service;

    private Client client;
    private Seller seller;

    @BeforeEach
    void setUp() {
        client = new Client(new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com"));
        seller = new Seller(new CreateSellerRequest("Maria Souza", "98765432100", "11988888888"));
        when(clientRepository.getReferenceById(1L)).thenReturn(client);
        when(sellerRepository.getReferenceById(1L)).thenReturn(seller);
    }

    @Test
    void should_create_correct_number_of_installments() {
        var request = orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15));

        // WHEN
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder(request));

        // ASSERT
        service.create(request);

        // InOrder
        InOrder inOrder = inOrder(orderRepository, installmentRepository);
        inOrder.verify(orderRepository).save(any(Order.class));
        inOrder.verify(installmentRepository, times(3)).save(any(Installment.class));
    }

    @Test
    void should_distribute_amount_with_rounding_on_last_installment() {
        var request = orderWith(new BigDecimal("100.00"), 3, LocalDate.of(2026, 1, 15));

        // WHEN
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder(request));

        // ASSERT
        service.create(request);

        var captor = ArgumentCaptor.forClass(Installment.class);
        List<Installment> installments = captor.getAllValues();

        // InOrder
        InOrder inOrder = inOrder(orderRepository, installmentRepository);
        inOrder.verify(orderRepository).save(any(Order.class));
        inOrder.verify(installmentRepository, times(3)).save(captor.capture());

        assertEquals(0, installments.get(0).getAmount().compareTo(new BigDecimal("33.33")));
        assertEquals(0, installments.get(1).getAmount().compareTo(new BigDecimal("33.33")));
        assertEquals(0, installments.get(2).getAmount().compareTo(new BigDecimal("33.34")));
    }

    @Test
    void should_distribute_equal_amounts_when_division_is_exact() {
        var request = orderWith(new BigDecimal("300.00"), 3, LocalDate.of(2026, 1, 15));

        // WHEN
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder(request));

        // ASSERT
        service.create(request);

        var captor = ArgumentCaptor.forClass(Installment.class);

        // InOrder
        InOrder inOrder = inOrder(orderRepository, installmentRepository);
        inOrder.verify(orderRepository).save(any(Order.class));
        inOrder.verify(installmentRepository, times(3)).save(captor.capture());

        captor.getAllValues().forEach(i ->
                assertEquals(0, i.getAmount().compareTo(new BigDecimal("100.00")))
        );
    }

    @Test
    void should_set_monthly_due_dates_from_order_date() {
        var orderDate = LocalDate.of(2026, 1, 15);
        var request = orderWith(new BigDecimal("200.00"), 2, orderDate);

        // WHEN
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder(request));

        // ASSERT
        service.create(request);

        var captor = ArgumentCaptor.forClass(Installment.class);

        // InOrder
        InOrder inOrder = inOrder(orderRepository, installmentRepository);
        inOrder.verify(orderRepository).save(any(Order.class));
        inOrder.verify(installmentRepository, times(2)).save(captor.capture());

        List<Installment> installments = captor.getAllValues();
        assertEquals(LocalDate.of(2026, 2, 15), installments.get(0).getDueDate());
        assertEquals(LocalDate.of(2026, 3, 15), installments.get(1).getDueDate());
    }

    private CreateOrderRequest orderWith(BigDecimal totalAmount, int totalInstallments, LocalDate orderDate) {
        return new CreateOrderRequest("PED-001", orderDate, orderDate, totalAmount, totalInstallments, null, 1L, 1L);
    }

    private Order savedOrder(CreateOrderRequest data) {
        return new Order(1L, data.orderNumber(), data.issueDate(), data.orderDate(),
                data.totalAmount(), data.totalInstallments(), data.notes(), client, seller, true);
    }
}
