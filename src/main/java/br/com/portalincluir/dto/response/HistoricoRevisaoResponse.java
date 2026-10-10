package br.com.portalincluir.dto.response;

import java.time.Instant;

public record HistoricoRevisaoResponse<T>(Number revisao, Instant data, String tipo, T dados) {}
