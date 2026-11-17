package br.com.portalincluir.repository;
import br.com.portalincluir.model.DestinatarioSolicitacaoPea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface DestinatarioSolicitacaoPeaRepository extends JpaRepository<DestinatarioSolicitacaoPea,Long> {
    List<DestinatarioSolicitacaoPea> findBySolicitacaoIdOrderById(Long solicitacaoId);
    List<DestinatarioSolicitacaoPea> findByUsuarioIdOrderByIdDesc(Long usuarioId);
    Optional<DestinatarioSolicitacaoPea> findBySolicitacaoIdAndUsuarioId(Long solicitacaoId, Long usuarioId);
    boolean existsByChavePendente(String chave);
}
