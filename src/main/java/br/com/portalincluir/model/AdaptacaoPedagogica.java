package br.com.portalincluir.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@org.hibernate.envers.Audited
@Entity
@Table(name = "adaptacoes_pedagogicas")
public class AdaptacaoPedagogica {

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
    @JoinColumn(name = "professor_id", nullable = false)
    @org.hibernate.envers.Audited(targetAuditMode = org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED, targetNotFoundAction = org.hibernate.envers.RelationTargetNotFoundAction.IGNORE)
    private Professor professor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_educacional_id")
    private PlanoEducacional planoEducacional;

    @Column(nullable = false, length = 150)
    private String componenteCurricular;

    @Column(nullable = false, length = 20)
    private String semestreAno;

    @Column(nullable = false, length = 5000)
    private String objetivoGeral;

    @Column(nullable = false, length = 5000)
    private String objetivosEspecificos;

    @Column(nullable = false, length = 5000)
    private String conteudosProgramaticos;

    @Column(nullable = false, length = 5000)
    private String metodologia;

    @Column(nullable = false, length = 5000)
    private String avaliacao;

    @Column(nullable = false, length = 5000)
    private String bibliografiaBasica;

    @Column(nullable = false, length = 5000)
    private String bibliografiaComplementar;

    @Column(nullable = false)
    private boolean arquivado = false;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

}
