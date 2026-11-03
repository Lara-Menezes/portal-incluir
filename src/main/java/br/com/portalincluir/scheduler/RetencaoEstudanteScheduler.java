package br.com.portalincluir.scheduler;

import br.com.portalincluir.repository.EstudanteRepository;
import br.com.portalincluir.service.PoliticaHistoricoService;
import br.com.portalincluir.service.RetencaoEstudanteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RetencaoEstudanteScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(RetencaoEstudanteScheduler.class);
    private final EstudanteRepository estudantes;
    private final PoliticaHistoricoService politica;
    private final RetencaoEstudanteService retencao;
    private final int tamanhoLote;

    public RetencaoEstudanteScheduler(EstudanteRepository estudantes, PoliticaHistoricoService politica,
            RetencaoEstudanteService retencao, @Value("${app.retencao.tamanho-lote:100}") int tamanhoLote) {
        this.estudantes = estudantes;
        this.politica = politica;
        this.retencao = retencao;
        this.tamanhoLote = Math.max(1, tamanhoLote);
    }

    @Scheduled(fixedDelayString = "${app.retencao.intervalo-ms:3600000}")
    public void executar() {
        for (Long id : estudantes.buscarVencidos(politica.hoje(), PageRequest.of(0, tamanhoLote))) {
            try {
                retencao.descartar(id);
            } catch (RuntimeException erro) {
                // Nao registra dados pessoais nem mensagens SQL nos logs.
                LOG.error("Falha no descarte de um registro vencido; nova tentativa no proximo ciclo ({})",
                        erro.getClass().getSimpleName());
            }
        }
    }
}
