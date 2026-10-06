package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.AdaptacaoPedagogicaRequest;
import br.com.portalincluir.dto.response.AdaptacaoPedagogicaResponse;
import br.com.portalincluir.model.AdaptacaoPedagogica;
import br.com.portalincluir.model.Estudante;
import br.com.portalincluir.model.Professor;
import br.com.portalincluir.repository.AdaptacaoPedagogicaRepository;
import br.com.portalincluir.repository.EstudanteRepository;
import br.com.portalincluir.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdaptacaoPedagogicaService {

    private final AdaptacaoPedagogicaRepository adaptacaoRepository;
    private final EstudanteRepository estudanteRepository;
    private final ProfessorRepository professorRepository;

    public AdaptacaoPedagogicaService(
            AdaptacaoPedagogicaRepository adaptacaoRepository,
            EstudanteRepository estudanteRepository,
            ProfessorRepository professorRepository) {

        this.adaptacaoRepository = adaptacaoRepository;
        this.estudanteRepository = estudanteRepository;
        this.professorRepository = professorRepository;
    }

    //Criar Adaptação Pedagogica
    public AdaptacaoPedagogicaResponse criar(
            AdaptacaoPedagogicaRequest request) {

        Estudante estudante = estudanteRepository.findById(request.getEstudanteId())
                .orElseThrow(() ->
                        new RuntimeException("Estudante não encontrado"));

        Professor professor = professorRepository.findById(request.getProfessorId())
                .orElseThrow(() ->
                        new RuntimeException("Professor não encontrado"));

        AdaptacaoPedagogica adaptacao = new AdaptacaoPedagogica();

        adaptacao.setEstudante(estudante);
        adaptacao.setProfessor(professor);

        preencherDados(adaptacao, request);

        adaptacao.setDataCriacao(LocalDateTime.now());

        AdaptacaoPedagogica salva =
                adaptacaoRepository.save(adaptacao);

        return new AdaptacaoPedagogicaResponse(salva);
    }

    //Listar tudo
    public List<AdaptacaoPedagogicaResponse> listarTodos() {

        return adaptacaoRepository.findAll()
                .stream()
                .map(AdaptacaoPedagogicaResponse::new)
                .toList();
    }

    //Buscar por id
    public AdaptacaoPedagogicaResponse buscarPorId(Long id) {

        return new AdaptacaoPedagogicaResponse(
                buscarEntidadePorId(id)
        );
    }

    //Buscar por estudante
    public List<AdaptacaoPedagogicaResponse> listarPorEstudante(
            Long estudanteId) {

        return adaptacaoRepository.findByEstudanteId(estudanteId)
                .stream()
                .map(AdaptacaoPedagogicaResponse::new)
                .toList();
    }

    //Buscar por professor
    public List<AdaptacaoPedagogicaResponse> listarPorProfessor(
            Long professorId) {

        return adaptacaoRepository.findByProfessorId(professorId)
                .stream()
                .map(AdaptacaoPedagogicaResponse::new)
                .toList();
    }

    //Buscar por estudante e professor
    public List<AdaptacaoPedagogicaResponse> listarPorEstudanteEProfessor(
            Long estudanteId,
            Long professorId) {

        return adaptacaoRepository
                .findByEstudanteIdAndProfessorId(estudanteId, professorId)
                .stream()
                .map(AdaptacaoPedagogicaResponse::new)
                .toList();
    }

    //Buscar por componente curricular
    public List<AdaptacaoPedagogicaResponse> buscarPorComponente(
            String componenteCurricular) {

        return adaptacaoRepository
                .findByComponenteCurricularContainingIgnoreCase(
                        componenteCurricular
                )
                .stream()
                .map(AdaptacaoPedagogicaResponse::new)
                .toList();
    }

    //Atualizar
    public AdaptacaoPedagogicaResponse atualizar(
            Long id,
            AdaptacaoPedagogicaRequest request) {

        AdaptacaoPedagogica adaptacao = buscarEntidadePorId(id);

        if (adaptacao.isArquivado()) {
            throw new RuntimeException(
                    "Não é possível atualizar uma adaptação pedagógica arquivada"
            );
        }

        Estudante estudante = estudanteRepository.findById(request.getEstudanteId())
                .orElseThrow(() ->
                        new RuntimeException("Estudante não encontrado"));

        Professor professor = professorRepository.findById(request.getProfessorId())
                .orElseThrow(() ->
                        new RuntimeException("Professor não encontrado"));

        adaptacao.setEstudante(estudante);
        adaptacao.setProfessor(professor);

        preencherDados(adaptacao, request);

        adaptacao.setDataAtualizacao(LocalDateTime.now());

        AdaptacaoPedagogica atualizada =
                adaptacaoRepository.save(adaptacao);

        return new AdaptacaoPedagogicaResponse(atualizada);
    }

    //Arquivar
    public AdaptacaoPedagogicaResponse arquivar(Long id) {
        AdaptacaoPedagogica adaptacao = buscarEntidadePorId(id);

        adaptacao.setArquivado(true);
        adaptacao.setDataAtualizacao(LocalDateTime.now());

        return new AdaptacaoPedagogicaResponse(
                adaptacaoRepository.save(adaptacao)
        );
    }

    //Desarquivar
    public AdaptacaoPedagogicaResponse desarquivar(Long id) {
        AdaptacaoPedagogica adaptacao = buscarEntidadePorId(id);

        adaptacao.setArquivado(false);
        adaptacao.setDataAtualizacao(LocalDateTime.now());

        return new AdaptacaoPedagogicaResponse(
                adaptacaoRepository.save(adaptacao)
        );
    }

    //Excluir
    public void excluir(Long id) {

        AdaptacaoPedagogica adaptacao = buscarEntidadePorId(id);

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
