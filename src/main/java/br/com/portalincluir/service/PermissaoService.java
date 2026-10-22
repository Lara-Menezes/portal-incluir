package br.com.portalincluir.service;

import br.com.portalincluir.model.Usuario;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.enums.StatusCoordenador;
import br.com.portalincluir.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor
public class PermissaoService {
    private final UsuarioRepository usuarios;
    public static final Set<String> CATALOGO = Set.of("estudantes.vincular", "estudantes.consultar", "estudantes.editar", "pedagogico.consultar", "pedagogico.editar", "academico.gerar", "sensivel.consultar", "suporte.consultar", "suporte.registrar", "anamnese", "planos.gerar", "pei.consultar", "documentos", "relatorios", "usuarios", "usuarios.gerenciar", "permissoes", "permissoes.gerenciar", "auditorias", "documentos.gerenciar", "pei.editar");
    public static Set<String> padrao(PerfilUsuario perfil) {
        return switch (perfil) {
            case COORDENADOR, ADMINISTRADOR -> CATALOGO;
            case PROFESSOR -> Set.of("estudantes.consultar", "pedagogico.consultar", "pedagogico.editar", "academico.gerar", "pei.consultar", "documentos");
            case EQUIPE_MULTIDISCIPLINAR -> Set.of("estudantes.consultar", "sensivel.consultar", "suporte.consultar", "suporte.registrar", "relatorios");
        };
    }
    public static boolean ativo(Usuario u) {
        return switch (u.getPerfil()) {
            case COORDENADOR -> u.getCoordenador().getStatus() == StatusCoordenador.ATIVO;
            case PROFESSOR -> u.getProfessor().isAtivo();
            case ADMINISTRADOR -> u.getAdministrador().isAtivo();
            case EQUIPE_MULTIDISCIPLINAR -> u.getEquipeMultidisciplinar().isAtivo();
        };
    }
    public static Set<String> efetivas(Usuario usuario) {
        Set<String> resultado = new TreeSet<>(padrao(usuario.getPerfil()));
        usuario.getPermissoesIndividuais().forEach((codigo, permitido) -> { if (permitido) resultado.add(codigo); else resultado.remove(codigo); });
        return resultado;
    }
    @Transactional(readOnly = true)
    public Usuario autenticado(HttpServletRequest request) {
        var sessao = request.getSession(false);
        Object id = sessao == null ? null : sessao.getAttribute("usuarioAutenticadoId");
        if (!(id instanceof Long usuarioId)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Entre novamente para continuar.");
        Usuario usuario = usuarios.findById(usuarioId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Esta conta não está mais disponível."));
        if (!ativo(usuario)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta conta está inativa.");
        // Initialize the audited collection while the transaction is open.
        usuario.getPermissoesIndividuais().size();
        return usuario;
    }
    public Usuario exigir(HttpServletRequest request, String permissao) {
        Usuario usuario = autenticado(request);
        if (!efetivas(usuario).contains(permissao)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para realizar esta ação.");
        return usuario;
    }
}
