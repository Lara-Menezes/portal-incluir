package br.com.portalincluir.controller;

import br.com.portalincluir.service.EventoPeaPendenteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequiredArgsConstructor @RequestMapping("/api/eventos-pea-pendentes")
public class EventoPeaPendenteController {
    private final EventoPeaPendenteService service;
    public record Encaminhamento(@NotNull @PositiveOrZero Long versao, @NotNull @Positive Long destinatarioUsuarioId) {}
    @GetMapping public List<EventoPeaPendenteService.Resposta> listar() { return service.listar(); }
    @PostMapping("/{id}/encaminhar")
    public EventoPeaPendenteService.Resposta encaminhar(@PathVariable Long id, @Valid @RequestBody Encaminhamento r) {
        return service.encaminhar(id, r.versao(), r.destinatarioUsuarioId());
    }
}
