package br.com.portalincluir.controller;

import br.com.portalincluir.dto.response.PlanoAcaoResponse;
import br.com.portalincluir.enums.StatusPlanoAcao;
import br.com.portalincluir.service.PlanoAcaoService;
import org.springframework.web.bind.annotation.*;
import br.com.portalincluir.dto.request.PlanoAcaoRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/planos-acao")
public class PlanoAcaoController {

    private final PlanoAcaoService planoAcaoService;

    public PlanoAcaoController(PlanoAcaoService planoAcaoService) {
        this.planoAcaoService = planoAcaoService;
    }

    //Criar
    @PostMapping
    public PlanoAcaoResponse criar(@Valid @RequestBody PlanoAcaoRequest request) {
        return planoAcaoService.criar(request);
    }

    //Listar
    @GetMapping
    public List<PlanoAcaoResponse> listar(
            @RequestParam(required = false) StatusPlanoAcao status) {

        if (status != null) {
            return planoAcaoService.listarPorStatus(status);
        }

        return planoAcaoService.listarTodos();
    }

    //Buscar ID
    @GetMapping("/{id}")
    public PlanoAcaoResponse buscarPorId(@PathVariable Long id) {
        return planoAcaoService.buscarPorId(id);
    }

    //Atualizar
    @PutMapping("/{id}")
    public PlanoAcaoResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PlanoAcaoRequest request) {

        return planoAcaoService.atualizar(id, request);
    }

    //Excluir
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        planoAcaoService.excluir(id);
    }

    //Arquivar
    @PostMapping("/{id}/arquivar")
    public PlanoAcaoResponse arquivar(@PathVariable Long id) {
        return planoAcaoService.arquivar(id);
    }

    //Desarquivar
    @PostMapping("/{id}/desarquivar")
    public PlanoAcaoResponse desarquivar(@PathVariable Long id) {
        return planoAcaoService.desarquivar(id);
    }

    //Assinar
    @PostMapping("/{id}/assinar")
    public PlanoAcaoResponse assinar(@PathVariable Long id) {
        return planoAcaoService.assinar(id);
    }

}
