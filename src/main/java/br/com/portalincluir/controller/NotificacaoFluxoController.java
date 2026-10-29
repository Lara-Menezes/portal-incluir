package br.com.portalincluir.controller;
import br.com.portalincluir.repository.NotificacaoFluxoRepository;
import br.com.portalincluir.service.PermissaoService;
import br.com.portalincluir.service.PoliticaHistoricoService;
import br.com.portalincluir.model.NotificacaoFluxo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
@RestController @RequestMapping("/api/notificacoes") @RequiredArgsConstructor @Transactional
public class NotificacaoFluxoController {
    private final NotificacaoFluxoRepository notificacoes;
    private final PermissaoService acesso;
    private final PoliticaHistoricoService politica;
    public record Resposta(Long id, Long estudanteId, String estudanteNome, String etapa, Long planoId, String remetente, String mensagem, LocalDateTime criadaEm, boolean lida) {}
    private Resposta resposta(NotificacaoFluxo n) { return new Resposta(n.getId(), n.getEstudante().getId(), n.getEstudante().getNome(), n.getEtapa(), n.getPlanoId(), n.getRemetente(), n.getMensagem(), n.getCriadaEm(), n.isLida()); }
    @GetMapping public List<Resposta> listar(HttpServletRequest req) {
        var u = acesso.autenticado(req);
        return notificacoes.findByDestinatarioIdOrderByCriadaEmDescIdDesc(u.getId()).stream().filter(n -> politica.disponivel(n.getEstudante())).map(this::resposta).toList();
    }
    @PatchMapping("/{id}/lida") public Resposta ler(@PathVariable Long id, HttpServletRequest req) {
        var u = acesso.autenticado(req);
        var n = notificacoes.findByIdAndDestinatarioId(id, u.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        politica.exigirDisponivel(n.getEstudante()); n.setLida(true);
        return resposta(notificacoes.save(n));
    }
}
