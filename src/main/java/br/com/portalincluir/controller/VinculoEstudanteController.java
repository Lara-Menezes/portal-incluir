package br.com.portalincluir.controller;
import br.com.portalincluir.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequiredArgsConstructor @RequestMapping("/api/estudantes/{estudanteId}/profissionais")
public class VinculoEstudanteController {
    private final PermissaoService acesso;
    private final VinculoEstudanteService service;
    public record Associacao(@NotNull Long profissionalId, @NotBlank @Size(max=1000) String motivo) {}
    public record Encerramento(@NotNull Long versao, @NotBlank @Size(max=1000) String motivo) {}
    @GetMapping public List<VinculoEstudanteService.Vinculo> listar(@PathVariable Long estudanteId, HttpServletRequest http) { acesso.exigir(http, "estudantes.consultar"); return service.listar(estudanteId); }
    @GetMapping("/disponiveis") public List<VinculoEstudanteService.Profissional> candidatos(@PathVariable Long estudanteId, HttpServletRequest http) { acesso.exigir(http, "estudantes.vincular"); return service.candidatos(estudanteId); }
    @PostMapping public VinculoEstudanteService.Vinculo associar(@PathVariable Long estudanteId, @Valid @RequestBody Associacao r, HttpServletRequest http) { var autor = acesso.exigir(http, "estudantes.vincular"); return service.associar(estudanteId, r.profissionalId(), r.motivo(), autor.getId()); }
    @PostMapping("/{id}/encerrar") public VinculoEstudanteService.Vinculo encerrar(@PathVariable Long estudanteId, @PathVariable Long id, @Valid @RequestBody Encerramento r, HttpServletRequest http) { var autor = acesso.exigir(http, "estudantes.vincular"); return service.encerrar(estudanteId, id, r.versao(), r.motivo(), autor.getId()); }
}
