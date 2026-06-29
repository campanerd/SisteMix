package org.example.parcela;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ParcelaRepository extends JpaRepository<Parcela, Long> {

    List<Parcela> findByPedidoId(Long pedidoId);

    @Query("SELECT p FROM Parcela p WHERE " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:vencimentoInicio IS NULL OR p.vencimento >= :vencimentoInicio) AND " +
           "(:vencimentoFim IS NULL OR p.vencimento <= :vencimentoFim) AND " +
           "(:valorMin IS NULL OR p.valor >= :valorMin) AND " +
           "(:valorMax IS NULL OR p.valor <= :valorMax)")
    List<Parcela> findWithFilters(
            @Param("status") StatusParcela status,
            @Param("vencimentoInicio") LocalDate vencimentoInicio,
            @Param("vencimentoFim") LocalDate vencimentoFim,
            @Param("valorMin") BigDecimal valorMin,
            @Param("valorMax") BigDecimal valorMax
    );
}
