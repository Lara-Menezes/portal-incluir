package br.com.portalincluir.service;

import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service @RequiredArgsConstructor @Transactional
public class EventoPeaPendenteService {
    private final EventoPeaPendenteRepository eventos;
    private final UsuarioRepository usuarios;
    private final PermissaoService acesso;
    private final PoliticaHistoricoService historico;
    private final NotificacaoPeaService notificacoes;
    private final HttpServletRequest request;
    private final EntityManager em;

    public record Resposta(Long id, long versao, Long estudanteId, Long peaId, String motivo,
                           LocalDateTime criadoEm, LocalDateTime encaminhadoEm, Long encaminhadoPorId, Long destinatarioId) {}

    private Usuario coordenador() {
        var u = acesso.exigir(request, "pei.editar");
        acesso.exigir(request, "estudantes.consultar");
        acesso.exigir(request, "pedagogico.consultar");
        if (u.getPerfil() != PerfilUsuario.COORDENADOR) throw erro(HttpStatus.FORBIDDEN, "Ação restrita à coordenação autorizada.");
        return u;
    }

    @Transactional(readOnly=true)
    public List<Resposta> listar() {
        coordenador();
        return eventos.findAll().stream().filter(e -> historico.disponivel(e.getEstudante())).map(this::resposta).toList();
    }

    public Resposta encaminhar(Long id, long versao, Long destinatarioId) {
        var autor = coordenador();
        var evento = eventos.findById(id).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Evento não encontrado."));
        var estudante = historico.bloquear(evento.getEstudante().getId());
        historico.exigirDisponivel(estudante); em.refresh(evento);
        if (evento.getVersao() != versao || evento.getEncaminhadoEm() != null)
            throw erro(HttpStatus.CONFLICT, "Evento alterado ou já encaminhado. Atualize a consulta.");
        var destino = usuarios.findById(destinatarioId).orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Destinatário não encontrado."));
        if (destino.getPerfil() != PerfilUsuario.COORDENADOR || !PermissaoService.ativo(destino)
                || !PermissaoService.efetivas(destino).containsAll(Set.of("estudantes.consultar", "pedagogico.consultar")))
            throw erro(HttpStatus.BAD_REQUEST, "Selecione uma coordenação ativa com acesso de consulta ao PEA.");
        // Somente encaminha a notificação: não altera vínculos, atribuições ou permissões.
        notificacoes.registrar(destino, estudante, evento.getAutor().getProfessor().getNome(),
                evento.getTipoEvento(), null, evento.getPeaId(), "PEA espontâneo registrado para acompanhamento.");
        evento.setEncaminhadoPor(autor); evento.setDestinatario(destino); evento.setEncaminhadoEm(historico.agora());
        eventos.saveAndFlush(evento); return resposta(evento);
    }

    private Resposta resposta(EventoPeaPendente e) {
        return new Resposta(e.getId(), e.getVersao(), e.getEstudante().getId(), e.getPeaId(), e.getMotivo(),
                e.getCriadoEm(), e.getEncaminhadoEm(), e.getEncaminhadoPor() == null ? null : e.getEncaminhadoPor().getId(),
                e.getDestinatario() == null ? null : e.getDestinatario().getId());
    }
    private static ResponseStatusException erro(HttpStatus status, String mensagem) { return new ResponseStatusException(status, mensagem); }
}
