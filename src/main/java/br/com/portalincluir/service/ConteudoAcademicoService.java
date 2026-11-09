package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.ConteudoAcademicoRequest;
import br.com.portalincluir.dto.response.ConteudoAcademicoResponse;
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
import java.net.URI;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class ConteudoAcademicoService {
    private final ConteudoAcademicoRepository conteudos;
    private final ArquivoConteudoAcademicoRepository arquivos;
    private final EstudanteRepository estudantes;
    private final VinculoEstudanteProfissionalRepository vinculos;
    private final PermissaoService permissoes;
    private final PoliticaHistoricoService historico;
    private final HttpServletRequest request;

    private Professor professor() {
        var usuario = permissoes.exigir(request, "academico.gerar");
        if (usuario.getPerfil() != PerfilUsuario.PROFESSOR) throw erro(HttpStatus.FORBIDDEN, "Esta biblioteca é exclusiva do professor autenticado.");
        return usuario.getProfessor();
    }

    @Transactional(readOnly = true)
    public List<ConteudoAcademicoResponse> listar() {
        return conteudos.findByProfessorIdOrderByDataAtualizacaoDescIdDesc(professor().getId()).stream()
                .filter(c -> c.getEstudante() == null || historico.disponivel(c.getEstudante()))
                .map(ConteudoAcademicoResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public ConteudoAcademicoResponse consultar(Long id) { return new ConteudoAcademicoResponse(buscar(id, false)); }

    public ConteudoAcademicoResponse criar(ConteudoAcademicoRequest r) {
        var c = new ConteudoAcademico();
        c.setProfessor(professor());
        if (r.getEstudanteId() != null) {
            // Verifica o vinculo antes de revelar se o estudante existe.
            exigirVinculo(r.getEstudanteId(), c.getProfessor().getId());
            c.setEstudante(estudantes.findById(r.getEstudanteId()).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Estudante não encontrado.")));
            historico.exigirEditavel(c.getEstudante());
        }
        preencher(c, r);
        c.setDataCriacao(historico.agora()); c.setDataAtualizacao(c.getDataCriacao());
        return new ConteudoAcademicoResponse(conteudos.saveAndFlush(c));
    }

    public ConteudoAcademicoResponse atualizar(Long id, ConteudoAcademicoRequest r) {
        var c = buscar(id, true);
        exigirVersao(c, r.getVersao());
        Long estudanteOriginal = c.getEstudante() == null ? null : c.getEstudante().getId();
        if (!Objects.equals(estudanteOriginal, r.getEstudanteId())) throw erro(HttpStatus.CONFLICT, "Não é permitido transferir o material entre estudantes.");
        preencher(c, r);
        c.setDataAtualizacao(historico.agora());
        return new ConteudoAcademicoResponse(conteudos.saveAndFlush(c));
    }

    public ConteudoAcademicoResponse criarComArquivo(ConteudoAcademicoRequest r, MultipartFile arquivo) {
        var criado = criar(r);
        return arquivo == null ? criado : enviarArquivo(criado.id(), criado.versao(), arquivo);
    }

    public void excluir(Long id, Long versao) {
        var c = buscar(id, true); exigirVersao(c, versao);
        arquivos.findById(id).ifPresent(arquivos::delete);
        arquivos.flush();
        conteudos.delete(c); conteudos.flush();
    }

    public ConteudoAcademicoResponse enviarArquivo(Long id, Long versao, MultipartFile file) {
        var c = buscar(id, true); exigirVersao(c, versao);
        if (c.getLink() != null) throw erro(HttpStatus.CONFLICT, "Remova o link do material antes de adicionar um arquivo.");
        var validado = ValidadorArquivoAcademico.validar(file);
        var arquivo = arquivos.findById(id).orElseGet(ArquivoConteudoAcademico::new);
        arquivo.setConteudo(c); arquivo.setDados(validado.dados()); arquivos.save(arquivo);
        c.setArquivoNome(validado.nome()); c.setArquivoTipo(validado.tipo()); c.setArquivoTamanho((long) validado.dados().length);
        c.setRevisaoArquivo(c.getRevisaoArquivo() + 1); c.setDataAtualizacao(historico.agora());
        return new ConteudoAcademicoResponse(conteudos.saveAndFlush(c));
    }

    public ConteudoAcademicoResponse removerArquivo(Long id, Long versao) {
        var c = buscar(id, true); exigirVersao(c, versao);
        arquivos.findById(id).ifPresent(arquivos::delete);
        c.setArquivoNome(null); c.setArquivoTipo(null); c.setArquivoTamanho(null);
        c.setRevisaoArquivo(c.getRevisaoArquivo() + 1); c.setDataAtualizacao(historico.agora());
        return new ConteudoAcademicoResponse(conteudos.saveAndFlush(c));
    }

    public record Download(String nome, String tipo, byte[] dados) { }
    @Transactional(readOnly = true)
    public Download baixarArquivo(Long id) {
        var c = buscar(id, false);
        var arquivo = arquivos.findById(id).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Este material não possui arquivo."));
        return new Download(c.getArquivoNome(), c.getArquivoTipo(), arquivo.getDados());
    }

    private ConteudoAcademico buscar(Long id, boolean escrita) {
        var autor = professor();
        var c = (escrita ? conteudos.buscarParaAlteracao(id) : conteudos.findById(id))
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Material não encontrado."));
        if (!c.getProfessor().getId().equals(autor.getId())) throw erro(HttpStatus.NOT_FOUND, "Material não encontrado.");
        if (c.getEstudante() != null) {
            historico.exigirDisponivel(c.getEstudante());
            if (escrita) {
                exigirVinculo(c.getEstudante().getId(), autor.getId());
                historico.exigirEditavel(c.getEstudante());
            }
        }
        return c;
    }

    private void exigirVinculo(Long estudanteId, Long professorId) {
        if (!vinculos.existeVinculoDocenteAtivo(estudanteId, professorId)) throw erro(HttpStatus.FORBIDDEN, "É necessário vínculo ativo com o estudante para cadastrar ou alterar este material.");
    }
    private static void exigirVersao(ConteudoAcademico c, Long versao) {
        if (versao == null) throw erro(HttpStatus.BAD_REQUEST, "Informe a versão consultada do material.");
        if (c.getVersao() != versao) throw erro(HttpStatus.CONFLICT, "O material foi atualizado. Consulte novamente antes de alterar.");
    }
    private static void preencher(ConteudoAcademico c, ConteudoAcademicoRequest r) {
        c.setTitulo(r.getTitulo().trim()); c.setTipo(r.getTipo()); c.setComponenteCurricular(r.getComponenteCurricular().trim());
        c.setDescricao(r.getDescricao().trim()); c.setOrientacoesAdaptacao(opcional(r.getOrientacoesAdaptacao()));
        String link = opcional(r.getLink());
        if (link != null) {
            try {
                URI uri = URI.create(link);
                if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                        || uri.getHost() == null || uri.getUserInfo() != null) throw new IllegalArgumentException();
            } catch (IllegalArgumentException ex) { throw erro(HttpStatus.BAD_REQUEST, "Informe um link HTTP ou HTTPS válido, sem credenciais."); }
            if (c.getArquivoNome() != null) throw erro(HttpStatus.CONFLICT, "Remova o arquivo antes de adicionar um link.");
        }
        c.setLink(link);
    }
    private static String opcional(String s) { return s == null || s.isBlank() ? null : s.trim(); }
    private static ResponseStatusException erro(HttpStatus status, String mensagem) { return new ResponseStatusException(status, mensagem); }
}
