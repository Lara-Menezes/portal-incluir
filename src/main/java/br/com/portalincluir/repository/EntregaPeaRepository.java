package br.com.portalincluir.repository;
import br.com.portalincluir.model.EntregaPea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface EntregaPeaRepository extends JpaRepository<EntregaPea,Long> {
    Optional<EntregaPea> findByDestinatarioId(Long destinatarioId);
    boolean existsByPeaId(Long peaId);
}
