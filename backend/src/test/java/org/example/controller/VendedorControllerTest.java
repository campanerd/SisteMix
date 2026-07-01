package org.example.controller;

import org.example.vendedor.*;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendedorControllerTest {

    @Mock
    private VendedorService service;

    @InjectMocks
    private VendedorController controller;

    private Vendedor vendedor;
    private UriComponentsBuilder uriBuilder;

    @BeforeEach
    void setUp() {
        vendedor = new Vendedor(1L, "Maria Souza", "12345678900", "11988888888", true);
        uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
    }

    @Test
    void shouldCreateSellerAndReturn201() {
        var data = new CreateSellerRequest("Maria Souza", "12345678900", "11988888888");
        when(service.create(any(CreateSellerRequest.class))).thenReturn(vendedor);

        var response = controller.create(data, uriBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().nome()).isEqualTo("Maria Souza");
        assertThat(response.getBody().cpf()).isEqualTo("12345678900");
    }

    @Test
    void shouldListActiveSellers() {
        var summary = new SellerSummary(vendedor);
        var page = new PageImpl<>(List.of(summary));
        when(service.list(any(Pageable.class))).thenReturn(page);

        var response = controller.list(Pageable.ofSize(10));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var content = response.getBody().getContent();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).nome()).isEqualTo("Maria Souza");
    }

    @Test
    void shouldUpdateSellerAndReturnUpdatedData() {
        var data = new UpdateSellerRequest(1L, "Maria Santos", null);
        var updated = new SellerResponse(1L, "Maria Santos", "12345678900", "11988888888");
        when(service.update(any(UpdateSellerRequest.class))).thenReturn(updated);

        var response = controller.update(data);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().nome()).isEqualTo("Maria Santos");
    }

    @Test
    void shouldDeleteSellerAndReturn204() {
        var response = controller.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).delete(1L);
    }

    @Test
    void shouldFindSellerByIdAndReturn200() {
        var sellerResponse = new SellerResponse(1L, "Maria Souza", "12345678900", "11988888888");
        when(service.findById(1L)).thenReturn(sellerResponse);

        var response = controller.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().nome()).isEqualTo("Maria Souza");
        assertThat(response.getBody().cpf()).isEqualTo("12345678900");
    }
}
