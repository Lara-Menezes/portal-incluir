package br.com.portalincluir.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import java.time.LocalDateTime;

@Entity @Getter @Setter @Audited
@Table(name="entregas_pea")
public class EntregaPea {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @OneToOne(optional=false) @JoinColumn(name="destinatario_id", nullable=false, unique=true) private DestinatarioSolicitacaoPea destinatario;
    @OneToOne(optional=false) @JoinColumn(name="pea_id", nullable=false, unique=true) private AdaptacaoPedagogica pea;
    @Column(nullable=false) private LocalDateTime entregueEm;
    @Column(nullable=false, length=100) private String chaveIdempotencia;
    @Column(nullable=false, length=64) private String hashRequisicao;
    @Column(nullable=false, length=60000, updatable=false) private String conteudoEntregue;
}
