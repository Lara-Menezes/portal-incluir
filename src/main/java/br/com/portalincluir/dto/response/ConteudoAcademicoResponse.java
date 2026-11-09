package br.com.portalincluir.dto.response;

import br.com.portalincluir.enums.TipoConteudoAcademico;
import br.com.portalincluir.model.ConteudoAcademico;
import java.time.LocalDateTime;

public record ConteudoAcademicoResponse(
        Long id, long versao, Long professorId, String professorNome,
        Long estudanteId, String estudanteNome, String estudanteMatricula,
        String titulo, TipoConteudoAcademico tipo, String componenteCurricular,
        String descricao, String orientacoesAdaptacao, String link,
        String arquivoNome, String arquivoTipo, Long arquivoTamanho,
        LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
    public ConteudoAcademicoResponse(ConteudoAcademico c) {
        this(c.getId(), c.getVersao(), c.getProfessor().getId(), c.getProfessor().getNome(),
                c.getEstudante() == null ? null : c.getEstudante().getId(),
                c.getEstudante() == null ? null : c.getEstudante().getNome(),
                c.getEstudante() == null ? null : c.getEstudante().getMatricula(),
                c.getTitulo(), c.getTipo(), c.getComponenteCurricular(), c.getDescricao(),
                c.getOrientacoesAdaptacao(), c.getLink(), c.getArquivoNome(), c.getArquivoTipo(),
                c.getArquivoTamanho(), c.getDataCriacao(), c.getDataAtualizacao());
    }
}
