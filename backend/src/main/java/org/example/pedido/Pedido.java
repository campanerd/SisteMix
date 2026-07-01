package org.example.pedido;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cliente.model.Client;
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
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor")
    private Vendedor vendedor;

    private Boolean ativo;

    public Pedido(DadosCadastroPedido dados, Client client, Vendedor vendedor) {
        this.numeroPedido = dados.numeroPedido();
        this.dataEmissao = dados.dataEmissao();
        this.dataPedido = dados.dataPedido();
        this.valorTotal = dados.valorTotal();
        this.totalParcelas = dados.totalParcelas();
        this.observacao = dados.observacao();
        this.client = client;
        this.vendedor = vendedor;
        this.ativo = true;
    }

    public void atualizarInformacoes(DadosAtualizacaoPedido dados, Vendedor vendedor) {
        if (dados.dataEmissao() != null) this.dataEmissao = dados.dataEmissao();
        if (dados.dataPedido() != null) this.dataPedido = dados.dataPedido();
        if (dados.valorTotal() != null) this.valorTotal = dados.valorTotal();
        if (dados.observacao() != null) this.observacao = dados.observacao();
        if (vendedor != null) this.vendedor = vendedor;
    }

    public void excluir() {
        this.ativo = false;
    }
}
