package br.com.portalincluir.controller;

import br.com.portalincluir.dto.request.OcorrenciaRequest;
import br.com.portalincluir.dto.response.OcorrenciaResponse;
import br.com.portalincluir.service.OcorrenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController @RequestMapping("/api/ocorrencias") @RequiredArgsConstructor
public class OcorrenciaController {
    private final OcorrenciaService service;
    @GetMapping public List<OcorrenciaResponse> listar(@RequestParam(required = false) Long estudanteId,
            @RequestParam(required = false) Long autorId, @RequestParam(required = false) String mes) {
        return service.listar(estudanteId, autorId, mes, false);
    }
    @GetMapping("/relatorio") public List<OcorrenciaResponse> relatorio(@RequestParam(required = false) Long estudanteId,
            @RequestParam(required = false) Long autorId, @RequestParam(required = false) String mes) {
        return service.listar(estudanteId, autorId, mes, true);
    }
    @GetMapping("/{id}") public OcorrenciaResponse consultar(@PathVariable Long id) { return service.consultar(id); }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OcorrenciaResponse> criar(@Valid @RequestPart("dados") OcorrenciaRequest dados,
            @RequestPart(value = "arquivo", required = false) MultipartFile arquivo) {
        var o = service.criar(dados, arquivo); return ResponseEntity.created(URI.create("/api/ocorrencias/" + o.id())).body(o);
    }
    @GetMapping("/{id}/arquivo") public ResponseEntity<byte[]> arquivo(@PathVariable Long id) {
        var a = service.arquivo(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(a.tipo()))
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(a.nome(), StandardCharsets.UTF_8).build().toString())
            .header("X-Content-Type-Options", "nosniff").cacheControl(CacheControl.noStore()).contentLength(a.dados().length).body(a.dados());
    }
}
