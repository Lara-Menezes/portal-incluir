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
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:rai-periodo;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS auditoria","spring.datasource.driver-class-name=org.h2.Driver","spring.datasource.username=sa","spring.datasource.password=","spring.jpa.database-platform=org.hibernate.dialect.H2Dialect","spring.jpa.hibernate.ddl-auto=create-drop","spring.sql.init.mode=never","spring.jpa.show-sql=false","logging.level.root=ERROR"}) @AutoConfigureMockMvc
class RaiPeriodoAcessoTest {
 @Autowired MockMvc mvc;@Autowired ObjectMapper json;@Autowired UsuarioService usuarios;@Autowired EstudanteRepository estudantes;@Autowired UsuarioRepository contas;
 MockHttpSession sessao(String email,PerfilUsuario perfil){var r=new CadastroUsuarioRequest();r.setNome("Teste RAI");r.setEmail(email);r.setSenha("senha123");r.setMatricula(email);r.setSetor("Ensino");r.setCargo("Coordenação");r.setPerfil(perfil);if(perfil==PerfilUsuario.PROFESSOR){r.setDisciplina("História");r.setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.get(0)));}var s=new MockHttpSession();s.setAttribute("usuarioAutenticadoId",usuarios.cadastrar(r).id());return s;}
 @Test void conclusaoPorPeriodoNaoEncerraEstudanteENaoLiberaProfessor() throws Exception {
  var coord=sessao("rai-v2-coord@teste.invalid",PerfilUsuario.COORDENADOR);var e=new HashMap<String,Object>();e.put("nomeCompleto","Estudante período");e.put("matricula","RAI-PERIODO");e.put("curso",CatalogoProfissional.CURSOS.get(0));e.put("periodoIngresso","2026.1");e.put("dataNascimento","2005-01-01");for(var k:List.of("necessidadesEspecificas","condicoesAssociadas","atendimentosCampus","acessibilidadesFisicas"))e.put(k,List.of());
  var resposta=mvc.perform(post("/api/estudantes").session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(e))).andExpect(status().isOk()).andReturn();long estudante=json.readTree(resposta.getResponse().getContentAsString()).get("id").asLong();String url="/api/rais/estudante/"+estudante;
  var prof=sessao("rai-v2-prof@teste.invalid",PerfilUsuario.PROFESSOR);var conta=contas.findById((Long)prof.getAttribute("usuarioAutenticadoId")).orElseThrow();for(var p:List.of("sensivel.consultar","anamnese","planos.gerar","pei.editar","estudantes.vincular"))conta.getPermissoesIndividuais().put(p,true);contas.saveAndFlush(conta);
  mvc.perform(get("/api/estudantes/"+estudante+"/fluxo").session(prof)).andExpect(status().isForbidden());mvc.perform(get(url).session(prof)).andExpect(status().isForbidden());
  var d=new HashMap<String,Object>();for(var k:List.of("campus","componenteCurricular","professor","periodoLetivo","deficiencia","acompanhamento","comunicacao","aprendizagem","saude","competencias","objetivosGerais","objetivosEspecificos","comentarios","avaliacao","encaminhamentos","consideracoesGerais","local","data"))d.put(k,"");d.put("anoLetivo","2026");d.put("periodicidade","BIMESTRAL");d.put("numeroPeriodo","3");d.put("habilidades",Map.of());d.put("adequacoes",List.of());
  var criado=mvc.perform(post(url).session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados",d)))).andExpect(status().isOk()).andReturn();var r=json.readTree(criado.getResponse().getContentAsString());long id=r.get("id").asLong(),versao=r.get("versao").asLong();
  mvc.perform(post(url+"/"+id+"/concluir").session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("versao",versao,"revisado",true)))).andExpect(status().isBadRequest());
  for(var k:List.of("competencias","objetivosGerais","objetivosEspecificos","professor"))d.put(k,"Revisado");d.put("data","2026-11-17");var h=new HashMap<String,String>();for(var k:List.of("atencao","processamento","comandos","curiosidade","assiduidade","interacao","participacao","grupo","perguntas"))h.put(k,"Sempre");d.put("habilidades",h);
  var salvo=mvc.perform(put(url+"/"+id).session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados",d,"versao",versao)))).andExpect(status().isOk()).andReturn();versao=json.readTree(salvo.getResponse().getContentAsString()).get("versao").asLong();
  mvc.perform(post(url+"/"+id+"/concluir").session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("versao",versao,"revisado",false)))).andExpect(status().isBadRequest());
  mvc.perform(post(url+"/"+id+"/concluir").session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("versao",versao,"revisado",true)))).andExpect(status().isOk()).andExpect(jsonPath("concluidoEm").isNotEmpty());
  var estudanteAtual=estudantes.findById(estudante).orElseThrow();assertTrue(estudanteAtual.isAtivo());assertFalse(estudanteAtual.isConcluido());
  mvc.perform(get("/api/estudantes/"+estudante+"/fluxo").session(coord)).andExpect(status().isOk()).andExpect(jsonPath("periodosConcluidos[0].numeroPeriodo").value("3"));
  mvc.perform(post(url).session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados",d)))).andExpect(status().isOk());
  mvc.perform(put(url+"/"+id).session(coord).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dados",d,"versao",versao)))).andExpect(status().isConflict());
  mvc.perform(get(url+"/"+id+"/arquivo").param("formato","docx").session(coord)).andExpect(status().isOk()).andExpect(header().string("Cache-Control","no-store"));
  mvc.perform(get(url+"/"+id+"/arquivo").param("formato","docx").session(prof)).andExpect(status().isForbidden());
 }
}
