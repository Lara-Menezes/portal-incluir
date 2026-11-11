package br.com.portalincluir.controller;

import br.com.portalincluir.config.CatalogoProfissional;
import br.com.portalincluir.dto.request.CadastroUsuarioRequest;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import br.com.portalincluir.service.*;
import com.fasterxml.jackson.databind.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:ocorrencias;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS auditoria",
    "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never", "spring.jpa.show-sql=false", "logging.level.root=ERROR"
})
@AutoConfigureMockMvc @Transactional
class OcorrenciaTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioService cadastro;
    @Autowired UsuarioRepository usuarios;
    @Autowired EstudanteRepository estudantes;
    @Autowired VinculoEstudanteProfissionalRepository vinculos;
    @Autowired OcorrenciaRepository ocorrencias;
    @Autowired NotificacaoFluxoRepository notificacoes;
    @Autowired RetencaoEstudanteService retencao;
    @Autowired EntityManager em;
    Usuario equipe, outraEquipe, professor, outroProfessor, coordenador;
    Estudante aluno;

    @BeforeEach void preparar() {
        equipe = conta(PerfilUsuario.EQUIPE_MULTIDISCIPLINAR); outraEquipe = conta(PerfilUsuario.EQUIPE_MULTIDISCIPLINAR);
        professor = conta(PerfilUsuario.PROFESSOR); outroProfessor = conta(PerfilUsuario.PROFESSOR); coordenador = conta(PerfilUsuario.COORDENADOR);
        aluno = new Estudante(); aluno.setNome("Estudante da ocorrência"); aluno.setMatricula(UUID.randomUUID().toString().substring(0,20));
        aluno.setCurso(CatalogoProfissional.CURSOS.getFirst()); aluno.setPeriodoIngresso("2026.2"); aluno.setDataNascimento(LocalDate.of(2005,1,1));
        estudantes.saveAndFlush(aluno);
        vincular(equipe); vincular(professor);
    }
    Usuario conta(PerfilUsuario perfil) {
        String chave = UUID.randomUUID().toString();
        var r = new CadastroUsuarioRequest(); r.setNome("Teste " + perfil); r.setEmail(chave + "@ocorrencia.test"); r.setSenha("senha123");
        r.setMatricula(chave.substring(0,20)); r.setSetor("Ensino"); r.setCargo("Profissional"); r.setAreaAtuacao(CatalogoProfissional.AREAS.getFirst());
        r.setPerfil(perfil); r.setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.getFirst()));
        return usuarios.findById(cadastro.cadastrar(r).id()).orElseThrow();
    }
    VinculoEstudanteProfissional vincular(Usuario u) {
        var v = new VinculoEstudanteProfissional(); v.setEstudante(aluno); v.setProfessor(u.getProfessor()); v.setMembro(u.getEquipeMultidisciplinar());
        v.setAtivo(true); v.setMotivo("Acompanhamento"); v.setDataInicio(LocalDateTime.now()); v.setResponsavelUsuarioId(coordenador.getId());
        return vinculos.saveAndFlush(v);
    }
    MockHttpSession sessao(Usuario u) { var s = new MockHttpSession(); s.setAttribute("usuarioAutenticadoId", u.getId()); return s; }
    MockMultipartFile dados(LocalDateTime data, String descricao) throws Exception {
        return new MockMultipartFile("dados", "", "application/json", json.writeValueAsBytes(Map.of("estudanteId", aluno.getId(), "descricao", descricao, "ocorridaEm", data.toString())));
    }
    JsonNode criar(LocalDateTime data) throws Exception {
        return json.readTree(mvc.perform(multipart("/api/ocorrencias").file(dados(data, "Estudante relatou dificuldade na atividade.")).session(sessao(equipe)))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }
    @Test void exigeSessaoAutoriaEscopoEPermissao() throws Exception {
        mvc.perform(get("/api/ocorrencias")).andExpect(status().isUnauthorized());
        mvc.perform(multipart("/api/ocorrencias").file(dados(LocalDateTime.now(), "Relato")).session(sessao(professor))).andExpect(status().isForbidden());
        mvc.perform(multipart("/api/ocorrencias").file(dados(LocalDateTime.now(), "Relato")).session(sessao(outraEquipe))).andExpect(status().isForbidden());
        equipe.getPermissoesIndividuais().put("suporte.registrar", false); usuarios.saveAndFlush(equipe);
        mvc.perform(multipart("/api/ocorrencias").file(dados(LocalDateTime.now(), "Relato")).session(sessao(equipe))).andExpect(status().isForbidden());
        assertEquals(0, ocorrencias.count());
    }
    @Test void registraAutoriaENotificaApenasCoordenacaoEDocentesVinculados() throws Exception {
        var o = criar(LocalDateTime.now().minusHours(1));
        assertEquals(equipe.getId(), o.get("autorId").asLong());
        for (var u : List.of(coordenador, professor)) {
            mvc.perform(get("/api/notificacoes").session(sessao(u))).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].etapa").value("OCORRENCIA")).andExpect(jsonPath("$[0].ocorrenciaId").value(o.get("id").asLong()));
        }
        mvc.perform(get("/api/notificacoes").session(sessao(outroProfessor))).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/ocorrencias").session(sessao(professor))).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/ocorrencias").session(sessao(outraEquipe))).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/ocorrencias/" + o.get("id").asLong()).session(sessao(outroProfessor))).andExpect(status().isForbidden());
        mvc.perform(get("/api/ocorrencias/" + o.get("id").asLong()).session(sessao(outraEquipe))).andExpect(status().isNotFound());
        var v = vinculos.findByEstudanteIdAndProfessorId(aluno.getId(), professor.getProfessor().getId()).orElseThrow();
        v.setAtivo(false); vinculos.saveAndFlush(v);
        mvc.perform(get("/api/ocorrencias").session(sessao(professor))).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/notificacoes").session(sessao(professor))).andExpect(jsonPath("$.length()").value(0));
    }
    @Test void arquivoAtomicoEProtegidoPorEscopo() throws Exception {
        var file = new MockMultipartFile("arquivo", "relato.txt", "text/plain", "Anexo de teste".getBytes());
        var o = json.readTree(mvc.perform(multipart("/api/ocorrencias").file(dados(LocalDateTime.now(), "Ocorrência com anexo")).file(file).session(sessao(equipe)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        String url = "/api/ocorrencias/" + o.get("id").asLong() + "/arquivo";
        mvc.perform(get(url).session(sessao(professor))).andExpect(status().isOk()).andExpect(content().bytes("Anexo de teste".getBytes()));
        mvc.perform(get(url).session(sessao(coordenador))).andExpect(status().isOk());
        mvc.perform(get(url).session(sessao(outroProfessor))).andExpect(status().isForbidden());
        long antes = ocorrencias.count(), alertas = notificacoes.count();
        var falso = new MockMultipartFile("arquivo", "foto.jpg", "image/jpeg", "Arquivo invalido".getBytes());
        mvc.perform(multipart("/api/ocorrencias").file(dados(LocalDateTime.now(), "Relato")).file(falso).session(sessao(equipe))).andExpect(status().isBadRequest());
        assertEquals(antes, ocorrencias.count()); assertEquals(alertas, notificacoes.count());
    }
    @Test void relatorioMensalEGeralRespeitaAutorELimitesDoMes() throws Exception {
        criar(LocalDateTime.of(2026,1,31,23,59)); criar(LocalDateTime.of(2026,2,1,0,0));
        vincular(outraEquipe);
        mvc.perform(multipart("/api/ocorrencias").file(dados(LocalDateTime.of(2026,1,15,10,0), "Relato de outro profissional")).session(sessao(outraEquipe))).andExpect(status().isCreated());
        mvc.perform(get("/api/ocorrencias/relatorio").param("mes", "2026-01").session(sessao(equipe))).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/ocorrencias/relatorio").param("autorId", outraEquipe.getId().toString()).session(sessao(equipe))).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/ocorrencias/relatorio").param("mes", "2026-01").session(sessao(coordenador))).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/ocorrencias/relatorio").session(sessao(coordenador))).andExpect(jsonPath("$.length()").value(3));
        mvc.perform(get("/api/ocorrencias/relatorio").session(sessao(professor))).andExpect(status().isForbidden());
        mvc.perform(get("/api/ocorrencias/relatorio").param("mes", "errado").session(sessao(equipe))).andExpect(status().isBadRequest());
    }
    @Test void validaDescricaoDataETamanhoSemPersistir() throws Exception {
        mvc.perform(multipart("/api/ocorrencias").file(dados(LocalDateTime.now(), "  ")).session(sessao(equipe))).andExpect(status().isBadRequest());
        mvc.perform(multipart("/api/ocorrencias").file(dados(LocalDateTime.now().plusDays(1), "Relato")).session(sessao(equipe))).andExpect(status().isBadRequest());
        var grande = new MockMultipartFile("arquivo", "foto.png", "image/png", new byte[10*1024*1024+1]);
        mvc.perform(multipart("/api/ocorrencias").file(dados(LocalDateTime.now(), "Relato")).file(grande).session(sessao(equipe))).andExpect(status().isPayloadTooLarge());
        assertEquals(0, ocorrencias.count());
    }
    @Test void retencaoRemoveOcorrenciaEAnexo() throws Exception {
        var file = new MockMultipartFile("arquivo", "relato.txt", "text/plain", "Anexo".getBytes());
        mvc.perform(multipart("/api/ocorrencias").file(dados(LocalDateTime.now(), "Relato")).file(file).session(sessao(equipe))).andExpect(status().isCreated());
        aluno.setDataLimiteRetencao(LocalDate.now().minusDays(1)); estudantes.saveAndFlush(aluno);
        retencao.descartar(aluno.getId());
        assertEquals(0, ocorrencias.count());
        assertEquals(0L, em.createQuery("select count(a) from ArquivoOcorrencia a", Long.class).getSingleResult());
    }
}
