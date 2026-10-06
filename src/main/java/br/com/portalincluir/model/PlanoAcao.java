package br.com.portalincluir.model;

import br.com.portalincluir.enums.StatusPlanoAcao;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "planos_acao")
public class PlanoAcao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estudante_id", nullable = false)
    private Estudante estudante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "coordenador_responsavel_id", nullable = false)
    private Coordenador coordenadorResponsavel;

    @ManyToOne
    @JoinColumn(name = "coordenador_assinante_id")
    private Coordenador coordenadorAssinante;

    @Column(nullable = false, length = 20)
    private String periodoLetivo;

    @Enumerated(EnumType.STRING)
    private StatusPlanoAcao status = StatusPlanoAcao.RASCUNHO;

    private boolean arquivado = false;

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

    @Column(length = 5000)
    private String necessidadesEspecificas;

    @Column(length = 5000)
    private String consideracoes;

    @Column(length = 5000)
    private String avancosDificuldadesPotencialidades;

    @Column(length = 5000)
    private String principaisDemandas;

    @Column(length = 5000)
    private String propostasIntervencao;


    // getters e setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estudante getEstudante() {return estudante;}

    public void setEstudante(Estudante estudante) {this.estudante = estudante;}

    public Coordenador getCoordenadorResponsavel() {return coordenadorResponsavel;}

    public void setCoordenadorResponsavel(Coordenador coordenadorResponsavel) {this.coordenadorResponsavel = coordenadorResponsavel;}

    public Coordenador getCoordenadorAssinante() {return coordenadorAssinante;}

    public void setCoordenadorAssinante(Coordenador coordenadorAssinante) {this.coordenadorAssinante = coordenadorAssinante;}

    public String getPeriodoLetivo() {
        return periodoLetivo;
    }

    public void setPeriodoLetivo(String periodoLetivo) {
        this.periodoLetivo = periodoLetivo;
    }

    public StatusPlanoAcao getStatus() {
        return status;
    }

    public void setStatus(StatusPlanoAcao status) {
        this.status = status;
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

    public LocalDateTime getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(LocalDateTime dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
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