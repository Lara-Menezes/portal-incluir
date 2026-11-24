package br.com.portalincluir.service;

import br.com.portalincluir.dto.response.*;
import br.com.portalincluir.model.*;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import org.hibernate.envers.*;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class HistoricoEstudanteService {
    private final EntityManager em;
    private final PoliticaHistoricoService politica;
    private final boolean habilitado;

    public HistoricoEstudanteService(EntityManager em, PoliticaHistoricoService politica,
            @Value("${app.historico.consulta-habilitada:false}") boolean habilitado) {
        this.em = em;
        this.politica = politica;
        this.habilitado = habilitado;
    }

    public List<HistoricoRevisaoResponse<EstudanteResponse>> estudantes(Long id, int pagina, int tamanho) {
        return consultar(Estudante.class, id, pagina, tamanho, EstudanteResponse::new);
    }

    public List<HistoricoRevisaoResponse<RegistroHistoricoResponse>> planos(Long id, int pagina, int tamanho) {
        return consultar(PlanoAcao.class, id, pagina, tamanho, RegistroHistoricoResponse::de);
    }

    public List<HistoricoRevisaoResponse<RegistroHistoricoResponse>> adaptacoes(Long id, int pagina, int tamanho) {
        return consultar(AdaptacaoPedagogica.class, id, pagina, tamanho, RegistroHistoricoResponse::de);
    }

    @SuppressWarnings("unchecked")
    private <E, D> List<HistoricoRevisaoResponse<D>> consultar(Class<E> tipo, Long id, int pagina, int tamanho,
            Function<E, D> converter) {
        if (!habilitado) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Consulta de revisoes desabilitada");
        if (pagina < 0 || tamanho < 1 || tamanho > 100 || (long) pagina * tamanho > Integer.MAX_VALUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paginacao invalida: tamanho entre 1 e 100");
        }
        Estudante estudante = em.find(Estudante.class, id);
        if (estudante == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante nao encontrado");
        politica.exigirDisponivel(estudante);
        AuditQuery query = AuditReaderFactory.get(em).createQuery().forRevisionsOfEntity(tipo, false, true);
        query.add(tipo == Estudante.class ? AuditEntity.id().eq(id) : AuditEntity.relatedId("estudante").eq(id));
        query.addOrder(AuditEntity.revisionNumber().desc()).addOrder(AuditEntity.id().asc());
        List<Object[]> linhas = query.setFirstResult(pagina * tamanho).setMaxResults(tamanho).getResultList();
        return linhas.stream().map(linha -> {
            RevisaoAuditoria revisao = (RevisaoAuditoria) linha[1];
            return new HistoricoRevisaoResponse<>(revisao.getId(), Instant.ofEpochMilli(revisao.getTimestamp()),
                    ((RevisionType) linha[2]).name(), converter.apply((E) linha[0]));
        }).toList();
    }
}
