package br.com.portalincluir.repository;
import br.com.portalincluir.model.RelatorioRai;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RelatorioRaiRepository extends JpaRepository<RelatorioRai, Long> {
    List<RelatorioRai> findByEstudanteIdOrderByDataAtualizacaoDescIdDesc(Long estudanteId);
}
