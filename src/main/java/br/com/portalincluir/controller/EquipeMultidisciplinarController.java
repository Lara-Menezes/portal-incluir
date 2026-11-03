package br.com.portalincluir.controller;

import br.com.portalincluir.dto.request.EquipeMultidisciplinarRequest;
import br.com.portalincluir.dto.response.EquipeMultidisciplinarResponse;
import br.com.portalincluir.service.EquipeMultidisciplinarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipe-multidisciplinar")
public class EquipeMultidisciplinarController {

    private final EquipeMultidisciplinarService service;

    public EquipeMultidisciplinarController(
            EquipeMultidisciplinarService service
    ) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipeMultidisciplinarResponse criar(
            @Valid @RequestBody EquipeMultidisciplinarRequest request
    ) {
        return service.criar(request);
    }

    @GetMapping
    public List<EquipeMultidisciplinarResponse> listar(
            @RequestParam(required = false) Boolean ativo
    ) {
        return service.listar(ativo);
    }

    @GetMapping("/{id}")
    public EquipeMultidisciplinarResponse buscarPorId(
            @PathVariable Long id
    ) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public EquipeMultidisciplinarResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EquipeMultidisciplinarRequest request
    ) {
        return service.atualizar(id, request);
    }

    @PatchMapping("/{id}/inativar")
    public EquipeMultidisciplinarResponse inativar(
            @PathVariable Long id
    ) {
        return service.inativar(id);
    }

    @PatchMapping("/{id}/reativar")
    public EquipeMultidisciplinarResponse reativar(
            @PathVariable Long id
    ) {
        return service.reativar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}