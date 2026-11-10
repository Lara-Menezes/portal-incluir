package br.com.portalincluir.repository;

import br.com.portalincluir.model.ConteudoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConteudoAcademicoRepository extends JpaRepository<ConteudoAcademico, Long> {
    List<ConteudoAcademico> findByProfessorIdOrderByDataAtualizacaoDescIdDesc(Long professorId);
    List<ConteudoAcademico> findAllByOrderByDataAtualizacaoDescIdDesc();
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ConteudoAcademico c where c.id = :id")
    Optional<ConteudoAcademico> buscarParaAlteracao(@Param("id") Long id);
}
