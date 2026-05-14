package com.livraria.api.dto;

import java.util.List;

public class AutorResponse {

    private Long id;
    private String nome;
    private String email;
    private List<String> livros;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getLivros() { return livros; }
    public void setLivros(List<String> livros) { this.livros = livros; }
}
