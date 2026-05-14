package com.livraria.api.service;

import com.livraria.api.dto.AutorRequest;
import com.livraria.api.dto.AutorResponse;
import com.livraria.api.exception.ResourceNotFoundException;
import com.livraria.api.model.Autor;
import com.livraria.api.repository.AutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AutorService {

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public List<AutorResponse> listarTodos() {
        return autorRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AutorResponse buscarPorId(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado com id: " + id));
        return toResponse(autor);
    }

    public AutorResponse salvar(AutorRequest request) {
        Autor autor = new Autor();
        autor.setNome(request.getNome());
        autor.setEmail(request.getEmail());
        return toResponse(autorRepository.save(autor));
    }

    public AutorResponse atualizar(Long id, AutorRequest request) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado com id: " + id));
        autor.setNome(request.getNome());
        autor.setEmail(request.getEmail());
        return toResponse(autorRepository.save(autor));
    }

    public void deletar(Long id) {
        if (!autorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Autor não encontrado com id: " + id);
        }
        autorRepository.deleteById(id);
    }

    private AutorResponse toResponse(Autor autor) {
        AutorResponse response = new AutorResponse();
        response.setId(autor.getId());
        response.setNome(autor.getNome());
        response.setEmail(autor.getEmail());
        response.setLivros(autor.getLivros().stream()
                .map(l -> l.getTitulo())
                .collect(Collectors.toList()));
        return response;
    }
}
