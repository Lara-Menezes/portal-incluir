package br.com.portalincluir.controller;

import br.com.portalincluir.config.CatalogoProfissional;
import br.com.portalincluir.dto.request.CadastroUsuarioRequest;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.model.Usuario;
import br.com.portalincluir.repository.*;
import br.com.portalincluir.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:recuperacao;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS auditoria",
    "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never", "spring.jpa.show-sql=false", "logging.level.root=ERROR",
    "spring.mail.test-connection=false", "app.recuperacao-senha.frontend-url=https://portal.example.test"
})
@AutoConfigureMockMvc
class RecuperacaoSenhaTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioService cadastro;
    @Autowired UsuarioRepository usuarios;
    @Autowired RecuperacaoSenhaRepository tokens;
    @Autowired ProfessorRepository professores;
    @Autowired RecuperacaoSenhaService service;
    @Autowired PasswordEncoder encoder;
    @MockBean SmtpEmailClient emails;
    @MockBean(name = "recuperacaoSenhaExecutor") ThreadPoolTaskExecutor executor;
    private String ip;

    @BeforeEach void preparar() {
        ip = UUID.randomUUID().toString();
        when(emails.isConfigurado()).thenReturn(true);
        // Executa a tarefa em linha; o envio continua ocorrendo somente depois do commit real.
        doAnswer(i -> { i.getArgument(0, Runnable.class).run(); return null; }).when(executor).execute(any(Runnable.class));
    }

    private Usuario usuario() {
        var r = new CadastroUsuarioRequest();
        String unico = UUID.randomUUID().toString();
        r.setNome("Pessoa Teste"); r.setEmail(unico + "@example.invalid"); r.setSenha("senha123");
        r.setPerfil(PerfilUsuario.PROFESSOR); r.setMatricula(unico.substring(0, 20)); r.setSetor("Ensino");
        r.setCursosAtuacao(Set.of(CatalogoProfissional.CURSOS.get(0)));
        cadastro.cadastrar(r);
        return usuarios.findByEmailIgnoreCase(r.getEmail()).orElseThrow();
    }
    private ResultActions solicitar(String email) throws Exception {
        return mvc.perform(post("/api/auth/recuperar-senha").with(r -> { r.setRemoteAddr(ip); return r; })
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("email", email))));
    }
    private String emitir(Usuario u) throws Exception {
        solicitar(u.getEmail()).andExpect(status().isAccepted()).andExpect(header().string("Cache-Control", "no-store"));
        var link = ArgumentCaptor.forClass(String.class);
        verify(emails, atLeastOnce()).enviarRecuperacaoSenha(eq(u.getEmail()), link.capture());
        String url = link.getValue();
        assertTrue(url.startsWith("https://portal.example.test/redefinir-senha#token="));
        return url.substring(url.indexOf("#token=") + 7);
    }
    private ResultActions redefinir(String token, String senha) throws Exception {
        return mvc.perform(post("/api/auth/redefinir-senha").with(r -> { r.setRemoteAddr(ip); return r; })
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("token", token, "novaSenha", senha))));
    }
    private ResultActions login(Usuario u, String senha) throws Exception {
        return mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(Map.of("email", u.getEmail(), "senha", senha))));
    }
    private String hash(String token) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
    }

    @Test void respostaNaoRevelaExistenciaDaContaENaoAlteraSenhaNaSolicitacao() throws Exception {
        var u = usuario();
        String conhecida = solicitar(u.getEmail()).andExpect(status().isAccepted()).andReturn().getResponse().getContentAsString();
        String ausente = solicitar("ausente@example.invalid").andExpect(status().isAccepted()).andReturn().getResponse().getContentAsString();
        assertEquals(conhecida, ausente);
        verify(emails, times(1)).enviarRecuperacaoSenha(eq(u.getEmail()), anyString());
        verify(emails, never()).enviarRecuperacaoSenha(eq("ausente@example.invalid"), anyString());
        assertTrue(encoder.matches("senha123", usuarios.findById(u.getId()).orElseThrow().getSenha()));
    }

    @Test void tokenArmazenadoComoHashUsadoUmaVezERevogaSessoesAntigas() throws Exception {
        var u = usuario();
        var sessao = (MockHttpSession) login(u, "senha123").andExpect(status().isOk()).andReturn().getRequest().getSession(false);
        var legada = new MockHttpSession(); legada.setAttribute("usuarioAutenticadoId", u.getId());
        String token = emitir(u);
        var registro = tokens.findByHashToken(hash(token)).orElseThrow();
        assertNotEquals(token, registro.getHashToken()); assertEquals(64, registro.getHashToken().length());
        mvc.perform(get("/api/auth/me").session(sessao)).andExpect(status().isOk());
        redefinir(token, "novaSenha123").andExpect(status().isOk());
        assertNotNull(tokens.findById(registro.getId()).orElseThrow().getUsadoEm());
        assertEquals(1, usuarios.findById(u.getId()).orElseThrow().getVersaoCredenciais());
        redefinir(token, "outraSenha123").andExpect(status().isBadRequest());
        mvc.perform(get("/api/auth/me").session(sessao)).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/auth/me").session(legada)).andExpect(status().isUnauthorized());
        assertTrue(sessao.isInvalid()); assertTrue(legada.isInvalid());
        login(u, "senha123").andExpect(status().isUnauthorized());
        var nova = (MockHttpSession) login(u, "novaSenha123").andExpect(status().isOk()).andReturn().getRequest().getSession(false);
        mvc.perform(get("/api/auth/me").session(nova)).andExpect(status().isOk());
    }

    @Test void novaSolicitacaoRevogaLinkAnterior() throws Exception {
        var u = usuario(); String primeiro = emitir(u); String segundo = emitir(u);
        assertNotEquals(primeiro, segundo);
        assertNotNull(tokens.findByHashToken(hash(primeiro)).orElseThrow().getRevogadoEm());
        redefinir(primeiro, "novaSenha123").andExpect(status().isBadRequest());
        redefinir(segundo, "novaSenha123").andExpect(status().isOk());
    }

    @Test void linkExpiradoOuDesconhecidoNaoTrocaSenha() throws Exception {
        var u = usuario(); String token = emitir(u);
        var registro = tokens.findByHashToken(hash(token)).orElseThrow();
        registro.setExpiraEm(Instant.now().minusSeconds(1)); tokens.saveAndFlush(registro);
        redefinir(token, "novaSenha123").andExpect(status().isBadRequest());
        redefinir("A".repeat(43), "novaSenha123").andExpect(status().isBadRequest());
        assertTrue(encoder.matches("senha123", usuarios.findById(u.getId()).orElseThrow().getSenha()));
    }

    @Test void validacaoDeSenhaNaoConsomeToken() throws Exception {
        var u = usuario(); String token = emitir(u);
        redefinir(token, "123").andExpect(status().isBadRequest());
        redefinir(token, "á".repeat(37)).andExpect(status().isBadRequest());
        redefinir("invalido", "novaSenha123").andExpect(status().isBadRequest());
        assertNull(tokens.findByHashToken(hash(token)).orElseThrow().getUsadoEm());
        redefinir(token, "novaSenha123").andExpect(status().isOk());
    }

    @Test void mudarEmailInvalidaLinkEmitido() throws Exception {
        var u = usuario(); String token = emitir(u);
        u.setEmail(UUID.randomUUID() + "@example.invalid"); usuarios.saveAndFlush(u);
        redefinir(token, "novaSenha123").andExpect(status().isBadRequest());
        assertTrue(encoder.matches("senha123", usuarios.findById(u.getId()).orElseThrow().getSenha()));
    }

    @Test void smtpIndisponivelNaoRevelaContaNemTrocaSenha() throws Exception {
        var u = usuario();
        doThrow(new IllegalStateException("SMTP simulado")).when(emails).enviarRecuperacaoSenha(anyString(), anyString());
        solicitar(u.getEmail()).andExpect(status().isAccepted());
        assertTrue(encoder.matches("senha123", usuarios.findById(u.getId()).orElseThrow().getSenha()));
    }

    @Test void contaInativadaNaoRecebeLinkENaoPodeUsarLinkAnterior() throws Exception {
        var u = usuario(); String token = emitir(u);
        var professor = professores.findById(u.getProfessor().getId()).orElseThrow();
        professor.setAtivo(false); professores.saveAndFlush(professor);
        redefinir(token, "novaSenha123").andExpect(status().isBadRequest());
        solicitar(u.getEmail()).andExpect(status().isAccepted());
        verify(emails, times(1)).enviarRecuperacaoSenha(eq(u.getEmail()), anyString());
        assertTrue(encoder.matches("senha123", usuarios.findById(u.getId()).orElseThrow().getSenha()));
    }

    @Test void contaTecnicaESmtpNaoConfiguradoNaoGeramTokens() throws Exception {
        var root = usuarios.findByRootTrue().orElseThrow();
        solicitar(root.getEmail()).andExpect(status().isAccepted());
        verify(emails, never()).enviarRecuperacaoSenha(eq(root.getEmail()), anyString());
        var u = usuario(); when(emails.isConfigurado()).thenReturn(false);
        solicitar(u.getEmail()).andExpect(status().isAccepted());
        verify(emails, never()).enviarRecuperacaoSenha(eq(u.getEmail()), anyString());
        assertTrue(tokens.findByUsuarioIdAndUsadoEmIsNullAndRevogadoEmIsNull(u.getId()).isEmpty());
    }

    @Test void redefinirUmaContaPreservaSessaoDeOutra() throws Exception {
        var u = usuario(); var outra = usuario();
        var sessao = (MockHttpSession) login(outra, "senha123").andExpect(status().isOk()).andReturn().getRequest().getSession(false);
        redefinir(emitir(u), "novaSenha123").andExpect(status().isOk());
        mvc.perform(get("/api/auth/me").session(sessao)).andExpect(status().isOk());
        assertFalse(sessao.isInvalid());
    }

    @Test void repeticoesSaoLimitadasSemRevelarConta() throws Exception {
        var u = usuario();
        for (int i = 0; i < 4; i++) solicitar(u.getEmail()).andExpect(status().isAccepted());
        verify(emails, times(3)).enviarRecuperacaoSenha(eq(u.getEmail()), anyString());
    }

    @Test void usoConcorrenteDoMesmoLinkPermiteApenasUmaTroca() throws Exception {
        var u = usuario(); String token = emitir(u);
        var iniciar = new CountDownLatch(1);
        try (var pool = Executors.newFixedThreadPool(2)) {
            Callable<Boolean> tentativa = () -> {
                iniciar.await();
                try { service.redefinir(token, "novaSenha123"); return true; }
                catch (ResponseStatusException e) { assertEquals(400, e.getStatusCode().value()); return false; }
            };
            var a = pool.submit(tentativa); var b = pool.submit(tentativa); iniciar.countDown();
            assertNotEquals(a.get(20, TimeUnit.SECONDS), b.get(20, TimeUnit.SECONDS));
        }
        assertEquals(1, usuarios.findById(u.getId()).orElseThrow().getVersaoCredenciais());
    }
}
