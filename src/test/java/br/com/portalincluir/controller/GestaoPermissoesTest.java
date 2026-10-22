package br.com.portalincluir.controller;

import br.com.portalincluir.repository.*;
import br.com.portalincluir.service.*;
import br.com.portalincluir.dto.request.CadastroUsuarioRequest;
import br.com.portalincluir.enums.PerfilUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:gestao;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS auditoria",
    "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop", "spring.sql.init.mode=never"
}) @AutoConfigureMockMvc
class GestaoPermissoesTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioService service;
    @Autowired UsuarioRepository usuarios;
    @Autowired ProfessorRepository professores;
    @Autowired EstudanteRepository estudantes;
    @Autowired org.springframework.jdbc.core.JdbcTemplate jdbc;
    private Long criar(String email, PerfilUsuario perfil) {
        var r = new CadastroUsuarioRequest(); r.setEmail(email); r.setNome("Pessoa Teste"); r.setSenha("senha123"); r.setPerfil(perfil); r.setMatricula(email); r.setSetor("CLAI"); r.setCargo("Coordenacao"); r.setDisciplina("Historia");
        r.setCursosAtuacao(java.util.Set.of(br.com.portalincluir.config.CatalogoProfissional.CURSOS.get(0)));
        return service.cadastrar(r).id();
    }
    private MockHttpSession sessao(Long id) { var s = new MockHttpSession(); s.setAttribute("usuarioAutenticadoId", id); return s; }
    @Test void acessoIndividualLoginEdicaoInativacaoExclusaoEAuditoria() throws Exception {
        Long coord = criar("coord@gestao.test", PerfilUsuario.COORDENADOR);
        Long prof = criar("prof@gestao.test", PerfilUsuario.PROFESSOR);
        var gestor = sessao(coord); var pessoa = sessao(prof);
        mvc.perform(get("/api/usuarios")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/usuarios").session(pessoa)).andExpect(status().isForbidden());
        mvc.perform(get("/api/usuarios").session(gestor)).andExpect(status().isOk()).andExpect(jsonPath("$[0].senha").doesNotExist());
        mvc.perform(put("/api/usuarios/"+prof+"/permissoes").session(gestor).contentType(MediaType.APPLICATION_JSON).content("{\"ajustes\":{\"usuarios\":true}}"))
            .andExpect(status().isOk()).andExpect(jsonPath("ajustes.usuarios").value(true));
        mvc.perform(get("/api/usuarios").session(pessoa)).andExpect(status().isOk());
        mvc.perform(delete("/api/usuarios/"+coord).session(pessoa)).andExpect(status().isForbidden());
        mvc.perform(get("/api/auth/me").session(pessoa)).andExpect(status().isOk()).andExpect(jsonPath("permissoes").isArray());
        mvc.perform(put("/api/usuarios/"+prof+"/permissoes").session(gestor).contentType(MediaType.APPLICATION_JSON).content("{\"ajustes\":{\"usuarios\":false,\"inexistente\":true}}"))
            .andExpect(status().isBadRequest());
        assertTrue(usuarios.findById(prof).orElseThrow().getPermissoesIndividuais().get("usuarios"));
        mvc.perform(put("/api/usuarios/"+prof).session(gestor).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Nome Atualizado\",\"email\":\"novo@gestao.test\",\"matricula\":\"prof-gestao\",\"setor\":\"Ensino\",\"disciplina\":\"Historia\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("email").value("novo@gestao.test"));
        assertEquals("novo@gestao.test", usuarios.findById(prof).orElseThrow().getProfessor().getEmail());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"prof@gestao.test\",\"senha\":\"senha123\"}")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"novo@gestao.test\",\"senha\":\"senha123\"}")).andExpect(status().isOk());
        mvc.perform(patch("/api/usuarios/"+prof+"/situacao").session(gestor).contentType(MediaType.APPLICATION_JSON).content("{\"ativo\":false}")).andExpect(status().isOk());
        mvc.perform(get("/api/usuarios").session(pessoa)).andExpect(status().isForbidden());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"novo@gestao.test\",\"senha\":\"senha123\"}")).andExpect(status().isForbidden());
        mvc.perform(patch("/api/usuarios/"+prof+"/situacao").session(gestor).contentType(MediaType.APPLICATION_JSON).content("{\"ativo\":true}")).andExpect(status().isOk());
        mvc.perform(get("/api/usuarios").session(pessoa)).andExpect(status().isOk());
        mvc.perform(put("/api/usuarios/"+coord+"/permissoes").session(gestor).contentType(MediaType.APPLICATION_JSON).content("{\"ajustes\":{}}")).andExpect(status().isConflict());
        mvc.perform(patch("/api/usuarios/"+coord+"/situacao").session(gestor).contentType(MediaType.APPLICATION_JSON).content("{\"ativo\":false}")).andExpect(status().isConflict());
        mvc.perform(delete("/api/usuarios/"+coord).session(gestor)).andExpect(status().isConflict());
        long perfil = usuarios.findById(prof).orElseThrow().getProfessor().getId();
        mvc.perform(delete("/api/usuarios/"+prof).session(gestor)).andExpect(status().isNoContent());
        assertTrue(professores.existsById(perfil)); assertFalse(usuarios.existsById(prof));
        mvc.perform(get("/api/auth/me").session(pessoa)).andExpect(status().isUnauthorized());
    }

    @Test void bloqueioDoPerfilERestauracaoDoPadrao() throws Exception {
        Long coord = criar("coord2@gestao.test", PerfilUsuario.COORDENADOR), prof = criar("prof2@gestao.test", PerfilUsuario.PROFESSOR);
        var gestor = sessao(coord); var pessoa = sessao(prof);
        mvc.perform(get("/api/estudantes").session(pessoa)).andExpect(status().isOk());
        mvc.perform(put("/api/usuarios/"+prof+"/permissoes").session(gestor).contentType(MediaType.APPLICATION_JSON).content("{\"ajustes\":{\"estudantes.consultar\":false}}")).andExpect(status().isOk());
        mvc.perform(get("/api/estudantes").session(pessoa)).andExpect(status().isForbidden());
        mvc.perform(put("/api/usuarios/"+prof+"/permissoes").session(gestor).contentType(MediaType.APPLICATION_JSON).content("{\"ajustes\":{}}")).andExpect(status().isOk());
        mvc.perform(get("/api/estudantes").session(pessoa)).andExpect(status().isOk());
    }
    @Test void bloquearConsultaTambemBloqueiaOsDadosDoPainel() throws Exception {
        Long gestor = criar("gestor4@gestao.test", PerfilUsuario.COORDENADOR), alvo = criar("coord4@gestao.test", PerfilUsuario.COORDENADOR);
        Long perfilId = usuarios.findById(alvo).orElseThrow().getCoordenador().getId();
        mvc.perform(put("/api/usuarios/"+alvo+"/permissoes").session(sessao(gestor)).contentType(MediaType.APPLICATION_JSON).content("{\"ajustes\":{\"estudantes.consultar\":false,\"estudantes.editar\":false,\"anamnese\":false}}")) .andExpect(status().isOk());
        mvc.perform(get("/api/coordenadores/"+perfilId+"/dashboard").session(sessao(alvo))).andExpect(status().isForbidden());
        mvc.perform(put("/api/usuarios/"+alvo+"/permissoes").session(sessao(gestor)).contentType(MediaType.APPLICATION_JSON).content("{\"ajustes\":{}}")) .andExpect(status().isOk());
        mvc.perform(get("/api/coordenadores/"+perfilId+"/dashboard").session(sessao(alvo))).andExpect(status().isOk());
    }
}
