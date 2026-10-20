package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdaptacaoPedagogicaRequest {

    @NotNull
    private Long estudanteId;

    @NotNull
    private Long professorId;

    private Long planoEducacionalId;

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

}
