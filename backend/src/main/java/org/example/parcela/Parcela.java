package org.example.parcela;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pedido.Pedido;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "parcelas")
@Entity(name = "Parcela")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Parcela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numeroParcela;
    private BigDecimal valor;
    private LocalDate vencimento;

    @Enumerated(EnumType.STRING)
    private InstallmentStatus status;

    private LocalDate dataPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    public Parcela(Integer numeroParcela, BigDecimal valor, LocalDate vencimento, Pedido pedido) {
        this.numeroParcela = numeroParcela;
        this.valor = valor;
        this.vencimento = vencimento;
        this.status = InstallmentStatus.PENDENTE;
        this.pedido = pedido;
    }

    public void updateStatus(InstallmentStatus newStatus) {
        this.status = newStatus;
        this.dataPagamento = newStatus == InstallmentStatus.PAGO ? LocalDate.now() : null;
    }
}
