package org.example.controller;

import org.example.cliente.*;
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
class ClienteControllerTest {

    @Mock
    private ClienteService service;

    @InjectMocks
    private ClienteController controller;

    private Cliente cliente;
    private UriComponentsBuilder uriBuilder;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(1L, "João Silva", "11999999999", "12345678900", "joao@email.com", true);
        uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
    }

    @Test
    void shouldCreateClientAndReturn201() {
        var data = new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com");
        when(service.create(any(CreateClientRequest.class))).thenReturn(cliente);

        var response = controller.create(data, uriBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().nome()).isEqualTo("João Silva");
        assertThat(response.getBody().cpfCnpj()).isEqualTo("12345678900");
    }

    @Test
    void shouldListActiveClients() {
        var summary = new ClientSummary(cliente);
        var page = new PageImpl<>(List.of(summary));
        when(service.list(any(Pageable.class))).thenReturn(page);

        var response = controller.list(Pageable.ofSize(10));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var content = response.getBody().getContent();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).nome()).isEqualTo("João Silva");
    }

    @Test
    void shouldUpdateClientAndReturnUpdatedData() {
        var data = new UpdateClientRequest(1L, "João Atualizado", null, null);
        var updated = new ClientResponse(1L, "João Atualizado", "11999999999", "12345678900", "joao@email.com");
        when(service.update(any(UpdateClientRequest.class))).thenReturn(updated);

        var response = controller.update(data);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().nome()).isEqualTo("João Atualizado");
    }

    @Test
    void shouldDeleteClientAndReturn204() {
        var response = controller.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).delete(1L);
    }

    @Test
    void shouldFindClientByIdAndReturn200() {
        var clientResponse = new ClientResponse(1L, "João Silva", "11999999999", "12345678900", "joao@email.com");
        when(service.findById(1L)).thenReturn(clientResponse);

        var response = controller.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().nome()).isEqualTo("João Silva");
        assertThat(response.getBody().email()).isEqualTo("joao@email.com");
    }
}
