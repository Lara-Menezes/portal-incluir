package br.com.portalincluir.dto.request;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
public record OcorrenciaRequest(@NotNull @Positive Long estudanteId,
        @NotBlank @Size(max = 5000) String descricao, @NotNull LocalDateTime ocorridaEm) {}
