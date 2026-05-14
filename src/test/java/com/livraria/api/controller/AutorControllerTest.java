package com.livraria.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livraria.api.dto.AutorRequest;
import com.livraria.api.dto.AutorResponse;
import com.livraria.api.exception.ResourceNotFoundException;
import com.livraria.api.service.AutorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutorController.class)
class AutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutorService autorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarTodos_retornaListaVazia_quandoNaoHaAutores() throws Exception {
        given(autorService.listarTodos()).willReturn(List.of());

        mockMvc.perform(get("/api/autores"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void listarTodos_retornaListaDeAutores() throws Exception {
        given(autorService.listarTodos()).willReturn(List.of(autorResponse(1L, "João Silva", "joao@email.com")));

        mockMvc.perform(get("/api/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$[0].email").value("joao@email.com"));
    }

    @Test
    void buscarPorId_retornaAutor_quandoExiste() throws Exception {
        given(autorService.buscarPorId(1L)).willReturn(autorResponse(1L, "João Silva", "joao@email.com"));

        mockMvc.perform(get("/api/autores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void buscarPorId_retorna404_quandoNaoExiste() throws Exception {
        given(autorService.buscarPorId(99L))
                .willThrow(new ResourceNotFoundException("Autor não encontrado com id: 99"));

        mockMvc.perform(get("/api/autores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Autor não encontrado com id: 99"));
    }

    @Test
    void criar_retornaAutorCriado_comStatus201() throws Exception {
        AutorRequest request = autorRequest("João Silva", "joao@email.com");
        given(autorService.salvar(any(AutorRequest.class))).willReturn(autorResponse(1L, "João Silva", "joao@email.com"));

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void criar_retorna400_quandoNomeVazio() throws Exception {
        AutorRequest request = autorRequest("", "joao@email.com");

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists());
    }

    @Test
    void criar_retorna400_quandoEmailInvalido() throws Exception {
        AutorRequest request = autorRequest("João Silva", "email-invalido");

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.email").exists());
    }

    @Test
    void atualizar_retornaAutorAtualizado() throws Exception {
        AutorRequest request = autorRequest("João Atualizado", "joao.novo@email.com");
        given(autorService.atualizar(eq(1L), any(AutorRequest.class)))
                .willReturn(autorResponse(1L, "João Atualizado", "joao.novo@email.com"));

        mockMvc.perform(put("/api/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Atualizado"));
    }

    @Test
    void atualizar_retorna404_quandoNaoExiste() throws Exception {
        AutorRequest request = autorRequest("João Silva", "joao@email.com");
        given(autorService.atualizar(eq(99L), any(AutorRequest.class)))
                .willThrow(new ResourceNotFoundException("Autor não encontrado com id: 99"));

        mockMvc.perform(put("/api/autores/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletar_retorna204_quandoSucesso() throws Exception {
        willDoNothing().given(autorService).deletar(1L);

        mockMvc.perform(delete("/api/autores/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletar_retorna404_quandoNaoExiste() throws Exception {
        willThrow(new ResourceNotFoundException("Autor não encontrado com id: 99"))
                .given(autorService).deletar(99L);

        mockMvc.perform(delete("/api/autores/99"))
                .andExpect(status().isNotFound());
    }

    private AutorRequest autorRequest(String nome, String email) {
        AutorRequest r = new AutorRequest();
        r.setNome(nome);
        r.setEmail(email);
        return r;
    }

    private AutorResponse autorResponse(Long id, String nome, String email) {
        AutorResponse r = new AutorResponse();
        r.setId(id);
        r.setNome(nome);
        r.setEmail(email);
        r.setLivros(List.of());
        return r;
    }
}
