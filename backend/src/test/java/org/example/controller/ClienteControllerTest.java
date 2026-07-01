package org.example.controller;

import org.example.cliente.dto.ClientResponse;
import org.example.cliente.dto.ClientSummary;
import org.example.cliente.dto.CreateClientRequest;
import org.example.cliente.dto.UpdateClientRequest;
import org.example.cliente.model.Client;
import org.example.cliente.service.ClientService;
import org.example.cliente.web.ClientController;
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
class ClienteControllerTest {

    @Mock
    private ClientService service;

    @InjectMocks
    private ClientController controller;

    private Client client;
    private UriComponentsBuilder uriBuilder;

    @BeforeEach
    void setUp() {
        client = new Client(1L, "João Silva", "11999999999", "12345678900", "joao@email.com", true);
        uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
    }

    @Test
    void deveriaCadastrarClienteERetornar201() {
        var dados = new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com");
        when(service.create(any(CreateClientRequest.class))).thenReturn(client);

        var response = controller.create(dados, uriBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var body = response.getBody();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.nome()).isEqualTo("João Silva");
        assertThat(body.cpfCnpj()).isEqualTo("12345678900");
    }

    @Test
    void deveriaListarClientesAtivos() {
        var summary = new ClientSummary(1L, "João Silva", "11999999999", "12345678900");
        var page = new PageImpl<>(List.of(summary));
        when(service.list(any(Pageable.class))).thenReturn(page);

        var response = controller.list(Pageable.ofSize(10));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var content = response.getBody().getContent();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).nome()).isEqualTo("João Silva");
    }

    @Test
    void deveriaAtualizarClienteERetornarDadosAtualizados() {
        var dados = new UpdateClientRequest(1L, "João Atualizado", null, null);
        var updated = new ClientResponse(1L, "João Atualizado", "11999999999", "12345678900", "joao@email.com");
        when(service.update(dados)).thenReturn(updated);

        var response = controller.update(dados);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().nome()).isEqualTo("João Atualizado");
    }

    @Test
    void deveriaExcluirClienteERetornar204() {
        doNothing().when(service).delete(1L);

        var response = controller.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).delete(1L);
    }

    @Test
    void deveriaDetalharClienteERetornar200() {
        var clientResponse = new ClientResponse(1L, "João Silva", "11999999999", "12345678900", "joao@email.com");
        when(service.findById(1L)).thenReturn(clientResponse);

        var response = controller.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = response.getBody();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.nome()).isEqualTo("João Silva");
        assertThat(body.email()).isEqualTo("joao@email.com");
    }
}
