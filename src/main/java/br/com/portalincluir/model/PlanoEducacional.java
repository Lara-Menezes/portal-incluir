package br.com.portalincluir.model;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public void setEstudante(Estudante estudante) {
        this.estudante = estudante;
    }

    public Coordenador getCoordenador() {
        return coordenador;
    }

    public void setCoordenador(Coordenador coordenador) {
        this.coordenador = coordenador;
    }

    public List<AdaptacaoPedagogica> getAdaptacoesPedagogicas() {
        return adaptacoesPedagogicas;
    }

    public void setAdaptacoesPedagogicas(
            List<AdaptacaoPedagogica> adaptacoesPedagogicas) {
        this.adaptacoesPedagogicas = adaptacoesPedagogicas;
    }

    public String getSemestreAno() {
        return semestreAno;
    }

    public void setSemestreAno(String semestreAno) {
        this.semestreAno = semestreAno;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getNecessidadesEspecificas() {
        return necessidadesEspecificas;
    }

    public void setNecessidadesEspecificas(String necessidadesEspecificas) {
        this.necessidadesEspecificas = necessidadesEspecificas;
    }

    public String getConhecimentosHabilidadesInteresses() {
        return conhecimentosHabilidadesInteresses;
    }

    public void setConhecimentosHabilidadesInteresses(
            String conhecimentosHabilidadesInteresses) {
        this.conhecimentosHabilidadesInteresses =
                conhecimentosHabilidadesInteresses;
    }

    public String getDificuldadesApresentadas() {
        return dificuldadesApresentadas;
    }

    public void setDificuldadesApresentadas(String dificuldadesApresentadas) {
        this.dificuldadesApresentadas = dificuldadesApresentadas;
    }

    public String getPropostasAdaptacoesCurriculares() {
        return propostasAdaptacoesCurriculares;
    }

    public void setPropostasAdaptacoesCurriculares(
            String propostasAdaptacoesCurriculares) {
        this.propostasAdaptacoesCurriculares =
                propostasAdaptacoesCurriculares;
    }

    public boolean isArquivado() {
        return arquivado;
    }

    public void setArquivado(boolean arquivado) {
        this.arquivado = arquivado;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}