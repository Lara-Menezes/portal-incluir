package br.com.portalincluir.dto.response;

import br.com.portalincluir.enums.StatusPlanoAcao;
import br.com.portalincluir.model.PlanoAcao;

import java.time.LocalDateTime;

public class PlanoAcaoResponse {

    private Long id;

    private String periodoLetivo;

    private StatusPlanoAcao status;

    private boolean arquivado;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataAssinatura;

    private String nomeResponsavel;
    private String grauParentesco;
    private String telefoneResponsavel;
    private String emailResponsavel;

    private Boolean possuiComorbidade;
    private String comorbidades;

    private Boolean usaMedicacaoContinua;
    private String medicacoes;

    private Boolean frequentaServicoApoio;
    private String servicosApoio;

    private Boolean possuiDocumentosComprobatorios;
    private String documentosComprobatorios;

    private String necessidadesEspecificas;
    private String consideracoes;
    private String avancosDificuldadesPotencialidades;
    private String principaisDemandas;
    private String propostasIntervencao;

    public PlanoAcaoResponse(PlanoAcao plano) {

        this.id = plano.getId();

        this.periodoLetivo = plano.getPeriodoLetivo();

        this.status = plano.getStatus();
        this.arquivado = plano.isArquivado();

        this.dataCriacao = plano.getDataCriacao();
        this.dataAtualizacao = plano.getDataAtualizacao();
        this.dataAssinatura = plano.getDataAssinatura();

        this.nomeResponsavel = plano.getNomeResponsavel();
        this.grauParentesco = plano.getGrauParentesco();
        this.telefoneResponsavel = plano.getTelefoneResponsavel();
        this.emailResponsavel = plano.getEmailResponsavel();

        this.possuiComorbidade = plano.getPossuiComorbidade();
        this.comorbidades = plano.getComorbidades();

        this.usaMedicacaoContinua = plano.getUsaMedicacaoContinua();
        this.medicacoes = plano.getMedicacoes();

        this.frequentaServicoApoio = plano.getFrequentaServicoApoio();
        this.servicosApoio = plano.getServicosApoio();

        this.possuiDocumentosComprobatorios =
                plano.getPossuiDocumentosComprobatorios();
        this.documentosComprobatorios =
                plano.getDocumentosComprobatorios();

        this.necessidadesEspecificas = plano.getNecessidadesEspecificas();
        this.consideracoes = plano.getConsideracoes();
        this.avancosDificuldadesPotencialidades =
                plano.getAvancosDificuldadesPotencialidades();
        this.principaisDemandas = plano.getPrincipaisDemandas();
        this.propostasIntervencao = plano.getPropostasIntervencao();
    }

    //Getters
    public Long getId() {
        return id;
    }

    public String getPeriodoLetivo() {
        return periodoLetivo;
    }

    public StatusPlanoAcao getStatus() {
        return status;
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

    public LocalDateTime getDataAssinatura() {
        return dataAssinatura;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public String getGrauParentesco() {
        return grauParentesco;
    }

    public String getTelefoneResponsavel() {
        return telefoneResponsavel;
    }

    public String getEmailResponsavel() {
        return emailResponsavel;
    }

    public Boolean getPossuiComorbidade() {
        return possuiComorbidade;
    }

    public String getComorbidades() {
        return comorbidades;
    }

    public Boolean getUsaMedicacaoContinua() {
        return usaMedicacaoContinua;
    }

    public String getMedicacoes() {
        return medicacoes;
    }

    public Boolean getFrequentaServicoApoio() {
        return frequentaServicoApoio;
    }

    public String getServicosApoio() {
        return servicosApoio;
    }

    public Boolean getPossuiDocumentosComprobatorios() {
        return possuiDocumentosComprobatorios;
    }

    public String getDocumentosComprobatorios() {
        return documentosComprobatorios;
    }

    public String getNecessidadesEspecificas() {
        return necessidadesEspecificas;
    }

    public String getConsideracoes() {
        return consideracoes;
    }

    public String getAvancosDificuldadesPotencialidades() {
        return avancosDificuldadesPotencialidades;
    }

    public String getPrincipaisDemandas() {
        return principaisDemandas;
    }

    public String getPropostasIntervencao() {
        return propostasIntervencao;
    }

}
