package br.com.portalincluir.service;

import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmtpEmailClientTest {
    @Test
    void enviaEmailPersonalizadoComHtmlELogoInline() throws Exception {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MimeMessage email = new JavaMailSenderImpl().createMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(email);
        SmtpEmailClient client = new SmtpEmailClient(
                mailSender,
                "sandbox.smtp.mailtrap.io",
                "mailtrap-user",
                "mailtrap-password",
                "Portal Incluir <portal@example.test>");

        client.enviar("estudante@example.test", "Assunto personalizado", "Mensagem <revisada>");

        assertEquals("Assunto personalizado", email.getSubject());
        assertEquals("estudante@example.test", email.getAllRecipients()[0].toString());
        String conteudo = lerConteudo(email.getContent());
        assertTrue(conteudo.contains("Mensagem &lt;revisada&gt;"));
        assertTrue(conteudo.contains("cid:portal-logo"));
        assertTrue(conteudo.contains("<portal-logo>"));
        verify(mailSender).send(email);
    }

    @Test
    void consideraConfiguradoSomenteComCredenciaisERemetente() {
        JavaMailSender mailSender = mock(JavaMailSender.class);

        SmtpEmailClient configurado = new SmtpEmailClient(mailSender, "smtp.test", "user", "pass", "from@test");
        SmtpEmailClient semCredenciais = new SmtpEmailClient(mailSender, "smtp.test", "", "", "from@test");

        assertTrue(configurado.isConfigurado());
        assertFalse(semCredenciais.isConfigurado());
        assertEquals(List.of("MAIL_USERNAME", "MAIL_PASSWORD"),
                semCredenciais.variaveisNaoConfiguradas());
    }

    @Test
    void traduzFalhaDoSenderSemEsconderErro() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MimeMessage email = new JavaMailSenderImpl().createMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(email);
        MailSendException causa = new MailSendException("SMTP indisponível");
        doThrow(causa).when(mailSender).send(email);
        SmtpEmailClient client = new SmtpEmailClient(
                mailSender, "smtp.test", "user", "pass", "from@test");

        EmailDeliveryException erro = assertThrows(EmailDeliveryException.class,
                () -> client.enviar("estudante@example.test", "Assunto", "Mensagem"));

        assertEquals(causa, erro.getCause());
    }

    private static String lerConteudo(Object conteudo) throws Exception {
        if (conteudo instanceof Multipart multipart) {
            StringBuilder resultado = new StringBuilder();
            for (int indice = 0; indice < multipart.getCount(); indice++) {
                BodyPart parte = multipart.getBodyPart(indice);
                resultado.append(String.join(" ", parte.getHeader("Content-ID") == null
                        ? new String[0] : parte.getHeader("Content-ID")));
                Object parteConteudo = parte.getContent();
                if (parteConteudo instanceof String texto) {
                    resultado.append(texto);
                } else {
                    resultado.append(lerConteudo(parteConteudo));
                }
            }
            return resultado.toString();
        }
        if (conteudo instanceof String texto) {
            return texto;
        }
        return "";
    }
}
