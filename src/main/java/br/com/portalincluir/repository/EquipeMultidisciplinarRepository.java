package br.com.portalincluir.repository;

import br.com.portalincluir.model.EquipeMultidisciplinar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipeMultidisciplinarRepository
        extends JpaRepository<EquipeMultidisciplinar, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByMatricula(String matricula);

    boolean existsByMatriculaAndIdNot(String matricula, Long id);

    List<EquipeMultidisciplinar> findByAtivo(boolean ativo);
}