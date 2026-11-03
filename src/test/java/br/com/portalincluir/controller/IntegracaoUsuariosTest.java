package br.com.portalincluir.controller;
import br.com.portalincluir.repository.*;
import br.com.portalincluir.config.RootInicialConfig;
import br.com.portalincluir.config.CatalogoProfissional;
import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.DefaultApplicationArguments;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:stateful;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS auditoria","spring.datasource.driver-class-name=org.h2.Driver","spring.datasource.username=sa","spring.datasource.password=","spring.jpa.database-platform=org.hibernate.dialect.H2Dialect","spring.jpa.hibernate.ddl-auto=create-drop","spring.sql.init.mode=never","spring.jpa.show-sql=false","logging.level.root=ERROR"})
@AutoConfigureMockMvc
class IntegracaoUsuariosTest {
 @Autowired MockMvc mvc; @Autowired ObjectMapper json; @Autowired UsuarioRepository usuarios; @Autowired SolicitacaoAcessoRepository pedidos; @Autowired PasswordEncoder encoder; @Autowired RootInicialConfig rootConfig;
 private Map<String,Object> dados(String perfil) {
   String unico=UUID.randomUUID().toString().substring(0,8);
   var d=new HashMap<String,Object>(); d.put("nome","Pessoa Teste");d.put("email","teste-"+unico+"@example.invalid");d.put("senha","senha123");d.put("matricula",unico);d.put("perfil",perfil);d.put("setor","Ensino");d.put("cargo","Coordenacao");d.put("areaAtuacao","Pedagogia");d.put("cursosAtuacao",List.of(CatalogoProfissional.CURSOS.get(0))); return d;
 }
 private MockHttpSession login(String email,String senha) throws Exception { return (MockHttpSession)mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("email",email,"senha",senha)))).andExpect(status().isOk()).andReturn().getRequest().getSession(false); }
 private long solicitar(Map<String,Object> d) throws Exception {
   var r=mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(d))).andExpect(status().isCreated()).andExpect(jsonPath("status").value("PENDENTE")).andExpect(jsonPath("usuarioId").isEmpty()).andReturn();
   assertNull(r.getRequest().getSession(false)); assertFalse(r.getResponse().getContentAsString().contains("senha"));
   return json.readTree(r.getResponse().getContentAsString()).get("id").asLong();
 }
 private void decidir(long id,boolean aprovar,MockHttpSession sessao) throws Exception { mvc.perform(post("/api/solicitacoes-acesso/"+id+"/decisao").session(sessao).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("aprovar",aprovar)))).andExpect(status().isOk()); }
 @Test void rootCriadoUmaVezETemAcessoCompleto() throws Exception {
   var raiz=usuarios.findByRootTrue().orElseThrow(); var hash=raiz.getSenha();assertTrue(encoder.matches("0123456789",hash));
   rootConfig.run(new DefaultApplicationArguments(new String[0])); assertEquals(hash,usuarios.findByRootTrue().orElseThrow().getSenha());assertEquals(1,usuarios.findAll().stream().filter(u->u.isRoot()).count());
   var s=login("root","0123456789");mvc.perform(get("/api/auth/me").session(s)).andExpect(status().isOk()).andExpect(jsonPath("root").value(true)).andExpect(jsonPath("permissoes").isArray());
   mvc.perform(get("/api/solicitacoes-acesso").session(s)).andExpect(status().isOk());
   mvc.perform(delete("/api/usuarios/"+raiz.getId()).session(s)).andExpect(status().isConflict());
 }
 @Test void cadastroPublicoSoLiberaLoginDepoisDaAprovacao() throws Exception {
   var root=login("root","0123456789");var d=dados("PROFESSOR");long id=solicitar(d);
   assertFalse(usuarios.existsByEmailIgnoreCase((String)d.get("email")));assertNotEquals("senha123",pedidos.findById(id).orElseThrow().getSenhaHash());
   mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("email",d.get("email"),"senha","senha123")))).andExpect(status().isUnauthorized());
   mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(d))).andExpect(status().isConflict());
   var p=pedidos.findById(id).orElseThrow();p.setCriadaEm(LocalDateTime.now().minusHours(49));pedidos.saveAndFlush(p);
   for(int i=0;i<2;i++) { var r=mvc.perform(get("/api/solicitacoes-acesso").session(root)).andExpect(status().isOk()).andReturn();JsonNode item=null;for(var n:json.readTree(r.getResponse().getContentAsString()))if(n.get("id").asLong()==id)item=n;assertNotNull(item);assertTrue(item.get("atrasada").asBoolean());assertEquals(2,item.get("diasPendente").asLong());assertFalse(item.toString().contains("senha")); }
   decidir(id,true,root);assertNull(pedidos.findById(id).orElseThrow().getSenhaHash());
   var s=login((String)d.get("email"),"senha123");mvc.perform(get("/api/auth/me").session(s)).andExpect(jsonPath("perfil").value("PROFESSOR"));
   mvc.perform(get("/api/auth/me").session(root)).andExpect(jsonPath("root").value(true));
   mvc.perform(post("/api/solicitacoes-acesso/"+id+"/decisao").session(root).contentType(MediaType.APPLICATION_JSON).content("{\"aprovar\":true}")).andExpect(status().isConflict());
 }
 @Test void coordenadorPrecisaDeAprovacaoDoRootParaAdministracao() throws Exception {
   var root=login("root","0123456789");var d=dados("COORDENADOR");long id=solicitar(d);decidir(id,true,root);var s=login((String)d.get("email"),"senha123");
   mvc.perform(get("/api/solicitacoes-acesso").session(s)).andExpect(status().isForbidden());mvc.perform(get("/api/usuarios").session(s)).andExpect(status().isForbidden());mvc.perform(get("/api/auditorias").session(s)).andExpect(status().isForbidden());
   var r=mvc.perform(post("/api/solicitacoes-acesso/administracao").session(s)).andExpect(status().isOk()).andExpect(jsonPath("tipo").value("ADMINISTRACAO")).andReturn();long admin=json.readTree(r.getResponse().getContentAsString()).get("id").asLong();
   mvc.perform(post("/api/solicitacoes-acesso/"+admin+"/decisao").session(s).contentType(MediaType.APPLICATION_JSON).content("{\"aprovar\":true}")).andExpect(status().isForbidden());
   mvc.perform(post("/api/solicitacoes-acesso/administracao").session(s)).andExpect(status().isConflict());decidir(admin,true,root);
   mvc.perform(get("/api/auth/me").session(s)).andExpect(jsonPath("root").value(false)).andExpect(jsonPath("administracaoAprovada").value(true));mvc.perform(get("/api/usuarios").session(s)).andExpect(status().isOk());
   long rootId=usuarios.findByRootTrue().orElseThrow().getId();mvc.perform(patch("/api/usuarios/"+rootId+"/situacao").session(s).contentType(MediaType.APPLICATION_JSON).content("{\"ativo\":false}")).andExpect(status().isConflict());
 }
 @Test void recusaNaoCriaContaELoginFalhoNaoDestroiSessaoAtual() throws Exception {
   var root=login("root","0123456789");var d=dados("COORDENADOR");long id=solicitar(d);decidir(id,false,root);assertFalse(usuarios.existsByEmailIgnoreCase((String)d.get("email")));
   String antigo=root.getId();var novo=mvc.perform(post("/api/auth/login").session(root).contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"root\",\"senha\":\"0123456789\"}")).andExpect(status().isOk()).andReturn();var s=(MockHttpSession)novo.getRequest().getSession(false);assertNotEquals(antigo,s.getId());assertTrue(root.isInvalid());
   mvc.perform(post("/api/auth/logout").session(s)).andExpect(status().isNoContent());assertTrue(s.isInvalid());mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());mvc.perform(get("/api/solicitacoes-acesso")).andExpect(status().isUnauthorized());
 }
}
