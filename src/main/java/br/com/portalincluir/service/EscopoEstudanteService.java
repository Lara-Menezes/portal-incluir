package br.com.portalincluir.service;
import br.com.portalincluir.model.Usuario;
import br.com.portalincluir.repository.VinculoEstudanteProfissionalRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service @RequiredArgsConstructor
public class EscopoEstudanteService {
    private final PermissaoService acesso;
    private final VinculoEstudanteProfissionalRepository vinculos;
    private final HttpServletRequest request;
    public static boolean gestao(Usuario usuario) { return usuario.getPerfil() == br.com.portalincluir.enums.PerfilUsuario.COORDENADOR || usuario.getPerfil() == br.com.portalincluir.enums.PerfilUsuario.ADMINISTRADOR; }
    @Transactional(readOnly=true) public Optional<Set<Long>> idsPermitidos() {
        Usuario usuario = acesso.autenticado(request);
        if (gestao(usuario)) return Optional.empty();
        Set<Long> ids = switch(usuario.getPerfil()) {
            case PROFESSOR -> vinculos.estudantesDoProfessor(usuario.getProfessor().getId());
            case EQUIPE_MULTIDISCIPLINAR -> vinculos.estudantesDoMembro(usuario.getEquipeMultidisciplinar().getId());
            default -> Set.of();
        };
        return Optional.of(ids);
    }
    public void exigir(Long estudanteId) {
        if (estudanteId == null || idsPermitidos().map(ids -> !ids.contains(estudanteId)).orElse(false)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este estudante não está vinculado ao seu acompanhamento.");
    }
    public void exigirGestao() {
        if (!gestao(acesso.autenticado(request))) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta ação é realizada pela coordenação.");
    }
}
