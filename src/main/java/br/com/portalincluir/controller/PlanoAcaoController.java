package br.com.portalincluir.controller;

import br.com.portalincluir.service.PlanoAcaoService;
import org.springframework.web.bind.annotation.*;
import br.com.portalincluir.dto.PlanoAcaoRequest;
import br.com.portalincluir.model.PlanoAcao;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/planos-acao")
public class PlanoAcaoController {

    private final PlanoAcaoService planoAcaoService;

    public PlanoAcaoController(PlanoAcaoService planoAcaoService) {
        this.planoAcaoService = planoAcaoService;
    }

    @PostMapping
    public PlanoAcao criar(@Valid @RequestBody PlanoAcaoRequest request) {
        return planoAcaoService.criar(request);
    }

    @GetMapping
    public List<PlanoAcao> listarTodos() {
        return planoAcaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public PlanoAcao buscarPorId(@PathVariable Long id) {
        return planoAcaoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public PlanoAcao atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PlanoAcaoRequest request) {

        return planoAcaoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        planoAcaoService.excluir(id);
    }

}
