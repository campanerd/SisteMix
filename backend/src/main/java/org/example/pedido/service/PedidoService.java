package org.example.pedido.service;

import org.example.cliente.repository.ClientRepository;
import org.example.parcela.model.Parcela;
import org.example.parcela.repository.ParcelaRepository;
import org.example.pedido.dto.CreateOrderRequest;
import org.example.pedido.dto.OrderResponse;
import org.example.pedido.dto.OrderSummary;
import org.example.pedido.dto.UpdateOrderRequest;
import org.example.pedido.model.Pedido;
import org.example.pedido.repository.PedidoRepository;
import org.example.vendedor.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ParcelaRepository parcelaRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Transactional
    public OrderResponse create(CreateOrderRequest data) {
        var client = clientRepository.getReferenceById(data.idCliente());
        var seller = sellerRepository.getReferenceById(data.idVendedor());
        var pedido = pedidoRepository.save(new Pedido(data, client, seller));
        generateInstallments(pedido);
        return new OrderResponse(pedido);
    }

    public Page<OrderSummary> list(Pageable pageable) {
        return pedidoRepository.findAllByAtivoTrue(pageable).map(OrderSummary::new);
    }

    @Transactional
    public OrderResponse update(UpdateOrderRequest data) {
        var pedido = pedidoRepository.getReferenceById(data.id());
        var seller = data.idVendedor() != null
                ? sellerRepository.getReferenceById(data.idVendedor())
                : null;
        pedido.update(data, seller);
        return new OrderResponse(pedido);
    }

    @Transactional
    public void delete(Long id) {
        var pedido = pedidoRepository.getReferenceById(id);
        pedido.deactivate();
    }

    public OrderResponse findById(Long id) {
        var pedido = pedidoRepository.getReferenceById(id);
        return new OrderResponse(pedido);
    }

    private void generateInstallments(Pedido pedido) {
        int total = pedido.getTotalParcelas();
        BigDecimal valorParcela = pedido.getValorTotal()
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.DOWN);
        BigDecimal valorUltima = pedido.getValorTotal()
                .subtract(valorParcela.multiply(BigDecimal.valueOf(total - 1)));

        for (int i = 1; i <= total; i++) {
            BigDecimal valor = (i == total) ? valorUltima : valorParcela;
            var vencimento = pedido.getDataPedido().plusMonths(i);
            parcelaRepository.save(new Parcela(i, valor, vencimento, pedido));
        }
    }
}
