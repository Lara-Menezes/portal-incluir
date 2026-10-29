package br.com.portalincluir.model;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Audited
@Table(name = "plano_educacional")
public class PlanoEducacional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estudante_id", nullable = false)
    private Estudante estudante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "coordenador_id", nullable = false)
    private Coordenador coordenador;

    @OneToMany(mappedBy = "planoEducacional")
    private List<AdaptacaoPedagogica> adaptacoesPedagogicas = new ArrayList<>();

    @Column(nullable = false, length = 20)
    private String semestreAno;

    @Column(nullable = false, length = 5000)
    private String historico;

    @Column(nullable = false, length = 5000)
    private String necessidadesEspecificas;

    @Column(nullable = false, length = 5000)
    private String conhecimentosHabilidadesInteresses;

    @Column(nullable = false, length = 5000)
    private String dificuldadesApresentadas;

    @Column(nullable = false, length = 5000)
    private String propostasAdaptacoesCurriculares;

    @Column(nullable = false)
    private boolean arquivado = false;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

}