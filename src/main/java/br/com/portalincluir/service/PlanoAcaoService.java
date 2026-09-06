package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.PlanoAcaoRequest;
import br.com.portalincluir.enums.StatusPlanoAcao;
import br.com.portalincluir.model.PlanoAcao;
import br.com.portalincluir.repository.PlanoAcaoRepository;
import br.com.portalincluir.dto.response.PlanoAcaoResponse;
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
    public PlanoAcaoResponse criar(PlanoAcaoRequest request) {

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

        PlanoAcao salvo = planoAcaoRepository.save(plano);

        return new PlanoAcaoResponse(salvo);
    }

    //Listar Tudo
    public List<PlanoAcaoResponse> listarTodos() {
        return planoAcaoRepository.findAll()
                .stream()
                .map(PlanoAcaoResponse::new)
                .toList();
    }

    //Listar Status
    public List<PlanoAcaoResponse> listarPorStatus(StatusPlanoAcao status) {
        return planoAcaoRepository.findByStatus(status)
                .stream()
                .map(PlanoAcaoResponse::new)
                .toList();
    }

    //Buscar ID
    public PlanoAcaoResponse buscarPorId(Long id) {
        return new PlanoAcaoResponse(buscarEntidadePorId(id));
    }

    private PlanoAcao buscarEntidadePorId(Long id) {
        return planoAcaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano de Ação não encontrado"));
    }

    //Atualizar
    public PlanoAcaoResponse atualizar(Long id, PlanoAcaoRequest request) {

        PlanoAcao plano = buscarEntidadePorId(id);

        if (plano.getStatus() == StatusPlanoAcao.ASSINADO) {
            throw new RuntimeException("Não é possível atualizar um plano assinado");
        }

        if (plano.isArquivado()) {
            throw new RuntimeException("Não é possível atualizar um plano arquivado");
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

        PlanoAcao atualizado = planoAcaoRepository.save(plano);

        return new PlanoAcaoResponse(atualizado);
    }

    //Deletar
    public void excluir(Long id) {

        PlanoAcao plano = buscarEntidadePorId(id);

        if (plano.getStatus() == StatusPlanoAcao.ASSINADO) {
            throw new RuntimeException("Não é possível excluir um plano assinado"); //só exclui se não estiver assinado
        }

        planoAcaoRepository.delete(plano);
    }

    //Arquivar
    public PlanoAcaoResponse arquivar(Long id) {

        PlanoAcao plano = buscarEntidadePorId(id);

        if (plano.isArquivado()) {
            throw new RuntimeException("O plano já está arquivado");
        }

        plano.setArquivado(true);

        PlanoAcao arquivado = planoAcaoRepository.save(plano);

        return new PlanoAcaoResponse(arquivado);
    }

    //Desarquivar
    public PlanoAcaoResponse desarquivar(Long id) {

        PlanoAcao plano = buscarEntidadePorId(id);

        if (!plano.isArquivado()) {
            throw new RuntimeException("O plano não está arquivado");
        }

        plano.setArquivado(false);

        PlanoAcao desarquivado = planoAcaoRepository.save(plano);

        return new PlanoAcaoResponse(desarquivado);
    }

    //Assinar Plano Ação
    public PlanoAcaoResponse assinar(Long id) {

        PlanoAcao plano = buscarEntidadePorId(id);

        if (plano.getStatus() == StatusPlanoAcao.ASSINADO) {
            throw new RuntimeException("O plano já está assinado");
        }

        plano.setStatus(StatusPlanoAcao.ASSINADO);
        plano.setDataAssinatura(LocalDateTime.now());

        PlanoAcao assinado = planoAcaoRepository.save(plano);

        return new PlanoAcaoResponse(assinado);
    }
}
