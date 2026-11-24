package br.com.portalincluir.service;
import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import br.com.portalincluir.dto.request.CadastroUsuarioRequest;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.config.CatalogoProfissional;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.*;
@Service @RequiredArgsConstructor @Transactional
public class SolicitacaoAcessoService {
  private final SolicitacaoAcessoRepository solicitacoes;
  private final UsuarioRepository usuarios;
  private final UsuarioService contas;
  private final PasswordEncoder encoder;
  private final ObjectMapper json;
  private final PermissaoService acesso;
  private final PoliticaHistoricoService tempo;
  public record Resposta(Long id, String tipo, String nome, String email, String perfil, String status, LocalDateTime criadaEm, LocalDateTime prazoAnalise, long diasPendente, boolean atrasada, LocalDateTime analisadaEm, Long usuarioId, String motivo, JsonNode dados) {}
  private Resposta resposta(SolicitacaoAcesso s, boolean detalhes) {
    boolean pendente=s.getStatus().equals("PENDENTE");
    long horas=Math.max(0, Duration.between(s.getCriadaEm(),tempo.agora()).toHours());
    try { return new Resposta(s.getId(),s.getTipo(),s.getNome(),s.getEmail(),s.getPerfil(),s.getStatus(),s.getCriadaEm(),s.getCriadaEm().plusHours(48),horas/24,pendente && !tempo.agora().isBefore(s.getCriadaEm().plusHours(48)),s.getAnalisadaEm(),s.getUsuarioId(),s.getMotivo(),detalhes ? json.readTree(s.getDadosJson()) : null); }
    catch (Exception e) { throw new IllegalStateException("Dados de solicitação inválidos",e); }
  }
  private Usuario gestor(HttpServletRequest req) {
    var u=acesso.exigir(req,"usuarios.gerenciar");
    if (u.getPerfil()!=PerfilUsuario.COORDENADOR && u.getPerfil()!=PerfilUsuario.ADMINISTRADOR) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"A análise de acesso é reservada à coordenação");
    return u;
  }
  public Resposta solicitar(CadastroUsuarioRequest r) {
    if (r.getPerfil()==PerfilUsuario.ADMINISTRADOR) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Selecione professor, coordenador ou equipe multidisciplinar");
    r.setEmail(r.getEmail().strip().toLowerCase(Locale.ROOT)); r.setNome(r.getNome().strip()); r.setMatricula(r.getMatricula().strip());
    if (usuarios.existsByEmailIgnoreCase(r.getEmail()) || solicitacoes.existsByEmailPendente(r.getEmail())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe uma conta ou solicitação pendente para este e-mail");
    if (r.getSetor()==null || r.getSetor().isBlank() || r.getPerfil()!=PerfilUsuario.PROFESSOR && (r.getCargo()==null || r.getCargo().isBlank())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Preencha setor e cargo quando obrigatório");
    if (r.getPerfil()==PerfilUsuario.PROFESSOR) r.setCursosAtuacao(CatalogoProfissional.cursos(r.getCursosAtuacao()));
    if (r.getPerfil()==PerfilUsuario.EQUIPE_MULTIDISCIPLINAR) r.setAreaAtuacao(CatalogoProfissional.area(r.getAreaAtuacao()));
    var s=new SolicitacaoAcesso(); s.setNome(r.getNome()); s.setEmail(r.getEmail()); s.setEmailPendente(r.getEmail()); s.setPerfil(r.getPerfil().name()); s.setCriadaEm(tempo.agora()); s.setSenhaHash(encoder.encode(r.getSenha()));
    ObjectNode dados=json.valueToTree(r); dados.remove("senha"); s.setDadosJson(dados.toString());
    return resposta(solicitacoes.saveAndFlush(s),false);
  }
  @Transactional(readOnly=true) public List<Resposta> listar(HttpServletRequest req, boolean todas) {
    var autor=gestor(req); return solicitacoes.findAllByOrderByCriadaEmAscIdAsc().stream().filter(s -> autor.isRoot() || !s.getTipo().equals("ADMINISTRACAO")).filter(s -> todas || s.getStatus().equals("PENDENTE")).map(s -> resposta(s,true)).toList();
  }
  public Resposta solicitarAdministracao(HttpServletRequest req) {
    var u=acesso.autenticado(req);
    if (u.getPerfil()!=PerfilUsuario.COORDENADOR && u.getPerfil()!=PerfilUsuario.ADMINISTRADOR) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Somente coordenadores podem solicitar acesso administrativo");
    if (u.isRoot() || u.isAdministracaoAprovada()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Seu acesso administrativo já está aprovado");
    String chave="admin:"+u.getId();
    if (solicitacoes.existsByEmailPendente(chave)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Você já possui uma solicitação administrativa pendente");
    var dados=contas.resposta(u); var s=new SolicitacaoAcesso(); s.setTipo("ADMINISTRACAO"); s.setSolicitanteId(u.getId()); s.setNome(dados.nome()); s.setEmail(u.getEmail()); s.setPerfil(u.getPerfil().name()); s.setEmailPendente(chave); s.setCriadaEm(tempo.agora()); s.setDadosJson(json.createObjectNode().put("nome",dados.nome()).put("email",u.getEmail()).toString());
    return resposta(solicitacoes.saveAndFlush(s),false);
  }
  @Transactional(readOnly=true) public List<Resposta> minhas(HttpServletRequest req) {
    var u=acesso.autenticado(req); return solicitacoes.findAllByOrderByCriadaEmAscIdAsc().stream().filter(s -> u.getId().equals(s.getSolicitanteId())).map(s -> resposta(s,false)).toList();
  }
  public Resposta decidir(Long id, boolean aprovar, String motivo, HttpServletRequest req) {
    var autor=gestor(req); var s=solicitacoes.bloquear(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Solicitação não encontrada"));
    if (!s.getStatus().equals("PENDENTE")) throw new ResponseStatusException(HttpStatus.CONFLICT,"Esta solicitação já foi analisada");
    if (s.getTipo().equals("ADMINISTRACAO") && !autor.isRoot()) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Somente o root pode analisar acesso administrativo");
    if (s.getPerfil().equals("COORDENADOR")) acesso.exigir(req,"permissoes.gerenciar");
    if (aprovar && s.getTipo().equals("ADMINISTRACAO")) {
      var alvo=usuarios.findById(s.getSolicitanteId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
      if (!PermissaoService.ativo(alvo)) throw new ResponseStatusException(HttpStatus.CONFLICT,"A conta solicitante está inativa");
      alvo.setAdministracaoAprovada(true); PermissaoService.ADMINISTRATIVAS.forEach(p -> alvo.getPermissoesIndividuais().put(p,true)); usuarios.saveAndFlush(alvo); s.setUsuarioId(alvo.getId());
    } else if (aprovar) {
      try { var dados=json.treeToValue(json.readTree(s.getDadosJson()),CadastroUsuarioRequest.class); var conta=contas.cadastrarAprovado(dados,s.getSenhaHash()); s.setUsuarioId(conta.id()); }
      catch (com.fasterxml.jackson.core.JsonProcessingException e) { throw new IllegalStateException(e); }
    }
    s.setStatus(aprovar ? "APROVADA" : "RECUSADA"); s.setAnalisadaEm(tempo.agora()); s.setAnalisadaPor(autor.getId()); s.setMotivo(motivo); s.setEmailPendente(null); s.setSenhaHash(null);
    return resposta(solicitacoes.saveAndFlush(s),true);
  }
}
