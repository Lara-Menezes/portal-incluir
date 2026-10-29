package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanoAcaoRequest {

    @NotNull
    private Long estudanteId;

    @NotNull
    private Long coordenadorResponsavelId;

    @Size(max = 20)
    private String periodoLetivo;

    private String nomeResponsavel;

    private String grauParentesco;

    private String telefoneResponsavel;

    @Email
    private String emailResponsavel;

    @NotNull
    private Boolean possuiComorbidade;

    private String comorbidades;

    @NotNull
    private Boolean usaMedicacaoContinua;

    private String medicacoes;

    @NotNull
    private Boolean frequentaServicoApoio;

    private String servicosApoio;

    @NotNull
    private Boolean possuiDocumentosComprobatorios;

    private String documentosComprobatorios;

    @Size(max = 5000)
    private String necessidadesEspecificas;

    @Size(max = 5000)
    private String consideracoes;

    @Size(max = 5000)
    private String avancosDificuldadesPotencialidades;

    @Size(max = 5000)
    private String principaisDemandas;

    @Size(max = 5000)
    private String propostasIntervencao;

}