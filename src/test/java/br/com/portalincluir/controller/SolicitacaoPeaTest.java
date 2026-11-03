package br.com.portalincluir.controller;

import br.com.portalincluir.config.CatalogoProfissional;
import br.com.portalincluir.dto.request.CadastroUsuarioRequest;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import br.com.portalincluir.service.*;
import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={
    "spring.datasource.url=jdbc:h2:mem:solicitacoes-pea;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS auditoria",
    "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never", "spring.jpa.show-sql=false", "logging.level.root=ERROR"
})
@AutoConfigureMockMvc
class SolicitacaoPeaTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioService cadastro;
    @Autowired UsuarioRepository usuarios;
    @Autowired EstudanteRepository estudantes;
    @Autowired VinculoEstudanteProfissionalRepository vinculos;
    @Autowired RetencaoEstudanteService retencao;
    @Autowired GestaoUsuarioService gestao;
    @Autowired PlatformTransactionManager tm;
    @Autowired JdbcTemplate jdbc;
    @Autowired SolicitacaoPeaRepository solicitacoes;
    @Autowired DestinatarioSolicitacaoPeaRepository destinatarios;
    @org.springframework.boot.test.mock.mockito.SpyBean NotificacaoPeaService notificacoesPea;
    TransactionTemplate tx;
    Usuario coord, prof, outro, admin;
    Estudante estudante;
    static final String BASE="/api/solicitacoes-pea/";
    static final String EVENTOS="/api/eventos-pea-pendentes";

    @BeforeEach void preparar() {
        tx=new TransactionTemplate(tm);
        coord=conta(PerfilUsuario.COORDENADOR); prof=conta(PerfilUsuario.PROFESSOR);
        outro=conta(PerfilUsuario.PROFESSOR); admin=conta(PerfilUsuario.ADMINISTRADOR);
        estudante=tx.execute(s -> {
            var e=new Estudante(); e.setNome("Estudante PEA"); e.setMatricula(UUID.randomUUID().toString().substring(0,20));
            e.setCurso(CatalogoProfissional.CURSOS.get(0)); e.setPeriodoIngresso("2026.1");
            e.setDataNascimento(LocalDate.of(2005,1,1)); return estudantes.saveAndFlush(e);
        });
    }
    Usuario conta(PerfilUsuario perfil) {
        var r=new CadastroUsuarioRequest(); String id=UUID.randomUUID().toString();
        r.setNome("Teste "+perfil); r.setEmail(id+"@pea.test"); r.setSenha("senha123"); r.setMatricula(id.substring(0,20));
        r.setSetor("Ensino"); r.setCargo("Gestao"); r.setPerfil(perfil); r.setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.get(0)));
        return tx.execute(s -> usuarios.findById(cadastro.cadastrar(r).id()).orElseThrow());
    }
    MockHttpSession sessao(Usuario u) { var s=new MockHttpSession(); s.setAttribute("usuarioAutenticadoId",u.getId()); return s; }
    String criacao() { return "/api/estudantes/"+estudante.getId()+"/solicitacoes-pea"; }
    Map<String,Object> conteudo() {
        var r=new HashMap<String,Object>(); r.put("componenteCurricular","Matematica"); r.put("semestreAno","2026.1");
        for(String c:List.of("objetivoGeral","objetivosEspecificos","conteudosProgramaticos","metodologia","avaliacao","bibliografiaBasica","bibliografiaComplementar")) r.put(c,"Conteudo original");
        return r;
    }
    JsonNode ler(MvcResult r) throws Exception { return json.readTree(r.getResponse().getContentAsString()); }
    ResultActions criar(String chave, String modalidade, String contexto, Object ids, String setor) throws Exception {
        var r=new HashMap<String,Object>(); r.put("modalidade",modalidade); r.put("contexto",contexto);
        if(ids!=null) r.put("professorIds",ids); if(setor!=null) r.put("setor",setor);
        return mvc.perform(post(criacao()).session(sessao(coord)).header("Idempotency-Key",chave).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(r)));
    }
    JsonNode solicitar(Usuario... docentes) throws Exception {
        return ler(criar(UUID.randomUUID().toString(),"PROFESSORES","Contexto",Arrays.stream(docentes).map(u->u.getProfessor().getId()).toList(),null).andExpect(status().isOk()).andReturn());
    }
    ResultActions entregar(long id, Usuario u, String chave) throws Exception {
        return mvc.perform(post(BASE+id+"/entregas").session(sessao(u)).header("Idempotency-Key",chave).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(conteudo())));
    }
    ResultActions cancelar(long id, long versao) throws Exception {
        return mvc.perform(post(BASE+id+"/cancelar").session(sessao(coord)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("versao",versao,"motivo","Encerramento dos pendentes"))));
    }
    JsonNode detalhes(long id) throws Exception { return ler(mvc.perform(get(BASE+id).session(sessao(coord))).andExpect(status().isOk()).andReturn()); }
    void alterar(Usuario u, java.util.function.Consumer<Usuario> f) { tx.executeWithoutResult(s->{ var atual=usuarios.findById(u.getId()).orElseThrow(); f.accept(atual); usuarios.saveAndFlush(atual); }); }
    void vincular() { tx.executeWithoutResult(s->{ if(vinculos.existeVinculoDocenteAtivo(estudante.getId(),prof.getProfessor().getId())) return; var v=new VinculoEstudanteProfissional(); v.setEstudante(estudante); v.setProfessor(prof.getProfessor()); v.setAtivo(true); v.setMotivo("Teste"); v.setDataInicio(LocalDateTime.now()); v.setResponsavelUsuarioId(coord.getId()); vinculos.saveAndFlush(v); }); }

    @Test void criaSemPeiEntregaComAutoriaEVinculoSemAmpliarPermissoes() throws Exception {
        var s=solicitar(prof); long id=s.get("id").asLong();
        mvc.perform(get("/api/estudantes/"+estudante.getId()).session(sessao(prof))).andExpect(status().isOk());
        mvc.perform(get(BASE+id+"/contexto").session(sessao(prof))).andExpect(status().isOk()).andExpect(jsonPath("destinatarios.length()").value(1));
        mvc.perform(get(BASE+id).session(sessao(outro))).andExpect(status().isForbidden());
        entregar(id,outro,"intruso").andExpect(status().isForbidden());
        entregar(id,prof,"entrega").andExpect(status().isOk()).andExpect(jsonPath("professorId").value(prof.getProfessor().getId()));
        assertEquals("ENTREGUE",detalhes(id).get("situacao").asText());
        assertTrue(jdbc.queryForObject("select plano_educacional_id is null from adaptacoes_pedagogicas where estudante_id=?",Boolean.class,estudante.getId()));
        mvc.perform(get("/api/estudantes/"+estudante.getId()).session(sessao(prof))).andExpect(status().isOk());
        mvc.perform(get("/api/notificacoes").session(sessao(prof))).andExpect(status().isOk()).andExpect(jsonPath("$[0].tipoEvento").value("PEA_SOLICITADO"));
        mvc.perform(get("/api/notificacoes").session(sessao(coord))).andExpect(status().isOk()).andExpect(jsonPath("$[0].tipoEvento").value("PEA_ENTREGUE"));
    }
    @Test void elegibilidadeInstitucionalMantemSetorAtividadeEPermissoes() throws Exception {
        alterar(prof,u->u.getProfessor().setCursosAtuacao(Set.of(CatalogoProfissional.BNCC)));
        alterar(outro,u->{u.getProfessor().setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.get(4)));u.getProfessor().setSetor("Outro");});
        JsonNode elegiveis=ler(mvc.perform(get(criacao()+"/elegiveis").param("setor","ENSINO").session(sessao(coord))).andExpect(status().isOk()).andReturn());
        assertTrue(java.util.stream.StreamSupport.stream(elegiveis.spliterator(),false).anyMatch(n->n.get("professorId").asLong()==prof.getProfessor().getId()));
        assertFalse(java.util.stream.StreamSupport.stream(elegiveis.spliterator(),false).anyMatch(n->n.get("professorId").asLong()==outro.getProfessor().getId()));
        criar("outro-curso","PROFESSORES","Outro curso sem vínculo",List.of(outro.getProfessor().getId()),null).andExpect(status().isOk());
        alterar(prof,u->u.getProfessor().setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.get(4)))); vincular();
        criar("vinculado","PROFESSORES","Vinculado",List.of(prof.getProfessor().getId()),null).andExpect(status().isOk());
        alterar(prof,u->u.getPermissoesIndividuais().put("pedagogico.editar",false));
        criar("sem-permissao","PROFESSORES","Sem permissao",List.of(prof.getProfessor().getId()),null).andExpect(status().isBadRequest());
        alterar(outro,u->u.getProfessor().setAtivo(false));
        criar("inativo","PROFESSORES","Inativo",List.of(outro.getProfessor().getId()),null).andExpect(status().isBadRequest());
        criar("setor","SETOR","Por setor",null,"Ensino").andExpect(status().isOk());
        criar("todos","TODOS","Todos",null,null).andExpect(status().isOk());
    }
    @Test void criacaoIdempotenteRejeitaConflitosEDuplicatas() throws Exception {
        var ids=List.of(prof.getProfessor().getId());
        long id=ler(criar("mesma","PROFESSORES","Contexto",ids,null).andExpect(status().isOk()).andReturn()).get("id").asLong();
        criar("mesma","PROFESSORES","Contexto",ids,null).andExpect(status().isOk()).andExpect(jsonPath("id").value(id));
        criar("mesma","PROFESSORES","Outro",ids,null).andExpect(status().isConflict());
        criar("outra","PROFESSORES","Contexto",ids,null).andExpect(status().isConflict());
        criar("criterios","TODOS","Contexto",ids,null).andExpect(status().isBadRequest());
    }
    @Test void entregaOriginalIndependeDeArquivamentoECadastroPosterior() throws Exception {
        long id=solicitar(prof).get("id").asLong();
        JsonNode original=ler(entregar(id,prof,"original").andExpect(status().isOk()).andReturn());
        long pea=original.get("id").asLong();
        mvc.perform(post("/api/adaptacoes-pedagogicas/"+pea+"/arquivar").session(sessao(coord))).andExpect(status().isOk());
        alterar(prof,u->u.getProfessor().setNome("Nome posterior"));
        assertEquals(original,ler(entregar(id,prof,"original").andExpect(status().isOk()).andReturn()));
        entregar(id,prof,"outra").andExpect(status().isConflict());
        var alterado=conteudo(); alterado.put("objetivoGeral","Outro conteudo");
        mvc.perform(post(BASE+id+"/entregas").session(sessao(prof)).header("Idempotency-Key","original").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(alterado))).andExpect(status().isConflict());
        mvc.perform(delete("/api/adaptacoes-pedagogicas/"+pea).session(sessao(coord))).andExpect(status().isConflict());
        var edicao=conteudo(); edicao.put("estudanteId",estudante.getId()); edicao.put("professorId",prof.getProfessor().getId());
        mvc.perform(put("/api/adaptacoes-pedagogicas/"+pea).session(sessao(coord)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(edicao))).andExpect(status().isConflict());
    }
    @Test void cancelamentoPreservaEntregaERejeitaVersaoAntiga() throws Exception {
        var s=solicitar(prof,outro); long id=s.get("id").asLong();
        entregar(id,prof,"entrega").andExpect(status().isOk());
        cancelar(id,s.get("versao").asLong()).andExpect(status().isConflict());
        var atual=detalhes(id); assertTrue(atual.get("versao").asLong()>s.get("versao").asLong());
        cancelar(id,atual.get("versao").asLong()).andExpect(status().isOk()).andExpect(jsonPath("situacao").value("PARCIALMENTE_ENTREGUE_CANCELADA"));
        assertTrue(vinculos.existeVinculoDocenteAtivo(estudante.getId(),prof.getProfessor().getId()));
        assertTrue(vinculos.existeVinculoDocenteAtivo(estudante.getId(),outro.getProfessor().getId()));
        entregar(id,outro,"tardia").andExpect(status().isConflict());
        entregar(id,prof,"entrega").andExpect(status().isOk());
        assertEquals(1,jdbc.queryForObject("select count(*) from entregas_pea t join destinatarios_solicitacao_pea d on d.id=t.destinatario_id where d.solicitacao_id=?",Integer.class,id));
    }
    @Test void entregaECancelamentoSimultaneosNaoPerdemHistorico() throws Exception {
        var s=solicitar(prof,outro); long id=s.get("id").asLong();
        var inicio=new CountDownLatch(1); var pool=Executors.newFixedThreadPool(2);
        try {
            var entrega=pool.submit(()->{inicio.await();return entregar(id,prof,"corrida").andReturn().getResponse().getStatus();});
            var cancela=pool.submit(()->{inicio.await();return cancelar(id,s.get("versao").asLong()).andReturn().getResponse().getStatus();});
            inicio.countDown(); int e=entrega.get(30,TimeUnit.SECONDS), c=cancela.get(30,TimeUnit.SECONDS);
            assertTrue(e==200 && c==409 || e==409 && c==200,"entrega="+e+", cancelamento="+c);
            assertEquals(e==200?"PENDENTE":"CANCELADA",detalhes(id).get("situacao").asText());
        } finally { pool.shutdownNow(); }
    }
    @Test void entregasSimultaneasComMesmaChaveGeramUmaEntregaENotificacao() throws Exception {
        long id=solicitar(prof).get("id").asLong(); var inicio=new CountDownLatch(1); var pool=Executors.newFixedThreadPool(2);
        try {
            Callable<JsonNode> tarefa=()->{inicio.await();return ler(entregar(id,prof,"mesma").andExpect(status().isOk()).andReturn());};
            var a=pool.submit(tarefa); var b=pool.submit(tarefa); inicio.countDown(); assertEquals(a.get(30,TimeUnit.SECONDS),b.get(30,TimeUnit.SECONDS));
            assertEquals(1,jdbc.queryForObject("select count(*) from notificacoes_fluxo where solicitacao_pea_id=? and tipo_evento='PEA_ENTREGUE'",Integer.class,id));
        } finally {pool.shutdownNow();}
    }
    JsonNode espontaneo() throws Exception {
        vincular(); var r=conteudo(); r.put("estudanteId",estudante.getId()); r.put("professorId",prof.getProfessor().getId());
        mvc.perform(post("/api/adaptacoes-pedagogicas").session(sessao(prof)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(r))).andExpect(status().isOk());
        var lista=ler(mvc.perform(get(EVENTOS).session(sessao(coord))).andExpect(status().isOk()).andReturn());
        for(var e:lista) if(e.get("estudanteId").asLong()==estudante.getId()) return e;
        throw new AssertionError("Evento pendente ausente");
    }
    ResultActions encaminhar(JsonNode evento, Usuario autor, Usuario destino) throws Exception {
        return mvc.perform(post(EVENTOS+"/"+evento.get("id").asLong()+"/encaminhar").session(sessao(autor)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("versao",evento.get("versao").asLong(),"destinatarioUsuarioId",destino.getId()))));
    }
    @Test void eventoPendenteExigeCoordenacaoEEncaminhaUmaVezComAuditoria() throws Exception {
        var evento=espontaneo();
        assertEquals(0,jdbc.queryForObject("select count(*) from notificacoes_fluxo where estudante_id=?",Integer.class,estudante.getId()));
        mvc.perform(get(EVENTOS)).andExpect(status().isUnauthorized());
        mvc.perform(get(EVENTOS).session(sessao(prof))).andExpect(status().isForbidden());
        encaminhar(evento,admin,coord).andExpect(status().isForbidden());
        encaminhar(evento,coord,prof).andExpect(status().isBadRequest());
        encaminhar(evento,coord,coord).andExpect(status().isOk()).andExpect(jsonPath("encaminhadoPorId").value(coord.getId()));
        encaminhar(evento,coord,coord).andExpect(status().isConflict());
        assertEquals(1,jdbc.queryForObject("select count(*) from notificacoes_fluxo where estudante_id=?",Integer.class,estudante.getId()));
        assertEquals(1,jdbc.queryForObject("select count(*) from auditoria.eventos_pea_pendentes_aud where id=? and encaminhado_por_id=? and destinatario_id=?",Integer.class,evento.get("id").asLong(),coord.getId(),coord.getId()));
        assertEquals(1,jdbc.queryForObject("select count(*) from vinculos_estudante_profissional where estudante_id=?",Integer.class,estudante.getId()));
        alterar(coord,u->u.getPermissoesIndividuais().put("pei.editar",false));
        mvc.perform(get(EVENTOS).session(sessao(coord))).andExpect(status().isForbidden());
    }
    @Test void contaReferenciadaNaoPodeSerExcluidaMasPodeSerDesativada() throws Exception {
        solicitar(prof);
        var ex=assertThrows(org.springframework.web.server.ResponseStatusException.class,()->gestao.excluir(prof.getId(),coord.getId()));
        assertEquals(409,ex.getStatusCode().value()); assertFalse(gestao.situacao(prof.getId(),false,coord.getId()).ativo());
    }
    @Test void retencaoPreservaPrazoEDescartaDependenciasEAuditorias() throws Exception {
        long id=solicitar(prof,outro).get("id").asLong(); entregar(id,prof,"entrega").andExpect(status().isOk());
        var evento=espontaneo(); encaminhar(evento,coord,coord).andExpect(status().isOk());
        Long entregaId=jdbc.queryForObject("select t.id from entregas_pea t join destinatarios_solicitacao_pea d on d.id=t.destinatario_id where d.solicitacao_id=?",Long.class,id);
        Long destinatarioId=jdbc.queryForObject("select destinatario_id from entregas_pea where id=?",Long.class,entregaId);
        var outroEstudante=tx.execute(st->{var e=new Estudante(); e.setNome("Preservado");e.setMatricula(UUID.randomUUID().toString().substring(0,20));e.setCurso(CatalogoProfissional.CURSOS.get(0));e.setPeriodoIngresso("2026.1");e.setDataNascimento(LocalDate.of(2005,1,1));return estudantes.saveAndFlush(e);});
        retencao.descartar(estudante.getId()); assertTrue(estudantes.existsById(estudante.getId()));
        tx.executeWithoutResult(s->{var e=estudantes.findById(estudante.getId()).orElseThrow();e.concluir(LocalDate.of(2000,1,1));estudantes.saveAndFlush(e);});
        mvc.perform(get(BASE+id).session(sessao(coord))).andExpect(status().isGone());
        retencao.descartar(estudante.getId()); assertFalse(estudantes.existsById(estudante.getId()));
        for(String tabela:List.of("solicitacoes_pea","eventos_pea_pendentes")) {
            assertEquals(0,jdbc.queryForObject("select count(*) from "+tabela+" where estudante_id=?",Integer.class,estudante.getId()));
            assertEquals(0,jdbc.queryForObject("select count(*) from auditoria."+tabela+"_aud where estudante_id=?",Integer.class,estudante.getId()));
        }
        assertTrue(estudantes.existsById(outroEstudante.getId()));
        assertEquals(0,jdbc.queryForObject("select count(*) from entregas_pea where id=?",Integer.class,entregaId));
        assertEquals(0,jdbc.queryForObject("select count(*) from auditoria.entregas_pea_aud where destinatario_id=?",Integer.class,destinatarioId));
        assertEquals(0,jdbc.queryForObject("select count(*) from auditoria.destinatarios_solicitacao_pea_aud where solicitacao_id=?",Integer.class,id));
        assertEquals(0,jdbc.queryForObject("select count(*) from auditoria.eventos_pea_pendentes_aud where id=?",Integer.class,evento.get("id").asLong()));
    }
    @Test void autorizacaoEValidacaoRejeitamPerfisEDadosInadequados() throws Exception {
        mvc.perform(get(criacao()+"/elegiveis").session(sessao(prof))).andExpect(status().isForbidden());
        mvc.perform(post(criacao()).session(sessao(coord)).header("Idempotency-Key","x").contentType(MediaType.APPLICATION_JSON).content("{}" )).andExpect(status().isBadRequest());
        long id=solicitar(prof).get("id").asLong();
        var segundo=conta(PerfilUsuario.COORDENADOR);
        mvc.perform(get(BASE+id).session(sessao(segundo))).andExpect(status().isForbidden());
        alterar(prof,u->u.getPermissoesIndividuais().put("pedagogico.editar",false)); entregar(id,prof,"negada").andExpect(status().isForbidden());
    }
    @Test void historicoConcluidoPreservaConsultaMasImpedeSolicitarEntregarECancelar() throws Exception {
        var solicitacao=solicitar(prof); long id=solicitacao.get("id").asLong();
        tx.executeWithoutResult(st->{var e=estudantes.findById(estudante.getId()).orElseThrow();e.concluir(LocalDate.now());estudantes.saveAndFlush(e);});
        mvc.perform(get(BASE+id).session(sessao(prof))).andExpect(status().isOk());
        entregar(id,prof,"concluido").andExpect(status().isConflict());
        cancelar(id,solicitacao.get("versao").asLong()).andExpect(status().isConflict());
        criar("concluido","TODOS","Novo",null,null).andExpect(status().isConflict());
        assertEquals("PENDENTE",detalhes(id).get("situacao").asText());
    }
    @Test void encaminhamentosSimultaneosEnviamUmaNotificacao() throws Exception {
        var evento=espontaneo(); var inicio=new CountDownLatch(1); var pool=Executors.newFixedThreadPool(2);
        try {
            Callable<Integer> tarefa=()->{inicio.await();return encaminhar(evento,coord,coord).andReturn().getResponse().getStatus();};
            var a=pool.submit(tarefa);var b=pool.submit(tarefa);inicio.countDown();
            assertEquals(Set.of(200,409),Set.of(a.get(30,TimeUnit.SECONDS),b.get(30,TimeUnit.SECONDS)));
            assertEquals(1,jdbc.queryForObject("select count(*) from notificacoes_fluxo where estudante_id=?",Integer.class,estudante.getId()));
        } finally {pool.shutdownNow();}
    }
    @Test void eventoNaoPodeSerEnviadoAContaInativaSemPermissaoOuAposRetencao() throws Exception {
        var evento=espontaneo();var destino=conta(PerfilUsuario.COORDENADOR);
        alterar(destino,u->u.getCoordenador().setStatus(br.com.portalincluir.enums.StatusCoordenador.INATIVO));
        encaminhar(evento,coord,destino).andExpect(status().isBadRequest());
        alterar(destino,u->{u.getCoordenador().setStatus(br.com.portalincluir.enums.StatusCoordenador.ATIVO);u.getPermissoesIndividuais().put("pedagogico.consultar",false);});
        encaminhar(evento,coord,destino).andExpect(status().isBadRequest());
        tx.executeWithoutResult(st->{var e=estudantes.findById(estudante.getId()).orElseThrow();e.concluir(LocalDate.of(2000,1,1));estudantes.saveAndFlush(e);});
        encaminhar(evento,coord,coord).andExpect(status().isGone());
        var lista=ler(mvc.perform(get(EVENTOS).session(sessao(coord))).andExpect(status().isOk()).andReturn());
        assertFalse(java.util.stream.StreamSupport.stream(lista.spliterator(),false).anyMatch(e->e.get("id").asLong()==evento.get("id").asLong()));
    }
    @Test void cursoEBnccSaoInformativosESetorSelecionaDestinatarios() throws Exception {
        alterar(prof,u->{u.getProfessor().setCursosAtuacao(Set.of(CatalogoProfissional.BNCC));u.getProfessor().setSetor("Setor exclusivo "+estudante.getId());});
        var s=ler(criar("setor-exclusivo","SETOR","Selecao por setor",null,"Setor exclusivo "+estudante.getId()).andExpect(status().isOk()).andReturn());
        assertEquals(1,s.get("destinatarios").size());assertEquals(prof.getProfessor().getId(),s.at("/destinatarios/0/professorId").asLong());
        tx.executeWithoutResult(st->{var e=estudantes.findById(estudante.getId()).orElseThrow();e.setCurso(CatalogoProfissional.CURSOS.get(3));estudantes.saveAndFlush(e);});
        criar("bncc-superior","PROFESSORES","Superior",List.of(prof.getProfessor().getId()),null).andExpect(status().isOk());
    }

    ResultActions pedirComVinculo(String chave, boolean criar, String motivo, Usuario... docentes) throws Exception {
        var body=new HashMap<String,Object>(); body.put("modalidade","PROFESSORES"); body.put("contexto","Acompanhamento");
        body.put("professorIds",Arrays.stream(docentes).map(u->u.getProfessor().getId()).toList());
        body.put("criarVinculoAcompanhamento",criar); if(motivo!=null) body.put("motivoVinculo",motivo);
        return mvc.perform(post(criacao()).session(sessao(coord)).header("Idempotency-Key",chave).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body)));
    }
    int totalVinculos() { return jdbc.queryForObject("select count(*) from vinculos_estudante_profissional where estudante_id=?",Integer.class,estudante.getId()); }

    @Test void institucionalIncluiOutroCursoNasTresModalidadesComAcompanhamento() throws Exception {
        alterar(prof,u->{u.getProfessor().setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.get(4)));u.getProfessor().setSetor("Setor "+estudante.getId());});
        var lista=ler(mvc.perform(get(criacao()+"/elegiveis").session(sessao(coord))).andExpect(status().isOk()).andReturn());
        var item=java.util.stream.StreamSupport.stream(lista.spliterator(),false).filter(n->n.get("professorId").asLong()==prof.getProfessor().getId()).findFirst().orElseThrow();
        assertFalse(item.get("vinculado").asBoolean()); assertFalse(item.get("mesmoCurso").asBoolean());
        var espontaneo=conteudo();espontaneo.put("estudanteId",estudante.getId());espontaneo.put("professorId",prof.getProfessor().getId());
        mvc.perform(post("/api/adaptacoes-pedagogicas").session(sessao(prof)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(espontaneo))).andExpect(status().isForbidden());
        criar("especifico","PROFESSORES","Especifico",List.of(prof.getProfessor().getId()),null).andExpect(status().isOk());
        criar("setor-institucional","SETOR","Setor institucional",null,"Setor "+estudante.getId()).andExpect(status().isOk()).andExpect(jsonPath("destinatarios.length()").value(1));
        var todos=ler(criar("todos-institucional","TODOS","Todos institucional",null,null).andExpect(status().isOk()).andReturn());
        assertTrue(java.util.stream.StreamSupport.stream(todos.get("destinatarios").spliterator(),false).anyMatch(n->n.get("professorId").asLong()==prof.getProfessor().getId()));
        assertEquals(todos.get("destinatarios").size(),totalVinculos());
        mvc.perform(get("/api/estudantes/"+estudante.getId()).session(sessao(prof))).andExpect(status().isOk());
    }
    @Test void novaSolicitacaoExigePermissaoDeVinculoMesmoComFlagAntigaFalse() throws Exception {
        alterar(coord,u->u.getPermissoesIndividuais().put("estudantes.vincular",false));
        pedirComVinculo("sem-permissao",true,"Acompanhar",prof).andExpect(status().isForbidden());
        pedirComVinculo("sem-vinculo",false,null,prof).andExpect(status().isForbidden());
        assertEquals(0,totalVinculos());
        alterar(coord,u->u.getPermissoesIndividuais().put("estudantes.vincular",true));
        pedirComVinculo("automatico",false,null,prof).andExpect(status().isOk());
        assertEquals(1,totalVinculos());
    }
    @Test void criaVinculoComAutoriaEAuditoriaSemConcederPermissoesENaoDuplicaNaRepeticao() throws Exception {
        alterar(prof,u->{u.getProfessor().setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.get(4)));u.getPermissoesIndividuais().put("sensivel.consultar",false);});
        var antes=jdbc.queryForList("select permissao,permitido from usuario_permissoes where usuario_id=? order by permissao",prof.getId());
        var primeira=ler(pedirComVinculo("vincular",true,"Acompanhamento autorizado",prof).andExpect(status().isOk()).andReturn());
        pedirComVinculo("vincular",true,"Acompanhamento autorizado",prof).andExpect(status().isOk()).andExpect(jsonPath("id").value(primeira.get("id").asLong()));
        assertEquals(1,totalVinculos());
        var v=tx.execute(st->vinculos.findByEstudanteIdAndProfessorId(estudante.getId(),prof.getProfessor().getId()).orElseThrow());
        assertEquals(coord.getId(),v.getResponsavelUsuarioId());assertEquals("Vínculo automático pela solicitação de PEA #"+primeira.get("id").asLong()+" — contexto: Acompanhamento",v.getMotivo());
        assertEquals(1,jdbc.queryForObject("select count(*) from auditoria.vinculos_estudante_profissional_aud where id=?",Integer.class,v.getId()));
        assertEquals(antes,jdbc.queryForList("select permissao,permitido from usuario_permissoes where usuario_id=? order by permissao",prof.getId()));
        mvc.perform(get("/api/estudantes/"+estudante.getId()).session(sessao(prof))).andExpect(status().isOk());
        pedirComVinculo("vincular",false,null,prof).andExpect(status().isOk());
        pedirComVinculo("vincular",true,"Outro motivo",prof).andExpect(status().isOk());
    }
    @Test void preservaVinculoAtivoEReativaEncerradoPeloServicoExistente() throws Exception {
        vincular();
        var ativo=tx.execute(st->vinculos.findByEstudanteIdAndProfessorId(estudante.getId(),prof.getProfessor().getId()).orElseThrow());
        tx.executeWithoutResult(st->{var v=new VinculoEstudanteProfissional();v.setEstudante(estudante);v.setProfessor(outro.getProfessor());v.setAtivo(false);v.setMotivo("Anterior");v.setDataInicio(LocalDateTime.now());v.setDataEncerramento(LocalDateTime.now());v.setMotivoEncerramento("Encerrado");v.setResponsavelUsuarioId(coord.getId());vinculos.saveAndFlush(v);});
        var encerrado=tx.execute(st->vinculos.findByEstudanteIdAndProfessorId(estudante.getId(),outro.getProfessor().getId()).orElseThrow());
        var nova=ler(pedirComVinculo("reativar",true,"Novo acompanhamento",prof,outro).andExpect(status().isOk()).andReturn());
        assertEquals(2,totalVinculos());
        var preservado=tx.execute(st->vinculos.findById(ativo.getId()).orElseThrow());assertEquals(ativo.getMotivo(),preservado.getMotivo());assertEquals(ativo.getDataInicio(),preservado.getDataInicio());assertEquals(ativo.getVersao(),preservado.getVersao());
        var reativado=tx.execute(st->vinculos.findById(encerrado.getId()).orElseThrow());assertTrue(reativado.isAtivo());assertNull(reativado.getDataEncerramento());assertEquals("Vínculo automático pela solicitação de PEA #"+nova.get("id").asLong()+" — contexto: Acompanhamento",reativado.getMotivo());
        assertEquals(2,jdbc.queryForObject("select count(*) from auditoria.vinculos_estudante_profissional_aud where id=?",Integer.class,encerrado.getId()));
    }
    @Test void falhaPosteriorReverteVinculoSolicitacaoDestinatarioENotificacao() throws Exception {
        org.mockito.Mockito.doThrow(new IllegalStateException("Falha simulada de notificação")).when(notificacoesPea).solicitada(org.mockito.ArgumentMatchers.any());
        assertThrows(jakarta.servlet.ServletException.class,()->pedirComVinculo("rollback",true,"Acompanhar",prof,outro).andReturn());
        assertEquals(0,totalVinculos());
        assertEquals(0,jdbc.queryForObject("select count(*) from destinatarios_solicitacao_pea where professor_id in (?,?)",Integer.class,prof.getProfessor().getId(),outro.getProfessor().getId()));
        for(String tabela:List.of("solicitacoes_pea","notificacoes_fluxo")) assertEquals(0,jdbc.queryForObject("select count(*) from "+tabela+" where estudante_id=?",Integer.class,estudante.getId()));
        assertEquals(0,jdbc.queryForObject("select count(*) from auditoria.vinculos_estudante_profissional_aud where estudante_id=?",Integer.class,estudante.getId()));
    }
    long legada(String chave, boolean comVinculo, String motivo) throws Exception {
        var parametros=new ArrayList<Object>(List.of(estudante.getId(),"PROFESSORES","acompanhamento","","",new TreeSet<>(Set.of(prof.getProfessor().getId()))));
        if(comVinculo) parametros.add(List.of("VINCULO_ACOMPANHAMENTO",motivo.strip()));
        String fingerprint=HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(json.writeValueAsString(parametros).getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        return tx.execute(st->{
            var s=new SolicitacaoPea();s.setEstudante(estudante);s.setSolicitante(coord);s.setContexto("Acompanhamento");s.setModalidade("PROFESSORES");s.setCriadaEm(LocalDateTime.now());s.setChaveIdempotencia(chave);s.setHashRequisicao(fingerprint);solicitacoes.saveAndFlush(s);
            var d=new DestinatarioSolicitacaoPea();d.setSolicitacao(s);d.setProfessor(prof.getProfessor());d.setUsuario(prof);d.setSetorNoEnvio("Ensino");d.setChavePendente("legada-"+s.getId());destinatarios.saveAndFlush(d);return s.getId();
        });
    }
    @Test void idempotenciaLegadaNaoCriaVinculoRetroativamenteNemExigeNovaPermissao() throws Exception {
        long id=legada("legada",false,null);
        alterar(coord,u->u.getPermissoesIndividuais().put("estudantes.vincular",false));
        pedirComVinculo("legada",false,null,prof).andExpect(status().isOk()).andExpect(jsonPath("id").value(id));
        var r=Map.of("modalidade","PROFESSORES","contexto","Acompanhamento","professorIds",List.of(prof.getProfessor().getId()));
        mvc.perform(post(criacao()).session(sessao(coord)).header("Idempotency-Key","legada").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(r))).andExpect(status().isOk()).andExpect(jsonPath("id").value(id));
        assertEquals(0,totalVinculos());
        entregar(id,prof,"entrega-legada").andExpect(status().isOk());
        assertEquals(0,totalVinculos());
    }
    @Test void reenvioLegadoNaoReativaVinculoEncerrado() throws Exception {
        long id=legada("legada-vinculada",true,"Motivo antigo");vincular();
        tx.executeWithoutResult(st->{var v=vinculos.findByEstudanteIdAndProfessorId(estudante.getId(),prof.getProfessor().getId()).orElseThrow();v.setAtivo(false);v.setDataEncerramento(LocalDateTime.now());v.setMotivoEncerramento("Fim");vinculos.saveAndFlush(v);});
        long revisoes=jdbc.queryForObject("select count(*) from auditoria.vinculos_estudante_profissional_aud where estudante_id=?",Long.class,estudante.getId());
        pedirComVinculo("legada-vinculada",true,"Motivo antigo",prof).andExpect(status().isOk()).andExpect(jsonPath("id").value(id));
        assertFalse(vinculos.existeVinculoDocenteAtivo(estudante.getId(),prof.getProfessor().getId()));
        assertEquals(revisoes,jdbc.queryForObject("select count(*) from auditoria.vinculos_estudante_profissional_aud where estudante_id=?",Long.class,estudante.getId()));
        pedirComVinculo("legada-vinculada",true,"Motivo alterado",prof).andExpect(status().isConflict());
    }
    @Test void consultarPeaAnteriorNaoEntregaNemReutilizaDocumentoAntigo() throws Exception {
        vincular();var r=conteudo();r.put("estudanteId",estudante.getId());r.put("professorId",prof.getProfessor().getId());
        long antigo=ler(mvc.perform(post("/api/adaptacoes-pedagogicas").session(sessao(prof)).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(r))).andExpect(status().isOk()).andReturn()).get("id").asLong();
        long id=solicitar(prof).get("id").asLong();
        mvc.perform(get("/api/adaptacoes-pedagogicas/estudante/"+estudante.getId()+"/professor/"+prof.getProfessor().getId()).session(sessao(prof))).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(antigo));
        assertEquals("PENDENTE",detalhes(id).get("situacao").asText());
        assertEquals(0,jdbc.queryForObject("select count(*) from entregas_pea t join destinatarios_solicitacao_pea d on d.id=t.destinatario_id where d.solicitacao_id=?",Integer.class,id));
        long novo=ler(entregar(id,prof,"nova-entrega").andExpect(status().isOk()).andReturn()).get("id").asLong();assertNotEquals(antigo,novo);
    }
    @Test void corsPermiteIdempotencyKeySemAmpliarOrigens() throws Exception {
        mvc.perform(options(criacao()).header("Origin","http://localhost:5173").header("Access-Control-Request-Method","POST").header("Access-Control-Request-Headers","content-type,idempotency-key")).andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Headers",org.hamcrest.Matchers.containsString("idempotency-key")));
        mvc.perform(options(criacao()).header("Origin","https://nao-autorizado.example").header("Access-Control-Request-Method","POST").header("Access-Control-Request-Headers","idempotency-key")).andExpect(status().isForbidden());
    }

    @Test void vinculoAutomaticoEmSetorETodosSegueOsDestinatariosEfetivos() throws Exception {
        String setor="Setor vínculo "+estudante.getId();
        for(var docente:List.of(prof,outro)) alterar(docente,u->{u.getProfessor().setSetor(setor);u.getProfessor().setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.get(4)));});
        var body=new HashMap<String,Object>();body.put("modalidade","SETOR");body.put("contexto","Setor com vínculo");body.put("setor",setor);body.put("criarVinculoAcompanhamento",true);body.put("motivoVinculo","Acompanhamento institucional");
        mvc.perform(post(criacao()).session(sessao(coord)).header("Idempotency-Key","setor-vinculo").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body))).andExpect(status().isOk()).andExpect(jsonPath("destinatarios.length()").value(2));
        assertEquals(2,totalVinculos());
        body.put("modalidade","TODOS");body.put("contexto","Todos com vínculo");body.remove("setor");
        var todos=ler(mvc.perform(post(criacao()).session(sessao(coord)).header("Idempotency-Key","todos-vinculo").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body))).andExpect(status().isOk()).andReturn());
        assertEquals(todos.get("destinatarios").size(),totalVinculos());
    }
    @Test void pedidosSimultaneosIdempotentesCriamUmVinculoEUmaSolicitacao() throws Exception {
        var inicio=new CountDownLatch(1);var pool=Executors.newFixedThreadPool(2);
        try {
            Callable<JsonNode> tarefa=()->{inicio.await();return ler(pedirComVinculo("simultaneo",true,"Acompanhar",prof).andExpect(status().isOk()).andReturn());};
            var a=pool.submit(tarefa);var b=pool.submit(tarefa);inicio.countDown();
            assertEquals(a.get(30,TimeUnit.SECONDS).get("id"),b.get(30,TimeUnit.SECONDS).get("id"));
            assertEquals(1,totalVinculos());
            assertEquals(1,jdbc.queryForObject("select count(*) from solicitacoes_pea where estudante_id=?",Integer.class,estudante.getId()));
            assertEquals(1,jdbc.queryForObject("select count(*) from notificacoes_fluxo where estudante_id=?",Integer.class,estudante.getId()));
        } finally {pool.shutdownNow();}
    }

}
