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
 "spring.datasource.url=jdbc:h2:mem:rai;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS auditoria",
 "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=",
 "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop", "spring.sql.init.mode=never",
 "spring.jpa.show-sql=false", "logging.level.root=ERROR"
}) @AutoConfigureMockMvc
class RelatorioRaiTest {
 @Autowired MockMvc mvc; @Autowired ObjectMapper json; @Autowired UsuarioService usuarios;
 @Autowired EstudanteRepository estudantes; @Autowired UsuarioRepository contas;
 MockHttpSession sessao(String email, PerfilUsuario perfil) {
  var r=new CadastroUsuarioRequest(); r.setNome("Teste RAI"); r.setEmail(email); r.setSenha("senha123"); r.setMatricula(email); r.setSetor("Ensino"); r.setCargo("Coordenação"); r.setPerfil(perfil);
  if (perfil==PerfilUsuario.PROFESSOR) { r.setDisciplina("História"); r.setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.get(0))); }
  var s=new MockHttpSession(); s.setAttribute("usuarioAutenticadoId",usuarios.cadastrar(r).id()); return s;
 }
 long estudante(MockHttpSession sessao, String matricula, int curso) throws Exception {
  var e=new HashMap<String,Object>(); e.put("nomeCompleto","Teste RAI"); e.put("matricula",matricula); e.put("curso",CatalogoProfissional.CURSOS.get(curso)); e.put("periodoIngresso","2026.1"); e.put("dataNascimento","2005-01-01");
  for(var k:List.of("necessidadesEspecificas","condicoesAssociadas","atendimentosCampus","acessibilidadesFisicas")) e.put(k,List.of());
  var resposta=mvc.perform(post("/api/estudantes").session(sessao).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(e))).andExpect(status().isOk()).andReturn();
  return json.readTree(resposta.getResponse().getContentAsString()).get("id").asLong();
 }
 Map<String,Object> dados(String periodicidade) {
  var d=new HashMap<String,Object>();
  for(var k:List.of("campus","componenteCurricular","professor","periodoLetivo","deficiencia","acompanhamento","comunicacao","aprendizagem","saude","competencias","objetivosGerais","objetivosEspecificos","comentarios","avaliacao","encaminhamentos","consideracoesGerais","local","data")) d.put(k,"");
  d.put("anoLetivo","2026"); d.put("periodicidade",periodicidade); d.put("numeroPeriodo","1"); d.put("habilidades",Map.of("curiosidade","Sempre")); d.put("adequacoes",List.of("Recursos")); return d;
 }
 @Test void persistenciaPeriodosVersaoEEscopo() throws Exception {
  var coord=sessao("rai-coord@teste.invalid",PerfilUsuario.COORDENADOR);
  long tecnico=estudante(coord,"RAI-T",0), superior=estudante(coord,"RAI-S",3);
  String url="/api/rais/estudante/"+tecnico;
  mvc.perform(get(url)).andExpect(status().isUnauthorized());
  var prof=sessao("rai-prof@teste.invalid",PerfilUsuario.PROFESSOR);
  var u=contas.findById((Long)prof.getAttribute("usuarioAutenticadoId")).orElseThrow(); u.getPermissoesIndividuais().put("sensivel.consultar",true); contas.saveAndFlush(u);
  mvc.perform(get(url).session(prof)).andExpect(status().isForbidden());
  var d=dados("BIMESTRAL"); String body=json.writeValueAsString(Map.of("dados",d));
  var criado=mvc.perform(post(url).session(coord).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andExpect(jsonPath("dados.habilidades.curiosidade").value("Sempre")).andReturn();
  var registro=json.readTree(criado.getResponse().getContentAsString()); long id=registro.get("id").asLong(), versao=registro.get("versao").asLong();
  mvc.perform(get(url).session(coord)).andExpect(jsonPath("$[0].id").value(id));
  mvc.perform(post(url).session(coord).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isConflict());
  d.put("consideracoesGerais","Evolução no bimestre");
  String edicao=json.writeValueAsString(Map.of("dados",d,"versao",versao));
  mvc.perform(put(url+"/"+id).session(coord).contentType(MediaType.APPLICATION_JSON).content(edicao)).andExpect(status().isOk()).andExpect(jsonPath("dados.consideracoesGerais").value("Evolução no bimestre"));
  mvc.perform(put(url+"/"+id).session(coord).contentType(MediaType.APPLICATION_JSON).content(edicao)).andExpect(status().isConflict());
  mvc.perform(put("/api/rais/estudante/"+superior+"/"+id).session(coord).contentType(MediaType.APPLICATION_JSON).content(edicao)).andExpect(status().isBadRequest());
  mvc.perform(post(url).session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados",dados("SEMESTRAL"))))).andExpect(status().isBadRequest());
  mvc.perform(post("/api/rais/estudante/"+superior).session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados",dados("SEMESTRAL"))))).andExpect(status().isOk());
  for(long estudanteId:List.of(tecnico,superior)) mvc.perform(post("/api/rais/estudante/"+estudanteId).session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados",dados("ANUAL"))))).andExpect(status().isOk());
  d.put("habilidades",Map.of("curiosidade","Talvez"));
  mvc.perform(post(url).session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados",d)))).andExpect(status().isBadRequest());
  var e=estudantes.findById(tecnico).orElseThrow(); e.concluir(java.time.LocalDate.of(2026,11,17)); estudantes.saveAndFlush(e);
  mvc.perform(post(url).session(coord).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isConflict());
  mvc.perform(get(url).session(coord)).andExpect(status().isOk());
 }
}
