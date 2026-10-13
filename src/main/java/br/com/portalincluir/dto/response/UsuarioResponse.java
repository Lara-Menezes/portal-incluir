package br.com.portalincluir.dto.response;
import br.com.portalincluir.enums.PerfilUsuario;
public record UsuarioResponse(Long id, String nome, String email, PerfilUsuario perfil, Long perfilId) {}
