package br.com.portalincluir.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RecuperarSenhaRequest {
    @NotBlank @Email @Size(max = 150) private String email;
}
