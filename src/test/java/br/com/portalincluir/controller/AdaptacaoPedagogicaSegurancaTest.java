package br.com.portalincluir.controller;

import br.com.portalincluir.config.CatalogoProfissional;
import br.com.portalincluir.dto.request.CadastroUsuarioRequest;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import br.com.portalincluir.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:pea-seguranca;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS auditoria",
        "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never", "spring.jpa.show-sql=false", "logging.level.root=ERROR"
})
@AutoConfigureMockMvc
@Transactional
class AdaptacaoPedagogicaSegurancaTest {
    private static final String BASE = "/api/adaptacoes-pedagogicas";
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioService cadastro;
    @Autowired UsuarioRepository usuarios;
    @Autowired EstudanteRepository estudantes;
    @Autowired AdaptacaoPedagogicaRepository adaptacoes;
    @Autowired VinculoEstudanteProfissionalRepository vinculos;
    @Autowired PlanoEducacionalRepository peis;
    @Autowired FluxoEstudanteRepository fluxos;
    private Usuario coord, admin, prof, outro, equipe;
    private Estudante estudante;
    private VinculoEstudanteProfissional vinculo;

    @BeforeEach void preparar() {
        coord = conta(PerfilUsuario.COORDENADOR);
        admin = conta(PerfilUsuario.ADMINISTRADOR);
        equipe = conta(PerfilUsuario.EQUIPE_MULTIDISCIPLINAR);
        prof = conta(PerfilUsuario.PROFESSOR);
        outro = conta(PerfilUsuario.PROFESSOR);
        estudante = estudante();
        vinculo = vincular(estudante, prof);
    }

