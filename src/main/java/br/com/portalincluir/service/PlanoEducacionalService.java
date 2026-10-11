package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.PlanoEducacionalRequest;
import br.com.portalincluir.dto.response.PlanoEducacionalResponse;
import br.com.portalincluir.model.Coordenador;
import br.com.portalincluir.model.Estudante;
import br.com.portalincluir.model.PlanoEducacional;
import br.com.portalincluir.repository.CoordenadorRepository;
import br.com.portalincluir.repository.EstudanteRepository;
import br.com.portalincluir.repository.PlanoEducacionalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlanoEducacionalService {

    private final PlanoEducacionalRepository planoRepository;
    private final EstudanteRepository estudanteRepository;
    private final CoordenadorRepository coordenadorRepository;

    public PlanoEducacionalService(
            PlanoEducacionalRepository planoRepository,
            EstudanteRepository estudanteRepository,
            CoordenadorRepository coordenadorRepository) {
        this.planoRepository = planoRepository;
        this.estudanteRepository = estudanteRepository;
        this.coordenadorRepository = coordenadorRepository;
    }

    //Criar
    public PlanoEducacionalResponse criar(PlanoEducacionalRequest request) {
        Estudante estudante = estudanteRepository
                .findById(request.getEstudanteId())
                .orElseThrow(() ->
                        new RuntimeException("Estudante não encontrado"));

        Coordenador coordenador = coordenadorRepository
                .findById(request.getCoordenadorId())
                .orElseThrow(() ->
                        new RuntimeException("Coordenador não encontrado"));

        PlanoEducacional plano = new PlanoEducacional();

        plano.setEstudante(estudante);
        plano.setCoordenador(coordenador);

        preencherDados(plano, request);

        plano.setArquivado(false);
        plano.setDataCriacao(LocalDateTime.now());

        PlanoEducacional salvo = planoRepository.save(plano);

        return new PlanoEducacionalResponse(salvo);
    }

    //Listar tudo
    public List<PlanoEducacionalResponse> listarTodos() {
        return planoRepository.findAll()
                .stream()
                .map(PlanoEducacionalResponse::new)
                .toList();
    }

    //Buscar por id
    public PlanoEducacionalResponse buscarPorId(Long id) {
        return new PlanoEducacionalResponse(buscarEntidadePorId(id));
    }

    private PlanoEducacional buscarEntidadePorId(Long id) {
        return planoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Plano educacional não encontrado"));
    }

    //Listar por estudante
    public List<PlanoEducacionalResponse> listarPorEstudante(
            Long estudanteId) {
        return planoRepository.findByEstudanteId(estudanteId)
                .stream()
                .map(PlanoEducacionalResponse::new)
                .toList();
    }

    //Listar por coordenador
    public List<PlanoEducacionalResponse> listarPorCoordenador(
            Long coordenadorId) {
        return planoRepository.findByCoordenadorId(coordenadorId)
                .stream()
                .map(PlanoEducacionalResponse::new)
                .toList();
    }

    //Listar por estudante e semestre
    public List<PlanoEducacionalResponse> listarPorEstudanteESemestre(
            Long estudanteId, String semestreAno) {
        return planoRepository
                .findByEstudanteIdAndSemestreAno(estudanteId, semestreAno)
                .stream()
                .map(PlanoEducacionalResponse::new)
                .toList();
    }

    //Listar por arquivado
    public List<PlanoEducacionalResponse> listarPorArquivamento(
            boolean arquivado) {
        return planoRepository.findByArquivado(arquivado)
                .stream()
                .map(PlanoEducacionalResponse::new)
                .toList();
    }

    //Atualizar
    public PlanoEducacionalResponse atualizar(
            Long id, PlanoEducacionalRequest request) {

        PlanoEducacional plano = buscarEntidadePorId(id);

        if (plano.isArquivado()) {
            throw new RuntimeException(
                    "Não é possível atualizar um plano educacional arquivado");
        }

        Estudante estudante = estudanteRepository
                .findById(request.getEstudanteId())
                .orElseThrow(() ->
                        new RuntimeException("Estudante não encontrado"));

        Coordenador coordenador = coordenadorRepository
                .findById(request.getCoordenadorId())
                .orElseThrow(() ->
                        new RuntimeException("Coordenador não encontrado"));

        plano.setEstudante(estudante);
        plano.setCoordenador(coordenador);

        preencherDados(plano, request);

        plano.setDataAtualizacao(LocalDateTime.now());

        PlanoEducacional atualizado = planoRepository.save(plano);

        return new PlanoEducacionalResponse(atualizado);
    }

    //Arquivar
    public PlanoEducacionalResponse arquivar(Long id) {
        PlanoEducacional plano = buscarEntidadePorId(id);

        plano.setArquivado(true);
        plano.setDataAtualizacao(LocalDateTime.now());

        return new PlanoEducacionalResponse(
                planoRepository.save(plano));
    }

    //Desarquivar
    public PlanoEducacionalResponse desarquivar(Long id) {
        PlanoEducacional plano = buscarEntidadePorId(id);

        plano.setArquivado(false);
        plano.setDataAtualizacao(LocalDateTime.now());

        return new PlanoEducacionalResponse(
                planoRepository.save(plano));
    }

    //Excluir
    public void excluir(Long id) {
        PlanoEducacional plano = buscarEntidadePorId(id);
        planoRepository.delete(plano);
    }


    private void preencherDados(
            PlanoEducacional plano,
            PlanoEducacionalRequest request) {

        plano.setSemestreAno(request.getSemestreAno().trim());
        plano.setHistorico(request.getHistorico().trim());
        plano.setNecessidadesEspecificas(
                request.getNecessidadesEspecificas().trim());
        plano.setConhecimentosHabilidadesInteresses(
                request.getConhecimentosHabilidadesInteresses().trim());
        plano.setDificuldadesApresentadas(
                request.getDificuldadesApresentadas().trim());
        plano.setPropostasAdaptacoesCurriculares(
                request.getPropostasAdaptacoesCurriculares().trim());
    }
}