package br.com.portalincluir.service;

import br.com.portalincluir.config.CatalogoProfissional;
import br.com.portalincluir.dto.request.*;
import br.com.portalincluir.dto.response.*;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class SolicitacaoPeaService {
    private final SolicitacaoPeaRepository solicitacoes;
    private final DestinatarioSolicitacaoPeaRepository destinatarios;
    private final EntregaPeaRepository entregas;
    private final UsuarioRepository usuarios;
    private final EstudanteRepository estudantes;
    private final AdaptacaoPedagogicaRepository adaptacoes;
    private final VinculoEstudanteProfissionalRepository vinculos;
    private final VinculoEstudanteService acompanhamento;
    private final PermissaoService acesso;
    private final PoliticaHistoricoService historico;
    private final NotificacaoPeaService notificacoes;
    private final HttpServletRequest request;
    private final ObjectMapper json;
    private final EntityManager em;

    public record Elegivel(Long professorId, String nome, String setor, String disciplina, Set<String> cursosAtuacao, boolean vinculado, boolean mesmoCurso) {}

    private Usuario coordenador() {
        var u = acesso.exigir(request, "pei.editar"); acesso.exigir(request, "estudantes.consultar");
        if (u.getPerfil() != PerfilUsuario.COORDENADOR) throw erro(HttpStatus.FORBIDDEN, "Somente coordenadores podem gerenciar solicitações de PEA.");
        return u;
    }

    private Usuario docente(boolean escrever) {
        var u = acesso.exigir(request, escrever ? "pedagogico.editar" : "pedagogico.consultar");
        if (u.getPerfil() != PerfilUsuario.PROFESSOR) throw erro(HttpStatus.FORBIDDEN, "Esta operação é destinada ao professor destinatário.");
        return u;
    }

    private Estudante estudante(Long id) {
        var e = estudantes.findById(id).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Estudante não encontrado."));
        historico.exigirDisponivel(e); return e;
    }

    private void exigirAtivo(Estudante e) {
        historico.exigirEditavel(e);
        if (!e.isAtivo()) throw erro(HttpStatus.CONFLICT, "O estudante deve estar ativo.");
    }

    private boolean elegivel(Usuario u) {
        return u.getPerfil() == PerfilUsuario.PROFESSOR && u.getProfessor() != null && PermissaoService.ativo(u)
                && PermissaoService.efetivas(u).containsAll(Set.of("pedagogico.consultar", "pedagogico.editar"));
    }

    @Transactional(readOnly=true)
    public List<Elegivel> elegiveis(Long estudanteId, String setor) {
        coordenador(); var e = estudante(estudanteId);
        if (!e.isAtivo() || e.isConcluido()) throw erro(HttpStatus.CONFLICT, "O estudante não está disponível para novas solicitações.");
        return usuarios.findAll().stream().filter(this::elegivel)
                .filter(u -> setor == null || normalizar(u.getProfessor().getSetor()).equals(normalizar(setor)))
                .map(u -> new Elegivel(u.getProfessor().getId(), u.getProfessor().getNome(), u.getProfessor().getSetor(),
                        u.getProfessor().getDisciplina(), Set.copyOf(u.getProfessor().getCursosAtuacao()),
                        vinculos.existeVinculoDocenteAtivo(e.getId(), u.getProfessor().getId()),
                        CatalogoProfissional.atuaNoCurso(u.getProfessor().getCursosAtuacao(), e.getCurso())))
                .sorted(Comparator.comparing(Elegivel::nome, String.CASE_INSENSITIVE_ORDER)).toList();
    }

    public SolicitacaoPeaResponse criar(Long estudanteId, SolicitacaoPeaRequest r, String chave) {
        var autor = coordenador(); chave = chave(chave);
        var e = historico.bloquear(estudanteId);
        List<Object> parametros = new ArrayList<>(List.of(estudanteId, r.modalidade(), normalizar(r.contexto()),
                Objects.toString(r.orientacoes(), "").strip(), normalizar(r.setor()), new TreeSet<>(r.professorIds() == null ? Set.of() : r.professorIds())));
        // A política integra o hash: reenvios antigos permanecem sem efeitos retroativos.
        String fingerprint = hash(serializar(List.of("VINCULO_AUTOMATICO_V1", parametros)));
        var parametrosLegados = new ArrayList<>(parametros);
        String fingerprintLegado = null;
        if (!r.criarVinculoAcompanhamento()) fingerprintLegado = hash(serializar(parametrosLegados));
        else if (r.motivoVinculo() != null && !r.motivoVinculo().isBlank()) {
            parametrosLegados.add(List.of("VINCULO_ACOMPANHAMENTO", r.motivoVinculo().strip()));
            fingerprintLegado = hash(serializar(parametrosLegados));
        }
        var anterior = solicitacoes.findBySolicitanteIdAndChaveIdempotencia(autor.getId(), chave);
        if (anterior.isPresent()) {
            String original = anterior.get().getHashRequisicao();
            if (!original.equals(fingerprint) && !original.equals(fingerprintLegado))
                throw erro(HttpStatus.CONFLICT, "Chave de idempotência já utilizada com outros dados.");
            historico.exigirDisponivel(e); return resposta(anterior.get(), autor);
        }
        acesso.exigir(request, "estudantes.vincular");
        exigirAtivo(e);
        Set<Long> ids = r.professorIds() == null ? Set.of() : r.professorIds();
        if (r.modalidade().equals("PROFESSORES") && (ids.isEmpty() || r.setor() != null && !r.setor().isBlank())
                || r.modalidade().equals("SETOR") && (normalizar(r.setor()).isEmpty() || !ids.isEmpty())
                || r.modalidade().equals("TODOS") && (!ids.isEmpty() || r.setor() != null && !r.setor().isBlank())) {
            throw erro(HttpStatus.BAD_REQUEST, "Informe somente os critérios da modalidade selecionada.");
        }
        var selecionados = usuarios.findAll().stream().filter(this::elegivel)
                .filter(u -> switch (r.modalidade()) {
                    case "PROFESSORES" -> ids.contains(u.getProfessor().getId());
                    case "SETOR" -> normalizar(u.getProfessor().getSetor()).equals(normalizar(r.setor()));
                    case "TODOS" -> true;
                    default -> false;
                }).toList();
        if (selecionados.isEmpty() || r.modalidade().equals("PROFESSORES") && selecionados.size() != ids.size()) {
            throw erro(HttpStatus.BAD_REQUEST, "Selecione professores elegíveis para o estudante.");
        }
        for (var u : selecionados) {
            if (destinatarios.existsByChavePendente(pendencia(e.getId(), u.getProfessor().getId(), r.contexto()))) {
                throw erro(HttpStatus.CONFLICT, "Já existe solicitação pendente equivalente para um dos professores.");
            }
        }
        var s = new SolicitacaoPea(); s.setEstudante(e); s.setSolicitante(autor); s.setContexto(r.contexto().strip());
        s.setOrientacoes(r.orientacoes() == null ? null : r.orientacoes().strip()); s.setModalidade(r.modalidade());
        s.setSetor(r.modalidade().equals("SETOR") ? r.setor().strip() : null); s.setCriadaEm(historico.agora());
        s.setChaveIdempotencia(chave); s.setHashRequisicao(fingerprint); solicitacoes.saveAndFlush(s);
        for (var u : selecionados) {
            // Mesma transação e bloqueio do estudante. Preserva vínculos já ativos.
            if (!vinculos.existeVinculoDocenteAtivo(e.getId(), u.getProfessor().getId())) {
                acompanhamento.associar(e.getId(), u.getId(), "Vínculo automático pela solicitação de PEA #" + s.getId()
                        + " — contexto: " + s.getContexto(), autor.getId());
            }
            var d = new DestinatarioSolicitacaoPea(); d.setSolicitacao(s); d.setProfessor(u.getProfessor()); d.setUsuario(u);
            d.setSetorNoEnvio(u.getProfessor().getSetor()); d.setChavePendente(pendencia(e.getId(), u.getProfessor().getId(), r.contexto()));
            destinatarios.saveAndFlush(d); notificacoes.solicitada(d);
        }
        return resposta(s, autor);
    }

    @Transactional(readOnly=true)
    public List<SolicitacaoPeaResponse> porEstudante(Long id) {
        var autor = coordenador(); estudante(id);
        return solicitacoes.findByEstudanteIdAndSolicitanteIdOrderByCriadaEmDesc(id, autor.getId()).stream().map(s -> resposta(s, autor)).toList();
    }

    @Transactional(readOnly=true)
    public List<SolicitacaoPeaResponse> recebidas() {
        var u = docente(false);
        return destinatarios.findByUsuarioIdOrderByIdDesc(u.getId()).stream().map(DestinatarioSolicitacaoPea::getSolicitacao)
                .filter(s -> historico.disponivel(s.getEstudante())).map(s -> resposta(s, u)).toList();
    }

    @Transactional(readOnly=true)
    public SolicitacaoPeaResponse detalhes(Long id) {
        var u = acesso.autenticado(request); var s = buscar(id);
        if (u.getPerfil() == PerfilUsuario.COORDENADOR) {
            coordenador(); exigirSolicitante(s, u);
        } else {
            docente(false); destinatario(s, u);
        }
        return resposta(s, u);
    }

    @Transactional(readOnly=true)
    public SolicitacaoPeaResponse contexto(Long id) {
        docente(false); return detalhes(id);
    }

    public AdaptacaoPedagogicaResponse entregar(Long id, ConteudoPeaRequest r, String chave) {
        var u = docente(true); acesso.exigir(request, "pedagogico.consultar"); chave = chave(chave);
        var s = buscar(id); destinatario(s, u); // Autorizar antes de obter bloqueios.
        var e = historico.bloquear(s.getEstudante().getId()); em.refresh(s);
        var d = destinatario(s, u); em.refresh(d);
        var pea = new AdaptacaoPedagogica(); pea.setEstudante(e); pea.setProfessor(u.getProfessor());
        AdaptacaoPedagogicaService.preencherDados(pea, r);
        String fingerprint = hash(serializar(List.of(pea.getComponenteCurricular(), pea.getSemestreAno(), pea.getObjetivoGeral(),
                pea.getObjetivosEspecificos(), pea.getConteudosProgramaticos(), pea.getMetodologia(), pea.getAvaliacao(),
                pea.getBibliografiaBasica(), pea.getBibliografiaComplementar())));
        var anterior = entregas.findByDestinatarioId(d.getId());
        if (anterior.isPresent()) {
            var entrega = anterior.get();
            if (!entrega.getChaveIdempotencia().equals(chave) || !entrega.getHashRequisicao().equals(fingerprint)) {
                throw erro(HttpStatus.CONFLICT, "Este destinatário já realizou sua entrega final.");
            }
            try { return json.readValue(entrega.getConteudoEntregue(), AdaptacaoPedagogicaResponse.class); }
            catch (JsonProcessingException ex) { throw new IllegalStateException("Falha ao recuperar a entrega original", ex); }
        }
        exigirAtivo(e);
        if (!d.getSituacao().equals("PENDENTE") || s.getCanceladaEm() != null) throw erro(HttpStatus.CONFLICT, "A solicitação não está pendente.");
        pea.setDataCriacao(historico.agora()); adaptacoes.saveAndFlush(pea);
        var entrega = new EntregaPea(); entrega.setDestinatario(d); entrega.setPea(pea); entrega.setEntregueEm(historico.agora());
        entrega.setChaveIdempotencia(chave); entrega.setHashRequisicao(fingerprint); entrega.setConteudoEntregue(serializar(new AdaptacaoPedagogicaResponse(pea)));
        entregas.saveAndFlush(entrega);
        d.setSituacao("ENTREGUE"); d.setRespondidaEm(entrega.getEntregueEm()); d.setChavePendente(null); destinatarios.saveAndFlush(d);
        s.setRevisaoAndamento(s.getRevisaoAndamento() + 1); solicitacoes.saveAndFlush(s);
        notificacoes.entregue(entrega); return new AdaptacaoPedagogicaResponse(pea);
    }

    public SolicitacaoPeaResponse cancelar(Long id, long versao, String motivo) {
        var u = coordenador(); var s = buscar(id); exigirSolicitante(s, u);
        historico.exigirEditavel(s.getEstudante()); em.refresh(s);
        if (s.getVersao() != versao) throw erro(HttpStatus.CONFLICT, "A solicitação foi alterada; atualize antes de cancelar.");
        var pendentes = destinatarios.findBySolicitacaoIdOrderById(id).stream().peek(em::refresh).filter(d -> d.getSituacao().equals("PENDENTE")).toList();
        if (s.getCanceladaEm() != null || pendentes.isEmpty()) throw erro(HttpStatus.CONFLICT, "Não há destinatários pendentes para cancelar.");
        s.setCanceladaEm(historico.agora()); s.setMotivoCancelamento(motivo.strip());
        for (var d : pendentes) { d.setSituacao("CANCELADO"); d.setCanceladaEm(s.getCanceladaEm()); d.setChavePendente(null); }
        destinatarios.saveAllAndFlush(pendentes); solicitacoes.saveAndFlush(s); return resposta(s, u);
    }

    private SolicitacaoPea buscar(Long id) {
        var s = solicitacoes.findById(id).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Solicitação não encontrada."));
        historico.exigirDisponivel(s.getEstudante()); return s;
    }

    private DestinatarioSolicitacaoPea destinatario(SolicitacaoPea s, Usuario u) {
        var d = destinatarios.findBySolicitacaoIdAndUsuarioId(s.getId(), u.getId())
                .orElseThrow(() -> erro(HttpStatus.FORBIDDEN, "Esta solicitação não foi enviada à sua conta."));
        if (u.getProfessor() == null || !d.getProfessor().getId().equals(u.getProfessor().getId())) throw erro(HttpStatus.FORBIDDEN, "Destinatário incompatível com a sessão.");
        return d;
    }

    private void exigirSolicitante(SolicitacaoPea s, Usuario u) {
        if (!s.getSolicitante().getId().equals(u.getId())) throw erro(HttpStatus.FORBIDDEN, "Esta solicitação pertence a outro coordenador.");
    }

    private SolicitacaoPeaResponse resposta(SolicitacaoPea s, Usuario u) {
        var todos = destinatarios.findBySolicitacaoIdOrderById(s.getId());
        var visiveis = todos.stream().filter(d -> u.getPerfil() == PerfilUsuario.COORDENADOR || d.getUsuario().getId().equals(u.getId())).toList();
        var itens = visiveis.stream().map(d -> new SolicitacaoPeaResponse.Destinatario(d.getId(), d.getProfessor().getId(), d.getProfessor().getNome(),
                d.getSetorNoEnvio(), d.getSituacao(), d.getRespondidaEm(), d.getCanceladaEm(),
                entregas.findByDestinatarioId(d.getId()).map(t -> t.getPea().getId()).orElse(null))).toList();
        String estado = visiveis.stream().anyMatch(d -> d.getSituacao().equals("PENDENTE")) ? "PENDENTE"
                : visiveis.stream().allMatch(d -> d.getSituacao().equals("ENTREGUE")) ? "ENTREGUE"
                : visiveis.stream().anyMatch(d -> d.getSituacao().equals("ENTREGUE")) ? "PARCIALMENTE_ENTREGUE_CANCELADA" : "CANCELADA";
        var e = s.getEstudante();
        return new SolicitacaoPeaResponse(s.getId(), s.getVersao(), new SolicitacaoPeaResponse.EstudanteMinimo(e.getId(), e.getNome(), e.getMatricula(), e.getCurso()),
                s.getSolicitante().getId(), s.getSolicitante().getCoordenador().getNome(), s.getContexto(), s.getOrientacoes(), s.getModalidade(), s.getSetor(),
                s.getCriadaEm(), s.getCanceladaEm(), s.getMotivoCancelamento(), estado, itens);
    }

    private static String chave(String chave) {
        if (chave == null || chave.isBlank() || chave.length() > 100) throw erro(HttpStatus.BAD_REQUEST, "Informe Idempotency-Key com até 100 caracteres.");
        return chave.strip();
    }
    private static String pendencia(Long estudante, Long professor, String contexto) { return estudante + ":" + professor + ":" + hash(normalizar(contexto)); }
    private static String normalizar(String valor) { return Normalizer.normalize(Objects.toString(valor, ""), Normalizer.Form.NFD).replaceAll("\\p{M}", "").strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT); }
    private String serializar(Object valor) { try { return json.writeValueAsString(valor); } catch (JsonProcessingException e) { throw new IllegalStateException("Falha ao preservar o conteúdo da operação", e); } }
    private static String hash(String valor) { try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(valor.getBytes(StandardCharsets.UTF_8))); } catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); } }
    private static ResponseStatusException erro(HttpStatus status, String mensagem) { return new ResponseStatusException(status, mensagem); }
}
