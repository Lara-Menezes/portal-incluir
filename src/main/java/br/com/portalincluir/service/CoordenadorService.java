package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.CoordenadorRequest;
import br.com.portalincluir.dto.response.CoordenadorResponse;
import br.com.portalincluir.dto.response.CoordenadorResponse.EstudanteResumo;
import br.com.portalincluir.enums.StatusCoordenador;
import br.com.portalincluir.model.Coordenador;
import br.com.portalincluir.repository.CoordenadorRepository;
import br.com.portalincluir.repository.EstudanteRepository;
import br.com.portalincluir.repository.ProfessorRepository;
import br.com.portalincluir.repository.PlanoEducacionalRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoordenadorService {

    private final CoordenadorRepository coordenadorRepository;
    private final EstudanteRepository estudanteRepository;
    private final ProfessorRepository professorRepository;
    private final PlanoEducacionalRepository planoEducacionalRepository;

    public CoordenadorService(CoordenadorRepository coordenadorRepository,
                              EstudanteRepository estudanteRepository,
                              ProfessorRepository professorRepository,
                              PlanoEducacionalRepository planoEducacionalRepository) {
        this.coordenadorRepository = coordenadorRepository;
        this.estudanteRepository = estudanteRepository;
        this.professorRepository = professorRepository;
        this.planoEducacionalRepository = planoEducacionalRepository;
    }

    @Transactional(readOnly = true)
    public CoordenadorResponse.Resumo buscarResumo(Long coordenadorId) {
        if (!coordenadorRepository.existsById(coordenadorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coordenador não encontrado");
        }

        var estudantesRecentes = estudanteRepository
                .findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id")))
                .stream()
                .map(estudante -> new EstudanteResumo(estudante.getId(), estudante.getNome(),
                        estudante.getCurso(), estudante.getMatricula(), estudante.isAtivo()))
                .toList();

        return new CoordenadorResponse.Resumo(
                estudanteRepository.countByAtivo(true),
                professorRepository.countByAtivo(true),
                planoEducacionalRepository.countByCoordenadorIdAndArquivado(coordenadorId, false),
                planoEducacionalRepository.countByCoordenadorIdAndArquivado(coordenadorId, true),
                estudantesRecentes);
    }

    // Criar coordenador
    public CoordenadorResponse criar(CoordenadorRequest request) {

        String email = normalizarEmail(request.getEmail());
        String matricula = normalizarMatricula(request.getMatricula());

        if (coordenadorRepository.existsByEmailIgnoreCase(email)) {
            throw new RuntimeException("E-mail já utilizado");
        }

        if (coordenadorRepository.existsByMatriculaIgnoreCase(matricula)) {
            throw new RuntimeException("Matrícula já utilizada");
        }

        Coordenador coordenador = new Coordenador();
        preencherDados(coordenador, request);
        coordenador.setEmail(email);
        coordenador.setMatricula(matricula);
        coordenador.setStatus(StatusCoordenador.ATIVO);
        coordenador.setDataCriacao(LocalDateTime.now());

        Coordenador salvo = coordenadorRepository.save(coordenador);
        return new CoordenadorResponse(salvo);
    }

    // Listar todos
    public List<CoordenadorResponse> listarTodos() {
        return coordenadorRepository.findAll()
                .stream()
                .map(CoordenadorResponse::new)
                .toList();
    }

    // Listar por status
    public List<CoordenadorResponse> listarPorStatus(StatusCoordenador status) {
        return coordenadorRepository.findByStatus(status)
                .stream()
                .map(CoordenadorResponse::new)
                .toList();
    }

    // Buscar por ID
    public CoordenadorResponse buscarPorId(Long id) {
        return new CoordenadorResponse(buscarEntidadePorId(id));
    }

    private Coordenador buscarEntidadePorId(Long id) {
        return coordenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado"));
    }

    // Atualizar coordenador
    public CoordenadorResponse atualizar(Long id, CoordenadorRequest request) {
        Coordenador coordenador = buscarEntidadePorId(id);
        String email = normalizarEmail(request.getEmail());
        String matricula = normalizarMatricula(request.getMatricula());

        if (coordenadorRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new RuntimeException("E-mail já utilizado");
        }

        if (coordenadorRepository.existsByMatriculaIgnoreCaseAndIdNot(matricula, id)) {
            throw new RuntimeException("Matrícula já utilizada");
        }

        preencherDados(coordenador, request);
        coordenador.setEmail(email);
        coordenador.setMatricula(matricula);

        coordenador.setDataAtualizacao(LocalDateTime.now());

        Coordenador atualizado = coordenadorRepository.save(coordenador);
        return new CoordenadorResponse(atualizado);
    }

    // Excluir coordenador
    public void excluir(Long id) {
        Coordenador coordenador = coordenadorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coordenador não encontrado"));
        coordenadorRepository.delete(coordenador);
    }

    // Inativar coordenador
    public CoordenadorResponse inativar(Long id) {
        Coordenador coordenador = buscarEntidadePorId(id);

        if (coordenador.getStatus() == StatusCoordenador.INATIVO) {
            throw new RuntimeException("O coordenador já está inativo");
        }

        coordenador.setStatus(StatusCoordenador.INATIVO);
        coordenador.setDataAtualizacao(LocalDateTime.now());

        Coordenador inativado = coordenadorRepository.save(coordenador);
        return new CoordenadorResponse(inativado);
    }

    // Reativar coordenador
    public CoordenadorResponse reativar(Long id) {
        Coordenador coordenador = buscarEntidadePorId(id);

        if (coordenador.getStatus() == StatusCoordenador.ATIVO) {
            throw new RuntimeException("O coordenador já está ativo");
        }

        coordenador.setStatus(StatusCoordenador.ATIVO);
        coordenador.setDataAtualizacao(LocalDateTime.now());

        Coordenador reativado = coordenadorRepository.save(coordenador);
        return new CoordenadorResponse(reativado);
    }

    private void preencherDados(Coordenador coordenador, CoordenadorRequest request) {
        coordenador.setNome(request.getNome().trim());
        coordenador.setEmail(request.getEmail().trim().toLowerCase());
        coordenador.setTelefone(normalizarCampoOpcional(request.getTelefone()));
        coordenador.setDataNascimento(request.getDataNascimento());
        coordenador.setMatricula(request.getMatricula().trim());
        coordenador.setCargo(request.getCargo().trim());
        coordenador.setSetor(request.getSetor().trim());
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String normalizarMatricula(String matricula) {
        return matricula.trim();
    }

    private String normalizarCampoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
