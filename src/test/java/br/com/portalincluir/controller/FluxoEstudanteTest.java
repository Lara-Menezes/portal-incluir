package br.com.portalincluir.controller;
import br.com.portalincluir.repository.*;
import br.com.portalincluir.service.UsuarioService;
import br.com.portalincluir.dto.request.CadastroUsuarioRequest;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.config.CatalogoProfissional;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.http.MediaType;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={
 "spring.datasource.url=jdbc:h2:mem:fluxo;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS auditoria",
 "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=",
 "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop", "spring.sql.init.mode=never",
 "spring.jpa.show-sql=false", "logging.level.root=ERROR"
}) @AutoConfigureMockMvc
class FluxoEstudanteTest {
 @Autowired MockMvc mvc; @Autowired ObjectMapper json; @Autowired UsuarioService usuarios;
 @Autowired PlanoAcaoRepository planos;
 @Autowired UsuarioRepository contas;
 @Autowired NotificacaoFluxoRepository notificacoes;
 private void permitir(Long id, String... codigos) { var u = contas.findById(id).orElseThrow(); for (var codigo : codigos) u.getPermissoesIndividuais().put(codigo,true); contas.saveAndFlush(u); }
 private Long usuario(String email, PerfilUsuario perfil) {
   var r = new CadastroUsuarioRequest(); r.setNome("Teste " + email); r.setEmail(email); r.setSenha("senha123"); r.setMatricula(email); r.setSetor("Ensino"); r.setCargo("Coordenacao"); r.setPerfil(perfil);
   if(perfil == PerfilUsuario.PROFESSOR) { r.setDisciplina("Historia"); r.setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.get(0))); }
   return usuarios.cadastrar(r).id();
 }
 private MockHttpSession sessao(Long id) { var s = new MockHttpSession(); s.setAttribute("usuarioAutenticadoId", id); return s; }
 @Test void rascunhoRetomadaAtribuicaoEConclusao() throws Exception {
   var coord = sessao(usuario("coord-fluxo@teste.invalid", PerfilUsuario.COORDENADOR));
   Long profissional = usuario("prof-fluxo@teste.invalid", PerfilUsuario.PROFESSOR);
   permitir(profissional, "planos.gerar", "sensivel.consultar");
   var prof = sessao(profissional);
   var outro = sessao(usuario("outro-fluxo@teste.invalid", PerfilUsuario.PROFESSOR));
   Map<String,Object> estudante = new HashMap<>();
   estudante.put("nomeCompleto", "Estudante de teste"); estudante.put("matricula", "FLUXO-1"); estudante.put("curso", CatalogoProfissional.CURSOS.get(0)); estudante.put("dataNascimento", "2005-02-03"); estudante.put("periodoIngresso", "2026.1");
   for(String lista : List.of("necessidadesEspecificas", "condicoesAssociadas", "atendimentosCampus", "acessibilidadesFisicas")) estudante.put(lista, List.of());
   var salvo = mvc.perform(post("/api/estudantes").session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(estudante))).andExpect(status().isOk()).andReturn();
   long id = json.readTree(salvo.getResponse().getContentAsString()).get("id").asLong();
   String raiz = "/api/estudantes/" + id + "/fluxo";
   mvc.perform(get(raiz).session(coord)).andExpect(status().isOk()).andExpect(jsonPath("anamneseStatus").value("RASCUNHO"));
   mvc.perform(get(raiz)).andExpect(status().isUnauthorized());
   Map<String,Object> plano = new HashMap<>(); plano.put("estudanteId", id); plano.put("coordenadorResponsavelId", 1);
   for(String campo : List.of("periodoLetivo", "nomeResponsavel", "grauParentesco", "telefoneResponsavel", "emailResponsavel", "necessidadesEspecificas", "consideracoes", "avancosDificuldadesPotencialidades", "principaisDemandas", "propostasIntervencao")) plano.put(campo, "");
   for(String campo : List.of("possuiComorbidade", "usaMedicacaoContinua", "frequentaServicoApoio", "possuiDocumentosComprobatorios")) plano.put(campo, false);
   var criado = mvc.perform(post(raiz + "/plano").session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados", plano)))).andExpect(status().isOk()).andExpect(jsonPath("status").value("RASCUNHO")).andReturn();
   mvc.perform(post(raiz + "/plano").session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados", plano)))).andExpect(status().isConflict());
   long planoId = json.readTree(criado.getResponse().getContentAsString()).get("id").asLong();
   mvc.perform(post("/api/planos-acao/" + planoId + "/assinar").session(coord)).andExpect(status().isBadRequest());
   mvc.perform(put(raiz + "/responsaveis/PLANO_ACAO").session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("usuarioId", profissional, "mensagem", "Finalize o plano", "planoId", planoId)))).andExpect(status().isOk()).andExpect(jsonPath("responsaveis.PLANO_ACAO.usuarioId").value(profissional));
   plano.put("consideracoes", "Retomada do rascunho");
   String atualizacao = json.writeValueAsString(Map.of("dados", plano, "planoId", planoId));
   mvc.perform(post(raiz + "/plano").session(outro).contentType(MediaType.APPLICATION_JSON).content(atualizacao)).andExpect(status().isForbidden());
   mvc.perform(post(raiz + "/plano").session(prof).contentType(MediaType.APPLICATION_JSON).content(atualizacao)).andExpect(status().isOk()).andExpect(jsonPath("id").value(planoId)).andExpect(jsonPath("consideracoes").value("Retomada do rascunho"));
   assertEquals(1, planos.findByEstudanteId(id).size());
   plano.put("estudanteId", id + 999);
   mvc.perform(post(raiz + "/plano").session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados", plano, "planoId", planoId)))).andExpect(status().isConflict());
   mvc.perform(put(raiz + "/anamnese").session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados", estudante, "concluir", true)))).andExpect(status().isBadRequest());
   estudante.put("possuiNecessidadeEspecifica", false);
   mvc.perform(put(raiz + "/anamnese").session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados", estudante, "concluir", true)))).andExpect(status().isOk());
   mvc.perform(get(raiz).session(coord)).andExpect(status().isOk()).andExpect(jsonPath("anamneseStatus").value("CONCLUIDA")).andExpect(jsonPath("planoStatus").value("RASCUNHO"));
 }
 @Test void encaminhamentoExigePermissaoENotificaSomenteDestinatario() throws Exception {
   var coord = sessao(usuario("coord-encaminhar@teste.invalid", PerfilUsuario.COORDENADOR));
   Long profissional = usuario("prof-encaminhar@teste.invalid", PerfilUsuario.PROFESSOR);
   var prof = sessao(profissional);
   var outro = sessao(usuario("outro-encaminhar@teste.invalid", PerfilUsuario.PROFESSOR));
   Map<String,Object> estudante = new HashMap<>();
   estudante.put("nomeCompleto", "Estudante encaminhamento"); estudante.put("matricula", "ENCAMINHAR-1"); estudante.put("curso", CatalogoProfissional.CURSOS.get(0)); estudante.put("dataNascimento", "2005-02-03"); estudante.put("periodoIngresso", "2026.1");
   for(String lista : List.of("necessidadesEspecificas", "condicoesAssociadas", "atendimentosCampus", "acessibilidadesFisicas")) estudante.put(lista, List.of());
   var salvo = mvc.perform(post("/api/estudantes").session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(estudante))).andExpect(status().isOk()).andReturn();
   long id = json.readTree(salvo.getResponse().getContentAsString()).get("id").asLong();
   String raiz = "/api/estudantes/" + id + "/fluxo";
   String corpo = json.writeValueAsString(Map.of("usuarioId", profissional, "mensagem", "Por favor, conclua as necessidades específicas."));
   long totalAntes = notificacoes.count();
   mvc.perform(put(raiz + "/responsaveis/ANAMNESE").session(coord).contentType(MediaType.APPLICATION_JSON).content(corpo)).andExpect(status().isBadRequest());
   assertEquals(totalAntes, notificacoes.count());
   mvc.perform(get(raiz).session(coord)).andExpect(jsonPath("responsaveis.ANAMNESE").doesNotExist());
   permitir(profissional, "anamnese", "sensivel.consultar", "estudantes.editar");
   mvc.perform(put(raiz + "/responsaveis/ANAMNESE").session(prof).contentType(MediaType.APPLICATION_JSON).content(corpo)).andExpect(status().isForbidden());
   mvc.perform(put(raiz + "/responsaveis/ANAMNESE").session(coord).contentType(MediaType.APPLICATION_JSON).content(corpo)).andExpect(status().isOk()).andExpect(jsonPath("anamneseStatus").value("RASCUNHO"));
   assertEquals(totalAntes + 1, notificacoes.count());
   var recebidas = mvc.perform(get("/api/notificacoes").session(prof)).andExpect(status().isOk()).andExpect(jsonPath("$[0].mensagem").value("Por favor, conclua as necessidades específicas.")).andExpect(jsonPath("$[0].lida").value(false)).andExpect(jsonPath("$[0].estudanteId").value(id)).andReturn();
   long aviso = json.readTree(recebidas.getResponse().getContentAsString()).get(0).get("id").asLong();
   mvc.perform(get("/api/notificacoes").session(outro)).andExpect(jsonPath("$.length()").value(0));
   mvc.perform(patch("/api/notificacoes/" + aviso + "/lida").session(outro)).andExpect(status().isNotFound());
   mvc.perform(patch("/api/notificacoes/" + aviso + "/lida").session(prof)).andExpect(status().isOk()).andExpect(jsonPath("lida").value(true));
   mvc.perform(get("/api/notificacoes")).andExpect(status().isUnauthorized());
   mvc.perform(get("/api/notificacoes").session(prof)).andExpect(jsonPath("$[0].lida").value(true));
   var u = contas.findById(profissional).orElseThrow(); u.getPermissoesIndividuais().put("anamnese", false); contas.saveAndFlush(u);
   mvc.perform(put(raiz + "/anamnese").session(prof).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados", estudante, "concluir", false)))).andExpect(status().isForbidden());
   mvc.perform(put(raiz + "/responsaveis/PEI").session(coord).contentType(MediaType.APPLICATION_JSON).content(corpo)).andExpect(status().isBadRequest());
   assertEquals(totalAntes + 1, notificacoes.count());
 }
}
