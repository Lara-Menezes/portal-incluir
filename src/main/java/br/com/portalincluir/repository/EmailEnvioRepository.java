package br.com.portalincluir.repository;

import br.com.portalincluir.model.EmailEnvio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailEnvioRepository extends JpaRepository<EmailEnvio, Long> {
}
