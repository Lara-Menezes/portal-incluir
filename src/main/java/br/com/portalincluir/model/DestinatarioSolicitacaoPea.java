package br.com.portalincluir.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import java.time.LocalDateTime;

@Entity @Getter @Setter @Audited
@Table(name="destinatarios_solicitacao_pea", uniqueConstraints={
    @UniqueConstraint(columnNames={"solicitacao_id", "professor_id"}), @UniqueConstraint(columnNames="chave_pendente")},
    indexes=@Index(name="idx_destinatario_pea_conta", columnList="usuario_id,situacao"))
public class DestinatarioSolicitacaoPea {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Version private long versao;
    @ManyToOne(optional=false) @JoinColumn(name="solicitacao_id", nullable=false) private SolicitacaoPea solicitacao;
    @ManyToOne(optional=false) @JoinColumn(name="professor_id", nullable=false) private Professor professor;
    @ManyToOne(optional=false) @JoinColumn(name="usuario_id", nullable=false) private Usuario usuario;
    @Column(nullable=false, length=20) private String situacao = "PENDENTE";
    @Column(nullable=false, length=120) private String setorNoEnvio;
    @Column(name="chave_pendente", length=120) private String chavePendente;
    private LocalDateTime respondidaEm;
    private LocalDateTime canceladaEm;
}
