package br.com.portalincluir.controller;

import br.com.portalincluir.dto.response.*;
import br.com.portalincluir.service.HistoricoEstudanteService;
import java.util.List;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estudantes/{id}/historico")
public class HistoricoEstudanteController {
    private final HistoricoEstudanteService historico;
    public HistoricoEstudanteController(HistoricoEstudanteService historico) { this.historico = historico; }

    @GetMapping
    public ResponseEntity<List<HistoricoRevisaoResponse<EstudanteResponse>>> estudantes(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamanho) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(historico.estudantes(id, pagina, tamanho));
    }

    @GetMapping("/planos")
    public ResponseEntity<List<HistoricoRevisaoResponse<RegistroHistoricoResponse>>> planos(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamanho) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(historico.planos(id, pagina, tamanho));
    }

    @GetMapping("/adaptacoes")
    public ResponseEntity<List<HistoricoRevisaoResponse<RegistroHistoricoResponse>>> adaptacoes(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamanho) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(historico.adaptacoes(id, pagina, tamanho));
    }
}
