package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.RaiRequest;
import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service @RequiredArgsConstructor
public class RelatorioRaiService {
    private final RelatorioRaiRepository relatorios;
    private final EstudanteRepository estudantes;
    private final PermissaoService permissoes;
    private final EscopoEstudanteService escopo;
    private final PoliticaHistoricoService historico;
    private final HttpServletRequest request;
    private final ObjectMapper json;
    public record Resposta(Long id, Long estudanteId, long versao, RaiRequest.Conteudo dados, LocalDateTime dataAtualizacao, LocalDateTime concluidoEm) {}

    private void autorizar(Long estudanteId, boolean escrita) {
        permissoes.exigir(request, "documentos");
        permissoes.exigir(request, "sensivel.consultar");
        escopo.exigir(estudanteId);
        escopo.exigirGestao();
        if (escrita) { escopo.exigirGestao(); permissoes.exigir(request, "pei.editar"); }
    }
    private Estudante estudante(Long id) {
        var e = estudantes.findById(id).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Estudante não encontrado."));
        historico.exigirDisponivel(e); return e;
    }
    @Transactional(readOnly=true)
    public List<Resposta> listar(Long estudanteId) {
        autorizar(estudanteId, false); estudante(estudanteId);
        return relatorios.findByEstudanteIdOrderByDataAtualizacaoDescIdDesc(estudanteId).stream().map(this::resposta).toList();
    }
    @Transactional
    public Resposta salvar(Long estudanteId, Long id, RaiRequest r) {
        autorizar(estudanteId, true);
        var e = historico.bloquear(estudanteId); historico.exigirDisponivel(e);
        if (!e.isAtivo() || e.isConcluido()) throw erro(HttpStatus.CONFLICT, "Estudante indisponível para edição.");
        var dados = r.dados();
        boolean tecnico = java.text.Normalizer.normalize(e.getCurso(), java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).contains("tecnico");
        if (tecnico && dados.periodicidade().equals("SEMESTRAL") || !tecnico && dados.periodicidade().equals("BIMESTRAL")
            || dados.periodicidade().equals("SEMESTRAL") && Integer.parseInt(dados.numeroPeriodo()) > 2) throw erro(HttpStatus.BAD_REQUEST, "Periodicidade incompatível com o curso.");
        if (!dados.data().isBlank()) {
            try { LocalDate.parse(dados.data()); } catch (java.time.DateTimeException ex) { throw erro(HttpStatus.BAD_REQUEST, "Data inválida."); }
        }
        RelatorioRai entidade;
        if (id == null) {
            if (r.versao() != null) throw erro(HttpStatus.BAD_REQUEST, "Novo relatório não deve informar versão.");
            entidade = new RelatorioRai(); entidade.setEstudante(e);
        } else {
            entidade = relatorios.findById(id).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "RAI não encontrado."));
            if (!entidade.getEstudante().getId().equals(estudanteId)) throw erro(HttpStatus.NOT_FOUND, "RAI não pertence ao estudante.");
            if (entidade.getConcluidoEm() != null) throw erro(HttpStatus.CONFLICT, "Relatório concluído. Gere um novo RAI para preservar o registro anterior.");
            if (r.versao() == null || r.versao() != entidade.getVersao()) throw erro(HttpStatus.CONFLICT, "RAI alterado por outro usuário. Consulte novamente antes de editar.");
        }
        // Student lock also serializes duplicate creation/retries for the same academic scope.
        for (var existente : relatorios.findByEstudanteIdOrderByDataAtualizacaoDescIdDesc(estudanteId)) {
            var d = resposta(existente).dados();
            if (existente.getConcluidoEm() == null && !Objects.equals(existente.getId(), id) && d.anoLetivo().equals(dados.anoLetivo()) && d.periodicidade().equals(dados.periodicidade())
                && (dados.periodicidade().equals("ANUAL") || d.numeroPeriodo().equals(dados.numeroPeriodo()))
                && d.componenteCurricular().strip().equalsIgnoreCase(dados.componenteCurricular().strip())) throw erro(HttpStatus.CONFLICT, "Já existe RAI para este período e componente. Edite o registro existente.");
        }
        try { entidade.setConteudo(json.writeValueAsString(dados)); } catch (JsonProcessingException ex) { throw new IllegalStateException(ex); }
        entidade.setDataAtualizacao(historico.agora());
        return resposta(relatorios.saveAndFlush(entidade));
    }
    public RaiRequest.Conteudo dados(RelatorioRai e) { return resposta(e).dados(); }
    @Transactional(readOnly=true)
    public RelatorioRai paraExportar(Long estudanteId, Long id) {
        autorizar(estudanteId, false); estudante(estudanteId);
        var r = relatorios.findById(id).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "RAI não encontrado."));
        if (!r.getEstudante().getId().equals(estudanteId)) throw erro(HttpStatus.NOT_FOUND, "RAI não encontrado para este estudante.");
        return r;
    }
    @Transactional
    public Resposta concluir(Long estudanteId, Long id, Long versao) {
        autorizar(estudanteId, true);
        var e = historico.bloquear(estudanteId); historico.exigirDisponivel(e);
        if (!e.isAtivo() || e.isConcluido()) throw erro(HttpStatus.CONFLICT, "Estudante indisponível para edição.");
        var r = paraExportar(estudanteId, id);
        if (r.getConcluidoEm() != null) return resposta(r);
        if (versao == null || versao != r.getVersao()) throw erro(HttpStatus.CONFLICT, "RAI alterado. Consulte a versão atual antes de concluir.");
        var d = dados(r);
        if (d.habilidades().size() != 9 || d.competencias().isBlank() || d.objetivosGerais().isBlank() || d.objetivosEspecificos().isBlank() || d.professor().isBlank() || d.data().isBlank()) throw erro(HttpStatus.BAD_REQUEST, "Revise as nove habilidades, competências, objetivos, docente e data antes de concluir o período.");
        r.setConcluidoEm(historico.agora()); r.setDataAtualizacao(historico.agora());
        return resposta(relatorios.saveAndFlush(r));
    }
    private Resposta resposta(RelatorioRai e) {
        try { return new Resposta(e.getId(), e.getEstudante().getId(), e.getVersao(), json.readValue(e.getConteudo(), RaiRequest.Conteudo.class), e.getDataAtualizacao(), e.getConcluidoEm()); }
        catch (JsonProcessingException ex) { throw new IllegalStateException("Conteúdo do RAI inválido", ex); }
    }
    private static ResponseStatusException erro(HttpStatus status, String mensagem) { return new ResponseStatusException(status, mensagem); }
}
