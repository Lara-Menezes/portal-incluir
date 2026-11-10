package br.com.portalincluir.controller;

import br.com.portalincluir.dto.request.EmailEstudanteRequest;
import br.com.portalincluir.dto.response.EmailEnviadoResponse;
import br.com.portalincluir.service.EmailEstudanteService;
import br.com.portalincluir.service.PermissaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/estudantes")
@RequiredArgsConstructor
public class EmailEstudanteController {
    private final EmailEstudanteService emailService;
    private final PermissaoService permissoes;

    @PostMapping("/{id}/email")
    public EmailEnviadoResponse enviar(
            @PathVariable Long id,
            @Valid @RequestBody EmailEstudanteRequest request,
            HttpServletRequest httpRequest) {
        permissoes.exigir(httpRequest, "estudantes.consultar");
        var remetente = permissoes.exigir(httpRequest, "emails.enviar");
        return emailService.enviar(id, request, remetente);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<java.util.Map<String, String>> tratarFalhaEnvio(ResponseStatusException erro) {
        return ResponseEntity.status(erro.getStatusCode())
                .body(java.util.Map.of("message", erro.getReason() == null
                        ? "Não foi possível enviar o e-mail." : erro.getReason()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<java.util.Map<String, String>> tratarValidacao() {
        return ResponseEntity.badRequest()
                .body(java.util.Map.of("message", "Preencha um assunto e uma mensagem válidos."));
    }
}
