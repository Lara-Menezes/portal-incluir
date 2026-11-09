package br.com.portalincluir.controller;

import br.com.portalincluir.dto.request.ConteudoAcademicoRequest;
import br.com.portalincluir.dto.response.ConteudoAcademicoResponse;
import br.com.portalincluir.service.ConteudoAcademicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController @RequestMapping("/api/conteudos-academicos") @RequiredArgsConstructor
public class ConteudoAcademicoController {
    private final ConteudoAcademicoService service;

    @GetMapping
    public List<ConteudoAcademicoResponse> listar() { return service.listar(); }
    @GetMapping("/{id}")
    public ConteudoAcademicoResponse consultar(@PathVariable Long id) { return service.consultar(id); }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConteudoAcademicoResponse> criar(@Valid @RequestBody ConteudoAcademicoRequest request) {
        return criado(service.criar(request));
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ConteudoAcademicoResponse> criarComArquivo(
            @Valid @RequestPart("dados") ConteudoAcademicoRequest request,
            @RequestPart(value = "arquivo", required = false) MultipartFile arquivo) {
        return criado(service.criarComArquivo(request, arquivo));
    }
    private ResponseEntity<ConteudoAcademicoResponse> criado(ConteudoAcademicoResponse resposta) {
        return ResponseEntity.created(URI.create("/api/conteudos-academicos/" + resposta.id())).body(resposta);
    }
    @PutMapping("/{id}")
    public ConteudoAcademicoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ConteudoAcademicoRequest request) {
        return service.atualizar(id, request);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, @RequestParam Long versao) {
        service.excluir(id, versao); return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/favorito")
    public ConteudoAcademicoResponse definirFavorito(@PathVariable Long id, @RequestParam Long versao,
            @RequestParam boolean favorito) {
        return service.definirFavorito(id, versao, favorito);
    }
    @PutMapping(value = "/{id}/arquivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ConteudoAcademicoResponse enviarArquivo(@PathVariable Long id, @RequestParam Long versao,
            @RequestPart("arquivo") MultipartFile arquivo) {
        return service.enviarArquivo(id, versao, arquivo);
    }
    @DeleteMapping("/{id}/arquivo")
    public ConteudoAcademicoResponse removerArquivo(@PathVariable Long id, @RequestParam Long versao) {
        return service.removerArquivo(id, versao);
    }
    @GetMapping("/{id}/arquivo")
    public ResponseEntity<byte[]> baixarArquivo(@PathVariable Long id) {
        var arquivo = service.baixarArquivo(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(arquivo.tipo()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(arquivo.nome(), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .cacheControl(CacheControl.noStore()).contentLength(arquivo.dados().length).body(arquivo.dados());
    }
}
