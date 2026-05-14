package com.livraria.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "mensagem", "Livraria API em execução",
                "endpoints", List.of(
                        "/api/autores",
                        "/api/livros"
                )
        );
    }
}
