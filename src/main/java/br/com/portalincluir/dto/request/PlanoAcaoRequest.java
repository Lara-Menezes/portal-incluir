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

}