package com.livraria.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public class LivroRequest {

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    @NotBlank(message = "ISBN é obrigatório")
    private String isbn;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser um valor positivo")
    private BigDecimal preco;

    @NotEmpty(message = "Pelo menos um autor deve ser informado")
    private List<@NotNull(message = "Id do autor é obrigatório") Long> autorIds;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public List<Long> getAutorIds() { return autorIds; }
    public void setAutorIds(List<Long> autorIds) { this.autorIds = autorIds; }
}
