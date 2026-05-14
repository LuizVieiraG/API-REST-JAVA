package com.livraria.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livraria.api.dto.LivroRequest;
import com.livraria.api.model.Autor;
import com.livraria.api.model.Livro;
import com.livraria.api.repository.AutorRepository;
import com.livraria.api.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LivroControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limpar() {
        livroRepository.deleteAll();
        autorRepository.deleteAll();
    }

    @Test
    void listarTodos_retornaLivrosSalvos() throws Exception {
        Autor autor = autorRepository.save(autor("Machado de Assis", "machado@assis.com"));
        livroRepository.save(livro("Dom Casmurro", "978-85-359-0277-0", new BigDecimal("39.90"), autor));

        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$[0].isbn").value("978-85-359-0277-0"));
    }

    @Test
    void buscarPorId_retornaLivro_quandoExiste() throws Exception {
        Autor autor = autorRepository.save(autor("Clarice Lispector", "clarice@email.com"));
        Livro salvo = livroRepository.save(livro("A Hora da Estrela", "978-85-359-0001-0", new BigDecimal("29.90"), autor));

        mockMvc.perform(get("/api/livros/" + salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(salvo.getId()))
                .andExpect(jsonPath("$.titulo").value("A Hora da Estrela"))
                .andExpect(jsonPath("$.autores[0].nome").value("Clarice Lispector"));
    }

    @Test
    void buscarPorId_retorna404_quandoNaoExiste() throws Exception {
        mockMvc.perform(get("/api/livros/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Livro não encontrado com id: 999999"));
    }

    @Test
    void criar_persisteERetornaLivroComStatus201() throws Exception {
        Autor autor = autorRepository.save(autor("José Saramago", "saramago@email.com"));

        LivroRequest request = new LivroRequest();
        request.setTitulo("O Evangelho Segundo Jesus Cristo");
        request.setIsbn("978-85-359-0002-0");
        request.setPreco(new BigDecimal("45.90"));
        request.setAutorIds(List.of(autor.getId()));

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titulo").value("O Evangelho Segundo Jesus Cristo"))
                .andExpect(jsonPath("$.autores[0].nome").value("José Saramago"));
    }

    @Test
    void criar_retorna404_quandoAutorNaoExiste() throws Exception {
        LivroRequest request = new LivroRequest();
        request.setTitulo("Livro Qualquer");
        request.setIsbn("978-85-359-0003-0");
        request.setPreco(new BigDecimal("30.00"));
        request.setAutorIds(List.of(999999L));

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void criar_retorna400_quandoTituloVazio() throws Exception {
        Autor autor = autorRepository.save(autor("Autor", "autor@email.com"));

        LivroRequest request = new LivroRequest();
        request.setTitulo("");
        request.setIsbn("978-85-359-0004-0");
        request.setPreco(new BigDecimal("30.00"));
        request.setAutorIds(List.of(autor.getId()));

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.titulo").exists());
    }

    @Test
    void criar_retorna400_quandoPrecoNulo() throws Exception {
        Autor autor = autorRepository.save(autor("Autor", "autor@email.com"));

        LivroRequest request = new LivroRequest();
        request.setTitulo("Título");
        request.setIsbn("978-85-359-0005-0");
        request.setPreco(null);
        request.setAutorIds(List.of(autor.getId()));

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.preco").exists());
    }

    @Test
    void atualizar_atualizaERetornaLivro() throws Exception {
        Autor autor = autorRepository.save(autor("Autor", "autor@email.com"));
        Livro salvo = livroRepository.save(livro("Título Antigo", "978-85-359-0006-0", new BigDecimal("20.00"), autor));

        LivroRequest request = new LivroRequest();
        request.setTitulo("Título Novo");
        request.setIsbn("978-85-359-0006-0");
        request.setPreco(new BigDecimal("25.00"));
        request.setAutorIds(List.of(autor.getId()));

        mockMvc.perform(put("/api/livros/" + salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Título Novo"))
                .andExpect(jsonPath("$.preco").value(25.00));
    }

    @Test
    void deletar_removeLivroERetorna204() throws Exception {
        Autor autor = autorRepository.save(autor("Autor", "autor@email.com"));
        Livro salvo = livroRepository.save(livro("Para Deletar", "978-85-359-0007-0", new BigDecimal("10.00"), autor));

        mockMvc.perform(delete("/api/livros/" + salvo.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletar_retorna404_quandoNaoExiste() throws Exception {
        mockMvc.perform(delete("/api/livros/999999"))
                .andExpect(status().isNotFound());
    }

    private Autor autor(String nome, String email) {
        Autor a = new Autor();
        a.setNome(nome);
        a.setEmail(email);
        return a;
    }

    private Livro livro(String titulo, String isbn, BigDecimal preco, Autor autor) {
        Livro l = new Livro();
        l.setTitulo(titulo);
        l.setIsbn(isbn);
        l.setPreco(preco);
        l.setAutores(Set.of(autor));
        return l;
    }
}
