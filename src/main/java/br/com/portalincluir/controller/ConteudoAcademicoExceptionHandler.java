package br.com.portalincluir.controller;

import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = ConteudoAcademicoController.class)
public class ConteudoAcademicoExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<?> status(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason() == null ? "Requisição inválida." : e.getReason()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validacao(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage()).collect(Collectors.joining("; "))));
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class, MissingServletRequestPartException.class})
    ResponseEntity<?> invalido(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("message", "Verifique o formato dos dados e os parâmetros obrigatórios."));
    }
    @ExceptionHandler({ConcurrencyFailureException.class, DataIntegrityViolationException.class})
    ResponseEntity<?> conflito(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Os dados foram alterados ou estão em uso. Consulte novamente antes de continuar."));
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<?> tamanho(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(Map.of("message", "O arquivo excede o tamanho permitido."));
    }
}
