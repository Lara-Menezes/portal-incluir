package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.EstudanteRequest;
import br.com.portalincluir.dto.response.EstudanteResponse;
import br.com.portalincluir.model.Estudante;
import br.com.portalincluir.repository.EstudanteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstudanteService {

    private final EstudanteRepository estudanteRepository;

    public EstudanteService(EstudanteRepository estudanteRepository) {
        this.estudanteRepository = estudanteRepository;
    }

    // Cadastrar
    public EstudanteResponse cadastrar(EstudanteRequest request) {

        if (estudanteRepository.existsByMatricula(request.getMatricula())) {
            throw new RuntimeException("Já existe um estudante com essa matrícula");
        }

        Estudante estudante = new Estudante();

        estudante.setNome(request.getNomeCompleto());
        estudante.setDataNascimento(request.getDataNascimento());
        estudante.setCurso(request.getCurso());
        estudante.setMatricula(request.getMatricula());
        estudante.setTelefone(request.getTelefone());
        estudante.setEmail(request.getEmail());
        estudante.setNomeResponsavel(request.getNomeResponsavel());
        estudante.setTelefoneResponsavel(request.getTelefoneResponsavel());
        estudante.setEmailResponsavel(request.getEmailResponsavel());

        Estudante salvo = estudanteRepository.save(estudante);

        return new EstudanteResponse(salvo);
    }

    // Listar todos
    public List<EstudanteResponse> listarTodos() {
        return estudanteRepository.findAll()
                .stream()
                .map(EstudanteResponse::new)
                .toList();
    }

    // Buscar por ID
    public EstudanteResponse buscarPorId(Long id) {
        Estudante estudante = buscarEntidadePorId(id);

        return new EstudanteResponse(estudante);
    }

    private Estudante buscarEntidadePorId(Long id) {
        return estudanteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Estudante não encontrado"));
    }

    // Listar estudantes ativos
    public List<EstudanteResponse> listarAtivos() {
        return estudanteRepository.findByAtivo(true)
                .stream()
                .map(EstudanteResponse::new)
                .toList();
    }

    // Listar estudantes inativos
    public List<EstudanteResponse> listarInativos() {
        return estudanteRepository.findByAtivo(false)
                .stream()
                .map(EstudanteResponse::new)
                .toList();
    }

    // Buscar estudantes por nome
    public List<EstudanteResponse> buscarPorNome(String nome) {
        return estudanteRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(EstudanteResponse::new)
                .toList();
    }

    // Atualizar
    public EstudanteResponse atualizar(Long id, EstudanteRequest request) {

        Estudante estudante = buscarEntidadePorId(id);

        if (!estudante.isAtivo()) {
            throw new RuntimeException("Não é possível atualizar um estudante inativo");
        }

        if (!estudante.getMatricula().equals(request.getMatricula())
                && estudanteRepository.existsByMatricula(request.getMatricula())) {

            throw new RuntimeException(
                    "Já existe um estudante com essa matrícula"
            );
        }

        estudante.setNome(request.getNomeCompleto());
        estudante.setDataNascimento(request.getDataNascimento());
        estudante.setCurso(request.getCurso());
        estudante.setMatricula(request.getMatricula());
        estudante.setTelefone(request.getTelefone());
        estudante.setEmail(request.getEmail());
        estudante.setNomeResponsavel(request.getNomeResponsavel());
        estudante.setTelefoneResponsavel(request.getTelefoneResponsavel());
        estudante.setEmailResponsavel(request.getEmailResponsavel());

        Estudante atualizado = estudanteRepository.save(estudante);

        return new EstudanteResponse(atualizado);
    }

    // Excluir
    public void excluir(Long id) {

        Estudante estudante = buscarEntidadePorId(id);

        estudanteRepository.delete(estudante);
    }

    // Inativar
    public EstudanteResponse inativar(Long id) {

        Estudante estudante = buscarEntidadePorId(id);

        if (!estudante.isAtivo()) {
            throw new RuntimeException("O estudante já está inativo");
        }

        estudante.setAtivo(false);

        Estudante atualizado = estudanteRepository.save(estudante);

        return new EstudanteResponse(atualizado);
    }

    // Reativar
    public EstudanteResponse ativar(Long id) {

        Estudante estudante = buscarEntidadePorId(id);

        if (estudante.isAtivo()) {
            throw new RuntimeException("O estudante já está ativo");
        }

        estudante.setAtivo(true);

        Estudante atualizado = estudanteRepository.save(estudante);

        return new EstudanteResponse(atualizado);
    }
}