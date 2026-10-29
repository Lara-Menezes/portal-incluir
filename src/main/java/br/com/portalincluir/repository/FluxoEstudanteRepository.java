package br.com.portalincluir.repository;
import br.com.portalincluir.model.FluxoEstudante;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FluxoEstudanteRepository extends JpaRepository<FluxoEstudante, Long> {
 @org.springframework.data.jpa.repository.Query("select f.estudante.id from FluxoEstudante f where f.estudante.ativo=true and (f.anamneseResponsavel.id=:id or f.planoResponsavel.id=:id or f.peiResponsavel.id=:id)")
 java.util.Set<Long> estudantesAtribuidos(@org.springframework.data.repository.query.Param("id") Long id);
}
