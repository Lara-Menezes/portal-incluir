package br.com.portalincluir.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "adaptacoes_pedagogicas")
public class AdaptacaoPedagogica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estudante_id", nullable = false)
    private Estudante estudante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

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

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public String getComponenteCurricular() {
        return componenteCurricular;
    }

    public void setComponenteCurricular(String componenteCurricular) {
        this.componenteCurricular = componenteCurricular;
    }

    public String getSemestreAno() {
        return semestreAno;
    }

    public void setSemestreAno(String semestreAno) {
        this.semestreAno = semestreAno;
    }

    public String getObjetivoGeral() {
        return objetivoGeral;
    }

    public void setObjetivoGeral(String objetivoGeral) {
        this.objetivoGeral = objetivoGeral;
    }

    public String getObjetivosEspecificos() {
        return objetivosEspecificos;
    }

    public void setObjetivosEspecificos(String objetivosEspecificos) {
        this.objetivosEspecificos = objetivosEspecificos;
    }

    public String getConteudosProgramaticos() {
        return conteudosProgramaticos;
    }

    public void setConteudosProgramaticos(String conteudosProgramaticos) {
        this.conteudosProgramaticos = conteudosProgramaticos;
    }

    public String getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(String metodologia) {
        this.metodologia = metodologia;
    }

    public String getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(String avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getBibliografiaBasica() {
        return bibliografiaBasica;
    }

    public void setBibliografiaBasica(String bibliografiaBasica) {
        this.bibliografiaBasica = bibliografiaBasica;
    }

    public String getBibliografiaComplementar() {
        return bibliografiaComplementar;
    }

    public void setBibliografiaComplementar(String bibliografiaComplementar) {
        this.bibliografiaComplementar = bibliografiaComplementar;
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
