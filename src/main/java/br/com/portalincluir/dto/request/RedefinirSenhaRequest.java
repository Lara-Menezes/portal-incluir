package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/** Sem toString para nao expor os segredos nos logs de requisicao. */
@Getter @Setter
public class RedefinirSenhaRequest {
    @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{43}") private String token;
    @NotBlank @Size(min = 6, max = 72) private String novaSenha;
}
