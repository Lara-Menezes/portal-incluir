package br.com.portalincluir.dto.response;

import java.time.Instant;

public record EmailEnviadoResponse(String destinatario, Instant enviadoEm) {
}
