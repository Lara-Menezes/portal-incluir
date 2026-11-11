package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.PlanoAcaoRequest;
import br.com.portalincluir.enums.StatusPlanoAcao;
import br.com.portalincluir.model.PlanoAcao;
import br.com.portalincluir.repository.PlanoAcaoRepository;
import br.com.portalincluir.dto.response.PlanoAcaoResponse;
import org.springframework.stereotype.Service;
import br.com.portalincluir.model.Estudante;
import br.com.portalincluir.model.Coordenador;
import br.com.portalincluir.repository.EstudanteRepository;
import br.com.portalincluir.repository.CoordenadorRepository;

import java.util.List;
import java.time.LocalDateTime;

@org.springframework.transaction.annotation.Transactional
@Service
public class PlanoAcaoService {

    private final PlanoAcaoRepository planoAcaoRepository;
    private final EstudanteRepository estudanteRepository;
    private final CoordenadorRepository coordenadorRepository;

    private final PoliticaHistoricoService politica;

    public PlanoAcaoService(
            PlanoAcaoRepository planoAcaoRepository,
            EstudanteRepository estudanteRepository,
            CoordenadorRepository coordenadorRepository, PoliticaHistoricoService politica) {
        this.politica = politica;

        this.planoAcaoRepository = planoAcaoRepository;
        this.estudanteRepository = estudanteRepository;
        this.coordenadorRepository = coordenadorRepository;
    }

    // Criar Plano Ação
    public PlanoAcaoResponse criar(PlanoAcaoRequest request) {

        PlanoAcao plano = new PlanoAcao();

        Estudante estudante = estudanteRepository.findById(request.getEstudanteId())
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));

        if (!estudante.isAtivo()) {
            throw new RuntimeException(
                    "Não é possível vincular um plano a um estudante inativo"
            );
        }

        Coordenador coordenador = coordenadorRepository.findById(request.getCoordenadorResponsavelId())
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado"));

        politica.exigirEditavel(estudante);
        boolean mesmoPeriodo = planoAcaoRepository.findByEstudanteId(estudante.getId()).stream()
                .anyMatch(p -> !p.isArquivado() && java.util.Objects.toString(p.getPeriodoLetivo(), "").strip().equalsIgnoreCase(java.util.Objects.toString(request.getPeriodoLetivo(), "").strip()));
        if (mesmoPeriodo) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,
                "Ja existe um plano de acao ativo para este estudante e periodo. Abra o registro existente para continuar.");
        plano.setEstudante(estudante);
        plano.setCoordenadorResponsavel(coordenador);

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
                .filter(registro -> politica.disponivel(registro.getEstudante()))
                .map(PlanoAcaoResponse::new)
                .toList();
    }

    //Listar Status
    public List<PlanoAcaoResponse> listarPorStatus(StatusPlanoAcao status) {
        return planoAcaoRepository.findByStatus(status)
                .stream()
                .filter(registro -> politica.disponivel(registro.getEstudante()))
                .map(PlanoAcaoResponse::new)
                .toList();
    }

    //Buscar ID
    public PlanoAcaoResponse buscarPorId(Long id) {
        PlanoAcao plano = buscarEntidadePorId(id);
        politica.exigirDisponivel(plano.getEstudante());
        return new PlanoAcaoResponse(plano);
    }

    private PlanoAcao buscarEntidadePorId(Long id) {
        return planoAcaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano de Ação não encontrado"));
    }

    //Atualizar
    public PlanoAcaoResponse atualizar(Long id, PlanoAcaoRequest request) {

        PlanoAcao plano = buscarEntidadePorId(id);
        politica.exigirEditavel(plano.getEstudante());
        politica.exigirMesmoEstudante(plano.getEstudante(), request.getEstudanteId());

        if (plano.getStatus() == StatusPlanoAcao.ASSINADO) {
            throw new RuntimeException("Não é possível atualizar um plano assinado");
        }

        if (plano.isArquivado()) {
            throw new RuntimeException("Não é possível atualizar um plano arquivado");
        }

        Estudante estudante = estudanteRepository.findById(request.getEstudanteId())
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));

        if (!estudante.isAtivo()) {
            throw new RuntimeException(
                    "Não é possível vincular um plano a um estudante inativo"
            );
        }

        Coordenador coordenador = coordenadorRepository.findById(request.getCoordenadorResponsavelId())
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado"));

        politica.exigirEditavel(estudante);
        plano.setEstudante(estudante);
        plano.setCoordenadorResponsavel(coordenador);

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
        plano.setDocumentosComprobatorios(request.getDocumentosComprobatorios());

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
        politica.exigirEditavel(plano.getEstudante());

        if (plano.getStatus() == StatusPlanoAcao.ASSINADO) {
            throw new RuntimeException("Não é possível excluir um plano assinado"); //só exclui se não estiver assinado
        }

        planoAcaoRepository.delete(plano);
    }

    //Arquivar
    public PlanoAcaoResponse arquivar(Long id) {

        PlanoAcao plano = buscarEntidadePorId(id);
        politica.exigirEditavel(plano.getEstudante());

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
        politica.exigirEditavel(plano.getEstudante());

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
        politica.exigirEditavel(plano.getEstudante());

        if (plano.getStatus() == StatusPlanoAcao.ASSINADO) {
            throw new RuntimeException("O plano já está assinado");
        }

        if (plano.isArquivado()) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Plano arquivado");
        if (java.util.stream.Stream.of(plano.getPeriodoLetivo(), plano.getNomeResponsavel(), plano.getGrauParentesco(), plano.getTelefoneResponsavel(), plano.getEmailResponsavel(), plano.getNecessidadesEspecificas(), plano.getConsideracoes(), plano.getAvancosDificuldadesPotencialidades(), plano.getPrincipaisDemandas(), plano.getPropostasIntervencao()).anyMatch(v -> v == null || v.isBlank())
          || Boolean.TRUE.equals(plano.getPossuiComorbidade()) && (plano.getComorbidades() == null || plano.getComorbidades().isBlank())
          || Boolean.TRUE.equals(plano.getUsaMedicacaoContinua()) && (plano.getMedicacoes() == null || plano.getMedicacoes().isBlank())
          || Boolean.TRUE.equals(plano.getFrequentaServicoApoio()) && (plano.getServicosApoio() == null || plano.getServicosApoio().isBlank())
          || Boolean.TRUE.equals(plano.getPossuiDocumentosComprobatorios()) && (plano.getDocumentosComprobatorios() == null || plano.getDocumentosComprobatorios().isBlank()))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Conclua os campos obrigatorios antes de assinar o plano");
        plano.setStatus(StatusPlanoAcao.ASSINADO);
        plano.setDataAssinatura(LocalDateTime.now());

        PlanoAcao assinado = planoAcaoRepository.save(plano);

        return new PlanoAcaoResponse(assinado);
    }
}
