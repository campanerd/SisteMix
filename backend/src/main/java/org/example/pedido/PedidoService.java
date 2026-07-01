package org.example.pedido;

import org.example.cliente.ClienteRepository;
import org.example.parcela.Parcela;
import org.example.parcela.ParcelaRepository;
import org.example.vendedor.VendedorRepository;
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
    private ClienteRepository clienteRepository;

    @Autowired
    private VendedorRepository vendedorRepository;

    @Transactional
    public Pedido create(CreateOrderRequest data) {
        var cliente = clienteRepository.getReferenceById(data.idCliente());
        var vendedor = vendedorRepository.getReferenceById(data.idVendedor());
        var pedido = pedidoRepository.save(new Pedido(data, cliente, vendedor));
        generateInstallments(pedido);
        return pedido;
    }

    public Page<OrderSummary> list(Pageable pageable) {
        return pedidoRepository.findAllByAtivoTrue(pageable).map(OrderSummary::new);
    }

    @Transactional
    public OrderResponse update(UpdateOrderRequest data) {
        var pedido = pedidoRepository.getReferenceById(data.id());
        var vendedor = data.idVendedor() != null
                ? vendedorRepository.getReferenceById(data.idVendedor())
                : null;
        pedido.update(data, vendedor);
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
        BigDecimal installmentValue = pedido.getValorTotal()
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.DOWN);
        BigDecimal lastInstallmentValue = pedido.getValorTotal()
                .subtract(installmentValue.multiply(BigDecimal.valueOf(total - 1)));

        for (int i = 1; i <= total; i++) {
            BigDecimal value = (i == total) ? lastInstallmentValue : installmentValue;
            var dueDate = pedido.getDataPedido().plusMonths(i);
            parcelaRepository.save(new Parcela(i, value, dueDate, pedido));
        }
    }
}
