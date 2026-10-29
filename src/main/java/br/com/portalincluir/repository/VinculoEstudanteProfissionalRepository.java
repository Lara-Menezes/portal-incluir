package br.com.portalincluir.repository;
import br.com.portalincluir.model.VinculoEstudanteProfissional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface VinculoEstudanteProfissionalRepository extends JpaRepository<VinculoEstudanteProfissional,Long> {
    List<VinculoEstudanteProfissional> findByEstudanteIdOrderByAtivoDescDataInicioDesc(Long estudanteId);
    Optional<VinculoEstudanteProfissional> findByEstudanteIdAndProfessorId(Long estudanteId, Long professorId);
    Optional<VinculoEstudanteProfissional> findByEstudanteIdAndMembroId(Long estudanteId, Long membroId);
    @Query("select v.estudante.id from VinculoEstudanteProfissional v where v.ativo=true and v.estudante.ativo=true and v.professor.id=:id")
    Set<Long> estudantesDoProfessor(@Param("id") Long id);
    @Query("select v.estudante.id from VinculoEstudanteProfissional v where v.ativo=true and v.estudante.ativo=true and v.membro.id=:id")
    Set<Long> estudantesDoMembro(@Param("id") Long id);
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from VinculoEstudanteProfissional v where v.id=:id")
    Optional<VinculoEstudanteProfissional> buscarComBloqueio(@Param("id") Long id);
}
