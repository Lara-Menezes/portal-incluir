package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PlanoAcaoRequest {

    @NotBlank
    private String periodoLetivo;

    @NotBlank
    private String nomeResponsavel;

    @NotBlank
    private String grauParentesco;

    @NotBlank
    private String telefoneResponsavel;

    @NotBlank
    @Email
    private String emailResponsavel;

    @NotNull
    private Boolean possuiComorbidade;

    @NotBlank
    private String comorbidades;

    @NotNull
    private Boolean usaMedicacaoContinua;

    @NotBlank
    private String medicacoes;

    @NotNull
    private Boolean frequentaServicoApoio;

    @NotBlank
    private String servicosApoio;

    @NotNull
    private Boolean possuiDocumentosComprobatorios;

    @NotBlank
    private String documentosComprobatorios;

    @NotBlank
    @Size(max = 5000)
    private String necessidadesEspecificas;

    @NotBlank
    @Size(max = 5000)
    private String consideracoes;

    @NotBlank
    @Size(max = 5000)
    private String avancosDificuldadesPotencialidades;

    @NotBlank
    @Size(max = 5000)
    private String principaisDemandas;

    @NotBlank
    @Size(max = 5000)
    private String propostasIntervencao;

    // getters e setters


    public String getPeriodoLetivo() {
        return periodoLetivo;
    }

    public void setPeriodoLetivo(String periodoLetivo) {
        this.periodoLetivo = periodoLetivo;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public String getGrauParentesco() {
        return grauParentesco;
    }

    public void setGrauParentesco(String grauParentesco) {
        this.grauParentesco = grauParentesco;
    }

    public String getTelefoneResponsavel() {
        return telefoneResponsavel;
    }

    public void setTelefoneResponsavel(String telefoneResponsavel) {
        this.telefoneResponsavel = telefoneResponsavel;
    }

    public String getEmailResponsavel() {
        return emailResponsavel;
    }

    public void setEmailResponsavel(String emailResponsavel) {
        this.emailResponsavel = emailResponsavel;
    }

    public Boolean getPossuiComorbidade() {
        return possuiComorbidade;
    }

    public void setPossuiComorbidade(Boolean possuiComorbidade) {
        this.possuiComorbidade = possuiComorbidade;
    }

    public String getComorbidades() {
        return comorbidades;
    }

    public void setComorbidades(String comorbidades) {
        this.comorbidades = comorbidades;
    }

    public Boolean getUsaMedicacaoContinua() {
        return usaMedicacaoContinua;
    }

    public void setUsaMedicacaoContinua(Boolean usaMedicacaoContinua) {
        this.usaMedicacaoContinua = usaMedicacaoContinua;
    }

    public String getMedicacoes() {
        return medicacoes;
    }

    public void setMedicacoes(String medicacoes) {
        this.medicacoes = medicacoes;
    }

    public Boolean getFrequentaServicoApoio() {
        return frequentaServicoApoio;
    }

    public void setFrequentaServicoApoio(Boolean frequentaServicoApoio) {
        this.frequentaServicoApoio = frequentaServicoApoio;
    }

    public String getServicosApoio() {
        return servicosApoio;
    }

    public void setServicosApoio(String servicosApoio) {
        this.servicosApoio = servicosApoio;
    }

    public Boolean getPossuiDocumentosComprobatorios() {
        return possuiDocumentosComprobatorios;
    }

    public void setPossuiDocumentosComprobatorios(Boolean possuiDocumentosComprobatorios) {
        this.possuiDocumentosComprobatorios = possuiDocumentosComprobatorios;
    }

    public String getDocumentosComprobatorios() {
        return documentosComprobatorios;
    }

    public void setDocumentosComprobatorios(String documentosComprobatorios) {
        this.documentosComprobatorios = documentosComprobatorios;
    }

    public String getNecessidadesEspecificas() {
        return necessidadesEspecificas;
    }

    public void setNecessidadesEspecificas(String necessidadesEspecificas) {
        this.necessidadesEspecificas = necessidadesEspecificas;
    }

    public String getConsideracoes() {
        return consideracoes;
    }

    public void setConsideracoes(String consideracoes) {
        this.consideracoes = consideracoes;
    }

    public String getAvancosDificuldadesPotencialidades() {
        return avancosDificuldadesPotencialidades;
    }

    public void setAvancosDificuldadesPotencialidades(String avancosDificuldadesPotencialidades) {
        this.avancosDificuldadesPotencialidades = avancosDificuldadesPotencialidades;
    }

    public String getPrincipaisDemandas() {
        return principaisDemandas;
    }

    public void setPrincipaisDemandas(String principaisDemandas) {
        this.principaisDemandas = principaisDemandas;
    }

    public String getPropostasIntervencao() {
        return propostasIntervencao;
    }

    public void setPropostasIntervencao(String propostasIntervencao) {
        this.propostasIntervencao = propostasIntervencao;
    }
}