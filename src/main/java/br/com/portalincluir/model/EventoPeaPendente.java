package br.com.portalincluir.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import java.time.LocalDateTime;

/** Evento preservado sem inventar uma coordenação responsável. */
@Entity @Getter @Setter @Audited
@Table(name="eventos_pea_pendentes")
public class EventoPeaPendente {
    @Version private long versao;
    private LocalDateTime encaminhadoEm;
    @ManyToOne @JoinColumn(name="encaminhado_por_id") private Usuario encaminhadoPor;
    @ManyToOne @JoinColumn(name="destinatario_id") private Usuario destinatario;
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="estudante_id", nullable=false) private Estudante estudante;
    @ManyToOne(optional=false) @JoinColumn(name="autor_id", nullable=false) private Usuario autor;
    @Column(nullable=false, unique=true) private Long peaId;
    @Column(nullable=false, length=40) private String tipoEvento = "PEA_ESPONTANEO_CRIADO";
    @Column(nullable=false) private LocalDateTime criadoEm;
    @Column(nullable=false, length=300) private String motivo = "Coordenação responsável não definida de forma inequívoca no cadastro do estudante.";
}
