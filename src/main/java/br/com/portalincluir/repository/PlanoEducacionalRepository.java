package br.com.portalincluir.repository;

import br.com.portalincluir.model.PlanoEducacional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanoEducacionalRepository
        extends JpaRepository<PlanoEducacional, Long> {

    List<PlanoEducacional> findByEstudanteId(Long estudanteId);

    List<PlanoEducacional> findByCoordenadorId(Long coordenadorId);

    List<PlanoEducacional> findByEstudanteIdAndSemestreAno(
            Long estudanteId,
            String semestreAno
    );

    List<PlanoEducacional> findByArquivado(boolean arquivado);
}