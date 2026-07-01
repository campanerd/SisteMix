package org.example.pedido;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cliente.Cliente;
import org.example.vendedor.Vendedor;

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
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor")
    private Vendedor vendedor;

    private Boolean ativo;

    public Pedido(CreateOrderRequest data, Cliente cliente, Vendedor vendedor) {
        this.numeroPedido = data.numeroPedido();
        this.dataEmissao = data.dataEmissao();
        this.dataPedido = data.dataPedido();
        this.valorTotal = data.valorTotal();
        this.totalParcelas = data.totalParcelas();
        this.observacao = data.observacao();
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.ativo = true;
    }

    public void update(UpdateOrderRequest data, Vendedor vendedor) {
        if (data.dataEmissao() != null) this.dataEmissao = data.dataEmissao();
        if (data.dataPedido() != null) this.dataPedido = data.dataPedido();
        if (data.valorTotal() != null) this.valorTotal = data.valorTotal();
        if (data.observacao() != null) this.observacao = data.observacao();
        if (vendedor != null) this.vendedor = vendedor;
    }

    public void deactivate() {
        this.ativo = false;
    }
}
