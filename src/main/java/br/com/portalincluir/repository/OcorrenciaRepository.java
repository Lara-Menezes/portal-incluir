package br.com.portalincluir.repository;

import br.com.portalincluir.model.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Sort;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long>, JpaSpecificationExecutor<Ocorrencia> {
    default List<Ocorrencia> consultar(Long autor, Long estudante, LocalDateTime inicio, LocalDateTime fim) {
        return filtrar(null, autor, estudante, inicio, fim);
    }
    default List<Ocorrencia> consultarVinculados(Set<Long> ids, Long estudante, LocalDateTime inicio, LocalDateTime fim) {
        return ids.isEmpty() ? List.of() : filtrar(ids, null, estudante, inicio, fim);
    }
    private List<Ocorrencia> filtrar(Set<Long> ids, Long autor, Long estudante, LocalDateTime inicio, LocalDateTime fim) {
        return findAll((raiz, consulta, cb) -> {
            var filtros = new ArrayList<Predicate>();
            if (ids != null) filtros.add(raiz.get("estudante").get("id").in(ids));
            if (autor != null) filtros.add(cb.equal(raiz.get("autor").get("id"), autor));
            if (estudante != null) filtros.add(cb.equal(raiz.get("estudante").get("id"), estudante));
            if (inicio != null) filtros.add(cb.greaterThanOrEqualTo(raiz.get("ocorridaEm"), inicio));
            if (fim != null) filtros.add(cb.lessThan(raiz.get("ocorridaEm"), fim));
            return cb.and(filtros.toArray(Predicate[]::new));
        }, Sort.by(Sort.Direction.DESC, "ocorridaEm", "id"));
    }
}
