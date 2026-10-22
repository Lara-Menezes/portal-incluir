package br.com.portalincluir.controller;
import br.com.portalincluir.dto.request.*;
import br.com.portalincluir.dto.response.UsuarioResponse;
import br.com.portalincluir.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final UsuarioService service;
    private final br.com.portalincluir.service.PermissaoService acesso;
    @GetMapping("/me")
    public UsuarioResponse sessao(HttpServletRequest http) { return service.resposta(acesso.autenticado(http)); }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@Valid @RequestBody CadastroUsuarioRequest request, HttpServletRequest http) {
        if ((request.getPerfil() == br.com.portalincluir.enums.PerfilUsuario.COORDENADOR || request.getPerfil() == br.com.portalincluir.enums.PerfilUsuario.ADMINISTRADOR) && service.possuiContas()) { acesso.exigir(http, "usuarios.gerenciar"); acesso.exigir(http, "permissoes.gerenciar"); }
        var usuario = service.cadastrar(request);
        var anterior = http.getSession(false);
        if (anterior != null) anterior.invalidate();
        http.getSession(true).setAttribute("usuarioAutenticadoId", usuario.id());
        return usuario;
    }
    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        var anterior = http.getSession(false);
        if (anterior != null) anterior.invalidate();
        var usuario = service.login(request);
        http.getSession(true).setAttribute("usuarioAutenticadoId", usuario.id());
        return usuario;
    }
    @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest http) {
        var sessao = http.getSession(false);
        if (sessao != null) sessao.invalidate();
    }
}
