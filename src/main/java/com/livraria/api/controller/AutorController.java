package com.livraria.api.controller;

import com.livraria.api.dto.AutorRequest;
import com.livraria.api.dto.AutorResponse;
import com.livraria.api.service.AutorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/autores")
public class AutorController {

    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @GetMapping
    public List<AutorResponse> listarTodos() {
        return autorService.listarTodos();
    }

    @GetMapping("/{id}")
    public AutorResponse buscarPorId(@PathVariable Long id) {
        return autorService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<AutorResponse> criar(@Valid @RequestBody AutorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autorService.salvar(request));
    }

    @PutMapping("/{id}")
    public AutorResponse atualizar(@PathVariable Long id, @Valid @RequestBody AutorRequest request) {
        return autorService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        autorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
