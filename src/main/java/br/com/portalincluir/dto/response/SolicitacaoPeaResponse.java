package br.com.portalincluir.dto.response;
import java.time.LocalDateTime;
import java.util.List;
public record SolicitacaoPeaResponse(
    Long id, long versao, EstudanteMinimo estudante, Long solicitanteUsuarioId, String solicitanteNome,
    String contexto, String orientacoes, String modalidade, String setor, LocalDateTime criadaEm,
    LocalDateTime canceladaEm, String motivoCancelamento, String situacao, List<Destinatario> destinatarios
) {
    public record EstudanteMinimo(Long id, String nome, String matricula, String curso) {}
    public record Destinatario(Long id, Long professorId, String professorNome, String setorNoEnvio,
                              String situacao, LocalDateTime respondidaEm, LocalDateTime canceladaEm, Long peaId) {}
}
