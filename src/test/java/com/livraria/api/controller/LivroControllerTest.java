package com.livraria.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livraria.api.dto.LivroRequest;
import com.livraria.api.dto.LivroResponse;
import com.livraria.api.exception.ResourceNotFoundException;
import com.livraria.api.service.LivroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivroController.class)
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LivroService livroService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarTodos_retornaListaVazia_quandoNaoHaLivros() throws Exception {
        given(livroService.listarTodos()).willReturn(List.of());

        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void listarTodos_retornaListaDeLivros() throws Exception {
        LivroResponse livro = livroResponse(1L, "Dom Casmurro", "978-85-359-0277-0", new BigDecimal("39.90"));
        given(livroService.listarTodos()).willReturn(List.of(livro));

        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$[0].isbn").value("978-85-359-0277-0"));
    }

    @Test
    void buscarPorId_retornaLivro_quandoExiste() throws Exception {
        LivroResponse livro = livroResponse(1L, "Dom Casmurro", "978-85-359-0277-0", new BigDecimal("39.90"));
        given(livroService.buscarPorId(1L)).willReturn(livro);

        mockMvc.perform(get("/api/livros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"));
    }

    @Test
    void buscarPorId_retorna404_quandoNaoExiste() throws Exception {
        given(livroService.buscarPorId(99L))
                .willThrow(new ResourceNotFoundException("Livro não encontrado com id: 99"));

        mockMvc.perform(get("/api/livros/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Livro não encontrado com id: 99"));
    }

    @Test
    void criar_retornaLivroCriado_comStatus201() throws Exception {
        LivroRequest request = livroRequest("Dom Casmurro", "978-85-359-0277-0", new BigDecimal("39.90"), List.of(1L));
        LivroResponse response = livroResponse(1L, "Dom Casmurro", "978-85-359-0277-0", new BigDecimal("39.90"));
        given(livroService.salvar(any(LivroRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"));
    }

    @Test
    void criar_retorna400_quandoTituloVazio() throws Exception {
        LivroRequest request = livroRequest("", "978-85-359-0277-0", new BigDecimal("39.90"), List.of(1L));

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.titulo").exists());
    }

    @Test
    void criar_retorna400_quandoPrecoNulo() throws Exception {
        LivroRequest request = livroRequest("Dom Casmurro", "978-85-359-0277-0", null, List.of(1L));

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.preco").exists());
    }

    @Test
    void criar_retorna400_quandoListaDeAutoresVazia() throws Exception {
        LivroRequest request = livroRequest("Dom Casmurro", "978-85-359-0277-0", new BigDecimal("39.90"), List.of());

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.autorIds").exists());
    }

    @Test
    void atualizar_retornaLivroAtualizado() throws Exception {
        LivroRequest request = livroRequest("Dom Casmurro - Ed. Especial", "978-85-359-0277-0", new BigDecimal("49.90"), List.of(1L));
        LivroResponse response = livroResponse(1L, "Dom Casmurro - Ed. Especial", "978-85-359-0277-0", new BigDecimal("49.90"));
        given(livroService.atualizar(eq(1L), any(LivroRequest.class))).willReturn(response);

        mockMvc.perform(put("/api/livros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro - Ed. Especial"));
    }

    @Test
    void atualizar_retorna404_quandoNaoExiste() throws Exception {
        LivroRequest request = livroRequest("Dom Casmurro", "978-85-359-0277-0", new BigDecimal("39.90"), List.of(1L));
        given(livroService.atualizar(eq(99L), any(LivroRequest.class)))
                .willThrow(new ResourceNotFoundException("Livro não encontrado com id: 99"));

        mockMvc.perform(put("/api/livros/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletar_retorna204_quandoSucesso() throws Exception {
        willDoNothing().given(livroService).deletar(1L);

        mockMvc.perform(delete("/api/livros/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletar_retorna404_quandoNaoExiste() throws Exception {
        willThrow(new ResourceNotFoundException("Livro não encontrado com id: 99"))
                .given(livroService).deletar(99L);

        mockMvc.perform(delete("/api/livros/99"))
                .andExpect(status().isNotFound());
    }

    private LivroRequest livroRequest(String titulo, String isbn, BigDecimal preco, List<Long> autorIds) {
        LivroRequest r = new LivroRequest();
        r.setTitulo(titulo);
        r.setIsbn(isbn);
        r.setPreco(preco);
        r.setAutorIds(autorIds);
        return r;
    }

    private LivroResponse livroResponse(Long id, String titulo, String isbn, BigDecimal preco) {
        LivroResponse r = new LivroResponse();
        r.setId(id);
        r.setTitulo(titulo);
        r.setIsbn(isbn);
        r.setPreco(preco);
        r.setAutores(List.of());
        return r;
    }
}
