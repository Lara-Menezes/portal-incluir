package br.com.portalincluir.controller;
import br.com.portalincluir.service.SolicitacaoAcessoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/solicitacoes-acesso") @RequiredArgsConstructor
public class SolicitacaoAcessoController {
  private final SolicitacaoAcessoService service;
  @PostMapping("/administracao") public SolicitacaoAcessoService.Resposta solicitarAdministracao(HttpServletRequest req) { return service.solicitarAdministracao(req); }
  @GetMapping("/minhas") public List<SolicitacaoAcessoService.Resposta> minhas(HttpServletRequest req) { return service.minhas(req); }
  public record Decisao(@NotNull Boolean aprovar, @Size(max=1000) String motivo) {}
  @GetMapping public List<SolicitacaoAcessoService.Resposta> listar(HttpServletRequest req,@RequestParam(defaultValue="false") boolean todas) { return service.listar(req,todas); }
  @PostMapping("/{id}/decisao") public SolicitacaoAcessoService.Resposta decidir(@PathVariable Long id,@Valid @RequestBody Decisao r,HttpServletRequest req) { return service.decidir(id,r.aprovar(),r.motivo(),req); }
}
