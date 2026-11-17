package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.EmailEstudanteRequest;
import br.com.portalincluir.dto.response.EmailEnviadoResponse;
import br.com.portalincluir.model.EmailEnvio;
import br.com.portalincluir.model.Estudante;
import br.com.portalincluir.model.Usuario;
import br.com.portalincluir.repository.EmailEnvioRepository;
import br.com.portalincluir.repository.EstudanteRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmailEstudanteService {
    private static final org.slf4j.Logger LOGGER =
            org.slf4j.LoggerFactory.getLogger(EmailEstudanteService.class);
    private final EstudanteRepository estudantes;
    private final EmailEnvioRepository envios;
    private final SmtpEmailClient emailClient;

    public EmailEstudanteService(
            EstudanteRepository estudantes,
            EmailEnvioRepository envios,
            SmtpEmailClient emailClient) {
        this.estudantes = estudantes;
        this.envios = envios;
        this.emailClient = emailClient;
    }

    @Transactional
    public EmailEnviadoResponse enviar(Long estudanteId, EmailEstudanteRequest request, Usuario remetente) {
        Estudante estudante = estudantes.findById(estudanteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante não encontrado."));
        String destinatario = Optional.ofNullable(estudante.getEmail()).orElse("").strip();
        if (!emailValido(destinatario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O estudante não possui um endereço de e-mail válido cadastrado.");
        }
        if (!emailClient.isConfigurado()) {
            String variaveisAusentes = String.join(", ", emailClient.variaveisNaoConfiguradas());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Defina no ambiente do backend as variáveis SMTP ausentes: " + variaveisAusentes + ".");
        }

        try {
            emailClient.enviar(destinatario, request.assunto().strip(), request.mensagem().strip());
        } catch (EmailDeliveryException erro) {
            LOGGER.warn("Falha no envio SMTP. Tipo de excecao: {}",
                    erro.getCause() == null ? erro.getClass().getSimpleName() : erro.getCause().getClass().getSimpleName());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível enviar pelo SMTP. Confira as credenciais do Mailtrap, host, porta e STARTTLS no backend.",
                    erro);
        }

        Instant enviadoEm = Instant.now();
        EmailEnvio envio = new EmailEnvio();
        envio.setEstudanteId(estudante.getId());
        envio.setRemetenteId(remetente.getId());
        envio.setRemetenteNome(nome(remetente));
        envio.setRemetenteEmail(remetente.getEmail());
        envio.setDestinatarioEmail(destinatario);
        envio.setEnviadoEm(enviadoEm);
        envios.saveAndFlush(envio);

        return new EmailEnviadoResponse(destinatario, enviadoEm);
    }

    private static String nome(Usuario usuario) {
        Object profissional = GestaoUsuarioService.profissional(usuario);
        return (String) new BeanWrapperImpl(profissional).getPropertyValue("nome");
    }

    private static boolean emailValido(String email) {
        return email.length() <= 254
                && email.matches("(?i)^[a-z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?(?:\\.[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?)+$");
    }
}
