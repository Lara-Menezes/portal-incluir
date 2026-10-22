package br.com.portalincluir.model;

import br.com.portalincluir.enums.StatusPlanoAcao;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@org.hibernate.envers.Audited
@Entity
@Table(name = "planos_acao")
public class PlanoAcao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false)
    @org.hibernate.annotations.ColumnDefault("0")
    private long versao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estudante_id", nullable = false)
    private Estudante estudante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "coordenador_responsavel_id", nullable = false)
    @org.hibernate.envers.Audited(targetAuditMode = org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED, targetNotFoundAction = org.hibernate.envers.RelationTargetNotFoundAction.IGNORE)
    private Coordenador coordenadorResponsavel;

    @ManyToOne
    @JoinColumn(name = "coordenador_assinante_id")
    @org.hibernate.envers.Audited(targetAuditMode = org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED, targetNotFoundAction = org.hibernate.envers.RelationTargetNotFoundAction.IGNORE)
    private Coordenador coordenadorAssinante;

    @Column(nullable = false, length = 20)
    private String periodoLetivo;

    @Enumerated(EnumType.STRING)
    private StatusPlanoAcao status = StatusPlanoAcao.RASCUNHO;

    private boolean arquivado = false;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataAssinatura;

    private String nomeResponsavel;
    private String grauParentesco;
    private String telefoneResponsavel;
    private String emailResponsavel;

    private Boolean possuiComorbidade;
    @Column(columnDefinition = "TEXT")
    private String comorbidades;

    private Boolean usaMedicacaoContinua;
    @Column(columnDefinition = "TEXT")
    private String medicacoes;

    private Boolean frequentaServicoApoio;
    @Column(columnDefinition = "TEXT")
    private String servicosApoio;

    private Boolean possuiDocumentosComprobatorios;
    @Column(columnDefinition = "TEXT")
    private String documentosComprobatorios;

    @Column(length = 5000)
    private String necessidadesEspecificas;

    @Column(length = 5000)
    private String consideracoes;

    @Column(length = 5000)
    private String avancosDificuldadesPotencialidades;

    @Column(length = 5000)
    private String principaisDemandas;

    @Column(length = 5000)
    private String propostasIntervencao;

}