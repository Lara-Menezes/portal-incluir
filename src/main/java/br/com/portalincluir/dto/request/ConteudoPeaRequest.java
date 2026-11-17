package br.com.portalincluir.dto.request;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
/** Conteúdo de uma resposta: identidade vem da sessão e da solicitação. */
@Getter @Setter
public class ConteudoPeaRequest {
    @NotBlank @Size(max=150) private String componenteCurricular;
    @NotBlank @Size(max=20) private String semestreAno;
    @NotBlank @Size(max=5000) private String objetivoGeral;
    @NotBlank @Size(max=5000) private String objetivosEspecificos;
    @NotBlank @Size(max=5000) private String conteudosProgramaticos;
    @NotBlank @Size(max=5000) private String metodologia;
    @NotBlank @Size(max=5000) private String avaliacao;
    @NotBlank @Size(max=5000) private String bibliografiaBasica;
    @NotBlank @Size(max=5000) private String bibliografiaComplementar;
}
