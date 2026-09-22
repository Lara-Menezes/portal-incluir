package br.com.portalincluir.controller;

import br.com.portalincluir.dto.request.EstudanteRequest;
import br.com.portalincluir.dto.response.EstudanteResponse;
import br.com.portalincluir.service.EstudanteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudantes")
public class EstudanteController {

    private final EstudanteService estudanteService;

    public EstudanteController(EstudanteService estudanteService) {
        this.estudanteService = estudanteService;
    }

    // Cadastrar
    @PostMapping
    public EstudanteResponse cadastrar(
            @Valid @RequestBody EstudanteRequest request) {

        return estudanteService.cadastrar(request);
    }

    // Listar todos
    @GetMapping
    public List<EstudanteResponse> listarTodos() {
        return estudanteService.listarTodos();
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public EstudanteResponse buscarPorId(@PathVariable Long id) {
        return estudanteService.buscarPorId(id);
    }

    // Listar ativos
    @GetMapping("/ativos")
    public List<EstudanteResponse> listarAtivos() {
        return estudanteService.listarAtivos();
    }

    // Listar inativos
    @GetMapping("/inativos")
    public List<EstudanteResponse> listarInativos() {
        return estudanteService.listarInativos();
    }

    // Buscar por nome
    @GetMapping("/buscar")
    public List<EstudanteResponse> buscarPorNome(
            @RequestParam String nome) {
        return estudanteService.buscarPorNome(nome);
    }

    // Atualizar
    @PutMapping("/{id}")
    public EstudanteResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstudanteRequest request) {

        return estudanteService.atualizar(id, request);
    }

    // Excluir
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        estudanteService.excluir(id);
    }

    // Inativar
    @PostMapping("/{id}/inativar")
    public EstudanteResponse inativar(@PathVariable Long id) {
        return estudanteService.inativar(id);
    }

    // Ativar
    @PostMapping("/{id}/ativar")
    public EstudanteResponse ativar(@PathVariable Long id) {
        return estudanteService.ativar(id);
    }
}