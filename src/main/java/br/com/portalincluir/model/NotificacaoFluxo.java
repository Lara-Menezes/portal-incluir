package br.com.portalincluir.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Entity @Getter @Setter @org.hibernate.envers.Audited
@Table(name = "notificacoes_fluxo", indexes = @Index(name = "idx_notificacao_destinatario_data", columnList = "destinatario_id,criada_em"))
public class NotificacaoFluxo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "destinatario_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Usuario destinatario;
    @ManyToOne(optional = false) @JoinColumn(name = "estudante_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Estudante estudante;
    @Column(nullable = false, length = 150) private String remetente;
    @Column(nullable = false, length = 20) private String etapa;
    private Long planoId;
    @Column(nullable = false, length = 2000) private String mensagem;
    @Column(name = "criada_em", nullable = false) private LocalDateTime criadaEm;
    @Column(nullable = false) private boolean lida;
}
