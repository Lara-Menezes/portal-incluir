package br.com.portalincluir.controller;
import br.com.portalincluir.config.CatalogoProfissional;
import br.com.portalincluir.dto.request.CadastroUsuarioRequest;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import br.com.portalincluir.service.UsuarioService;
import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:vinculos;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS auditoria", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop", "spring.sql.init.mode=never"})
@AutoConfigureMockMvc
class VinculosEstudanteTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioService cadastro;
    @Autowired UsuarioRepository usuarios;
    @Autowired EstudanteRepository estudantes;
    @Autowired PlanoAcaoRepository planos;
    @Autowired org.springframework.jdbc.core.JdbcTemplate jdbc;
    private Long conta(String email, PerfilUsuario perfil) {
        var r = new CadastroUsuarioRequest(); r.setNome("Profissional Teste"); r.setEmail(email); r.setSenha("senha123"); r.setPerfil(perfil); r.setMatricula(email); r.setSetor("CLAI"); r.setCargo("Profissional"); r.setAreaAtuacao("Psicologia"); r.setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.get(0)));
        return cadastro.cadastrar(r).id();
    }
    private MockHttpSession sessao(Long id) { var s = new MockHttpSession(); s.setAttribute("usuarioAutenticadoId", id); return s; }
    private Estudante estudante(String matricula) { var e = new Estudante(); e.setNome(matricula); e.setMatricula(matricula); e.setCurso(CatalogoProfissional.CURSOS.get(0)); e.setPeriodoIngresso("2026.1"); e.setDataNascimento(LocalDate.of(2000,1,1)); return estudantes.saveAndFlush(e); }
    private JsonNode vincular(MockHttpSession gestor, Long estudante, Long profissional) throws Exception {
        var resposta = mvc.perform(post("/api/estudantes/"+estudante+"/profissionais").session(gestor).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("profissionalId", profissional, "motivo", "Acompanhamento educacional registrado pela coordenacao")))) .andExpect(status().isOk()).andReturn();
        return json.readTree(resposta.getResponse().getContentAsString());
    }
    @Test void cursoNaoConcedeAcessoEOVinculoControlaListasEnderecosDiretosEEncerramento() throws Exception {
        Long coord = conta("coord@vinculos.test", PerfilUsuario.COORDENADOR), prof = conta("prof@vinculos.test", PerfilUsuario.PROFESSOR), equipe = conta("equipe@vinculos.test", PerfilUsuario.EQUIPE_MULTIDISCIPLINAR);
        var gestor = sessao(coord); var docente = sessao(prof); var membro = sessao(equipe);
        Estudante a = estudante("Aluno A"), b = estudante("Aluno B");
        mvc.perform(get("/api/estudantes").session(docente)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/estudantes/"+a.getId()).session(docente)).andExpect(status().isForbidden());
        mvc.perform(get("/api/estudantes/"+a.getId()+"/profissionais/disponiveis").session(docente)).andExpect(status().isForbidden());
        JsonNode vinculo = vincular(gestor, a.getId(), prof); vincular(gestor, b.getId(), equipe);
        mvc.perform(get("/api/estudantes").session(docente)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].id").value(a.getId()));
        mvc.perform(get("/api/estudantes").session(membro)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].id").value(b.getId()));
        mvc.perform(get("/api/estudantes/"+b.getId()).session(docente)).andExpect(status().isForbidden());
        mvc.perform(get("/api/estudantes/buscar").param("nome", "Aluno B").session(docente)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/estudantes/"+a.getId()+"/profissionais").session(docente)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        var responsavel = usuarios.findById(coord).orElseThrow().getCoordenador();
        var pa = new PlanoAcao(); pa.setEstudante(a); pa.setCoordenadorResponsavel(responsavel); pa.setPeriodoLetivo("2026.1"); planos.saveAndFlush(pa);
        var pb = new PlanoAcao(); pb.setEstudante(b); pb.setCoordenadorResponsavel(responsavel); pb.setPeriodoLetivo("2026.1"); planos.saveAndFlush(pb);
        mvc.perform(get("/api/planos-acao").session(docente)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].estudanteId").value(a.getId()));
        mvc.perform(get("/api/planos-acao/"+pb.getId()).session(docente)).andExpect(status().isForbidden());
        mvc.perform(post("/api/adaptacoes-pedagogicas").session(docente).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("estudanteId", b.getId())))).andExpect(status().isForbidden());
        mvc.perform(post("/api/estudantes/"+a.getId()+"/profissionais").session(gestor).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("profissionalId", prof, "motivo", "Duplicado")))).andExpect(status().isConflict());
        mvc.perform(post("/api/estudantes/"+a.getId()+"/inativar").session(gestor)).andExpect(status().isOk());
        mvc.perform(get("/api/estudantes").session(docente)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(post("/api/estudantes/"+a.getId()+"/ativar").session(gestor)).andExpect(status().isOk());
        String encerramento = json.writeValueAsString(Map.of("versao", vinculo.get("versao").asLong(), "motivo", "Encerramento do acompanhamento"));
        mvc.perform(post("/api/estudantes/"+a.getId()+"/profissionais/"+vinculo.get("id").asLong()+"/encerrar").session(gestor).contentType(MediaType.APPLICATION_JSON).content(encerramento)).andExpect(status().isOk());
        mvc.perform(get("/api/estudantes/"+a.getId()).session(docente)).andExpect(status().isForbidden());
        mvc.perform(get("/api/planos-acao").session(docente)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(post("/api/estudantes/"+a.getId()+"/profissionais/"+vinculo.get("id").asLong()+"/encerrar").session(gestor).contentType(MediaType.APPLICATION_JSON).content(encerramento)).andExpect(status().isConflict());
        assertEquals(vinculo.get("id").asLong(), vincular(gestor, a.getId(), prof).get("id").asLong(), "Reativar deve preservar a identidade e as revisoes do vinculo");
        mvc.perform(get("/api/estudantes/"+a.getId()).session(docente)).andExpect(status().isOk());
        mvc.perform(put("/api/usuarios/"+prof+"/permissoes").session(gestor).contentType(MediaType.APPLICATION_JSON).content("{\"ajustes\":{\"usuarios\":true,\"usuarios.gerenciar\":true}}")) .andExpect(status().isOk());
        mvc.perform(post("/api/auth/register").session(docente).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("nome", "Sem elevacao de acesso", "email", "nao-coord@vinculos.test", "senha", "senha123", "perfil", "COORDENADOR", "matricula", "nao-coord", "cargo", "Gestor", "setor", "CLAI")))).andExpect(status().isForbidden());
        assertTrue(jdbc.queryForObject("select count(*) from auditoria.vinculos_estudante_profissional_aud where id=?", Long.class, vinculo.get("id").asLong()) >= 3);
    }
    @Test void baseComumCorrespondeSomenteAoEnsinoMedio() {
        var atuacoes = CatalogoProfissional.cursos(Set.of(CatalogoProfissional.BNCC));
        for (String curso : CatalogoProfissional.CURSOS.subList(0, 3)) assertTrue(CatalogoProfissional.atuaNoCurso(atuacoes, curso));
        for (String curso : CatalogoProfissional.CURSOS.subList(3, 5)) assertFalse(CatalogoProfissional.atuaNoCurso(atuacoes, curso));
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> CatalogoProfissional.cursoEstudante(CatalogoProfissional.BNCC, null));
    }
    @Test void cadastroSemDisciplinaExigeCursoValidoEAreaPadronizada() throws Exception {
        var body = new HashMap<String,Object>(); body.put("nome", "Professor sem disciplina"); body.put("email", "opcional@vinculos.test"); body.put("senha", "senha123"); body.put("perfil", "PROFESSOR"); body.put("matricula", "opcional-prof"); body.put("setor", "Ensino"); body.put("cursosAtuacao", List.of(CatalogoProfissional.CURSOS.get(3)));
        var resposta = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body))).andExpect(status().isCreated()).andReturn();
        Long id = json.readTree(resposta.getResponse().getContentAsString()).get("id").asLong();
        var p = usuarios.findById(id).orElseThrow().getProfessor(); assertEquals("", p.getDisciplina()); assertEquals(Set.of(CatalogoProfissional.CURSOS.get(3)), p.getCursosAtuacao());
        body.put("email", "curso-invalido@vinculos.test"); body.put("matricula", "curso-invalido"); body.put("cursosAtuacao", List.of("Curso inexistente"));
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body))).andExpect(status().isBadRequest());
        assertFalse(usuarios.existsByEmailIgnoreCase("curso-invalido@vinculos.test"));
        body.put("perfil", "EQUIPE_MULTIDISCIPLINAR"); body.put("email", "area-invalida@vinculos.test"); body.put("cargo", "Apoio"); body.put("areaAtuacao", "Area inexistente");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body))).andExpect(status().isBadRequest());
        assertFalse(usuarios.existsByEmailIgnoreCase("area-invalida@vinculos.test"));
    }
}
