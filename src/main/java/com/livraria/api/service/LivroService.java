package com.livraria.api.service;

import com.livraria.api.dto.AutorResponse;
import com.livraria.api.dto.LivroRequest;
import com.livraria.api.dto.LivroResponse;
import com.livraria.api.exception.ResourceNotFoundException;
import com.livraria.api.model.Autor;
import com.livraria.api.model.Livro;
import com.livraria.api.repository.AutorRepository;
import com.livraria.api.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class LivroService {

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    public LivroService(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public List<LivroResponse> listarTodos() {
        return livroRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public LivroResponse buscarPorId(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado com id: " + id));
        return toResponse(livro);
    }

    public LivroResponse salvar(LivroRequest request) {
        Set<Autor> autores = resolverAutores(request.getAutorIds());
        Livro livro = new Livro();
        livro.setTitulo(request.getTitulo());
        livro.setIsbn(request.getIsbn());
        livro.setPreco(request.getPreco());
        livro.setAutores(autores);
        return toResponse(livroRepository.save(livro));
    }

    public LivroResponse atualizar(Long id, LivroRequest request) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado com id: " + id));
        Set<Autor> autores = resolverAutores(request.getAutorIds());
        livro.setTitulo(request.getTitulo());
        livro.setIsbn(request.getIsbn());
        livro.setPreco(request.getPreco());
        livro.setAutores(autores);
        return toResponse(livroRepository.save(livro));
    }

    public void deletar(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Livro não encontrado com id: " + id);
        }
        livroRepository.deleteById(id);
    }

    private Set<Autor> resolverAutores(List<Long> autorIds) {
        Set<Long> idsUnicos = new HashSet<>(autorIds);
        List<Autor> autores = autorRepository.findAllById(idsUnicos);
        if (autores.size() != idsUnicos.size()) {
            throw new ResourceNotFoundException("Um ou mais autores não encontrados");
        }
        return new HashSet<>(autores);
    }

    private LivroResponse toResponse(Livro livro) {
        LivroResponse response = new LivroResponse();
        response.setId(livro.getId());
        response.setTitulo(livro.getTitulo());
        response.setIsbn(livro.getIsbn());
        response.setPreco(livro.getPreco());
        response.setAutores(livro.getAutores().stream()
                .map(a -> {
                    AutorResponse ar = new AutorResponse();
                    ar.setId(a.getId());
                    ar.setNome(a.getNome());
                    ar.setEmail(a.getEmail());
                    return ar;
                })
                .collect(Collectors.toList()));
        return response;
    }
}
