package br.com.portalincluir.repository;

import br.com.portalincluir.model.Estudante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstudanteRepository extends JpaRepository<Estudante, Long> {

    boolean existsByMatricula(String matricula);
    List<Estudante> findByAtivo(boolean ativo);
    List<Estudante> findByNome(String nome);
}