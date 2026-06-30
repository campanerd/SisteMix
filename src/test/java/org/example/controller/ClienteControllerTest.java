package org.example.controller;

import org.example.cliente.Cliente;
import org.example.cliente.ClienteRepository;
import org.example.cliente.DadosAtualizacaoCliente;
import org.example.cliente.DadosCadastroCliente;
import org.example.cliente.DadosDetalhamentoCliente;
import org.example.cliente.DadosListagemCliente;
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
class ClienteControllerTest {

    @Mock
    private ClienteRepository repository;

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
    void deveriaCadastrarClienteERetornar201() {
        var dados = new DadosCadastroCliente("João Silva", "11999999999", "12345678900", "joao@email.com");
        when(repository.save(any(Cliente.class))).thenReturn(cliente);

        var response = controller.cadastrar(dados, uriBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var body = (DadosDetalhamentoCliente) response.getBody();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.nome()).isEqualTo("João Silva");
        assertThat(body.cpfCnpj()).isEqualTo("12345678900");
    }

    @Test
    void deveriaListarClientesAtivos() {
        var page = new PageImpl<>(List.of(cliente));
        when(repository.findAllByAtivoTrue(any(Pageable.class))).thenReturn(page);

        var response = controller.listar(Pageable.ofSize(10));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var content = response.getBody().getContent();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).nome()).isEqualTo("João Silva");
    }

    @Test
    void deveriaAtualizarClienteERetornarDadosAtualizados() {
        var dados = new DadosAtualizacaoCliente(1L, "João Atualizado", null, null);
        when(repository.getReferenceById(1L)).thenReturn(cliente);

        var response = controller.atualizar(dados);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (DadosDetalhamentoCliente) response.getBody();
        assertThat(body.nome()).isEqualTo("João Atualizado");
    }

    @Test
    void deveriaExcluirClienteERetornar204() {
        when(repository.getReferenceById(1L)).thenReturn(cliente);

        var response = controller.excluir(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(cliente.getAtivo()).isFalse();
    }

    @Test
    void deveriaDetalharClienteERetornar200() {
        when(repository.getReferenceById(1L)).thenReturn(cliente);

        var response = controller.detalhar(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (DadosDetalhamentoCliente) response.getBody();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.nome()).isEqualTo("João Silva");
        assertThat(body.email()).isEqualTo("joao@email.com");
    }
}
