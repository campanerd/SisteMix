package org.example.pedido;

import org.example.cliente.repository.ClientRepository;
import org.example.parcela.Parcela;
import org.example.parcela.ParcelaRepository;
import org.example.vendedor.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Pedido cadastrar(DadosCadastroPedido dados) {
        var client = clientRepository.getReferenceById(dados.idCliente());
        var seller = sellerRepository.getReferenceById(dados.idVendedor());
        var pedido = pedidoRepository.save(new Pedido(dados, client, seller));

        gerarParcelas(pedido);

        return pedido;
    }

    private void gerarParcelas(Pedido pedido) {
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
