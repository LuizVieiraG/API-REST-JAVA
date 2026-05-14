package com.livraria.api.dto;

import java.math.BigDecimal;
import java.util.List;

public class LivroResponse {

    private Long id;
    private String titulo;
    private String isbn;
    private BigDecimal preco;
    private List<AutorResponse> autores;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public List<AutorResponse> getAutores() { return autores; }
    public void setAutores(List<AutorResponse> autores) { this.autores = autores; }
}
