package br.com.portalincluir.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class HistoricoConfig {
    @Bean
    public Clock relogioHistorico() { return Clock.system(ZoneId.of("America/Sao_Paulo")); }
}
