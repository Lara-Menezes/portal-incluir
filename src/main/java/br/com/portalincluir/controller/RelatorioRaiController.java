package br.com.portalincluir.controller;
import br.com.portalincluir.dto.request.RaiRequest;
import br.com.portalincluir.service.RelatorioRaiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequiredArgsConstructor @RequestMapping("/api/rais/estudante/{estudanteId}")
public class RelatorioRaiController {
 private final RelatorioRaiService service;
 private final br.com.portalincluir.service.RaiDocumentoService documentos;
 public record Conclusao(Long versao, boolean revisado) {}
 @GetMapping public List<RelatorioRaiService.Resposta> listar(@PathVariable Long estudanteId) { return service.listar(estudanteId); }
 @PostMapping public RelatorioRaiService.Resposta criar(@PathVariable Long estudanteId, @Valid @RequestBody RaiRequest request) { return service.salvar(estudanteId, null, request); }
 @PutMapping("/{id}") public RelatorioRaiService.Resposta atualizar(@PathVariable Long estudanteId, @PathVariable Long id, @Valid @RequestBody RaiRequest request) { return service.salvar(estudanteId, id, request); }
 @PostMapping("/{id}/concluir") public RelatorioRaiService.Resposta concluir(@PathVariable Long estudanteId, @PathVariable Long id, @RequestBody Conclusao request) { if (!request.revisado()) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Confirme a revisão do relatório."); return service.concluir(estudanteId,id,request.versao()); }
 @GetMapping("/{id}/arquivo") public org.springframework.http.ResponseEntity<byte[]> arquivo(@PathVariable Long estudanteId, @PathVariable Long id, @RequestParam String formato) {
  if(!formato.equals("docx") && !formato.equals("pdf")) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Formato inválido.");
  var r=service.paraExportar(estudanteId,id); byte[] word=documentos.word(service.dados(r),r.getEstudante());
  byte[] bytes=formato.equals("pdf")?documentos.pdf(word):word;
  String mime=formato.equals("pdf")?"application/pdf":"application/vnd.openxmlformats-officedocument.wordprocessingml.document";
  return org.springframework.http.ResponseEntity.ok().contentType(org.springframework.http.MediaType.parseMediaType(mime)).header("Cache-Control","no-store").header("Content-Disposition",org.springframework.http.ContentDisposition.attachment().filename("rai-"+id+"."+formato).build().toString()).body(bytes);
 }
}
