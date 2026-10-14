package br.com.portalincluir.controller;
import br.com.portalincluir.dto.request.*;
import br.com.portalincluir.dto.response.UsuarioResponse;
import br.com.portalincluir.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final UsuarioService service;
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@Valid @RequestBody CadastroUsuarioRequest request) { return service.cadastrar(request); }
    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest request) { return service.login(request); }
}
