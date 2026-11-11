package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.OcorrenciaRequest;
import br.com.portalincluir.dto.response.OcorrenciaResponse;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class OcorrenciaService {
    private final OcorrenciaRepository ocorrencias;
    private final ArquivoOcorrenciaRepository arquivos;
    private final UsuarioRepository usuarios;
    private final VinculoEstudanteProfissionalRepository vinculos;
    private final FluxoEstudanteRepository fluxos;
    private final NotificacaoFluxoRepository notificacoes;
    private final PermissaoService acesso;
    private final EscopoEstudanteService escopo;
    private final PoliticaHistoricoService politica;
    private final HttpServletRequest request;

    private Usuario leitor(boolean relatorio) {
        var u = acesso.exigir(request, "estudantes.consultar");
        if (u.getPerfil() == PerfilUsuario.EQUIPE_MULTIDISCIPLINAR || EscopoEstudanteService.gestao(u)) acesso.exigir(request, "suporte.consultar");
        if (relatorio) {
            if (u.getPerfil() == PerfilUsuario.PROFESSOR) throw erro(HttpStatus.FORBIDDEN, "Relatórios disponíveis para a equipe e coordenação.");
            acesso.exigir(request, "relatorios");
        }
        return u;
    }
    public OcorrenciaResponse criar(OcorrenciaRequest r, MultipartFile file) {
        var u = acesso.exigir(request, "suporte.registrar");
        acesso.exigir(request, "estudantes.consultar");
        if (u.getPerfil() != PerfilUsuario.EQUIPE_MULTIDISCIPLINAR) throw erro(HttpStatus.FORBIDDEN, "O registro é realizado pela equipe multidisciplinar.");
        var e = politica.bloquear(r.estudanteId());
        politica.exigirDisponivel(e); politica.exigirEditavel(e); escopo.exigir(e.getId());
        if (!e.isAtivo()) throw erro(HttpStatus.CONFLICT, "Selecione um estudante ativo.");
        if (r.ocorridaEm().isAfter(politica.agora().plusMinutes(5))) throw erro(HttpStatus.BAD_REQUEST, "A data da ocorrência não pode estar no futuro.");
        var arquivo = file == null ? null : ValidadorArquivoAcademico.validar(file);
        var o = new Ocorrencia(); o.setEstudante(e); o.setAutor(u); o.setAutorNome(u.getEquipeMultidisciplinar().getNome());
        o.setDescricao(r.descricao().strip()); o.setOcorridaEm(r.ocorridaEm()); o.setRegistradaEm(politica.agora());
        if (arquivo != null) { o.setArquivoNome(arquivo.nome()); o.setArquivoTipo(arquivo.tipo()); o.setArquivoTamanho((long) arquivo.dados().length); }
        ocorrencias.saveAndFlush(o);
        if (arquivo != null) { var a = new ArquivoOcorrencia(); a.setOcorrencia(o); a.setDados(arquivo.dados()); arquivos.save(a); }
        avisar(o);
        return resposta(o);
    }
    @Transactional(readOnly = true)
    public List<OcorrenciaResponse> listar(Long estudanteId, Long autorId, String mes, boolean relatorio) {
        var u = leitor(relatorio);
        LocalDateTime inicio = null, fim = null;
        if (mes != null && !mes.isBlank()) {
            try { var periodo = YearMonth.parse(mes); inicio = periodo.atDay(1).atStartOfDay(); fim = periodo.plusMonths(1).atDay(1).atStartOfDay(); }
            catch (DateTimeException ex) { throw erro(HttpStatus.BAD_REQUEST, "Informe o mês no formato AAAA-MM."); }
        }
        if (estudanteId != null && u.getPerfil() != PerfilUsuario.EQUIPE_MULTIDISCIPLINAR) escopo.exigir(estudanteId);
        List<Ocorrencia> lista;
        if (u.getPerfil() == PerfilUsuario.PROFESSOR) {
            var ids = escopo.idsPermitidos().orElse(Set.of());
            lista = ids.isEmpty() ? List.of() : ocorrencias.consultarVinculados(ids, estudanteId, inicio, fim);
        } else {
            // O profissional consulta e relata somente os registros que ele próprio fez.
            Long autor = u.getPerfil() == PerfilUsuario.EQUIPE_MULTIDISCIPLINAR ? u.getId() : autorId;
            lista = ocorrencias.consultar(autor, estudanteId, inicio, fim);
        }
        return lista.stream().filter(o -> politica.disponivel(o.getEstudante())).map(this::resposta).toList();
    }
    private Ocorrencia consultarPermitida(Long id) {
        var u = leitor(false);
        var o = ocorrencias.findById(id).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Ocorrência não encontrada."));
        if (u.getPerfil() == PerfilUsuario.EQUIPE_MULTIDISCIPLINAR) {
            if (!o.getAutor().getId().equals(u.getId())) throw erro(HttpStatus.NOT_FOUND, "Ocorrência não encontrada.");
        } else escopo.exigir(o.getEstudante().getId());
        politica.exigirDisponivel(o.getEstudante()); return o;
    }
    @Transactional(readOnly = true) public OcorrenciaResponse consultar(Long id) { return resposta(consultarPermitida(id)); }
    public record Arquivo(String nome, String tipo, byte[] dados) {}
    @Transactional(readOnly = true) public Arquivo arquivo(Long id) {
        var o = consultarPermitida(id);
        var a = arquivos.findById(id).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Esta ocorrência não possui anexo."));
        return new Arquivo(o.getArquivoNome(), o.getArquivoTipo(), a.getDados());
    }
    private void avisar(Ocorrencia o) {
        var professores = new HashSet<Long>();
        for (var v : vinculos.findByEstudanteIdOrderByAtivoDescDataInicioDesc(o.getEstudante().getId()))
            if (v.isAtivo() && v.getProfessor() != null) professores.add(v.getProfessor().getId());
        var atribuidos = new HashSet<Long>();
        fluxos.findById(o.getEstudante().getId()).ifPresent(f -> {
            for (var u : Arrays.asList(f.getAnamneseResponsavel(), f.getPlanoResponsavel(), f.getPeiResponsavel()))
                if (u != null && u.getPerfil() == PerfilUsuario.PROFESSOR) atribuidos.add(u.getId());
        });
        for (var u : usuarios.findAll()) {
            if (!PermissaoService.ativo(u) || !PermissaoService.efetivas(u).contains("estudantes.consultar")) continue;
            boolean coordenador = u.getPerfil() == PerfilUsuario.COORDENADOR && PermissaoService.efetivas(u).contains("suporte.consultar");
            boolean professor = u.getPerfil() == PerfilUsuario.PROFESSOR && (professores.contains(u.getProfessor().getId()) || atribuidos.contains(u.getId()));
            if (!coordenador && !professor) continue;
            var n = new NotificacaoFluxo(); n.setDestinatario(u); n.setEstudante(o.getEstudante()); n.setRemetente(o.getAutorNome());
            n.setEtapa("OCORRENCIA"); n.setTipoEvento("OCORRENCIA_REGISTRADA"); n.setOcorrenciaId(o.getId());
            n.setMensagem("Nova ocorrência registrada pela equipe multidisciplinar. Abra o registro para consultar os detalhes.");
            n.setCriadaEm(politica.agora()); notificacoes.save(n);
        }
    }
    private OcorrenciaResponse resposta(Ocorrencia o) {
        return new OcorrenciaResponse(o.getId(), o.getEstudante().getId(), o.getEstudante().getNome(), o.getEstudante().getMatricula(),
                o.getAutor().getId(), o.getAutorNome(), o.getDescricao(), o.getOcorridaEm(), o.getRegistradaEm(), o.getArquivoNome(), o.getArquivoTipo(), o.getArquivoTamanho());
    }
    private static ResponseStatusException erro(HttpStatus status, String mensagem) { return new ResponseStatusException(status, mensagem); }
}
