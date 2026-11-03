package br.com.portalincluir.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import java.time.LocalDateTime;

@Entity @Getter @Setter @Audited
@Table(name="solicitacoes_pea", uniqueConstraints=@UniqueConstraint(columnNames={"solicitante_id", "chave_idempotencia"}),
       indexes=@Index(name="idx_solicitacao_pea_estudante", columnList="estudante_id,criada_em"))
public class SolicitacaoPea {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Version private long versao;
    @Column(nullable=false) private long revisaoAndamento;
    @ManyToOne(optional=false) @JoinColumn(name="estudante_id", nullable=false) private Estudante estudante;
    @ManyToOne(optional=false) @JoinColumn(name="solicitante_id", nullable=false) private Usuario solicitante;
    @Column(nullable=false, length=150) private String contexto;
    @Column(length=2000) private String orientacoes;
    @Column(nullable=false, length=20) private String modalidade;
    @Column(length=120) private String setor;
    @Column(name="criada_em", nullable=false) private LocalDateTime criadaEm;
    @Column(name="chave_idempotencia", nullable=false, length=100) private String chaveIdempotencia;
    @Column(nullable=false, length=64) private String hashRequisicao;
    private LocalDateTime canceladaEm;
    @Column(length=1000) private String motivoCancelamento;
}
