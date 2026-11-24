package br.com.portalincluir.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import java.util.List;

public record RaiRequest(@NotNull @Valid Conteudo dados, Long versao) {
    public record Conteudo(
        @NotNull @Size(max=150) String campus,
        @NotNull @Size(max=150) String componenteCurricular,
        @NotNull @Size(max=150) String professor,
        @NotNull @Pattern(regexp="20[0-9]{2}|2100") String anoLetivo,
        @NotNull @Size(max=150) String periodoLetivo,
        @NotNull @Pattern(regexp="BIMESTRAL|SEMESTRAL|ANUAL") String periodicidade,
        @NotNull @Pattern(regexp="[1-4]") String numeroPeriodo,
        @NotNull @Size(max=5000) String deficiencia,
        @NotNull @Size(max=5000) String acompanhamento,
        @NotNull @Size(max=5000) String comunicacao,
        @NotNull @Size(max=5000) String aprendizagem,
        @NotNull @Size(max=5000) String saude,
        @NotNull @Size(max=5000) String competencias,
        @NotNull @Size(max=5000) String objetivosGerais,
        @NotNull @Size(max=5000) String objetivosEspecificos,
        @NotNull @Size(max=5000) String comentarios,
        @NotNull @Size(max=5000) String avaliacao,
        @NotNull @Size(max=5000) String encaminhamentos,
        @NotNull @Size(max=5000) String consideracoesGerais,
        @NotNull @Size(max=150) String local,
        @NotNull @Pattern(regexp="|[0-9]{4}-[0-9]{2}-[0-9]{2}") String data,
        @NotNull @Size(max=9) Map<@Pattern(regexp="atencao|processamento|comandos|curiosidade|assiduidade|interacao|participacao|grupo|perguntas") String,
             @NotNull @Pattern(regexp="Sempre|Às vezes|Raramente|Nunca") String> habilidades,
        @NotNull @Size(max=6) List<@NotNull @Pattern(regexp="Conteúdos|Objetivos|Métodos|Recursos|Temporalidade|Avaliação") String> adequacoes
    ) {}
}
