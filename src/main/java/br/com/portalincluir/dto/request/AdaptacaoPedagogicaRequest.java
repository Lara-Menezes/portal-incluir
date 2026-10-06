package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdaptacaoPedagogicaRequest {

    @NotNull
    private Long estudanteId;

    @NotNull
    private Long professorId;

    @NotBlank
    @Size(max = 150)
    private String componenteCurricular;

    @NotBlank
    @Size(max = 20)
    private String semestreAno;

    @NotBlank
    @Size(max = 5000)
    private String objetivoGeral;

    @NotBlank
    @Size(max = 5000)
    private String objetivosEspecificos;

    @NotBlank
    @Size(max = 5000)
    private String conteudosProgramaticos;

    @NotBlank
    @Size(max = 5000)
    private String metodologia;

    @NotBlank
    @Size(max = 5000)
    private String avaliacao;

    @NotBlank
    @Size(max = 5000)
    private String bibliografiaBasica;

    @NotBlank
    @Size(max = 5000)
    private String bibliografiaComplementar;

    public Long getEstudanteId() {
        return estudanteId;
    }

    public void setEstudanteId(Long estudanteId) {
        this.estudanteId = estudanteId;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
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
}
