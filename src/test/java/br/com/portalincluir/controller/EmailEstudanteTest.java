package br.com.portalincluir.controller;

import br.com.portalincluir.dto.request.CadastroUsuarioRequest;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.model.EmailEnvio;
import br.com.portalincluir.model.Estudante;
import br.com.portalincluir.repository.EmailEnvioRepository;
import br.com.portalincluir.repository.EstudanteRepository;
import br.com.portalincluir.repository.UsuarioRepository;
import br.com.portalincluir.service.SmtpEmailClient;
import br.com.portalincluir.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:email;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS auditoria",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never",
        "spring.mail.host=smtp.test",
        "spring.mail.username=test-user",
        "spring.mail.password=test-password",
        "app.email.smtp.from=Portal <portal@example.test>"
})
@AutoConfigureMockMvc
class EmailEstudanteTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioService usuariosService;
    @Autowired UsuarioRepository usuarios;
    @Autowired EstudanteRepository estudantes;
    @Autowired EmailEnvioRepository envios;
    @MockBean SmtpEmailClient emailClient;

    @BeforeEach
    void limparEnvios() {
        envios.deleteAll();
        reset(emailClient);
        when(emailClient.isConfigurado()).thenReturn(true);
    }

    private Long criarUsuario(PerfilUsuario perfil) {
        String sufixo = UUID.randomUUID().toString().substring(0, 8);
        CadastroUsuarioRequest request = new CadastroUsuarioRequest();
        request.setEmail(perfil.name().toLowerCase() + "-" + sufixo + "@test.example");
        request.setNome("Pessoa Autorizada");
        request.setSenha("senha123");
        request.setPerfil(perfil);
        request.setMatricula("mat-" + sufixo);
        request.setSetor("CLAI");
        request.setCargo("Coordenação");
        request.setDisciplina("História");
        request.setCursosAtuacao(Set.of(br.com.portalincluir.config.CatalogoProfissional.CURSOS.get(0)));
        return usuariosService.cadastrar(request).id();
    }

    private Long criarEstudante(String email) {
        String sufixo = UUID.randomUUID().toString().substring(0, 8);
        Estudante estudante = new Estudante();
        estudante.setNome("Estudante Teste");
        estudante.setDataNascimento(LocalDate.of(2000, 1, 1));
        estudante.setCurso("Curso de teste");
        estudante.setMatricula("est-" + sufixo);
        estudante.setPeriodoIngresso("2025.1");
        estudante.setEmail(email);
        return estudantes.saveAndFlush(estudante).getId();
    }

    private MockHttpSession sessao(Long usuarioId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioAutenticadoId", usuarioId);
        return session;
    }

    private String corpoEmail(String assunto, String mensagem) throws Exception {
        return json.writeValueAsString(java.util.Map.of("assunto", assunto, "mensagem", mensagem));
    }

    @Test
    void bloqueiaUsuarioSemPermissaoDeEnvio() throws Exception {
        Long professor = criarUsuario(PerfilUsuario.PROFESSOR);
        Long estudante = criarEstudante("estudante@test.example");

        mvc.perform(post("/api/estudantes/" + estudante + "/email")
                        .session(sessao(professor))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoEmail("Contato", "Mensagem")))
                .andExpect(status().isForbidden());

        verify(emailClient, never()).enviar(anyString(), anyString(), anyString());
        assertTrue(envios.findAll().isEmpty());
    }

    @Test
    void impedeEnvioParaEnderecoInvalido() throws Exception {
        Long coordenador = criarUsuario(PerfilUsuario.COORDENADOR);
        Long estudante = criarEstudante("endereco-invalido");

        mvc.perform(post("/api/estudantes/" + estudante + "/email")
                        .session(sessao(coordenador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoEmail("Contato", "Mensagem")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O estudante não possui um endereço de e-mail válido cadastrado."));

        verify(emailClient, never()).enviar(anyString(), anyString(), anyString());
        assertTrue(envios.findAll().isEmpty());
    }

    @Test
    void informaSomenteVariaveisSmtpAusentes() throws Exception {
        Long coordenador = criarUsuario(PerfilUsuario.COORDENADOR);
        Long estudante = criarEstudante("estudante-" + UUID.randomUUID() + "@test.example");
        when(emailClient.isConfigurado()).thenReturn(false);
        when(emailClient.variaveisNaoConfiguradas()).thenReturn(java.util.List.of("MAIL_PASSWORD", "MAIL_FROM"));

        mvc.perform(post("/api/estudantes/" + estudante + "/email")
                        .session(sessao(coordenador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoEmail("Contato", "Mensagem")))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value(
                        "Defina no ambiente do backend as variáveis SMTP ausentes: MAIL_PASSWORD, MAIL_FROM."));

        verify(emailClient, never()).enviar(anyString(), anyString(), anyString());
    }

    @Test
    void enviaPorSmtpERegistraSomenteMetadados() throws Exception {
        Long coordenador = criarUsuario(PerfilUsuario.COORDENADOR);
        String destinatario = "estudante-" + UUID.randomUUID() + "@test.example";
        Long estudante = criarEstudante(destinatario);

        mvc.perform(post("/api/estudantes/" + estudante + "/email")
                        .session(sessao(coordenador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoEmail("  Contato  ", "  Mensagem revisada  ")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destinatario").value(destinatario))
                .andExpect(jsonPath("$.enviadoEm").isNotEmpty());

        verify(emailClient).enviar(destinatario, "Contato", "Mensagem revisada");
        EmailEnvio envio = envios.findAll().get(0);
        assertEquals(estudante, envio.getEstudanteId());
        assertEquals(coordenador, envio.getRemetenteId());
        assertEquals("Pessoa Autorizada", envio.getRemetenteNome());
        assertEquals(usuarios.findById(coordenador).orElseThrow().getEmail(), envio.getRemetenteEmail());
        assertEquals(destinatario, envio.getDestinatarioEmail());
        assertNotNull(envio.getEnviadoEm());
    }

    @Test
    void informaFalhaDeAutenticacaoSmtp() throws Exception {
        Long coordenador = criarUsuario(PerfilUsuario.COORDENADOR);
        Long estudante = criarEstudante("estudante-" + UUID.randomUUID() + "@test.example");
        doThrow(new br.com.portalincluir.service.EmailDeliveryException(
                new org.springframework.mail.MailAuthenticationException("Falha de autenticação")))
                .when(emailClient).enviar(anyString(), anyString(), anyString());

        mvc.perform(post("/api/estudantes/" + estudante + "/email")
                        .session(sessao(coordenador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoEmail("Contato", "Mensagem")))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value("Não foi possível enviar pelo SMTP. Confira as credenciais do Mailtrap, host, porta e STARTTLS no backend."));

        assertTrue(envios.findAll().isEmpty());
    }

    @Test
    void informaFalhaDeTransporteSmtp() throws Exception {
        Long coordenador = criarUsuario(PerfilUsuario.COORDENADOR);
        Long estudante = criarEstudante("estudante-" + UUID.randomUUID() + "@test.example");
        doThrow(new br.com.portalincluir.service.EmailDeliveryException(
                new org.springframework.mail.MailSendException("Falha na conexão")))
                .when(emailClient).enviar(anyString(), anyString(), anyString());

        mvc.perform(post("/api/estudantes/" + estudante + "/email")
                        .session(sessao(coordenador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoEmail("Contato", "Mensagem")))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value("Não foi possível enviar pelo SMTP. Confira as credenciais do Mailtrap, host, porta e STARTTLS no backend."));

        assertTrue(envios.findAll().isEmpty());
    }

    @Test
    void validaCamposObrigatorios() throws Exception {
        Long coordenador = criarUsuario(PerfilUsuario.COORDENADOR);
        Long estudante = criarEstudante("estudante-" + UUID.randomUUID() + "@test.example");

        mvc.perform(post("/api/estudantes/" + estudante + "/email")
                        .session(sessao(coordenador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoEmail(" ", " ")))
                .andExpect(status().isBadRequest());

        verify(emailClient, never()).enviar(anyString(), anyString(), anyString());
        assertTrue(envios.findAll().isEmpty());
    }
}
