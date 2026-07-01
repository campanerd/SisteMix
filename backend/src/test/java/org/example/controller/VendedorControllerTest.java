package org.example.controller;

import org.example.vendedor.dto.CreateSellerRequest;
import org.example.vendedor.dto.SellerResponse;
import org.example.vendedor.dto.SellerSummary;
import org.example.vendedor.dto.UpdateSellerRequest;
import org.example.vendedor.model.Seller;
import org.example.vendedor.service.SellerService;
import org.example.vendedor.web.SellerController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendedorControllerTest {

    @Mock
    private SellerService service;

    @InjectMocks
    private SellerController controller;

    private Seller seller;
    private UriComponentsBuilder uriBuilder;

    @BeforeEach
    void setUp() {
        seller = new Seller(1L, "Maria Souza", "12345678900", "11988888888", true);
        uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
    }

    @Test
    void deveriaCadastrarVendedorERetornar201() {
        var dados = new CreateSellerRequest("Maria Souza", "12345678900", "11988888888");
        when(service.create(any(CreateSellerRequest.class))).thenReturn(seller);

        var response = controller.create(dados, uriBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var body = response.getBody();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.nome()).isEqualTo("Maria Souza");
        assertThat(body.cpf()).isEqualTo("12345678900");
    }

    @Test
    void deveriaListarVendedoresAtivos() {
        var summary = new SellerSummary(1L, "Maria Souza", "12345678900");
        var page = new PageImpl<>(List.of(summary));
        when(service.list(any(Pageable.class))).thenReturn(page);

        var response = controller.list(Pageable.ofSize(10));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var content = response.getBody().getContent();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).nome()).isEqualTo("Maria Souza");
    }

    @Test
    void deveriaAtualizarVendedorERetornarDadosAtualizados() {
        var dados = new UpdateSellerRequest(1L, "Maria Santos", null);
        var updated = new SellerResponse(1L, "Maria Santos", "12345678900", "11988888888");
        when(service.update(dados)).thenReturn(updated);

        var response = controller.update(dados);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().nome()).isEqualTo("Maria Santos");
    }

    @Test
    void deveriaExcluirVendedorERetornar204() {
        doNothing().when(service).delete(1L);

        var response = controller.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).delete(1L);
    }

    @Test
    void deveriaDetalharVendedorERetornar200() {
        var sellerResponse = new SellerResponse(1L, "Maria Souza", "12345678900", "11988888888");
        when(service.findById(1L)).thenReturn(sellerResponse);

        var response = controller.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = response.getBody();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.nome()).isEqualTo("Maria Souza");
        assertThat(body.cpf()).isEqualTo("12345678900");
    }
}
