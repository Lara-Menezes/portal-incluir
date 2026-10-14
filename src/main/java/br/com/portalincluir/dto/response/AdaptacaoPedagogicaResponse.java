package br.com.portalincluir.dto.response;

import br.com.portalincluir.model.AdaptacaoPedagogica;

import java.time.LocalDateTime;

public class AdaptacaoPedagogicaResponse {

    private Long id;

    private Long estudanteId;
    private String estudanteNome;
    private String estudanteMatricula;
    private String estudanteCurso;

    private Long professorId;
    private String professorNome;

    private String componenteCurricular;
    private String semestreAno;

    private String objetivoGeral;
    private String objetivosEspecificos;
    private String conteudosProgramaticos;
    private String metodologia;
    private String avaliacao;
    private String bibliografiaBasica;
    private String bibliografiaComplementar;

    private boolean arquivado;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public AdaptacaoPedagogicaResponse(AdaptacaoPedagogica adaptacao) {

        this.id = adaptacao.getId();

        this.estudanteId = adaptacao.getEstudante().getId();
        this.estudanteNome = adaptacao.getEstudante().getNome();
        this.estudanteMatricula = adaptacao.getEstudante().getMatricula();
        this.estudanteCurso = adaptacao.getEstudante().getCurso();

        this.professorId = adaptacao.getProfessor().getId();
        this.professorNome = adaptacao.getProfessor().getNome();

        this.componenteCurricular = adaptacao.getComponenteCurricular();
        this.semestreAno = adaptacao.getSemestreAno();

        this.objetivoGeral = adaptacao.getObjetivoGeral();
        this.objetivosEspecificos = adaptacao.getObjetivosEspecificos();
        this.conteudosProgramaticos = adaptacao.getConteudosProgramaticos();
        this.metodologia = adaptacao.getMetodologia();
        this.avaliacao = adaptacao.getAvaliacao();
        this.bibliografiaBasica = adaptacao.getBibliografiaBasica();
        this.bibliografiaComplementar = adaptacao.getBibliografiaComplementar();

        this.arquivado = adaptacao.isArquivado();

        this.dataCriacao = adaptacao.getDataCriacao();
        this.dataAtualizacao = adaptacao.getDataAtualizacao();
    }

    public Long getId() {
        return id;
    }

    public Long getEstudanteId() {
        return estudanteId;
    }

    public String getEstudanteNome() {
        return estudanteNome;
    }

    public String getEstudanteMatricula() {
        return estudanteMatricula;
    }

    public String getEstudanteCurso() {
        return estudanteCurso;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public String getProfessorNome() {
        return professorNome;
    }

    public String getComponenteCurricular() {
        return componenteCurricular;
    }

    public String getSemestreAno() {
        return semestreAno;
    }

    public String getObjetivoGeral() {
        return objetivoGeral;
    }

    public String getObjetivosEspecificos() {
        return objetivosEspecificos;
    }

    public String getConteudosProgramaticos() {
        return conteudosProgramaticos;
    }

    public String getMetodologia() {
        return metodologia;
    }

    public String getAvaliacao() {
        return avaliacao;
    }

    public String getBibliografiaBasica() {
        return bibliografiaBasica;
    }

    public String getBibliografiaComplementar() {
        return bibliografiaComplementar;
    }

    public boolean isArquivado() {
        return arquivado;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
}
