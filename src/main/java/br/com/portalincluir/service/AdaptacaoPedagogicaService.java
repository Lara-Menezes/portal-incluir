package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.AdaptacaoPedagogicaRequest;
import br.com.portalincluir.dto.response.AdaptacaoPedagogicaResponse;
import br.com.portalincluir.model.AdaptacaoPedagogica;
import br.com.portalincluir.model.Estudante;
import br.com.portalincluir.model.Professor;
import br.com.portalincluir.model.PlanoEducacional;
import br.com.portalincluir.repository.AdaptacaoPedagogicaRepository;
import br.com.portalincluir.repository.EstudanteRepository;
import br.com.portalincluir.repository.PlanoEducacionalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.transaction.annotation.Transactional
@Service
public class AdaptacaoPedagogicaService {

    private final AdaptacaoPedagogicaRepository adaptacaoRepository;
    private final EstudanteRepository estudanteRepository;
    private final PlanoEducacionalRepository planoEducacionalRepository;

    private final PoliticaHistoricoService politica;
    private final AutorizacaoPeaService autorizacao;

    public AdaptacaoPedagogicaService(
            AdaptacaoPedagogicaRepository adaptacaoRepository,
            EstudanteRepository estudanteRepository,
            PlanoEducacionalRepository planoEducacionalRepository,
            PoliticaHistoricoService politica,
            AutorizacaoPeaService autorizacao) {

        this.politica = politica;
        this.adaptacaoRepository = adaptacaoRepository;
        this.estudanteRepository = estudanteRepository;
        this.autorizacao = autorizacao;
        this.planoEducacionalRepository = planoEducacionalRepository;
    }

    //Criar Adaptação Pedagogica
    public AdaptacaoPedagogicaResponse criar(
            AdaptacaoPedagogicaRequest request) {
        autorizacao.exigirEscrita();

        Estudante estudante = estudanteRepository.findById(request.getEstudanteId())
                .orElseThrow(() ->
                        new RuntimeException("Estudante não encontrado"));

        politica.exigirEditavel(estudante);
        Professor professor = autorizacao.exigirCriacao(estudante, request.getProfessorId());

        PlanoEducacional plano = null;

        if (request.getPlanoEducacionalId() != null) {
            plano = planoEducacionalRepository
                    .findById(request.getPlanoEducacionalId())
                    .orElseThrow(() ->
                            new RuntimeException("Plano educacional não encontrado"));

            if (!plano.getEstudante().getId()
                    .equals(request.getEstudanteId())) {
                throw new RuntimeException(
                        "O plano educacional não pertence ao estudante informado");
            }

            if (plano.isArquivado()) {
                throw new RuntimeException(
                        "Não é possível vincular uma adaptação a um PEI arquivado");
            }
        }

        AdaptacaoPedagogica adaptacao = new AdaptacaoPedagogica();

        adaptacao.setEstudante(estudante);
        adaptacao.setProfessor(professor);
        adaptacao.setPlanoEducacional(plano);

        preencherDados(adaptacao, request);

        adaptacao.setDataCriacao(LocalDateTime.now());

        AdaptacaoPedagogica salva =
                adaptacaoRepository.save(adaptacao);

        return new AdaptacaoPedagogicaResponse(salva);
    }

    //Listar tudo
    public List<AdaptacaoPedagogicaResponse> listarTodos() {
        var filtro = autorizacao.filtroConsulta();
        return adaptacaoRepository.findAll()
                .stream()
                .filter(filtro)
                .map(AdaptacaoPedagogicaResponse::new)
                .toList();
    }

    //Buscar por id
    public AdaptacaoPedagogicaResponse buscarPorId(Long id) {

        AdaptacaoPedagogica adaptacao = buscarEntidadePorId(id);
        autorizacao.exigirConsulta(adaptacao);
        return new AdaptacaoPedagogicaResponse(adaptacao);
    }

    //Buscar por estudante
    public List<AdaptacaoPedagogicaResponse> listarPorEstudante(
            Long estudanteId) {
        var filtro = autorizacao.filtroConsulta();
        return adaptacaoRepository.findByEstudanteId(estudanteId)
                .stream()
                .filter(filtro)
                .map(AdaptacaoPedagogicaResponse::new)
                .toList();
    }

    //Buscar por professor
    public List<AdaptacaoPedagogicaResponse> listarPorProfessor(
            Long professorId) {
        var filtro = autorizacao.filtroConsulta();
        return adaptacaoRepository.findByProfessorId(professorId)
                .stream()
                .filter(filtro)
                .map(AdaptacaoPedagogicaResponse::new)
                .toList();
    }

