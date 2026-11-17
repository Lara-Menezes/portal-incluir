package br.com.portalincluir.service;

import br.com.portalincluir.model.RecuperacaoSenha;
import br.com.portalincluir.model.Usuario;
import br.com.portalincluir.repository.RecuperacaoSenhaRepository;
import br.com.portalincluir.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Set;

@Service
public class RecuperacaoSenhaService {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RecuperacaoSenhaService.class);
    private final UsuarioRepository usuarios;
    private final RecuperacaoSenhaRepository tokens;
    private final SmtpEmailClient emails;
    private final PasswordEncoder encoder;
    private final EntityManager em;
    private final Clock clock;
    private final String frontend;
    private final SecureRandom random = new SecureRandom();

    public RecuperacaoSenhaService(UsuarioRepository usuarios, RecuperacaoSenhaRepository tokens, SmtpEmailClient emails,
            PasswordEncoder encoder, EntityManager em, Clock clock,
            @Value("${app.recuperacao-senha.frontend-url:${FRONTEND_URL:http://localhost:5173}}") String frontend) {
        this.usuarios = usuarios; this.tokens = tokens; this.emails = emails;
        this.encoder = encoder; this.em = em; this.clock = clock;
        URI uri = URI.create(frontend.strip());
        boolean seguro = "https".equalsIgnoreCase(uri.getScheme()) || ("http".equalsIgnoreCase(uri.getScheme())
                && Set.of("localhost", "127.0.0.1", "[::1]").contains(uri.getHost()));
        if (!seguro || uri.getHost() == null || uri.getUserInfo() != null || uri.getQuery() != null || uri.getFragment() != null)
            throw new IllegalArgumentException("Configure FRONTEND_URL com HTTPS (HTTP somente para localhost).");
        this.frontend = uri.toString().replaceAll("/+$", "");
    }

    @Transactional
    public void emitir(String email) {
        if (!emails.isConfigurado()) { LOGGER.warn("Recuperacao de senha: SMTP nao configurado."); return; }
        var candidato = usuarios.findByEmailIgnoreCase(email.strip());
        if (candidato.isEmpty()) return;
        var usuario = usuarios.buscarParaCredenciais(candidato.get().getId()).orElse(null);
        if (usuario == null) return;
        em.refresh(usuario, LockModeType.PESSIMISTIC_WRITE);
        if (!elegivel(usuario) || !usuario.getEmail().equalsIgnoreCase(email)) return;
        revogarPendentes(usuario.getId());
        byte[] bytes = new byte[32]; random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        var recuperacao = new RecuperacaoSenha();
        recuperacao.setUsuario(usuario); recuperacao.setHashToken(hash(token));
        recuperacao.setEmailDestino(usuario.getEmail().toLowerCase(Locale.ROOT));
        recuperacao.setVersaoCredenciais(usuario.getVersaoCredenciais());
        recuperacao.setCriadoEm(clock.instant()); recuperacao.setExpiraEm(clock.instant().plus(Duration.ofMinutes(30)));
        tokens.saveAndFlush(recuperacao);
        String destinatario = usuario.getEmail();
        // O fragmento evita enviar o token na URL HTTP e nos logs de acesso do frontend.
        String link = frontend + "/redefinir-senha#token=" + token;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                try { emails.enviarRecuperacaoSenha(destinatario, link); }
                catch (RuntimeException falha) { LOGGER.warn("Recuperacao de senha: falha no envio SMTP ({}).", falha.getClass().getSimpleName()); }
            }
        });
    }

    @Transactional
    public void redefinir(String token, String novaSenha) {
        if (novaSenha.getBytes(StandardCharsets.UTF_8).length > 72)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha deve ter no máximo 72 bytes em UTF-8. Reduza seu tamanho.");
        var recuperacao = tokens.findByHashToken(hash(token)).orElseThrow(RecuperacaoSenhaService::linkInvalido);
        var usuario = usuarios.buscarParaCredenciais(recuperacao.getUsuario().getId()).orElseThrow(RecuperacaoSenhaService::linkInvalido);
        // Todas as emissões e redefinições da mesma conta usam o mesmo bloqueio.
        em.refresh(usuario, LockModeType.PESSIMISTIC_WRITE); em.refresh(recuperacao);
        if (!elegivel(usuario) || recuperacao.getUsadoEm() != null || recuperacao.getRevogadoEm() != null
                || !clock.instant().isBefore(recuperacao.getExpiraEm())
                || recuperacao.getVersaoCredenciais() != usuario.getVersaoCredenciais()
                || !usuario.getEmail().equalsIgnoreCase(recuperacao.getEmailDestino())) throw linkInvalido();
        usuario.setSenha(encoder.encode(novaSenha));
        usuario.setVersaoCredenciais(Math.addExact(usuario.getVersaoCredenciais(), 1));
        recuperacao.setUsadoEm(clock.instant());
        tokens.saveAndFlush(recuperacao); revogarPendentes(usuario.getId()); usuarios.saveAndFlush(usuario);
    }

    private void revogarPendentes(Long usuarioId) {
        tokens.findByUsuarioIdAndUsadoEmIsNullAndRevogadoEmIsNull(usuarioId).forEach(t -> t.setRevogadoEm(clock.instant()));
    }
    private static boolean elegivel(Usuario u) {
        // A conta tecnica root possui recuperacao administrativa fora deste fluxo publico.
        return !u.isRoot() && PermissaoService.ativo(u);
    }
    static String hash(String valor) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(valor.getBytes(StandardCharsets.UTF_8))); }
        catch (NoSuchAlgorithmException ex) { throw new IllegalStateException("SHA-256 indisponivel", ex); }
    }
    private static ResponseStatusException linkInvalido() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este link é inválido, expirou ou já foi utilizado. Solicite uma nova recuperação.");
    }
}
