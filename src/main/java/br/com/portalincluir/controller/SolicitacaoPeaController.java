package br.com.portalincluir.controller;

import br.com.portalincluir.dto.request.*;
import br.com.portalincluir.dto.response.*;
import br.com.portalincluir.service.SolicitacaoPeaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class SolicitacaoPeaController {
    private final SolicitacaoPeaService service;
    public record Cancelamento(@NotNull @PositiveOrZero Long versao, @NotBlank @Size(max=1000) String motivo) {}

    @GetMapping("/estudantes/{id}/solicitacoes-pea/elegiveis")
    public List<SolicitacaoPeaService.Elegivel> elegiveis(@PathVariable Long id, @RequestParam(required=false) String setor) { return service.elegiveis(id, setor); }
    @PostMapping("/estudantes/{id}/solicitacoes-pea")
    public SolicitacaoPeaResponse criar(@PathVariable Long id, @Valid @RequestBody SolicitacaoPeaRequest r, @RequestHeader("Idempotency-Key") String chave) { return service.criar(id, r, chave); }
    @GetMapping("/estudantes/{id}/solicitacoes-pea")
    public List<SolicitacaoPeaResponse> listar(@PathVariable Long id) { return service.porEstudante(id); }
    @GetMapping("/solicitacoes-pea/recebidas")
    public List<SolicitacaoPeaResponse> recebidas() { return service.recebidas(); }
    @GetMapping("/solicitacoes-pea/{id}")
    public SolicitacaoPeaResponse detalhes(@PathVariable Long id) { return service.detalhes(id); }
    @GetMapping("/solicitacoes-pea/{id}/contexto")
    public SolicitacaoPeaResponse contexto(@PathVariable Long id) { return service.contexto(id); }
    @GetMapping("/solicitacoes-pea/{id}/andamento")
    public SolicitacaoPeaResponse andamento(@PathVariable Long id) { return service.detalhes(id); }
    @PostMapping("/solicitacoes-pea/{id}/entregas")
    public AdaptacaoPedagogicaResponse entregar(@PathVariable Long id, @Valid @RequestBody ConteudoPeaRequest r, @RequestHeader("Idempotency-Key") String chave) { return service.entregar(id, r, chave); }
    @PostMapping("/solicitacoes-pea/{id}/cancelar")
    public SolicitacaoPeaResponse cancelar(@PathVariable Long id, @Valid @RequestBody Cancelamento r) { return service.cancelar(id, r.versao(), r.motivo()); }
}
