package br.com.portalincluir.service;

import java.time.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LimiteRecuperacaoSenhaTest {
    @Test void limitesPorEmailIpERedefinicaoExpiramAposQuinzeMinutos() {
        var agora = new AtomicReference<>(Instant.parse("2026-11-16T12:00:00Z"));
        var clock = mock(Clock.class); when(clock.instant()).thenAnswer(i -> agora.get());
        var limite = new LimiteRecuperacaoSenha(clock);
        for (int i = 0; i < 3; i++) assertTrue(limite.permitirSolicitacao("pessoa@example.invalid", "ip-a"));
        assertFalse(limite.permitirSolicitacao("pessoa@example.invalid", "ip-b"));
        for (int i = 0; i < 20; i++) assertTrue(limite.permitirSolicitacao(i + "@example.invalid", "ip-c"));
        assertFalse(limite.permitirSolicitacao("outra@example.invalid", "ip-c"));
        for (int i = 0; i < 60; i++) assertTrue(limite.permitirRedefinicao("ip-a"));
        assertFalse(limite.permitirRedefinicao("ip-a"));
        agora.set(agora.get().plus(Duration.ofMinutes(15)));
        assertTrue(limite.permitirSolicitacao("pessoa@example.invalid", "ip-a"));
        assertTrue(limite.permitirRedefinicao("ip-a"));
    }
}
