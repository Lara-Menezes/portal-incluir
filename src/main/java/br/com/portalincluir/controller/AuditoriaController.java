package br.com.portalincluir.controller;

import br.com.portalincluir.service.PermissaoService;
import br.com.portalincluir.service.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auditorias")
public class AuditoriaController {
    private final AuditoriaService service;
    private final PermissaoService acesso;
    public AuditoriaController(AuditoriaService service, PermissaoService acesso) {
        this.service = service;
        this.acesso = acesso;
    }
    private void autorizar(HttpServletRequest request) {
        acesso.exigir(request, "auditorias");
    }
    @GetMapping("/tabelas")
    public ResponseEntity<List<String>> tabelas(HttpServletRequest request) {
        autorizar(request);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.tabelas());
    }
    @GetMapping
    public ResponseEntity<AuditoriaService.Consulta> consultar(HttpServletRequest request,
            @RequestParam(defaultValue = "") String tabela, @RequestParam(defaultValue = "") String operacao,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(defaultValue = "") String registro,
            @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamanho) {
        autorizar(request);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
            .body(service.consultar(tabela, operacao, inicio, fim, registro, pagina, tamanho));
    }
}
