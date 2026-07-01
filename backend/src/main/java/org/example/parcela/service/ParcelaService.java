package org.example.parcela.service;

import org.example.parcela.dto.DadosAtualizacaoParcela;
import org.example.parcela.dto.DadosDetalhamentoParcela;
import org.example.parcela.dto.DadosListagemParcela;
import org.example.parcela.enums.StatusParcela;
import org.example.parcela.repository.ParcelaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ParcelaService {

    @Autowired
    private ParcelaRepository repository;

    public List<DadosListagemParcela> list(StatusParcela status, LocalDate vencimentoInicio, LocalDate vencimentoFim, BigDecimal valorMin, BigDecimal valorMax) {
        return repository.findWithFilters(status, vencimentoInicio, vencimentoFim, valorMin, valorMax)
                .stream().map(DadosListagemParcela::new).toList();
    }

    public DadosDetalhamentoParcela findById(Long id) {
        return new DadosDetalhamentoParcela(repository.getReferenceById(id));
    }

    public List<DadosListagemParcela> listByOrder(Long orderId) {
        return repository.findByPedidoId(orderId)
                .stream().map(DadosListagemParcela::new).toList();
    }

    @Transactional
    public DadosDetalhamentoParcela updateStatus(Long id, DadosAtualizacaoParcela dados) {
        var parcela = repository.getReferenceById(id);
        parcela.atualizarStatus(dados.status());
        return new DadosDetalhamentoParcela(parcela);
    }
}
