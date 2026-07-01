package org.example.pedido;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cliente.model.Client;
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

    public Pedido(DadosCadastroPedido dados, Client client, Seller seller) {
        this.numeroPedido = dados.numeroPedido();
        this.dataEmissao = dados.dataEmissao();
        this.dataPedido = dados.dataPedido();
        this.valorTotal = dados.valorTotal();
        this.totalParcelas = dados.totalParcelas();
        this.observacao = dados.observacao();
        this.client = client;
        this.seller = seller;
        this.ativo = true;
    }

    public void atualizarInformacoes(DadosAtualizacaoPedido dados, Seller seller) {
        if (dados.dataEmissao() != null) this.dataEmissao = dados.dataEmissao();
        if (dados.dataPedido() != null) this.dataPedido = dados.dataPedido();
        if (dados.valorTotal() != null) this.valorTotal = dados.valorTotal();
        if (dados.observacao() != null) this.observacao = dados.observacao();
        if (seller != null) this.seller = seller;
    }

    public void excluir() {
        this.ativo = false;
    }
}
