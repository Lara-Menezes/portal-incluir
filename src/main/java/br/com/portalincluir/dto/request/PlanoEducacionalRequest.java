package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PlanoEducacionalRequest {

    @NotNull
    private Long estudanteId;

    @NotNull
    private Long coordenadorId;

    @NotBlank
    @Size(max = 20)
    private String semestreAno;

    @NotBlank
    @Size(max = 5000)
    private String historico;

    @NotBlank
    @Size(max = 5000)
    private String necessidadesEspecificas;

    @NotBlank
    @Size(max = 5000)
    private String conhecimentosHabilidadesInteresses;

    @NotBlank
    @Size(max = 5000)
    private String dificuldadesApresentadas;

    @NotBlank
    @Size(max = 5000)
    private String propostasAdaptacoesCurriculares;

    public Long getEstudanteId() {
        return estudanteId;
    }

    public void setEstudanteId(Long estudanteId) {
        this.estudanteId = estudanteId;
    }

    public Long getCoordenadorId() {
        return coordenadorId;
    }

    public void setCoordenadorId(Long coordenadorId) {
        this.coordenadorId = coordenadorId;
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
}