package br.com.portalincluir.controller;

import br.com.portalincluir.dto.request.PlanoEducacionalRequest;
import br.com.portalincluir.dto.response.PlanoEducacionalResponse;
import br.com.portalincluir.service.PlanoEducacionalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planos-educacionais")
public class PlanoEducacionalController {

    private final PlanoEducacionalService planoService;

    public PlanoEducacionalController(
            PlanoEducacionalService planoService) {
        this.planoService = planoService;
    }

    //Criar
    @PostMapping
    public PlanoEducacionalResponse criar(
            @Valid @RequestBody PlanoEducacionalRequest request) {
        return planoService.criar(request);
    }

    //Listar
    @GetMapping
    public List<PlanoEducacionalResponse> listarTodos() {
        return planoService.listarTodos();
    }

    //Buscar por id
    @GetMapping("/{id}")
    public PlanoEducacionalResponse buscarPorId(
            @PathVariable Long id) {
        return planoService.buscarPorId(id);
    }

    //Listar por estudante
    @GetMapping("/estudante/{estudanteId}")
    public List<PlanoEducacionalResponse> listarPorEstudante(
            @PathVariable Long estudanteId) {
        return planoService.listarPorEstudante(estudanteId);
    }

    //Listar por coordenador
    @GetMapping("/coordenador/{coordenadorId}")
    public List<PlanoEducacionalResponse> listarPorCoordenador(
            @PathVariable Long coordenadorId) {
        return planoService.listarPorCoordenador(coordenadorId);
    }

    //Listar por estudante e semestre
    @GetMapping("/estudante/{estudanteId}/semestre/{semestreAno}")
    public List<PlanoEducacionalResponse> listarPorEstudanteESemestre(
            @PathVariable Long estudanteId,
            @PathVariable String semestreAno) {
        return planoService.listarPorEstudanteESemestre(
                estudanteId, semestreAno);
    }

    //listar por arquivado
    @GetMapping("/arquivados")
    public List<PlanoEducacionalResponse> listarArquivados() {
        return planoService.listarPorArquivamento(true);
    }

    //Listar por ativos
    @GetMapping("/ativos")
    public List<PlanoEducacionalResponse> listarNaoArquivados() {
        return planoService.listarPorArquivamento(false);
    }

    //Atualizar
    @PutMapping("/{id}")
    public PlanoEducacionalResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PlanoEducacionalRequest request) {
        return planoService.atualizar(id, request);
    }

    //arquivar
    @PostMapping("/{id}/arquivar")
    public PlanoEducacionalResponse arquivar(
            @PathVariable Long id) {
        return planoService.arquivar(id);
    }

    //desarquivar
    @PostMapping("/{id}/desarquivar")
    public PlanoEducacionalResponse desarquivar(
            @PathVariable Long id) {
        return planoService.desarquivar(id);
    }

    //Excluir
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        planoService.excluir(id);
    }
}