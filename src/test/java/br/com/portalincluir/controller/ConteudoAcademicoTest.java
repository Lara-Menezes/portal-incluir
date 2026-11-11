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
import org.springframework.http.MediaType;
import org.springframework.mock.web.*;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.*;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:conteudo-academico;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS auditoria",
        "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never", "spring.jpa.show-sql=false", "logging.level.root=ERROR"
})
@AutoConfigureMockMvc @Transactional
class ConteudoAcademicoTest {
    static final String BASE = "/api/conteudos-academicos";
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioService cadastro;
    @Autowired UsuarioRepository usuarios;
    @Autowired EstudanteRepository estudantes;
    @Autowired VinculoEstudanteProfissionalRepository vinculos;
    @Autowired ConteudoAcademicoRepository conteudos;
    @Autowired ArquivoConteudoAcademicoRepository arquivos;
    @Autowired RetencaoEstudanteService retencao;
    @Autowired EntityManager em;
    Usuario professor, outro, coordenador;

    @BeforeEach void preparar() {
        professor = conta(PerfilUsuario.PROFESSOR); outro = conta(PerfilUsuario.PROFESSOR);
        coordenador = conta(PerfilUsuario.COORDENADOR);
    }
    Usuario conta(PerfilUsuario perfil) {
        String chave = UUID.randomUUID().toString();
        var r = new CadastroUsuarioRequest(); r.setNome("Teste " + perfil); r.setEmail(chave + "@conteudo.test");
        r.setSenha("senha123"); r.setMatricula(chave.substring(0, 20)); r.setSetor("Ensino"); r.setCargo("Profissional");
        r.setPerfil(perfil); r.setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.getFirst()));
        return usuarios.findById(cadastro.cadastrar(r).id()).orElseThrow();
    }
    MockHttpSession sessao(Usuario u) {
        var s = new MockHttpSession(); s.setAttribute("usuarioAutenticadoId", u.getId()); return s;
    }
    Map<String, Object> dados() {
        var r = new HashMap<String, Object>(); r.put("titulo", " Atividade adaptada "); r.put("tipo", "ATIVIDADE");
        r.put("componenteCurricular", " Matemática "); r.put("descricao", " Orientações da atividade ");
        r.put("orientacoesAdaptacao", " Leitura orientada "); return r;
    }
    JsonNode criar(Map<String, Object> r) throws Exception {
        return json.readTree(mvc.perform(post(BASE).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(r))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }
    Estudante estudante() {
        var e = new Estudante(); e.setNome("Estudante"); e.setMatricula(UUID.randomUUID().toString().substring(0, 20));
        e.setCurso(CatalogoProfissional.CURSOS.getFirst()); e.setPeriodoIngresso("2026.2"); e.setDataNascimento(LocalDate.of(2005, 1, 1));
        return estudantes.saveAndFlush(e);
    }
    VinculoEstudanteProfissional vincular(Estudante e) {
        var v = new VinculoEstudanteProfissional(); v.setEstudante(e); v.setProfessor(professor.getProfessor());
        v.setAtivo(true); v.setMotivo("Acompanhamento"); v.setDataInicio(LocalDateTime.now());
        v.setResponsavelUsuarioId(coordenador.getId()); return vinculos.saveAndFlush(v);
    }
    MockMultipartFile txt(String conteudo) { return new MockMultipartFile("arquivo", "atividade.txt", "text/plain", conteudo.getBytes(java.nio.charset.StandardCharsets.UTF_8)); }
    JsonNode enviar(JsonNode c, MockMultipartFile file) throws Exception {
        return json.readTree(mvc.perform(multipart(BASE + "/" + c.get("id").asLong() + "/arquivo").file(file)
                .with(r -> { r.setMethod("PUT"); return r; }).param("versao", c.get("versao").asText()).session(sessao(professor)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    }

    @Test void exigeSessaoPerfilPermissaoEContaAtiva() throws Exception {
        mvc.perform(get(BASE)).andExpect(status().isUnauthorized());
        mvc.perform(get(BASE).session(sessao(coordenador))).andExpect(status().isOk());
        professor.getPermissoesIndividuais().put("academico.gerar", false); usuarios.saveAndFlush(professor);
        mvc.perform(get(BASE).session(sessao(professor))).andExpect(status().isForbidden());
        mvc.perform(post(BASE).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(dados())))
                .andExpect(status().isForbidden());
        professor.getPermissoesIndividuais().clear(); professor.getProfessor().setAtivo(false); em.flush();
        mvc.perform(get(BASE).session(sessao(professor))).andExpect(status().isForbidden());
    }
    @Test void criaSemEstudanteUsaSessaoEPreservaAutoria() throws Exception {
        var r = dados(); r.put("professorId", outro.getProfessor().getId());
        var c = criar(r); long id = c.get("id").asLong();
        assertEquals(professor.getProfessor().getId(), c.get("professorId").asLong());
        assertEquals("Atividade adaptada", c.get("titulo").asText()); assertTrue(c.get("estudanteId").isNull());
        mvc.perform(get(BASE).session(sessao(outro))).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get(BASE + "/" + id).session(sessao(outro))).andExpect(status().isNotFound());
        r.put("versao", c.get("versao").asLong()); r.put("titulo", "Atualizado");
        mvc.perform(put(BASE + "/" + id).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(r)))
                .andExpect(status().isOk()).andExpect(jsonPath("titulo").value("Atualizado"))
                .andExpect(jsonPath("professorId").value(professor.getProfessor().getId()));
        mvc.perform(put(BASE + "/" + id).session(sessao(outro)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(r))).andExpect(status().isNotFound());
        mvc.perform(delete(BASE + "/" + id).param("versao", "0").session(sessao(outro))).andExpect(status().isNotFound());
    }
    @Test void favoritosSaoPersistentesPrivadosEProtegidosPorVersao() throws Exception {
        var c = criar(dados()); long id = c.get("id").asLong();
        assertFalse(c.get("favorito").asBoolean());
        mvc.perform(put(BASE + "/" + id + "/favorito").param("versao", "0").param("favorito", "true").session(sessao(outro)))
                .andExpect(status().isNotFound());
        var atualizado = json.readTree(mvc.perform(put(BASE + "/" + id + "/favorito").param("versao", "0").param("favorito", "true").session(sessao(professor)))
                .andExpect(status().isOk()).andExpect(jsonPath("favorito").value(true)).andReturn().getResponse().getContentAsString());
        assertEquals(c.get("dataAtualizacao"), atualizado.get("dataAtualizacao"));
        assertEquals(c.get("titulo"), atualizado.get("titulo"));
        em.clear();
        mvc.perform(get(BASE + "/" + id).session(sessao(professor))).andExpect(jsonPath("favorito").value(true));
        mvc.perform(put(BASE + "/" + id + "/favorito").param("versao", "0").param("favorito", "false").session(sessao(professor)))
                .andExpect(status().isConflict());
        mvc.perform(put(BASE + "/" + id + "/favorito").param("versao", atualizado.get("versao").asText()).param("favorito", "false").session(sessao(professor)))
                .andExpect(status().isOk()).andExpect(jsonPath("favorito").value(false));
    }
    @Test void favoritarNaoLiberaEdicaoDeHistoricoSemVinculo() throws Exception {
        var e = estudante(); var v = vincular(e); var r = dados(); r.put("estudanteId", e.getId()); var c = criar(r);
        v.setAtivo(false); vinculos.saveAndFlush(v);
        mvc.perform(put(BASE + "/" + c.get("id").asLong() + "/favorito").param("versao", "0").param("favorito", "true").session(sessao(professor)))
                .andExpect(status().isOk());
        r.put("versao", 1);
        mvc.perform(put(BASE + "/" + c.get("id").asLong()).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(r)))
                .andExpect(status().isForbidden());
    }
    @Test void coordenacaoConsultaMateriaisEArquivosMasNaoGerenciaNemVeFavoritos() throws Exception {
        var c = enviar(criar(dados()), txt("Material consultavel")); long id = c.get("id").asLong();
        c = json.readTree(mvc.perform(put(BASE + "/" + id + "/favorito").param("versao", c.get("versao").asText()).param("favorito", "true").session(sessao(professor)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        mvc.perform(get(BASE).session(sessao(coordenador))).andExpect(status().isOk()).andExpect(jsonPath("$[0].favorito").value(false));
        mvc.perform(get(BASE + "/" + id).session(sessao(coordenador))).andExpect(status().isOk()).andExpect(jsonPath("favorito").value(false));
        mvc.perform(get(BASE + "/" + id + "/arquivo").session(sessao(coordenador))).andExpect(status().isOk())
                .andExpect(content().bytes("Material consultavel".getBytes()));
        mvc.perform(post(BASE).session(sessao(coordenador)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(dados()))).andExpect(status().isForbidden());
        var r = dados(); r.put("versao", c.get("versao").asLong());
        mvc.perform(put(BASE + "/" + id).session(sessao(coordenador)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(r))).andExpect(status().isForbidden());
        mvc.perform(delete(BASE + "/" + id).session(sessao(coordenador)).param("versao", c.get("versao").asText())).andExpect(status().isForbidden());
        mvc.perform(put(BASE + "/" + id + "/favorito").session(sessao(coordenador)).param("versao", c.get("versao").asText()).param("favorito", "false")).andExpect(status().isForbidden());
        mvc.perform(delete(BASE + "/" + id + "/arquivo").session(sessao(coordenador)).param("versao", c.get("versao").asText())).andExpect(status().isForbidden());
        mvc.perform(multipart(BASE + "/" + id + "/arquivo").file(txt("Troca"))
                .with(req -> { req.setMethod("PUT"); return req; }).param("versao", c.get("versao").asText()).session(sessao(coordenador))).andExpect(status().isForbidden());
        mvc.perform(get(BASE + "/" + id).session(sessao(outro))).andExpect(status().isNotFound());
        mvc.perform(get(BASE).session(sessao(conta(PerfilUsuario.ADMINISTRADOR)))).andExpect(status().isForbidden());
    }
    @Test void consultaCoordenacaoRespeitaPermissaoERetencao() throws Exception {
        var e = estudante(); vincular(e); var r = dados(); r.put("estudanteId", e.getId());
        var c = enviar(criar(r), txt("Privado")); long id = c.get("id").asLong();
        mvc.perform(get(BASE + "/" + id).session(sessao(coordenador))).andExpect(status().isOk());
        e.setDataLimiteRetencao(LocalDate.now().minusDays(1)); estudantes.saveAndFlush(e);
        mvc.perform(get(BASE).session(sessao(coordenador))).andExpect(jsonPath("$[?(@.id == " + id + ")]").isEmpty());
        mvc.perform(get(BASE + "/" + id).session(sessao(coordenador))).andExpect(status().isGone());
        mvc.perform(get(BASE + "/" + id + "/arquivo").session(sessao(coordenador))).andExpect(status().isGone());
        coordenador.getPermissoesIndividuais().put("academico.gerar", false); usuarios.saveAndFlush(coordenador);
        mvc.perform(get(BASE).session(sessao(coordenador))).andExpect(status().isForbidden());
        mvc.perform(get(BASE + "/" + id).session(sessao(coordenador))).andExpect(status().isForbidden());
        mvc.perform(get(BASE + "/" + id + "/arquivo").session(sessao(coordenador))).andExpect(status().isForbidden());
    }
    @Test void validaCamposTiposLimitesELinks() throws Exception {
        for (var entrada : Map.<String, Object>of("titulo", " ", "descricao", "x".repeat(5001), "componenteCurricular", "x".repeat(151), "tipo", "INVALIDO").entrySet()) {
            var r = dados(); r.put(entrada.getKey(), entrada.getValue());
            mvc.perform(post(BASE).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(r))).andExpect(status().isBadRequest());
        }
        for (String link : List.of("javascript:alert(1)", "file:///tmp/arquivo", "https://usuario:senha@example.org")) {
            var r = dados(); r.put("link", link);
            mvc.perform(post(BASE).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(r))).andExpect(status().isBadRequest());
        }
        var r = dados(); r.put("link", " https://example.org/material ");
        assertEquals("https://example.org/material", criar(r).get("link").asText());
    }
    @Test void vinculoAtivoParaCriarEEditarMasNaoParaConsultarHistorico() throws Exception {
        var e = estudante(); var r = dados(); r.put("estudanteId", e.getId());
        mvc.perform(post(BASE).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(r))).andExpect(status().isForbidden());
        var v = vincular(e); var c = criar(r); long id = c.get("id").asLong();
        var mudanca = dados(); mudanca.put("versao", c.get("versao").asLong());
        mvc.perform(put(BASE + "/" + id).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(mudanca))).andExpect(status().isConflict());
        v.setAtivo(false); vinculos.saveAndFlush(v);
        mvc.perform(get(BASE + "/" + id).session(sessao(professor))).andExpect(status().isOk());
        r.put("versao", c.get("versao").asLong());
        mvc.perform(put(BASE + "/" + id).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(r))).andExpect(status().isForbidden());
        e.setDataLimiteRetencao(LocalDate.now().minusDays(1)); estudantes.saveAndFlush(e);
        mvc.perform(get(BASE + "/" + id).session(sessao(professor))).andExpect(status().isGone());
        mvc.perform(get(BASE).session(sessao(professor))).andExpect(jsonPath("$.length()").value(0));
    }
    @Test void uploadDownloadSubstituicaoRemocaoEExclusaoSaoPrivados() throws Exception {
        var c = enviar(criar(dados()), txt("Atividade acessível")); long id = c.get("id").asLong();
        mvc.perform(get(BASE + "/" + id + "/arquivo").session(sessao(professor)))
                .andExpect(status().isOk()).andExpect(header().string("Content-Type", "text/plain"))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(content().bytes("Atividade acessível".getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        mvc.perform(get(BASE + "/" + id + "/arquivo").session(sessao(outro))).andExpect(status().isNotFound());
        mvc.perform(multipart(BASE + "/" + id + "/arquivo").file(txt("Outro"))
                .with(r -> { r.setMethod("PUT"); return r; }).param("versao", c.get("versao").asText()).session(sessao(outro))).andExpect(status().isNotFound());
        long anterior = c.get("versao").asLong(); c = enviar(c, txt("Nova atividade")); assertTrue(c.get("versao").asLong() > anterior);
        c = json.readTree(mvc.perform(delete(BASE + "/" + id + "/arquivo").param("versao", c.get("versao").asText()).session(sessao(professor)))
                .andExpect(status().isOk()).andExpect(jsonPath("arquivoNome").isEmpty()).andReturn().getResponse().getContentAsString());
        mvc.perform(get(BASE + "/" + id + "/arquivo").session(sessao(professor))).andExpect(status().isNotFound());
        c = enviar(c, txt("Excluir"));
        mvc.perform(delete(BASE + "/" + id).param("versao", c.get("versao").asText()).session(sessao(professor))).andExpect(status().isNoContent());
        assertFalse(arquivos.existsById(id)); assertFalse(conteudos.existsById(id));
    }
    @Test void rejeitaUploadInvalidoExcessivoEVersaoDesatualizada() throws Exception {
        var c = criar(dados()); String url = BASE + "/" + c.get("id").asLong() + "/arquivo";
        for (var f : List.of(new MockMultipartFile("arquivo", "falso.pdf", "application/pdf", "texto".getBytes()),
                new MockMultipartFile("arquivo", "programa.exe", "text/plain", "texto".getBytes()), txt(""))) {
            mvc.perform(multipart(url).file(f).with(r -> { r.setMethod("PUT"); return r; }).param("versao", "0").session(sessao(professor))).andExpect(status().isBadRequest());
        }
        mvc.perform(multipart(url).file(new MockMultipartFile("arquivo", "grande.txt", "text/plain", new byte[10485761]))
                .with(r -> { r.setMethod("PUT"); return r; }).param("versao", "0").session(sessao(professor))).andExpect(status().isPayloadTooLarge());
        enviar(c, txt("Valido"));
        mvc.perform(multipart(url).file(txt("Repetido")).with(r -> { r.setMethod("PUT"); return r; }).param("versao", "0").session(sessao(professor))).andExpect(status().isConflict());
    }
    @Test void validaDocxEImpedeMisturarArquivoELink() throws Exception {
        var buffer = new ByteArrayOutputStream();
        try (var zip = new ZipOutputStream(buffer)) {
            for (String nome : List.of("[Content_Types].xml", "word/document.xml")) { zip.putNextEntry(new ZipEntry(nome)); zip.write("<xml/>".getBytes()); zip.closeEntry(); }
        }
        var c = enviar(criar(dados()), new MockMultipartFile("arquivo", "prova.docx", "application/octet-stream", buffer.toByteArray()));
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", c.get("arquivoTipo").asText());
        var r = dados(); r.put("versao", c.get("versao").asLong()); r.put("link", "https://example.org");
        mvc.perform(put(BASE + "/" + c.get("id").asLong()).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(r))).andExpect(status().isConflict());
    }
    @Test void versaoObrigatoriaEvitaSobrescritaEExclusaoAntiga() throws Exception {
        var c = criar(dados()); long id = c.get("id").asLong(); var r = dados();
        mvc.perform(put(BASE + "/" + id).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(r))).andExpect(status().isBadRequest());
        r.put("versao", 0); r.put("titulo", "Novo titulo");
        mvc.perform(put(BASE + "/" + id).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(r))).andExpect(status().isOk());
        mvc.perform(put(BASE + "/" + id).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(r))).andExpect(status().isConflict());
        mvc.perform(delete(BASE + "/" + id).param("versao", "0").session(sessao(professor))).andExpect(status().isConflict());
    }
    @Test @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void falhaDeUploadNoCadastroMultipartNaoDeixaMaterialParcial() throws Exception {
        long antes = conteudos.count();
        var dados = new MockMultipartFile("dados", "", "application/json", json.writeValueAsBytes(dados()));
        mvc.perform(multipart(BASE).file(dados).file(new MockMultipartFile("arquivo", "falso.pdf", "application/pdf", "invalido".getBytes()))
                .session(sessao(professor))).andExpect(status().isBadRequest());
        assertEquals(antes, conteudos.count());
        mvc.perform(multipart(BASE).file(dados).file(txt("Criacao atomica")).session(sessao(professor)))
                .andExpect(status().isCreated()).andExpect(jsonPath("arquivoNome").value("atividade.txt"));
    }
    @Test @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void descartePorRetencaoRemoveMaterialEArquivoSemOrfaos() throws Exception {
        var e = estudante(); vincular(e); var r = dados(); r.put("estudanteId", e.getId());
        var c = enviar(criar(r), txt("Privado")); long id = c.get("id").asLong();
        var excluido = enviar(criar(r), txt("Arquivo excluido antes da retencao"));
        mvc.perform(delete(BASE + "/" + excluido.get("id").asLong()).param("versao", excluido.get("versao").asText())
                .session(sessao(professor))).andExpect(status().isNoContent());
        assertTrue(((Number) em.createNativeQuery("select count(*) from auditoria.conteudos_academicos_aud where estudante_id = :id")
                .setParameter("id", e.getId()).getSingleResult()).longValue() > 0);
        e.setDataLimiteRetencao(LocalDate.now().minusDays(1)); estudantes.saveAndFlush(e);
        retencao.descartar(e.getId());
        assertFalse(conteudos.existsById(id)); assertFalse(arquivos.existsById(id));
        assertEquals(0L, ((Number) em.createNativeQuery("select count(*) from auditoria.conteudos_academicos_aud where estudante_id = :id")
                .setParameter("id", e.getId()).getSingleResult()).longValue());
        assertEquals(0L, ((Number) em.createNativeQuery("select count(*) from auditoria.arquivos_conteudo_academico_aud where conteudo_id in (:ids)")
                .setParameter("ids", List.of(id, excluido.get("id").asLong())).getSingleResult()).longValue());
    }
    @Test @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void duasEdicoesConcorrentesNaoSobrescrevemSilenciosamente() throws Exception {
        var c = criar(dados()); long id = c.get("id").asLong();
        try (var executor = Executors.newFixedThreadPool(2)) {
            var inicio = new CountDownLatch(1);
            List<Future<Integer>> resultados = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                final int indice = i;
                resultados.add(executor.submit(() -> {
                    inicio.await(); var r = dados(); r.put("versao", c.get("versao").asLong()); r.put("titulo", "Edicao " + indice);
                    return mvc.perform(put(BASE + "/" + id).session(sessao(professor)).contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsBytes(r))).andReturn().getResponse().getStatus();
                }));
            }
            inicio.countDown();
            var status = new ArrayList<Integer>(); for (var f : resultados) status.add(f.get(15, TimeUnit.SECONDS));
            Collections.sort(status); assertEquals(List.of(200, 409), status);
        }
    }
}
