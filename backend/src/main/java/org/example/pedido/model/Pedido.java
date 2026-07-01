package org.example.pedido.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cliente.model.Client;
import org.example.pedido.dto.CreateOrderRequest;
import org.example.pedido.dto.UpdateOrderRequest;
import org.example.vendedor.model.Seller;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "pedidos")
@Entity(name = "Pedido")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroPedido;
    private LocalDate dataEmissao;
    private LocalDate dataPedido;
    private BigDecimal valorTotal;
    private Integer totalParcelas;
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor")
    private Seller seller;

    private Boolean ativo;

    public Pedido(CreateOrderRequest data, Client client, Seller seller) {
        this.numeroPedido = data.numeroPedido();
        this.dataEmissao = data.dataEmissao();
        this.dataPedido = data.dataPedido();
        this.valorTotal = data.valorTotal();
        this.totalParcelas = data.totalParcelas();
        this.observacao = data.observacao();
        this.client = client;
        this.seller = seller;
        this.ativo = true;
    }

    public void update(UpdateOrderRequest data, Seller seller) {
        if (data.dataEmissao() != null) this.dataEmissao = data.dataEmissao();
        if (data.dataPedido() != null) this.dataPedido = data.dataPedido();
        if (data.valorTotal() != null) this.valorTotal = data.valorTotal();
        if (data.observacao() != null) this.observacao = data.observacao();
        if (seller != null) this.seller = seller;
    }

    public void deactivate() {
        this.ativo = false;
    }
}
