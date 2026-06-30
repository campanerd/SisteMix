package org.example.controller;

import org.example.vendedor.DadosAtualizacaoVendedor;
import org.example.vendedor.DadosCadastroVendedor;
import org.example.vendedor.DadosDetalhamentoVendedor;
import org.example.vendedor.Vendedor;
import org.example.vendedor.VendedorRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendedorControllerTest {

    @Mock
    private VendedorRepository repository;

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
    void deveriaCadastrarVendedorERetornar201() {
        var dados = new DadosCadastroVendedor("Maria Souza", "12345678900", "11988888888");
        when(repository.save(any(Vendedor.class))).thenReturn(vendedor);

        var response = controller.cadastrar(dados, uriBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var body = (DadosDetalhamentoVendedor) response.getBody();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.nome()).isEqualTo("Maria Souza");
        assertThat(body.cpf()).isEqualTo("12345678900");
    }

    @Test
    void deveriaListarVendedoresAtivos() {
        var page = new PageImpl<>(List.of(vendedor));
        when(repository.findAllByAtivoTrue(any(Pageable.class))).thenReturn(page);

        var response = controller.listar(Pageable.ofSize(10));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var content = response.getBody().getContent();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).nome()).isEqualTo("Maria Souza");
    }

    @Test
    void deveriaAtualizarVendedorERetornarDadosAtualizados() {
        var dados = new DadosAtualizacaoVendedor(1L, "Maria Santos", null);
        when(repository.getReferenceById(1L)).thenReturn(vendedor);

        var response = controller.atualizar(dados);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (DadosDetalhamentoVendedor) response.getBody();
        assertThat(body.nome()).isEqualTo("Maria Santos");
    }

    @Test
    void deveriaExcluirVendedorERetornar204() {
        when(repository.getReferenceById(1L)).thenReturn(vendedor);

        var response = controller.excluir(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(vendedor.getAtivo()).isFalse();
    }

    @Test
    void deveriaDetalharVendedorERetornar200() {
        when(repository.getReferenceById(1L)).thenReturn(vendedor);

        var response = controller.detalhar(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (DadosDetalhamentoVendedor) response.getBody();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.nome()).isEqualTo("Maria Souza");
        assertThat(body.cpf()).isEqualTo("12345678900");
    }
}
