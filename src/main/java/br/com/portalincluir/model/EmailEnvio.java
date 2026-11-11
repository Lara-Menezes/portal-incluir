package br.com.portalincluir.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@org.hibernate.envers.Audited
@Table(name = "emails_enviados")
@Getter
@Setter
public class EmailEnvio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estudante_id", nullable = false)
    private Long estudanteId;

    @Column(name = "remetente_id", nullable = false)
    private Long remetenteId;

    @Column(name = "remetente_nome", nullable = false, length = 150)
    private String remetenteNome;

    @Column(name = "remetente_email", nullable = false, length = 150)
    private String remetenteEmail;

    @Column(name = "destinatario_email", nullable = false, length = 150)
    private String destinatarioEmail;

    @Column(name = "enviado_em", nullable = false)
    private Instant enviadoEm;
}
