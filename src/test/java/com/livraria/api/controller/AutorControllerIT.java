package com.livraria.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livraria.api.dto.AutorRequest;
import com.livraria.api.model.Autor;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AutorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limpar() {
        livroRepository.deleteAll();
        autorRepository.deleteAll();
    }

    @Test
    void listarTodos_retornaAutoresSalvos() throws Exception {
        autorRepository.save(autor("Machado de Assis", "machado@assis.com"));

        mockMvc.perform(get("/api/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Machado de Assis"));
    }

    @Test
    void buscarPorId_retornaAutor_quandoExiste() throws Exception {
        Autor salvo = autorRepository.save(autor("Clarice Lispector", "clarice@email.com"));

        mockMvc.perform(get("/api/autores/" + salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(salvo.getId()))
                .andExpect(jsonPath("$.nome").value("Clarice Lispector"))
                .andExpect(jsonPath("$.email").value("clarice@email.com"));
    }

    @Test
    void buscarPorId_retorna404_quandoNaoExiste() throws Exception {
        mockMvc.perform(get("/api/autores/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Autor não encontrado com id: 999999"));
    }

    @Test
    void criar_persisteERetornaAutorComStatus201() throws Exception {
        AutorRequest request = new AutorRequest();
        request.setNome("José Saramago");
        request.setEmail("saramago@email.com");

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("José Saramago"))
                .andExpect(jsonPath("$.email").value("saramago@email.com"));
    }

    @Test
    void criar_retorna400_quandoNomeVazio() throws Exception {
        AutorRequest request = new AutorRequest();
        request.setNome("");
        request.setEmail("email@email.com");

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists());
    }

    @Test
    void criar_retorna400_quandoEmailInvalido() throws Exception {
        AutorRequest request = new AutorRequest();
        request.setNome("João");
        request.setEmail("email-invalido");

        mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.email").exists());
    }

    @Test
    void atualizar_atualizaERetornaAutor() throws Exception {
        Autor salvo = autorRepository.save(autor("Nome Antigo", "antigo@email.com"));

        AutorRequest request = new AutorRequest();
        request.setNome("Nome Novo");
        request.setEmail("novo@email.com");

        mockMvc.perform(put("/api/autores/" + salvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(salvo.getId()))
                .andExpect(jsonPath("$.nome").value("Nome Novo"))
                .andExpect(jsonPath("$.email").value("novo@email.com"));
    }

    @Test
    void atualizar_retorna404_quandoNaoExiste() throws Exception {
        AutorRequest request = new AutorRequest();
        request.setNome("Qualquer");
        request.setEmail("qualquer@email.com");

        mockMvc.perform(put("/api/autores/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletar_removeAutorERetorna204() throws Exception {
        Autor salvo = autorRepository.save(autor("Para Deletar", "deletar@email.com"));

        mockMvc.perform(delete("/api/autores/" + salvo.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletar_retorna404_quandoNaoExiste() throws Exception {
        mockMvc.perform(delete("/api/autores/999999"))
                .andExpect(status().isNotFound());
    }

    private Autor autor(String nome, String email) {
        Autor a = new Autor();
        a.setNome(nome);
        a.setEmail(email);
        return a;
    }
}
