package br.com.portalincluir.model;

import br.com.portalincluir.enums.TipoConteudoAcademico;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;

@Entity
@org.hibernate.envers.Audited
@Table(name = "conteudos_academicos", indexes = @Index(name = "idx_conteudo_professor", columnList = "professor_id"))
@Getter @Setter
public class ConteudoAcademico {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private long versao;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    @org.hibernate.envers.Audited(targetAuditMode = org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED,
            targetNotFoundAction = org.hibernate.envers.RelationTargetNotFoundAction.IGNORE)
    private Professor professor;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudante_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Estudante estudante;
    @Column(nullable = false, length = 150)
    private String titulo;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private TipoConteudoAcademico tipo;
    @Column(nullable = false, length = 150)
    private String componenteCurricular;
    @Column(nullable = false, length = 5000)
    private String descricao;
    @Column(length = 5000)
    private String orientacoesAdaptacao;
    @Column(length = 2000)
    private String link;
    @Column(length = 180)
    private String arquivoNome;
    private String arquivoTipo;
    private Long arquivoTamanho;
    private long revisaoArquivo;
    @Column(nullable = false)
    private LocalDateTime dataCriacao;
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;
}
