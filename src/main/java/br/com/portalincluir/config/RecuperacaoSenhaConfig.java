package br.com.portalincluir.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class RecuperacaoSenhaConfig {
    @Bean("recuperacaoSenhaExecutor")
    public ThreadPoolTaskExecutor recuperacaoSenhaExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1); executor.setMaxPoolSize(2); executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("recuperacao-senha-");
        return executor;
    }
}
