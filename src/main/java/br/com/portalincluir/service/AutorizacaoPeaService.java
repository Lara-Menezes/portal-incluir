package br.com.portalincluir.service;

import br.com.portalincluir.dto.response.PlanoEducacionalResponse;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.model.AdaptacaoPedagogica;
import br.com.portalincluir.model.Estudante;
import br.com.portalincluir.model.Professor;
import br.com.portalincluir.model.Usuario;
import br.com.portalincluir.repository.VinculoEstudanteProfissionalRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.function.Predicate;

/** Política específica de PEA; não concede acesso ao perfil ou a outros documentos do estudante. */
@Service
@RequiredArgsConstructor
public class AutorizacaoPeaService {
    private final PermissaoService permissoes;
    private final EscopoEstudanteService escopo;
    private final VinculoEstudanteProfissionalRepository vinculos;
    private final PoliticaHistoricoService historico;
    private final HttpServletRequest request;

    public Usuario exigirEscrita() {
        return permissoes.exigir(request, "pedagogico.editar");
    }

    public Professor exigirCriacao(Estudante estudante, Long professorId) {
        Usuario usuario = exigirEscrita();
        if (usuario.getPerfil() != PerfilUsuario.PROFESSOR
                || !Objects.equals(usuario.getProfessor().getId(), professorId)) {
            throw proibido("O professor autenticado deve ser o autor do PEA.");
        }
        if (!vinculos.existeVinculoDocenteAtivo(estudante.getId(), usuario.getProfessor().getId())) {
            throw proibido("É necessário vínculo de acompanhamento ativo para cadastrar um PEA.");
        }
        return usuario.getProfessor();
    }

    public Predicate<AdaptacaoPedagogica> filtroConsulta() {
        Usuario usuario = permissoes.exigir(request, "pedagogico.consultar");
        if (usuario.getPerfil() == PerfilUsuario.PROFESSOR) {
            Long professorId = usuario.getProfessor().getId();
            // Autoria preserva a consulta histórica, mesmo após encerrar o vínculo.
            return pea -> professorId.equals(pea.getProfessor().getId()) && historico.disponivel(pea.getEstudante());
        }
        var ids = escopo.idsPermitidos();
        return pea -> ids.map(permitidos -> permitidos.contains(pea.getEstudante().getId())).orElse(true)
                && historico.disponivel(pea.getEstudante());
    }

    public void exigirConsulta(AdaptacaoPedagogica pea) {
        Usuario usuario = permissoes.exigir(request, "pedagogico.consultar");
        if (usuario.getPerfil() == PerfilUsuario.PROFESSOR) {
            exigirAutor(usuario, pea);
        } else {
            escopo.exigir(pea.getEstudante().getId());
        }
        historico.exigirDisponivel(pea.getEstudante());
    }

    public void exigirGerenciamento(AdaptacaoPedagogica pea) {
        Usuario usuario = exigirEscrita();
        if (usuario.getPerfil() == PerfilUsuario.PROFESSOR) exigirAutor(usuario, pea);
        // Consulta histórica não implica autorização para modificar registros sem escopo atual.
        escopo.exigir(pea.getEstudante().getId());
    }

    public PlanoEducacionalResponse filtrarIncorporadas(PlanoEducacionalResponse resposta, Estudante estudante) {
        Usuario usuario = permissoes.autenticado(request);
        if (!PermissaoService.efetivas(usuario).contains("pedagogico.consultar") || !historico.disponivel(estudante)) {
            resposta.setAdaptacoesPedagogicas(java.util.List.of());
        } else if (usuario.getPerfil() == PerfilUsuario.PROFESSOR) {
            Long professorId = usuario.getProfessor().getId();
            resposta.setAdaptacoesPedagogicas(resposta.getAdaptacoesPedagogicas().stream()
                    .filter(pea -> professorId.equals(pea.getProfessorId())).toList());
        }
        return resposta;
    }

    private void exigirAutor(Usuario usuario, AdaptacaoPedagogica pea) {
        if (!usuario.getProfessor().getId().equals(pea.getProfessor().getId())) {
            throw proibido("Este PEA pertence a outro professor.");
        }
    }

    private static ResponseStatusException proibido(String mensagem) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, mensagem);
    }
}
