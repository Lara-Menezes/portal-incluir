package br.com.portalincluir.controller;

import br.com.portalincluir.dto.request.ProfessorRequest;
import br.com.portalincluir.dto.response.ProfessorResponse;
import br.com.portalincluir.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professores")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @PostMapping
    public ProfessorResponse criar(
            @Valid @RequestBody ProfessorRequest request) {

        return professorService.criar(request);
    }

    @GetMapping
    public List<ProfessorResponse> listarTodos() {
        return professorService.listarTodos();
    }

    @GetMapping("/{id}")
    public ProfessorResponse buscarPorId(@PathVariable Long id) {
        return professorService.buscarPorId(id);
    }

    @GetMapping("/ativos")
    public List<ProfessorResponse> listarAtivos() {
        return professorService.listarAtivos();
    }

    @GetMapping("/inativos")
    public List<ProfessorResponse> listarInativos() {
        return professorService.listarInativos();
    }

    @GetMapping("/buscar")
    public List<ProfessorResponse> buscarPorNome(
            @RequestParam String nome) {

        return professorService.buscarPorNome(nome);
    }

    @PutMapping("/{id}")
    public ProfessorResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProfessorRequest request) {

        return professorService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        professorService.excluir(id);
    }

    @PatchMapping("/{id}/inativar")
    public ProfessorResponse inativar(@PathVariable Long id) {
        return professorService.inativar(id);
    }

    @PatchMapping("/{id}/ativar")
    public ProfessorResponse ativar(@PathVariable Long id) {
        return professorService.ativar(id);
    }
}