    private Usuario conta(PerfilUsuario perfil) {
        String chave = UUID.randomUUID().toString();
        var r = new CadastroUsuarioRequest();
        r.setNome("Teste " + perfil); r.setEmail(chave + "@pea.test"); r.setSenha("senha123");
        r.setMatricula(chave.substring(0, 20)); r.setSetor("Ensino"); r.setCargo("Profissional");
        r.setAreaAtuacao("Psicologia"); r.setPerfil(perfil);
        r.setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.get(0)));
        return usuarios.findById(cadastro.cadastrar(r).id()).orElseThrow();
    }

    private Estudante estudante() {
        var e = new Estudante(); e.setNome("Estudante PEA"); e.setMatricula(UUID.randomUUID().toString().substring(0, 20));
        e.setCurso(CatalogoProfissional.CURSOS.get(0)); e.setPeriodoIngresso("2026.1");
        e.setDataNascimento(LocalDate.of(2005, 1, 1));
        return estudantes.saveAndFlush(e);
    }

    private VinculoEstudanteProfissional vincular(Estudante e, Usuario u) {
        var v = new VinculoEstudanteProfissional(); v.setEstudante(e); v.setProfessor(u.getProfessor());
        v.setAtivo(true); v.setMotivo("Acompanhamento"); v.setDataInicio(LocalDateTime.now());
        v.setResponsavelUsuarioId(coord.getId()); return vinculos.saveAndFlush(v);
    }

    private MockHttpSession sessao(Usuario u) {
        var s = new MockHttpSession(); s.setAttribute("usuarioAutenticadoId", u.getId()); return s;
    }

    private Map<String, Object> dados(Estudante e, Usuario u) {
        Map<String, Object> r = new HashMap<>(); r.put("estudanteId", e.getId()); r.put("professorId", u.getProfessor().getId());
        r.put("componenteCurricular", "Matematica"); r.put("semestreAno", "2026.1");
        for (String campo : List.of("objetivoGeral", "objetivosEspecificos", "conteudosProgramaticos", "metodologia", "avaliacao", "bibliografiaBasica", "bibliografiaComplementar")) r.put(campo, "Conteudo completo");
        return r;
    }

    private AdaptacaoPedagogica legado(Usuario autor, PlanoEducacional pei) {
        var a = new AdaptacaoPedagogica(); a.setEstudante(estudante); a.setProfessor(autor.getProfessor());
        a.setPlanoEducacional(pei); a.setComponenteCurricular("Matematica"); a.setSemestreAno("2026.1");
        a.setObjetivoGeral("Objetivo"); a.setObjetivosEspecificos("Objetivos"); a.setConteudosProgramaticos("Conteudos");
        a.setMetodologia("Metodo"); a.setAvaliacao("Avaliacao"); a.setBibliografiaBasica("Basica"); a.setBibliografiaComplementar("Complementar");
        if (pei != null) pei.getAdaptacoesPedagogicas().add(a);
        return adaptacoes.saveAndFlush(a);
    }

    private PlanoEducacional pei() {
        var p = new PlanoEducacional(); p.setEstudante(estudante); p.setCoordenador(coord.getCoordenador());
        p.setSemestreAno("2026.1"); p.setHistorico("Historico"); p.setNecessidadesEspecificas("Necessidades");
        p.setConhecimentosHabilidadesInteresses("Interesses"); p.setDificuldadesApresentadas("Dificuldades");
        p.setPropostasAdaptacoesCurriculares("Propostas"); return peis.saveAndFlush(p);
    }

    private void permissao(Usuario u, String codigo, boolean permitir) {
        u.getPermissoesIndividuais().put(codigo, permitir); usuarios.saveAndFlush(u);
    }

    @Test void criacaoExigeSessaoAutorEVinculoAtivoSemExigirPei() throws Exception {
        assertNotEquals(prof.getId(), prof.getProfessor().getId());
        mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dados(estudante, prof)))).andExpect(status().isUnauthorized());
        mvc.perform(post(BASE).session(sessao(prof)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dados(estudante, outro)))).andExpect(status().isForbidden());
        mvc.perform(post(BASE).session(sessao(coord)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dados(estudante, prof)))).andExpect(status().isForbidden());
        mvc.perform(post(BASE).session(sessao(prof)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dados(estudante, prof))))
                .andExpect(status().isOk()).andExpect(jsonPath("professorId").value(prof.getProfessor().getId()))
                .andExpect(jsonPath("estudanteId").value(estudante.getId()));
        assertEquals(1, adaptacoes.count()); assertNull(adaptacoes.findAll().get(0).getPlanoEducacional());
        permissao(prof, "pedagogico.editar", false);
        mvc.perform(post(BASE).session(sessao(prof)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dados(estudante, prof)))).andExpect(status().isForbidden());
    }

    @Test void atribuicaoDeEtapaNaoSubstituiVinculoParaCriar() throws Exception {
        vinculo.setAtivo(false); vinculos.saveAndFlush(vinculo);
        var f = new FluxoEstudante(); f.setEstudante(estudante); f.setAnamneseResponsavel(prof); fluxos.saveAndFlush(f);
        mvc.perform(get("/api/estudantes/" + estudante.getId()).session(sessao(prof))).andExpect(status().isOk());
        mvc.perform(post(BASE).session(sessao(prof)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dados(estudante, prof)))).andExpect(status().isForbidden());
        assertEquals(0, adaptacoes.count());
    }

    @Test void edicaoNaoTransfereEstudanteOuAutorEGerenciamentoExigeAutoria() throws Exception {
        var proprio = legado(prof, null); var alheio = legado(outro, null);
        var docente = sessao(prof);
        mvc.perform(put(BASE + "/" + alheio.getId()).session(docente).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dados(estudante, prof)))).andExpect(status().isForbidden());
        mvc.perform(put(BASE + "/" + proprio.getId()).session(docente).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dados(estudante, outro)))).andExpect(status().isConflict());
        var segundo = estudante(); vincular(segundo, prof);
        mvc.perform(put(BASE + "/" + proprio.getId()).session(docente).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dados(segundo, prof)))).andExpect(status().isConflict());
        mvc.perform(put(BASE + "/" + proprio.getId()).session(docente).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dados(estudante, prof)))).andExpect(status().isOk()).andExpect(jsonPath("id").value(proprio.getId()));
        for (String acao : List.of("arquivar", "desarquivar")) {
            mvc.perform(post(BASE + "/" + alheio.getId() + "/" + acao).session(docente)).andExpect(status().isForbidden());
        }
        mvc.perform(delete(BASE + "/" + alheio.getId()).session(docente)).andExpect(status().isForbidden());
        mvc.perform(post(BASE + "/" + proprio.getId() + "/arquivar").session(docente)).andExpect(status().isOk()).andExpect(jsonPath("arquivado").value(true));
        mvc.perform(put(BASE + "/" + proprio.getId()).session(docente).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dados(estudante, prof)))).andExpect(status().is4xxClientError());
        mvc.perform(post(BASE + "/" + proprio.getId() + "/desarquivar").session(docente)).andExpect(status().isOk()).andExpect(jsonPath("arquivado").value(false));
        mvc.perform(delete(BASE + "/" + proprio.getId()).session(docente)).andExpect(status().isOk());
        assertTrue(adaptacoes.findById(alheio.getId()).isPresent());
    }

    @Test void consultaHistoricaDoAutorNaoAmpliaAcessoAoEstudanteOuGerenciamento() throws Exception {
        var proprio = legado(prof, null); var alheio = legado(outro, null);
        vinculo.setAtivo(false); vinculos.saveAndFlush(vinculo);
        var docente = sessao(prof);
        mvc.perform(get("/api/estudantes/" + estudante.getId()).session(docente)).andExpect(status().isForbidden());
        mvc.perform(get("/api/estudantes").session(docente)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        for (String caminho : List.of(BASE, BASE + "/estudante/" + estudante.getId(), BASE + "/professor/" + prof.getProfessor().getId(),
                BASE + "/estudante/" + estudante.getId() + "/professor/" + prof.getProfessor().getId(), BASE + "/buscar?componenteCurricular=Matematica")) {
            mvc.perform(get(caminho).session(docente)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].id").value(proprio.getId()));
        }
        mvc.perform(get(BASE + "/" + proprio.getId()).session(docente)).andExpect(status().isOk());
        mvc.perform(get(BASE + "/" + alheio.getId()).session(docente)).andExpect(status().isForbidden());
        mvc.perform(get(BASE + "/professor/" + outro.getProfessor().getId()).session(docente)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(post(BASE + "/" + proprio.getId() + "/arquivar").session(docente)).andExpect(status().isForbidden());
        mvc.perform(post(BASE + "/" + proprio.getId() + "/desarquivar").session(docente)).andExpect(status().isForbidden());
        mvc.perform(delete(BASE + "/" + proprio.getId()).session(docente)).andExpect(status().isForbidden());
        mvc.perform(put(BASE + "/" + proprio.getId()).session(docente).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dados(estudante, prof)))).andExpect(status().isForbidden());
        estudante.concluir(LocalDate.now()); estudantes.saveAndFlush(estudante);
        mvc.perform(get(BASE + "/" + proprio.getId()).session(docente)).andExpect(status().isOk());
        permissao(prof, "pedagogico.consultar", false);
        mvc.perform(get(BASE + "/" + proprio.getId()).session(docente)).andExpect(status().isForbidden());
    }

    @Test void peiIncorporadoFiltraAutoriaESeguePermissoesSemMudarOEscopoDoPei() throws Exception {
        var p = pei(); var proprio = legado(prof, p); legado(outro, p);
        var docente = sessao(prof);
        String raiz = "/api/planos-educacionais";
        mvc.perform(get(raiz + "/" + p.getId()).session(docente)).andExpect(status().isOk())
                .andExpect(jsonPath("adaptacoesPedagogicas.length()").value(1)).andExpect(jsonPath("adaptacoesPedagogicas[0].id").value(proprio.getId()));
        for (String caminho : List.of(raiz, raiz + "/estudante/" + estudante.getId(), raiz + "/coordenador/" + coord.getCoordenador().getId(), raiz + "/ativos", raiz + "/estudante/" + estudante.getId() + "/semestre/2026.1")) {
            mvc.perform(get(caminho).session(docente)).andExpect(status().isOk()).andExpect(jsonPath("$[0].adaptacoesPedagogicas.length()").value(1));
        }
        for (Usuario gestor : List.of(coord, admin)) {
            mvc.perform(get(BASE).session(sessao(gestor))).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
            mvc.perform(get(raiz + "/" + p.getId()).session(sessao(gestor))).andExpect(status().isOk()).andExpect(jsonPath("adaptacoesPedagogicas.length()").value(2));
            mvc.perform(post(BASE + "/" + proprio.getId() + "/arquivar").session(sessao(gestor))).andExpect(status().isOk());
            mvc.perform(post(BASE + "/" + proprio.getId() + "/desarquivar").session(sessao(gestor))).andExpect(status().isOk());
        }
        permissao(prof, "pedagogico.consultar", false);
        mvc.perform(get(raiz + "/" + p.getId()).session(docente)).andExpect(status().isOk()).andExpect(jsonPath("adaptacoesPedagogicas.length()").value(0));
        vinculo.setAtivo(false); vinculos.saveAndFlush(vinculo);
        mvc.perform(get(raiz + "/" + p.getId()).session(docente)).andExpect(status().isForbidden());
    }

    @Test void outrosPerfisNaoGanhamPermissaoEHistoricoEncerradoNaoPermiteEscrita() throws Exception {
        var proprio = legado(prof, null);
        mvc.perform(get(BASE).session(sessao(equipe))).andExpect(status().isForbidden());
        permissao(coord, "pedagogico.editar", false);
        mvc.perform(post(BASE + "/" + proprio.getId() + "/arquivar").session(sessao(coord))).andExpect(status().isForbidden());
        permissao(prof, "pedagogico.editar", false);
        mvc.perform(delete(BASE + "/" + proprio.getId()).session(sessao(prof))).andExpect(status().isForbidden());
        estudante.concluir(LocalDate.now()); estudantes.saveAndFlush(estudante);
        mvc.perform(post(BASE + "/" + proprio.getId() + "/arquivar").session(sessao(admin))).andExpect(status().isConflict());
    }

    @Test void retencaoExpiradaNaoVazaPeasNemPeloPei() throws Exception {
        var p = pei(); var proprio = legado(prof, p);
        estudante.concluir(LocalDate.of(2000, 1, 1)); estudantes.saveAndFlush(estudante);
        mvc.perform(get(BASE + "/" + proprio.getId()).session(sessao(prof))).andExpect(status().isGone());
        mvc.perform(get(BASE).session(sessao(prof))).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/planos-educacionais/" + p.getId()).session(sessao(coord)))
                .andExpect(status().isOk()).andExpect(jsonPath("adaptacoesPedagogicas.length()").value(0));
    }
}
