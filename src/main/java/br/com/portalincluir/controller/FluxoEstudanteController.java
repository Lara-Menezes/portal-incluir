package br.com.portalincluir.controller;
import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import br.com.portalincluir.service.*;
import br.com.portalincluir.dto.request.*;
import br.com.portalincluir.dto.response.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController @RequestMapping("/api/estudantes/{id}/fluxo") @RequiredArgsConstructor @Transactional
public class FluxoEstudanteController {
  private final FluxoEstudanteRepository fluxos;
  private final EstudanteRepository estudantes;
  private final UsuarioRepository usuarios;
  private final PlanoAcaoRepository planos;
  private final PlanoEducacionalRepository peis;
  private final PermissaoService acesso;
  private final PoliticaHistoricoService politica;
  private final EstudanteService estudanteService;
  private final PlanoAcaoService planoService;
  private final NotificacaoFluxoRepository notificacoes;
  public record Responsavel(Long usuarioId, String nome, String perfil, Set<String> permissoes) {}
  public record Resposta(Long estudanteId, String anamneseStatus, String planoStatus, boolean peiRegistrado, Map<String, Responsavel> responsaveis) {}
  public record Atribuicao(Long usuarioId, @jakarta.validation.constraints.Size(max=2000) String mensagem, Long planoId) {}
  public record Anamnese(@NotNull @Valid EstudanteRequest dados, boolean concluir) {}
  public record Plano(@NotNull @Valid PlanoAcaoRequest dados, Long planoId) {}
  private boolean gestao(Usuario u) { return u.getPerfil().name().equals("COORDENADOR") || u.getPerfil().name().equals("ADMINISTRADOR"); }
  private String nome(Usuario u) {
    return switch (u.getPerfil()) {
      case PROFESSOR -> u.getProfessor().getNome();
      case EQUIPE_MULTIDISCIPLINAR -> u.getEquipeMultidisciplinar().getNome();
      case COORDENADOR -> u.getCoordenador().getNome();
      case ADMINISTRADOR -> u.getAdministrador().getNome();
    };
  }
  private Responsavel responsavel(Usuario u) { return new Responsavel(u.getId(), nome(u), u.getPerfil().name(), PermissaoService.efetivas(u)); }
  private Set<String> permissoesEtapa(String etapa) {
    return switch(etapa) {
      case "ANAMNESE" -> Set.of("estudantes.consultar", "anamnese", "sensivel.consultar");
      case "PLANO_ACAO" -> Set.of("estudantes.consultar", "planos.gerar", "sensivel.consultar");
      case "PEI" -> Set.of("estudantes.consultar", "pei.consultar", "pei.editar", "sensivel.consultar");
      default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Etapa inválida");
    };
  }
  private void exigirPermissoes(Usuario u, String etapa) {
    var faltantes = new TreeSet<>(permissoesEtapa(etapa)); faltantes.removeAll(PermissaoService.efetivas(u));
    if (!faltantes.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conceda primeiro as permissões ao profissional e depois atribua a tarefa. Permissões necessárias: " + String.join(", ", faltantes));
  }
  private FluxoEstudante obter(Long id, boolean editar) {
    Estudante e = editar ? politica.bloquear(id) : estudantes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    politica.exigirDisponivel(e);
    if (editar) { politica.exigirEditavel(e); if (!e.isAtivo()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Estudante inativo"); }
    return fluxos.findById(id).orElseGet(() -> { var f = new FluxoEstudante(); f.setEstudante(e); return f; });
  }
  private Usuario atribuido(FluxoEstudante f, String etapa) {
    return switch(etapa) { case "ANAMNESE" -> f.getAnamneseResponsavel(); case "PLANO_ACAO" -> f.getPlanoResponsavel(); case "PEI" -> f.getPeiResponsavel(); default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Etapa invalida"); };
  }
  private void exigirEtapa(HttpServletRequest req, FluxoEstudante f, String etapa) {
    Usuario u = acesso.autenticado(req);
    if (gestao(u)) { acesso.exigir(req, etapa.equals("ANAMNESE") ? "anamnese" : "planos.gerar"); return; }
    Usuario responsavel = atribuido(f, etapa);
    if (responsavel == null || !responsavel.getId().equals(u.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta etapa nao esta atribuida a sua conta");
    for (String permissao : permissoesEtapa(etapa)) acesso.exigir(req, permissao);
  }
  private Resposta resposta(Long id, FluxoEstudante f) {
    Map<String,Responsavel> r = new LinkedHashMap<>();
    for (String etapa : List.of("ANAMNESE", "PLANO_ACAO", "PEI")) { Usuario u = atribuido(f, etapa); if (u != null) r.put(etapa, responsavel(u)); }
    var ativos = planos.findByEstudanteId(id).stream().filter(p -> !p.isArquivado()).toList();
    String planoStatus = ativos.stream().anyMatch(p -> p.getStatus() == br.com.portalincluir.enums.StatusPlanoAcao.ASSINADO) ? "ASSINADO" : ativos.isEmpty() ? null : "RASCUNHO";
    boolean temPei = peis.findByEstudanteId(id).stream().anyMatch(p -> !p.isArquivado());
    return new Resposta(id, f.getAnamneseStatus(), planoStatus, temPei, r);
  }
  @GetMapping public Resposta consultar(@PathVariable Long id, HttpServletRequest req) {
    acesso.exigir(req, "estudantes.consultar");
    return resposta(id, obter(id, false));
  }
  @GetMapping("/profissionais") public List<Responsavel> profissionais(@PathVariable Long id, HttpServletRequest req) {
    acesso.exigir(req, "estudantes.vincular"); obter(id, false);
    return usuarios.findAll().stream().filter(PermissaoService::ativo).filter(u -> Set.of("PROFESSOR", "EQUIPE_MULTIDISCIPLINAR").contains(u.getPerfil().name())).map(this::responsavel).sorted(Comparator.comparing(Responsavel::nome)).toList();
  }
  @PutMapping("/responsaveis/{etapa}") public Resposta atribuir(@PathVariable Long id, @PathVariable String etapa, @Valid @RequestBody Atribuicao r, HttpServletRequest req) {
    Usuario autor = acesso.exigir(req, "estudantes.vincular");
    if (!gestao(autor)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    FluxoEstudante f = obter(id, true); atribuido(f, etapa);
    Usuario u = r.usuarioId() == null ? null : usuarios.findById(r.usuarioId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional nao encontrado"));
    if (u != null && (!PermissaoService.ativo(u) || !Set.of("PROFESSOR", "EQUIPE_MULTIDISCIPLINAR").contains(u.getPerfil().name()))) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione um profissional ativo");
    if (u != null) exigirPermissoes(u, etapa);
    if (u != null && (r.mensagem() == null || r.mensagem().isBlank())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe uma mensagem ao profissional");
    if (r.planoId() != null) {
      if (!etapa.equals("PLANO_ACAO")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plano incompatível com a etapa");
      var plano = planos.findById(r.planoId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
      politica.exigirMesmoEstudante(plano.getEstudante(), id);
      if (plano.isArquivado() || plano.getStatus() == br.com.portalincluir.enums.StatusPlanoAcao.ASSINADO) throw new ResponseStatusException(HttpStatus.CONFLICT, "Este plano já está concluído ou arquivado");
    }
    switch(etapa) { case "ANAMNESE" -> f.setAnamneseResponsavel(u); case "PLANO_ACAO" -> f.setPlanoResponsavel(u); case "PEI" -> f.setPeiResponsavel(u); }
    if (autor.getCoordenador() != null) f.setCoordenador(autor.getCoordenador());
    fluxos.save(f);
    if (u != null) {
      var n = new NotificacaoFluxo(); n.setDestinatario(u); n.setEstudante(f.getEstudante()); n.setRemetente(nome(autor)); n.setEtapa(etapa); n.setPlanoId(r.planoId()); n.setMensagem(r.mensagem().strip()); n.setCriadaEm(politica.agora());
      notificacoes.save(n);
    }
    return resposta(id,f);
  }
  @PutMapping("/anamnese") public EstudanteResponse anamnese(@PathVariable Long id, @Valid @RequestBody Anamnese r, HttpServletRequest req) {
    FluxoEstudante f = obter(id,true); exigirEtapa(req,f,"ANAMNESE");
    if (r.concluir() && r.dados().getPossuiNecessidadeEspecifica() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe se o estudante possui necessidade especifica antes de concluir");
    EstudanteResponse salvo = estudanteService.atualizar(id, r.dados());
    f.setAnamneseStatus(r.concluir() ? "CONCLUIDA" : "RASCUNHO"); fluxos.save(f); return salvo;
  }
  @PostMapping("/plano") public PlanoAcaoResponse plano(@PathVariable Long id, @Valid @RequestBody Plano r, HttpServletRequest req) {
    FluxoEstudante f = obter(id,true); exigirEtapa(req,f,"PLANO_ACAO");
    if (!id.equals(r.dados().getEstudanteId())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Estudante diferente do fluxo");
    Usuario autor = acesso.autenticado(req);
    if (r.planoId() != null) {
      PlanoAcao existente = planos.findById(r.planoId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
      politica.exigirMesmoEstudante(existente.getEstudante(),id);
      r.dados().setCoordenadorResponsavelId(existente.getCoordenadorResponsavel().getId());
      return planoService.atualizar(r.planoId(),r.dados());
    }
    Coordenador coordenador = autor.getCoordenador() != null ? autor.getCoordenador() : f.getCoordenador();
    if (coordenador == null) throw new ResponseStatusException(HttpStatus.CONFLICT, "A coordenacao deve iniciar o plano ou indicar o responsavel");
    r.dados().setCoordenadorResponsavelId(coordenador.getId());
    return planoService.criar(r.dados());
  }
}
