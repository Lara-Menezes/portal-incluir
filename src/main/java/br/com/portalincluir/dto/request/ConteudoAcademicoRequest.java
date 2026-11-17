package br.com.portalincluir.dto.request;

import br.com.portalincluir.enums.TipoConteudoAcademico;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ConteudoAcademicoRequest {
    @NotBlank @Size(max = 150)
    private String titulo;
    @NotNull
    private TipoConteudoAcademico tipo;
    @NotBlank @Size(max = 150)
    private String componenteCurricular;
    @NotBlank @Size(max = 5000)
    private String descricao;
    @Size(max = 5000)
    private String orientacoesAdaptacao;
    @Size(max = 2000)
    private String link;
    @Positive
    private Long estudanteId;
    @PositiveOrZero
    private Long versao;
}
