package br.com.portalincluir.repository;

import br.com.portalincluir.model.AdaptacaoPedagogica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdaptacaoPedagogicaRepository
        extends JpaRepository<AdaptacaoPedagogica, Long> {

    List<AdaptacaoPedagogica> findByEstudanteId(Long estudanteId);

    List<AdaptacaoPedagogica> findByProfessorId(Long professorId);

    List<AdaptacaoPedagogica> findByEstudanteIdAndProfessorId(
            Long estudanteId,
            Long professorId
    );

    List<AdaptacaoPedagogica> findByComponenteCurricularContainingIgnoreCase(
            String componenteCurricular
    );
}
