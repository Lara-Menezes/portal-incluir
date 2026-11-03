package br.com.portalincluir.repository;
import br.com.portalincluir.model.SolicitacaoPea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SolicitacaoPeaRepository extends JpaRepository<SolicitacaoPea,Long> {
    Optional<SolicitacaoPea> findBySolicitanteIdAndChaveIdempotencia(Long usuarioId, String chave);
    List<SolicitacaoPea> findByEstudanteIdAndSolicitanteIdOrderByCriadaEmDesc(Long estudanteId, Long solicitanteId);
}