    //Buscar por estudante e professor
    public List<AdaptacaoPedagogicaResponse> listarPorEstudanteEProfessor(
            Long estudanteId,
            Long professorId) {
        var filtro = autorizacao.filtroConsulta();
        return adaptacaoRepository
                .findByEstudanteIdAndProfessorId(estudanteId, professorId)
                .stream()
                .filter(filtro)
                .map(AdaptacaoPedagogicaResponse::new)
                .toList();
    }

    //Buscar por componente curricular
    public List<AdaptacaoPedagogicaResponse> buscarPorComponente(
            String componenteCurricular) {
        var filtro = autorizacao.filtroConsulta();
        return adaptacaoRepository
                .findByComponenteCurricularContainingIgnoreCase(
                        componenteCurricular
                )
                .stream()
                .filter(filtro)
                .map(AdaptacaoPedagogicaResponse::new)
                .toList();
    }

    //Atualizar
    public AdaptacaoPedagogicaResponse atualizar(
            Long id,
            AdaptacaoPedagogicaRequest request) {

        AdaptacaoPedagogica adaptacao = buscarEntidadePorId(id);
        autorizacao.exigirGerenciamento(adaptacao);
        politica.exigirEditavel(adaptacao.getEstudante());
        politica.exigirMesmoEstudante(adaptacao.getEstudante(), request.getEstudanteId());
        if (!adaptacao.getProfessor().getId().equals(request.getProfessorId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT, "Não é permitido transferir a autoria de um PEA.");
        }

        if (adaptacao.isArquivado()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,
                    "Não é possível atualizar uma adaptação pedagógica arquivada"
            );
        }

        preencherDados(adaptacao, request);

        adaptacao.setDataAtualizacao(LocalDateTime.now());

        AdaptacaoPedagogica atualizada =
                adaptacaoRepository.save(adaptacao);

        return new AdaptacaoPedagogicaResponse(atualizada);
    }

    //Arquivar
    public AdaptacaoPedagogicaResponse arquivar(Long id) {
        AdaptacaoPedagogica adaptacao = buscarEntidadePorId(id);
        autorizacao.exigirGerenciamento(adaptacao);
        politica.exigirEditavel(adaptacao.getEstudante());

        adaptacao.setArquivado(true);
        adaptacao.setDataAtualizacao(LocalDateTime.now());

        return new AdaptacaoPedagogicaResponse(
                adaptacaoRepository.save(adaptacao)
        );
    }

    //Desarquivar
    public AdaptacaoPedagogicaResponse desarquivar(Long id) {
        AdaptacaoPedagogica adaptacao = buscarEntidadePorId(id);
        autorizacao.exigirGerenciamento(adaptacao);
        politica.exigirEditavel(adaptacao.getEstudante());

        adaptacao.setArquivado(false);
        adaptacao.setDataAtualizacao(LocalDateTime.now());

        return new AdaptacaoPedagogicaResponse(
                adaptacaoRepository.save(adaptacao)
        );
    }

    //Excluir
    public void excluir(Long id) {

        AdaptacaoPedagogica adaptacao = buscarEntidadePorId(id);
        autorizacao.exigirGerenciamento(adaptacao);
        politica.exigirEditavel(adaptacao.getEstudante());

        adaptacaoRepository.delete(adaptacao);
    }

    private AdaptacaoPedagogica buscarEntidadePorId(Long id) {

        return adaptacaoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Adaptação pedagógica não encontrada"
                        ));
    }

    private void preencherDados(
            AdaptacaoPedagogica adaptacao,
            AdaptacaoPedagogicaRequest request) {

        adaptacao.setComponenteCurricular(
                request.getComponenteCurricular().trim()
        );

        adaptacao.setSemestreAno(
                request.getSemestreAno().trim()
        );

        adaptacao.setObjetivoGeral(
                request.getObjetivoGeral().trim()
        );

        adaptacao.setObjetivosEspecificos(
                request.getObjetivosEspecificos().trim()
        );

        adaptacao.setConteudosProgramaticos(
                request.getConteudosProgramaticos().trim()
        );

        adaptacao.setMetodologia(
                request.getMetodologia().trim()
        );

        adaptacao.setAvaliacao(
                request.getAvaliacao().trim()
        );

        adaptacao.setBibliografiaBasica(
                request.getBibliografiaBasica().trim()
        );

        adaptacao.setBibliografiaComplementar(
                request.getBibliografiaComplementar().trim()
        );
    }
}
