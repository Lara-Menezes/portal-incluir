package br.com.portalincluir.repository;

import br.com.portalincluir.enums.StatusCoordenador;
import br.com.portalincluir.model.Coordenador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoordenadorRepository extends JpaRepository<Coordenador, Long> {
    boolean existsByIdentificadorAcessoIgnoreCase(String identificadorAcesso);
    boolean existsByIdentificadorAcessoIgnoreCaseAndIdNot(String identificadorAcesso, Long id);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByMatriculaIgnoreCase(String matricula);
    boolean existsByMatriculaIgnoreCaseAndIdNot(String matricula, Long id);
    List<Coordenador> findByStatus(StatusCoordenador status);
}
