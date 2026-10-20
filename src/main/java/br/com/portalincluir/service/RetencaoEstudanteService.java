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
        executar("delete from auditoria.planos_acao_aud where estudante_id = :id", id);
        executar("delete from auditoria.adaptacoes_pedagogicas_aud where estudante_id = :id", id);
        executar("delete from auditoria.estudantes_aud where id = :id", id);
        executar("delete from planos_acao where estudante_id = :id", id);
        executar("delete from adaptacoes_pedagogicas where estudante_id = :id", id);
        executar("delete from estudantes where id = :id", id);
        em.clear();
    }

    private void executar(String sql, Long id) {
        em.createNativeQuery(sql).setParameter("id", id).executeUpdate();
    }
}
