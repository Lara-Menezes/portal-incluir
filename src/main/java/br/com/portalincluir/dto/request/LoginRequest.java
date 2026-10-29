package br.com.portalincluir.dto.request;
import jakarta.validation.constraints.*;
public record LoginRequest(@NotBlank @Size(max=150) String email, @NotBlank String senha) {}
