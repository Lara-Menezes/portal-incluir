package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

}