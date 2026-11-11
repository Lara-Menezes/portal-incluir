package br.com.portalincluir.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionEntity;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/** Instala os detalhes depois da criacao das tabelas pelo Hibernate e antes do startup terminar. */
@Configuration
public class AuditoriaDetalhesConfig {
    @Bean
    SmartInitializingSingleton instalarDetalhesAuditoria(DataSource dataSource, EntityManagerFactory emf) {
        return () -> {
            verificarEntidadesAuditadas(emf);
            try (var connection = dataSource.getConnection()) {
                if (!"PostgreSQL".equals(connection.getMetaData().getDatabaseProductName())) return;
            } catch (java.sql.SQLException e) {
                throw new IllegalStateException("Nao foi possivel configurar a auditoria", e);
            }
            var script = new ResourceDatabasePopulator(new ClassPathResource("db/auditoria-detalhes.sql"));
            script.setSeparator("^^^");
            script.execute(dataSource);
        };
    }

    private void verificarEntidadesAuditadas(EntityManagerFactory emf) {
        try (var em = emf.createEntityManager()) {
            var leitor = AuditReaderFactory.get(em);
            var pendentes = emf.getMetamodel().getEntities().stream()
                .map(entidade -> entidade.getJavaType())
                .filter(tipo -> !tipo.isAnnotationPresent(RevisionEntity.class))
                .filter(tipo -> !leitor.isEntityClassAudited(tipo))
                .map(Class::getName).sorted().toList();
            if (!pendentes.isEmpty()) {
                throw new IllegalStateException("Entidades sem auditoria: " + pendentes
                    + ". Adicione @org.hibernate.envers.Audited antes de iniciar o projeto.");
            }
        }
    }
}
