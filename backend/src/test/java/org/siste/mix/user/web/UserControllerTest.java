package org.siste.mix.user.web;

import org.siste.mix.user.dto.CreateUserRequest;
import org.siste.mix.user.dto.UpdateUserRequest;
import org.siste.mix.user.dto.UserResponse;
import org.siste.mix.user.enums.UserRole;
import org.siste.mix.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    private UriComponentsBuilder uriBuilder;

    @BeforeEach
    void setUp() {
        uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
    }

    @Test
    void should_create_user_and_return_201() {
        var request = new CreateUserRequest("João Silva", "joao@email.com", "123456", UserRole.ROLE_USER);
        var response = new UserResponse(1L, "João Silva", "joao@email.com");

        // WHEN
        when(service.create(request)).thenReturn(response);

        // ASSERT
        var result = controller.create(request, uriBuilder);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(1L, result.getBody().id());
        assertEquals("João Silva", result.getBody().name());

        // InOrder
        InOrder inOrder = inOrder(service);
        inOrder.verify(service).create(request);
    }

    @Test
    void should_list_active_users() {
        var users = List.of(new UserResponse(1L, "João Silva", "joao@email.com"));

        // WHEN
        when(service.list()).thenReturn(users);

        // ASSERT
        var result = controller.list();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("João Silva", result.getBody().get(0).name());

        // InOrder
        InOrder inOrder = inOrder(service);
        inOrder.verify(service).list();
    }

    @Test
    void should_deactivate_user_and_return_204() {
        // WHEN
        doNothing().when(service).deactivate(1L);

        // ASSERT
        var result = controller.deactivate(1L);

        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        // InOrder
        InOrder inOrder = inOrder(service);
        inOrder.verify(service).deactivate(1L);
    }

    @Test
    void should_update_user_and_return_updated_data() {
        var request = new UpdateUserRequest(1L, "João Atualizado", null, null, null);
        var response = new UserResponse(1L, "João Atualizado", "joao@email.com");

        // WHEN
        when(service.update(request)).thenReturn(response);

        // ASSERT
        var result = controller.update(request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("João Atualizado", result.getBody().name());

        // InOrder
        InOrder inOrder = inOrder(service);
        inOrder.verify(service).update(request);
    }

    @Test
    void should_return_user_detail_with_200() {
        var response = new UserResponse(1L, "João Silva", "joao@email.com");

        // WHEN
        when(service.findById(1L)).thenReturn(response);

        // ASSERT
        var result = controller.findById(1L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("João Silva", result.getBody().name());
        assertEquals("joao@email.com", result.getBody().email());

        // InOrder
        InOrder inOrder = inOrder(service);
        inOrder.verify(service).findById(1L);
    }
}
