package br.com.portalincluir.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Component
public class SmtpEmailClient {
    private final JavaMailSender mailSender;
    private final String host;
    private final String username;
    private final String password;
    private final String remetente;

    public SmtpEmailClient(
            JavaMailSender mailSender,
            @Value("${spring.mail.host:}") String host,
            @Value("${spring.mail.username:}") String username,
            @Value("${spring.mail.password:}") String password,
            @Value("${app.email.smtp.from:}") String remetente) {
        this.mailSender = mailSender;
        this.host = host.strip();
        this.username = username.strip();
        this.password = password.strip();
        this.remetente = remetente.strip();
    }

    public boolean isConfigurado() {
        return variaveisNaoConfiguradas().isEmpty();
    }

    public List<String> variaveisNaoConfiguradas() {
        List<String> pendentes = new ArrayList<>();
        if (host.isBlank()) pendentes.add("MAIL_HOST");
        if (username.isBlank()) pendentes.add("MAIL_USERNAME");
        if (password.isBlank()) pendentes.add("MAIL_PASSWORD");
        if (remetente.isBlank()) pendentes.add("MAIL_FROM");
        return List.copyOf(pendentes);
    }

    public void enviar(String destinatario, String assunto, String mensagem) {
        enviarConteudo(destinatario, assunto, mensagem, montarHtml(mensagem));
    }

    public void enviarRecuperacaoSenha(String destinatario, String link) {
        String mensagem = "Recebemos um pedido para redefinir sua senha no Portal Incluir.\n\n"
                + "Acesse o link em até 30 minutos:\n" + link + "\n\n"
                + "Se você não solicitou a recuperação, ignore esta mensagem. Sua senha permanece a mesma.";
        String html = montarTemplate("<p>Recebemos um pedido para redefinir sua senha no Portal Incluir.</p>"
                + "<p>O link é válido por 30 minutos e pode ser utilizado uma única vez.</p>"
                + "<p><a style=\"display:inline-block;background:#14532d;color:#fff;padding:12px 20px;border-radius:8px;text-decoration:none\" href=\""
                + HtmlUtils.htmlEscape(link) + "\">Redefinir minha senha</a></p>"
                + "<p>Se você não solicitou a recuperação, ignore esta mensagem. Sua senha permanece a mesma.</p>");
        enviarConteudo(destinatario, "Recuperação de senha · Portal Incluir", mensagem, html);
    }

    private void enviarConteudo(String destinatario, String assunto, String mensagem, String html) {
        try {
            MimeMessage email = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    email, MimeMessageHelper.MULTIPART_MODE_RELATED, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(mensagem, html);
            helper.addInline("portal-logo", new ClassPathResource("email/logo-portal.png"), "image/png");
            mailSender.send(email);
        } catch (MailException | MessagingException erro) {
            throw new EmailDeliveryException(erro);
        }
    }

    private static String montarHtml(String mensagem) {
        String conteudo = HtmlUtils.htmlEscape(mensagem).replace("\r\n", "\n").replace("\n", "<br>");
        return montarTemplate(conteudo);
    }

    private static String montarTemplate(String conteudo) {
        return """
                <!doctype html>
                <html lang="pt-BR">
                  <body style="margin:0;background:#f1f5f9;font-family:Arial,sans-serif;color:#1f2937;">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="padding:24px 12px;background:#f1f5f9;">
                      <tr><td align="center">
                        <table role="presentation" width="600" cellspacing="0" cellpadding="0" style="max-width:600px;width:100%%;background:#ffffff;border-radius:12px;overflow:hidden;">
                          <tr><td align="center" style="padding:22px;background:#14532d;">
                            <img src="cid:portal-logo" alt="Portal Incluir" width="220" style="display:block;width:220px;max-width:100%%;height:auto;">
                          </td></tr>
                          <tr><td style="padding:30px 28px;font-size:15px;line-height:1.7;">%s</td></tr>
                        </table>
                      </td></tr>
                    </table>
                  </body>
                </html>
                """.formatted(conteudo);
    }
}
