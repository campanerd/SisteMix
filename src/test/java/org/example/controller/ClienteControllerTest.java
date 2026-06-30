package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cliente.Cliente;
import org.example.cliente.ClienteRepository;
import org.example.cliente.DadosAtualizacaoCliente;
import org.example.cliente.DadosCadastroCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteRepository repository;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(1L, "João Silva", "11999999999", "12345678900", "joao@email.com", true);
    }

    @Test
    void deveriaCadastrarClienteERetornar201() throws Exception {
        var dados = new DadosCadastroCliente("João Silva", "11999999999", "12345678900", "joao@email.com");
        when(repository.save(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpfCnpj").value("12345678900"));
    }

    @Test
    void deveriaRetornar400QuandoNomeEstiverAusente() throws Exception {
        var json = """
                {
                    "telefone": "11999999999"
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveriaListarClientesAtivosComPaginacao() throws Exception {
        var page = new PageImpl<>(List.of(cliente));
        when(repository.findAllByAtivoTrue(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("João Silva"))
                .andExpect(jsonPath("$.content[0].cpfCnpj").value("12345678900"));
    }

    @Test
    void deveriaAtualizarClienteERetornar200() throws Exception {
        var dados = new DadosAtualizacaoCliente(1L, "João Atualizado", null, null);
        when(repository.getReferenceById(1L)).thenReturn(cliente);

        mockMvc.perform(put("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveriaRetornar400QuandoIdEstiverAusenteNaAtualizacao() throws Exception {
        var json = """
                {
                    "nome": "João Atualizado"
                }
                """;

        mockMvc.perform(put("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveriaExcluirClienteERetornar204() throws Exception {
        when(repository.getReferenceById(1L)).thenReturn(cliente);

        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveriaDetalharClienteERetornar200() throws Exception {
        when(repository.getReferenceById(1L)).thenReturn(cliente);

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }
}
