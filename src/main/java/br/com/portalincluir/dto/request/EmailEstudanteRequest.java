package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailEstudanteRequest(
        @NotBlank @Size(max = 200) String assunto,
        @NotBlank @Size(max = 10000) String mensagem) {
}
