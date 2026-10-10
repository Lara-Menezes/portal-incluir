package br.com.portalincluir.repository;

import br.com.portalincluir.model.Estudante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstudanteRepository extends JpaRepository<Estudante, Long> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from Estudante e where e.id = :id")
    java.util.Optional<Estudante> buscarComBloqueio(@org.springframework.data.repository.query.Param("id") Long id);

    @org.springframework.data.jpa.repository.Query("select e.id from Estudante e where e.dataLimiteRetencao <= :hoje order by e.id")
    List<Long> buscarVencidos(@org.springframework.data.repository.query.Param("hoje") java.time.LocalDate hoje,
            org.springframework.data.domain.Pageable pageable);

    boolean existsByMatricula(String matricula);
    List<Estudante> findByAtivo(boolean ativo);
    List<Estudante> findByNomeContainingIgnoreCase(String nome);
}