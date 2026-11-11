package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdaptacaoPedagogicaRequest extends ConteudoPeaRequest {

    @NotNull
    private Long estudanteId;

    @NotNull
    private Long professorId;

    private Long planoEducacionalId;

}
