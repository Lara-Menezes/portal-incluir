package br.com.portalincluir.repository;
import br.com.portalincluir.model.NotificacaoFluxo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface NotificacaoFluxoRepository extends JpaRepository<NotificacaoFluxo, Long> {
    List<NotificacaoFluxo> findByDestinatarioIdOrderByCriadaEmDescIdDesc(Long usuarioId);
    Optional<NotificacaoFluxo> findByIdAndDestinatarioId(Long id, Long usuarioId);
}
