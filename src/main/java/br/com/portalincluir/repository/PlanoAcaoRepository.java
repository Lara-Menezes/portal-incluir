package br.com.portalincluir.repository;

import br.com.portalincluir.enums.StatusPlanoAcao;
import br.com.portalincluir.model.PlanoAcao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanoAcaoRepository extends JpaRepository<PlanoAcao, Long> {

    List<PlanoAcao> findByStatus(StatusPlanoAcao status);

}