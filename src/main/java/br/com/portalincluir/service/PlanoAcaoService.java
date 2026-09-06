package br.com.portalincluir.service;

import br.com.portalincluir.dto.PlanoAcaoRequest;
import br.com.portalincluir.enums.StatusPlanoAcao;
import br.com.portalincluir.model.PlanoAcao;
import br.com.portalincluir.repository.PlanoAcaoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class PlanoAcaoService {

    private final PlanoAcaoRepository planoAcaoRepository;

    public PlanoAcaoService(PlanoAcaoRepository planoAcaoRepository) {
        this.planoAcaoRepository = planoAcaoRepository;
    }

    // Criar Plano Ação
    public PlanoAcao criar(PlanoAcaoRequest request) {

        PlanoAcao plano = new PlanoAcao();

        plano.setPeriodoLetivo(request.getPeriodoLetivo());

        plano.setNomeResponsavel(request.getNomeResponsavel());
        plano.setGrauParentesco(request.getGrauParentesco());
        plano.setTelefoneResponsavel(request.getTelefoneResponsavel());
        plano.setEmailResponsavel(request.getEmailResponsavel());

        plano.setPossuiComorbidade(request.getPossuiComorbidade());
        plano.setComorbidades(request.getComorbidades());

        plano.setUsaMedicacaoContinua(request.getUsaMedicacaoContinua());
        plano.setMedicacoes(request.getMedicacoes());

        plano.setFrequentaServicoApoio(request.getFrequentaServicoApoio());
        plano.setServicosApoio(request.getServicosApoio());

        plano.setPossuiDocumentosComprobatorios(
                request.getPossuiDocumentosComprobatorios()
        );
        plano.setDocumentosComprobatorios(
                request.getDocumentosComprobatorios()
        );

        plano.setNecessidadesEspecificas(request.getNecessidadesEspecificas());
        plano.setConsideracoes(request.getConsideracoes());
        plano.setAvancosDificuldadesPotencialidades(
                request.getAvancosDificuldadesPotencialidades()
        );
        plano.setPrincipaisDemandas(request.getPrincipaisDemandas());
        plano.setPropostasIntervencao(request.getPropostasIntervencao());

        plano.setStatus(StatusPlanoAcao.RASCUNHO);
        plano.setArquivado(false);
        plano.setDataCriacao(LocalDateTime.now());

        return planoAcaoRepository.save(plano);
    }

    //Listar Tudo
    public List<PlanoAcao> listarTodos() {
        return planoAcaoRepository.findAll();
    }

    //Buscar por ID
    public PlanoAcao buscarPorId(Long id) {
        return planoAcaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano de Ação não encontrado"));
    }


    //Atualizar
    public PlanoAcao atualizar(Long id, PlanoAcaoRequest request) {

        PlanoAcao plano = buscarPorId(id);

        if (plano.getStatus() == StatusPlanoAcao.ASSINADO) {
            throw new RuntimeException("Não é possível atualizar um plano assinado");
        }

        plano.setPeriodoLetivo(request.getPeriodoLetivo());

        plano.setNomeResponsavel(request.getNomeResponsavel());
        plano.setGrauParentesco(request.getGrauParentesco());
        plano.setTelefoneResponsavel(request.getTelefoneResponsavel());
        plano.setEmailResponsavel(request.getEmailResponsavel());

        plano.setPossuiComorbidade(request.getPossuiComorbidade());
        plano.setComorbidades(request.getComorbidades());

        plano.setUsaMedicacaoContinua(request.getUsaMedicacaoContinua());
        plano.setMedicacoes(request.getMedicacoes());

        plano.setFrequentaServicoApoio(request.getFrequentaServicoApoio());
        plano.setServicosApoio(request.getServicosApoio());

        plano.setPossuiDocumentosComprobatorios(
                request.getPossuiDocumentosComprobatorios()
        );
        plano.setDocumentosComprobatorios(
                request.getDocumentosComprobatorios()
        );

        plano.setNecessidadesEspecificas(request.getNecessidadesEspecificas());
        plano.setConsideracoes(request.getConsideracoes());
        plano.setAvancosDificuldadesPotencialidades(
                request.getAvancosDificuldadesPotencialidades()
        );
        plano.setPrincipaisDemandas(request.getPrincipaisDemandas());
        plano.setPropostasIntervencao(request.getPropostasIntervencao());

        plano.setDataAtualizacao(LocalDateTime.now());

        return planoAcaoRepository.save(plano);
    }

    //Deletar
    public void excluir(Long id) {

        PlanoAcao plano = buscarPorId(id);

        if (plano.getStatus() == StatusPlanoAcao.ASSINADO) {
            throw new RuntimeException("Não é possível excluir um plano assinado"); //só exclui se não estiver assinado
        }

        planoAcaoRepository.delete(plano);
    }


}
