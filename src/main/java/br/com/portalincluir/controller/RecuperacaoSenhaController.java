package br.com.portalincluir.controller;

import br.com.portalincluir.dto.request.RecuperarSenhaRequest;
import br.com.portalincluir.dto.request.RedefinirSenhaRequest;
import br.com.portalincluir.service.LimiteRecuperacaoSenha;
import br.com.portalincluir.service.RecuperacaoSenhaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Locale;
import java.util.Map;

@RestController @RequestMapping("/api/auth")
public class RecuperacaoSenhaController {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RecuperacaoSenhaController.class);
    private final RecuperacaoSenhaService service;
    private final LimiteRecuperacaoSenha limites;
    private final TaskExecutor executor;
    public RecuperacaoSenhaController(RecuperacaoSenhaService service, LimiteRecuperacaoSenha limites,
            @Qualifier("recuperacaoSenhaExecutor") TaskExecutor executor) {
        this.service = service; this.limites = limites; this.executor = executor;
    }
    @PostMapping("/recuperar-senha")
    public ResponseEntity<?> solicitar(@Valid @RequestBody RecuperarSenhaRequest dados, HttpServletRequest http) {
        String email = dados.getEmail().strip().toLowerCase(Locale.ROOT);
        if (limites.permitirSolicitacao(email, http.getRemoteAddr())) {
            try {
                executor.execute(() -> {
                    try { service.emitir(email); }
                    catch (RuntimeException erro) { LOGGER.warn("Recuperacao de senha: solicitacao nao processada ({}).", erro.getClass().getSimpleName()); }
                });
            } catch (RuntimeException erro) { LOGGER.warn("Recuperacao de senha: fila temporariamente indisponivel."); }
        }
        return ResponseEntity.accepted().cacheControl(CacheControl.noStore()).body(Map.of("message",
                "Se houver uma conta elegível para este e-mail, você receberá as instruções de recuperação."));
    }
    @PostMapping("/redefinir-senha")
    public ResponseEntity<?> redefinir(@Valid @RequestBody RedefinirSenhaRequest dados, HttpServletRequest http) {
        if (!limites.permitirRedefinicao(http.getRemoteAddr())) return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, "900").cacheControl(CacheControl.noStore())
                .body(Map.of("message", "Muitas tentativas. Aguarde alguns minutos antes de tentar novamente."));
        service.redefinir(dados.getToken(), dados.getNovaSenha());
        // Nao autentica automaticamente; a nova senha deve ser usada em um novo login.
        var sessao = http.getSession(false); if (sessao != null) sessao.invalidate();
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Map.of("message", "Senha redefinida com sucesso. Entre com sua nova senha."));
    }
}
