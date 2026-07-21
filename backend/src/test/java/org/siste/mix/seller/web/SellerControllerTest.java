package org.siste.mix.seller.web;

import org.siste.mix.seller.dto.CreateSellerRequest;
import org.siste.mix.seller.dto.SellerResponse;
import org.siste.mix.seller.dto.SellerSummary;
import org.siste.mix.seller.dto.UpdateSellerRequest;
import org.siste.mix.seller.model.Seller;
import org.siste.mix.seller.usecase.CreateSellerUseCase;
import org.siste.mix.seller.usecase.DeleteSellerUseCase;
import org.siste.mix.seller.usecase.FindSellerByIdUseCase;
import org.siste.mix.seller.usecase.ListSellersUseCase;
import org.siste.mix.seller.usecase.UpdateSellerUseCase;
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
class SellerControllerTest {

    @Mock
    private CreateSellerUseCase createSellerUseCase;
    @Mock
    private ListSellersUseCase listSellersUseCase;
    @Mock
    private UpdateSellerUseCase updateSellerUseCase;
    @Mock
    private DeleteSellerUseCase deleteSellerUseCase;
    @Mock
    private FindSellerByIdUseCase findSellerByIdUseCase;

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
    void should_create_seller_and_return_201() {
        var request = new CreateSellerRequest("Maria Souza", "12345678900", "11988888888");

        // WHEN
        when(createSellerUseCase.create(any(CreateSellerRequest.class))).thenReturn(seller);

        // ASSERT
        var response = controller.create(request, uriBuilder);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        assertEquals("Maria Souza", response.getBody().name());
        assertEquals("12345678900", response.getBody().cpf());

        // InOrder
        InOrder inOrder = inOrder(createSellerUseCase);
        inOrder.verify(createSellerUseCase).create(any(CreateSellerRequest.class));
    }

    @Test
    void should_list_active_sellers() {
        var page = new PageImpl<>(List.of(new SellerSummary(1L, "Maria Souza", "12345678900")));

        // WHEN
        when(listSellersUseCase.list(any(Pageable.class))).thenReturn(page);

        // ASSERT
        var response = controller.list(Pageable.ofSize(10));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Maria Souza", response.getBody().getContent().get(0).name());

        // InOrder
        InOrder inOrder = inOrder(listSellersUseCase);
        inOrder.verify(listSellersUseCase).list(any(Pageable.class));
    }

    @Test
    void should_update_seller_and_return_updated_data() {
        var request = new UpdateSellerRequest(1L, "Maria Santos", null);
        var updated = new SellerResponse(1L, "Maria Santos", "12345678900", "11988888888");

        // WHEN
        when(updateSellerUseCase.update(request)).thenReturn(updated);

        // ASSERT
        var response = controller.update(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Maria Santos", response.getBody().name());

        // InOrder
        InOrder inOrder = inOrder(updateSellerUseCase);
        inOrder.verify(updateSellerUseCase).update(request);
    }

    @Test
    void should_delete_seller_and_return_204() {
        // WHEN
        doNothing().when(deleteSellerUseCase).delete(1L);

        // ASSERT
        var response = controller.delete(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // InOrder
        InOrder inOrder = inOrder(deleteSellerUseCase);
        inOrder.verify(deleteSellerUseCase).delete(1L);
    }

    @Test
    void should_return_seller_detail_with_200() {
        var sellerResponse = new SellerResponse(1L, "Maria Souza", "12345678900", "11988888888");

        // WHEN
        when(findSellerByIdUseCase.findById(1L)).thenReturn(sellerResponse);

        // ASSERT
        var response = controller.findById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        assertEquals("Maria Souza", response.getBody().name());
        assertEquals("12345678900", response.getBody().cpf());

        // InOrder
        InOrder inOrder = inOrder(findSellerByIdUseCase);
        inOrder.verify(findSellerByIdUseCase).findById(1L);
    }
}
