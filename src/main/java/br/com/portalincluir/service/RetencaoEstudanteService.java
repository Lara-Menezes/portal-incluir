package br.com.portalincluir.service;

import br.com.portalincluir.model.Estudante;
import br.com.portalincluir.repository.EstudanteRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetencaoEstudanteService {
    private final EstudanteRepository estudantes;
    private final PoliticaHistoricoService politica;
    private final EntityManager em;

    public RetencaoEstudanteService(EstudanteRepository estudantes, PoliticaHistoricoService politica, EntityManager em) {
        this.estudantes = estudantes;
        this.politica = politica;
        this.em = em;
    }

    @Transactional
    public void descartar(Long id) {
        Estudante estudante = estudantes.buscarComBloqueio(id).orElse(null);
        if (estudante == null || politica.disponivel(estudante)) return;
        // SQL em lote evita que o proprio descarte gere novas copias no Envers.
        // Inclui revisoes de filhos excluidos anteriormente; vinculos nao podem ser transferidos.
        // Mantém os critérios existentes; remove filhos e revisões somente no descarte vencido.
        executar("delete from auditoria.entregas_pea_aud where destinatario_id in (select id from auditoria.destinatarios_solicitacao_pea_aud where solicitacao_id in (select id from auditoria.solicitacoes_pea_aud where estudante_id = :id))", id);
        executar("delete from auditoria.destinatarios_solicitacao_pea_aud where solicitacao_id in (select id from auditoria.solicitacoes_pea_aud where estudante_id = :id)", id);
        executar("delete from auditoria.solicitacoes_pea_aud where estudante_id = :id", id);
        executar("delete from auditoria.eventos_pea_pendentes_aud where estudante_id = :id", id);
        executar("delete from entregas_pea where destinatario_id in (select d.id from destinatarios_solicitacao_pea d join solicitacoes_pea s on s.id = d.solicitacao_id where s.estudante_id = :id)", id);
        executar("delete from destinatarios_solicitacao_pea where solicitacao_id in (select id from solicitacoes_pea where estudante_id = :id)", id);
        executar("delete from solicitacoes_pea where estudante_id = :id", id);
        executar("delete from eventos_pea_pendentes where estudante_id = :id", id);
        executar("delete from auditoria.planos_acao_aud where estudante_id = :id", id);
        executar("delete from auditoria.adaptacoes_pedagogicas_aud where estudante_id = :id", id);
        executar("delete from auditoria.estudantes_aud where id = :id", id);
        executar("delete from auditoria.fluxos_estudantes_aud where estudante_id = :id", id);
        executar("delete from auditoria.notificacoes_fluxo_aud where estudante_id = :id", id);
        executar("delete from notificacoes_fluxo where estudante_id = :id", id);
        executar("delete from auditoria.vinculos_estudante_profissional_aud where estudante_id = :id", id);
        executar("delete from vinculos_estudante_profissional where estudante_id = :id", id);
        executar("delete from fluxos_estudantes where estudante_id = :id", id);
        executar("delete from planos_acao where estudante_id = :id", id);
        executar("delete from adaptacoes_pedagogicas where estudante_id = :id", id);
        executar("delete from estudantes where id = :id", id);
        em.clear();
    }

    private void executar(String sql, Long id) {
        em.createNativeQuery(sql).setParameter("id", id).executeUpdate();
    }
}
