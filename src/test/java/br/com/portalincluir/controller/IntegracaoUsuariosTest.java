package br.com.portalincluir.controller;

import br.com.portalincluir.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:integracao;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS auditoria",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.sql.init.mode=never"
})
@AutoConfigureMockMvc
class IntegracaoUsuariosTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioRepository usuarios;
    @Autowired ProfessorRepository professores;
    @Autowired br.com.portalincluir.service.UsuarioService service;

    @Test void cadastroLoginDashboardECorsComSessao() throws Exception {
        String body = json.writeValueAsString(Map.of("nome", "Coordenação Teste", "email", "coord@teste.com",
                "senha", "senha123", "perfil", "COORDENADOR", "matricula", "coord-1", "cargo", "Coordenação", "setor", "CLAI"));
        org.springframework.mock.web.MockHttpSession gestao = null;
        if (usuarios.count() > 0) {
            var r = new br.com.portalincluir.dto.request.CadastroUsuarioRequest();
            r.setNome("Gestor"); r.setEmail("gestor@teste.com"); r.setSenha("senha123"); r.setPerfil(br.com.portalincluir.enums.PerfilUsuario.COORDENADOR); r.setMatricula("gestor-1"); r.setCargo("Gestor"); r.setSetor("CLAI");
            Long id = service.cadastrar(r).id(); gestao = new org.springframework.mock.web.MockHttpSession(); gestao.setAttribute("usuarioAutenticadoId", id);
        }
        var cadastro = post("/api/auth/register");
        if (gestao != null) cadastro.session(gestao);
        var result = mvc.perform(cadastro.contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("perfil").value("COORDENADOR"))
                .andExpect(jsonPath("senha").doesNotExist()).andReturn();
        long perfilId = json.readTree(result.getResponse().getContentAsString()).get("perfilId").asLong();
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"COORD@TESTE.COM\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("perfilId").value(perfilId));
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"coord@teste.com\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("message").exists());
        mvc.perform(get("/api/coordenadores/" + perfilId + "/dashboard").session((org.springframework.mock.web.MockHttpSession) result.getRequest().getSession(false)))
                .andExpect(status().isOk()).andExpect(jsonPath("planosAtivos").value(0))
                .andExpect(jsonPath("estudantesRecentes").isArray());
        mvc.perform(options("/api/auth/register").header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST").header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
        mvc.perform(post("/api/auth/register").session((org.springframework.mock.web.MockHttpSession) result.getRequest().getSession(false)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test void duplicidadeDePerfilNaoDeixaContaOuPerfilParcial() throws Exception {
        String body = json.writeValueAsString(Map.of("nome", "Professor", "email", "prof@teste.com",
                "senha", "senha123", "perfil", "PROFESSOR", "matricula", "prof-1", "disciplina", "História", "setor", "Ensino", "cursosAtuacao", java.util.List.of(br.com.portalincluir.config.CatalogoProfissional.CURSOS.get(0))));
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated());
        long totalUsuarios = usuarios.count(), totalProfessores = professores.count();
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("prof@teste.com", "outro@teste.com"))).andExpect(status().isConflict());
        assertEquals(totalUsuarios, usuarios.count()); assertEquals(totalProfessores, professores.count());
        assertFalse(usuarios.existsByEmailIgnoreCase("outro@teste.com"));
    }

    @Test void estudanteNaoPodeCriarConta() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"perfil\":\"ESTUDANTE\"}")).andExpect(status().isBadRequest());
    }
}
