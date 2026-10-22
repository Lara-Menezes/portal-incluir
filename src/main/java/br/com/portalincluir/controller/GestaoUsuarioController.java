package br.com.portalincluir.controller;
import br.com.portalincluir.service.*;
import br.com.portalincluir.dto.request.EditarUsuarioRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.*;

@RestController @RequestMapping("/api/usuarios") @RequiredArgsConstructor
public class GestaoUsuarioController {
    private final GestaoUsuarioService service;
    private final PermissaoService acesso;
    public record Situacao(@NotNull Boolean ativo) {}
    public record Ajustes(@NotNull Map<String,Boolean> ajustes) {}
    @GetMapping public List<GestaoUsuarioService.Detalhe> listar(HttpServletRequest http) { acesso.exigir(http, "usuarios"); return service.listar(); }
    @PutMapping("/{id}") public GestaoUsuarioService.Detalhe editar(@PathVariable Long id, @Valid @RequestBody EditarUsuarioRequest r, HttpServletRequest http) { acesso.exigir(http, "usuarios.gerenciar"); return service.editar(id, r); }
    @PatchMapping("/{id}/situacao") public GestaoUsuarioService.Detalhe situacao(@PathVariable Long id, @Valid @RequestBody Situacao r, HttpServletRequest http) { var autor = acesso.exigir(http, "usuarios.gerenciar"); return service.situacao(id, r.ativo(), autor.getId()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void excluir(@PathVariable Long id, HttpServletRequest http) { var autor = acesso.exigir(http, "usuarios.gerenciar"); service.excluir(id, autor.getId()); }
    @PutMapping("/{id}/permissoes") public GestaoUsuarioService.Detalhe permissoes(@PathVariable Long id, @Valid @RequestBody Ajustes r, HttpServletRequest http) { var autor = acesso.exigir(http, "permissoes.gerenciar"); return service.permissoes(id, r.ajustes(), autor.getId()); }
}
