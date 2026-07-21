package org.siste.mix.client.web;

import org.siste.mix.client.dto.ClientResponse;
import org.siste.mix.client.dto.ClientSummary;
import org.siste.mix.client.dto.CreateClientRequest;
import org.siste.mix.client.dto.UpdateClientRequest;
import org.siste.mix.client.model.Client;
import org.siste.mix.client.usecase.CreateClientUseCase;
import org.siste.mix.client.usecase.DeleteClientUseCase;
import org.siste.mix.client.usecase.FindClientByIdUseCase;
import org.siste.mix.client.usecase.ListClientsUseCase;
import org.siste.mix.client.usecase.UpdateClientUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private CreateClientUseCase createClientUseCase;
    @Mock
    private ListClientsUseCase listClientsUseCase;
    @Mock
    private UpdateClientUseCase updateClientUseCase;
    @Mock
    private DeleteClientUseCase deleteClientUseCase;
    @Mock
    private FindClientByIdUseCase findClientByIdUseCase;

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
    void should_create_client_and_return_201() {
        var request = new CreateClientRequest("João Silva", "11999999999", "12345678900", "joao@email.com");

        // WHEN
        when(createClientUseCase.create(any(CreateClientRequest.class))).thenReturn(client);

        // ASSERT
        var response = controller.create(request, uriBuilder);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        assertEquals("João Silva", response.getBody().name());
        assertEquals("12345678900", response.getBody().cpfCnpj());

        // InOrder
        InOrder inOrder = inOrder(createClientUseCase);
        inOrder.verify(createClientUseCase).create(any(CreateClientRequest.class));
    }

    @Test
    void should_list_active_clients() {
        var page = new PageImpl<>(List.of(new ClientSummary(1L, "João Silva", "11999999999", "12345678900")));

        // WHEN
        when(listClientsUseCase.list(any(Pageable.class))).thenReturn(page);

        // ASSERT
        var response = controller.list(Pageable.ofSize(10));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("João Silva", response.getBody().getContent().get(0).name());

        // InOrder
        InOrder inOrder = inOrder(listClientsUseCase);
        inOrder.verify(listClientsUseCase).list(any(Pageable.class));
    }

    @Test
    void should_update_client_and_return_updated_data() {
        var request = new UpdateClientRequest(1L, "João Atualizado", null, null);
        var updated = new ClientResponse(1L, "João Atualizado", "11999999999", "12345678900", "joao@email.com");

        // WHEN
        when(updateClientUseCase.update(request)).thenReturn(updated);

        // ASSERT
        var response = controller.update(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("João Atualizado", response.getBody().name());

        // InOrder
        InOrder inOrder = inOrder(updateClientUseCase);
        inOrder.verify(updateClientUseCase).update(request);
    }

    @Test
    void should_delete_client_and_return_204() {
        // WHEN
        doNothing().when(deleteClientUseCase).delete(1L);

        // ASSERT
        var response = controller.delete(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // InOrder
        InOrder inOrder = inOrder(deleteClientUseCase);
        inOrder.verify(deleteClientUseCase).delete(1L);
    }

    @Test
    void should_return_client_detail_with_200() {
        var clientResponse = new ClientResponse(1L, "João Silva", "11999999999", "12345678900", "joao@email.com");

        // WHEN
        when(findClientByIdUseCase.findById(1L)).thenReturn(clientResponse);

        // ASSERT
        var response = controller.findById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        assertEquals("João Silva", response.getBody().name());
        assertEquals("joao@email.com", response.getBody().email());

        // InOrder
        InOrder inOrder = inOrder(findClientByIdUseCase);
        inOrder.verify(findClientByIdUseCase).findById(1L);
    }
}
