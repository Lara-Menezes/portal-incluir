package br.com.portalincluir.repository;
import br.com.portalincluir.model.SolicitacaoAcesso;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface SolicitacaoAcessoRepository extends JpaRepository<SolicitacaoAcesso,Long> {
  boolean existsByEmailPendente(String email);
  List<SolicitacaoAcesso> findAllByOrderByCriadaEmAscIdAsc();
  @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select s from SolicitacaoAcesso s where s.id=:id")
  Optional<SolicitacaoAcesso> bloquear(@Param("id") Long id);
}
