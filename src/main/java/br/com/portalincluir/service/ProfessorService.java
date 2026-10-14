package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.ProfessorRequest;
import br.com.portalincluir.dto.response.ProfessorResponse;
import br.com.portalincluir.model.Professor;
import br.com.portalincluir.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public ProfessorResponse criar(ProfessorRequest request) {

        String email = normalizarEmail(request.getEmail());
        String matricula = normalizarMatricula(request.getMatricula());

        if (professorRepository.existsByEmailIgnoreCase(email)) {
            throw new RuntimeException("E-mail já utilizado");
        }

        if (professorRepository.existsByMatriculaIgnoreCase(matricula)) {
            throw new RuntimeException("Matrícula já utilizada");
        }

        Professor professor = new Professor();

        preencherDados(professor, request);

        professor.setEmail(email);
        professor.setMatricula(matricula);
        professor.setAtivo(true);
        professor.setDataCriacao(LocalDateTime.now());

        Professor salvo = professorRepository.save(professor);

        return new ProfessorResponse(salvo);
    }

    public List<ProfessorResponse> listarTodos() {
        return professorRepository.findAll()
                .stream()
                .map(ProfessorResponse::new)
                .toList();
    }

    public List<ProfessorResponse> listarAtivos() {
        return professorRepository.findByAtivo(true)
                .stream()
                .map(ProfessorResponse::new)
                .toList();
    }

    public List<ProfessorResponse> listarInativos() {
        return professorRepository.findByAtivo(false)
                .stream()
                .map(ProfessorResponse::new)
                .toList();
    }

    public List<ProfessorResponse> buscarPorNome(String nome) {
        return professorRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(ProfessorResponse::new)
                .toList();
    }

    public ProfessorResponse buscarPorId(Long id) {
        return new ProfessorResponse(buscarEntidadePorId(id));
    }

    private Professor buscarEntidadePorId(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
    }

    public ProfessorResponse atualizar(Long id, ProfessorRequest request) {

        Professor professor = buscarEntidadePorId(id);

        String email = normalizarEmail(request.getEmail());
        String matricula = normalizarMatricula(request.getMatricula());

        if (professorRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new RuntimeException("E-mail já utilizado");
        }

        if (professorRepository.existsByMatriculaIgnoreCaseAndIdNot(matricula, id)) {
            throw new RuntimeException("Matrícula já utilizada");
        }

        preencherDados(professor, request);

        professor.setEmail(email);
        professor.setMatricula(matricula);
        professor.setDataAtualizacao(LocalDateTime.now());

        Professor atualizado = professorRepository.save(professor);

        return new ProfessorResponse(atualizado);
    }

    public ProfessorResponse inativar(Long id) {

        Professor professor = buscarEntidadePorId(id);

        if (!professor.isAtivo()) {
            throw new RuntimeException("O professor já está inativo");
        }

        professor.setAtivo(false);
        professor.setDataAtualizacao(LocalDateTime.now());

        Professor atualizado = professorRepository.save(professor);

        return new ProfessorResponse(atualizado);
    }

    public ProfessorResponse ativar(Long id) {

        Professor professor = buscarEntidadePorId(id);

        if (professor.isAtivo()) {
            throw new RuntimeException("O professor já está ativo");
        }

        professor.setAtivo(true);
        professor.setDataAtualizacao(LocalDateTime.now());

        Professor atualizado = professorRepository.save(professor);

        return new ProfessorResponse(atualizado);
    }

    public void excluir(Long id) {

        Professor professor = buscarEntidadePorId(id);

        professorRepository.delete(professor);
    }

    private void preencherDados(
            Professor professor,
            ProfessorRequest request) {

        professor.setNome(request.getNome().trim());
        professor.setEmail(normalizarEmail(request.getEmail()));
        professor.setTelefone(normalizarCampoOpcional(request.getTelefone()));
        professor.setDataNascimento(request.getDataNascimento());
        professor.setMatricula(normalizarMatricula(request.getMatricula()));
        professor.setDisciplina(request.getDisciplina().trim());
        professor.setSetor(request.getSetor().trim());
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String normalizarMatricula(String matricula) {
        return matricula.trim();
    }

    private String normalizarCampoOpcional(String valor) {
        return valor == null || valor.isBlank()
                ? null
                : valor.trim();
    }
}
