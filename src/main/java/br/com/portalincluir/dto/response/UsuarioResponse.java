package br.com.portalincluir.dto.response;
import br.com.portalincluir.enums.PerfilUsuario;
import java.util.Set;
public record UsuarioResponse(Long id, String nome, String email, PerfilUsuario perfil, Long perfilId, Set<String> permissoes) {
    public UsuarioResponse(Long id, String nome, String email, PerfilUsuario perfil, Long perfilId) { this(id, nome, email, perfil, perfilId, Set.of()); }
}
