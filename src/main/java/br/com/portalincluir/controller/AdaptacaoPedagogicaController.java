package br.com.portalincluir.controller;

import br.com.portalincluir.dto.request.AdaptacaoPedagogicaRequest;
import br.com.portalincluir.dto.response.AdaptacaoPedagogicaResponse;
import br.com.portalincluir.service.AdaptacaoPedagogicaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adaptacoes-pedagogicas")
public class AdaptacaoPedagogicaController {

    private final AdaptacaoPedagogicaService adaptacaoService;

    public AdaptacaoPedagogicaController(
            AdaptacaoPedagogicaService adaptacaoService) {
        this.adaptacaoService = adaptacaoService;
    }

    //Criar
    @PostMapping
    public AdaptacaoPedagogicaResponse criar(
            @Valid @RequestBody AdaptacaoPedagogicaRequest request) {

        return adaptacaoService.criar(request);
    }

    //Listar
    @GetMapping
    public List<AdaptacaoPedagogicaResponse> listarTodos() {
        return adaptacaoService.listarTodos();
    }

    //Buscar por id
    @GetMapping("/{id}")
    public AdaptacaoPedagogicaResponse buscarPorId(
            @PathVariable Long id) {

        return adaptacaoService.buscarPorId(id);
    }

    //Buscar por estudante
    @GetMapping("/estudante/{estudanteId}")
    public List<AdaptacaoPedagogicaResponse> listarPorEstudante(
            @PathVariable Long estudanteId) {

        return adaptacaoService.listarPorEstudante(estudanteId);
    }

    //Buscar por professor
    @GetMapping("/professor/{professorId}")
    public List<AdaptacaoPedagogicaResponse> listarPorProfessor(
            @PathVariable Long professorId) {

        return adaptacaoService.listarPorProfessor(professorId);
    }

    //Buscar por estudante e professor
    @GetMapping("/estudante/{estudanteId}/professor/{professorId}")
    public List<AdaptacaoPedagogicaResponse> listarPorEstudanteEProfessor(
            @PathVariable Long estudanteId,
            @PathVariable Long professorId) {

        return adaptacaoService.listarPorEstudanteEProfessor(
                estudanteId,
                professorId
        );
    }

    //Buscar por componenete curricular
    @GetMapping("/buscar")
    public List<AdaptacaoPedagogicaResponse> buscarPorComponente(
            @RequestParam String componenteCurricular) {

        return adaptacaoService.buscarPorComponente(
                componenteCurricular
        );
    }

    //Atualizar
    @PutMapping("/{id}")
    public AdaptacaoPedagogicaResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AdaptacaoPedagogicaRequest request) {

        return adaptacaoService.atualizar(id, request);
    }

    //Arquivar
    @PostMapping("/{id}/arquivar")
    public AdaptacaoPedagogicaResponse arquivar(@PathVariable Long id) {
        return adaptacaoService.arquivar(id);
    }

    //Desarquivar
    @PostMapping("/{id}/desarquivar")
    public AdaptacaoPedagogicaResponse desarquivar(@PathVariable Long id) {
        return adaptacaoService.desarquivar(id);
    }

    //Excluir
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        adaptacaoService.excluir(id);
    }
}
