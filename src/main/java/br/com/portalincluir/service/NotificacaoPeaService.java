package br.com.portalincluir.service;

import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class NotificacaoPeaService {
    private final NotificacaoFluxoRepository notificacoes;
    private final EventoPeaPendenteRepository pendentes;
    private final PoliticaHistoricoService tempo;

    public void solicitada(DestinatarioSolicitacaoPea d) {
        var s = d.getSolicitacao();
        registrar(d.getUsuario(), s.getEstudante(), s.getSolicitante().getCoordenador().getNome(),
                "PEA_SOLICITADO", s.getId(), null, "Solicitação de PEA: " + s.getContexto());
    }

    public void entregue(EntregaPea e) {
        var s = e.getDestinatario().getSolicitacao();
        registrar(s.getSolicitante(), s.getEstudante(), e.getDestinatario().getProfessor().getNome(),
                "PEA_ENTREGUE", s.getId(), e.getPea().getId(), "PEA entregue para a solicitação: " + s.getContexto());
    }

    public void espontaneo(AdaptacaoPedagogica pea, Usuario autor) {
        // Os coordenadores de documentos e do fluxo não constituem uma atribuição inequívoca do estudante.
        var evento = new EventoPeaPendente(); evento.setEstudante(pea.getEstudante()); evento.setAutor(autor);
        evento.setPeaId(pea.getId()); evento.setCriadoEm(tempo.agora()); pendentes.save(evento);
    }

    void registrar(Usuario destinatario, Estudante estudante, String remetente, String tipo, Long solicitacaoId, Long peaId, String mensagem) {
        var n = new NotificacaoFluxo(); n.setDestinatario(destinatario); n.setEstudante(estudante);
        n.setRemetente(remetente); n.setEtapa("PEA"); n.setTipoEvento(tipo); n.setSolicitacaoPeaId(solicitacaoId);
        n.setPeaId(peaId); n.setMensagem(mensagem); n.setCriadaEm(tempo.agora()); notificacoes.save(n);
    }
}
