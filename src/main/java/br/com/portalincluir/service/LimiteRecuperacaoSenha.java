package br.com.portalincluir.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Limite por instancia, com armazenamento limitado e sem emails/IPs em texto. */
@Component
public class LimiteRecuperacaoSenha {
    private record Janela(Instant expira, int tentativas) { }
    private final Map<String, Janela> janelas = new HashMap<>();
    private final Clock clock;
    public LimiteRecuperacaoSenha(Clock clock) { this.clock = clock; }

    public synchronized boolean permitirSolicitacao(String email, String ip) {
        return permitir("solicitacao-ip:" + ip, 20) && permitir("solicitacao-email:" + email, 3);
    }
    public synchronized boolean permitirRedefinicao(String ip) { return permitir("redefinicao-ip:" + ip, 60); }
    private boolean permitir(String identificacao, int limite) {
        Instant agora = clock.instant();
        String chave = RecuperacaoSenhaService.hash(identificacao);
        var janela = janelas.get(chave);
        if (janela == null || !agora.isBefore(janela.expira())) {
            if (janelas.size() >= 10000) {
                janelas.entrySet().removeIf(e -> !agora.isBefore(e.getValue().expira()));
                if (janelas.size() >= 10000 && !janelas.containsKey(chave)) return false;
            }
            janelas.put(chave, new Janela(agora.plus(Duration.ofMinutes(15)), 1)); return true;
        }
        if (janela.tentativas() >= limite) return false;
        janelas.put(chave, new Janela(janela.expira(), janela.tentativas() + 1)); return true;
    }
}
