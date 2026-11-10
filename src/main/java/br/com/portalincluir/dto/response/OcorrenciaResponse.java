package br.com.portalincluir.dto.response;
import java.time.LocalDateTime;
public record OcorrenciaResponse(Long id, Long estudanteId, String estudanteNome, String estudanteMatricula,
        Long autorId, String autorNome, String descricao, LocalDateTime ocorridaEm, LocalDateTime registradaEm,
        String arquivoNome, String arquivoTipo, Long arquivoTamanho) {}
