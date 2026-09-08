package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.CoordenadorRequest;
import br.com.portalincluir.dto.response.CoordenadorResponse;
import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.enums.StatusCoordenador;
import br.com.portalincluir.model.Coordenador;
import br.com.portalincluir.repository.CoordenadorRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoordenadorService {

    private final CoordenadorRepository coordenadorRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CoordenadorService(CoordenadorRepository coordenadorRepository) {
        this.coordenadorRepository = coordenadorRepository;
    }

    // Criar coordenador
    public CoordenadorResponse criar(CoordenadorRequest request) {
        validarSenhaObrigatoria(request.getSenha());

        String identificadorAcesso = normalizarIdentificador(request.getIdentificadorAcesso());
        String email = normalizarEmail(request.getEmail());
        String matricula = normalizarMatricula(request.getMatricula());

        if (coordenadorRepository.existsByIdentificadorAcessoIgnoreCase(identificadorAcesso)) {
            throw new RuntimeException("Identificador de acesso já utilizado");
        }

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
        coordenador.setIdentificadorAcesso(identificadorAcesso);
        coordenador.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        coordenador.setPerfil(PerfilUsuario.COORDENADOR);
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
        String identificadorAcesso = normalizarIdentificador(request.getIdentificadorAcesso());
        String email = normalizarEmail(request.getEmail());
        String matricula = normalizarMatricula(request.getMatricula());

        if (coordenadorRepository.existsByIdentificadorAcessoIgnoreCaseAndIdNot(identificadorAcesso, id)) {
            throw new RuntimeException("Identificador de acesso já utilizado");
        }

        if (coordenadorRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new RuntimeException("E-mail já utilizado");
        }

        if (coordenadorRepository.existsByMatriculaIgnoreCaseAndIdNot(matricula, id)) {
            throw new RuntimeException("Matrícula já utilizada");
        }

        preencherDados(coordenador, request);
        coordenador.setEmail(email);
        coordenador.setMatricula(matricula);
        coordenador.setIdentificadorAcesso(identificadorAcesso);

        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            coordenador.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        }

        coordenador.setDataAtualizacao(LocalDateTime.now());

        Coordenador atualizado = coordenadorRepository.save(coordenador);
        return new CoordenadorResponse(atualizado);
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
        coordenador.setPerfil(PerfilUsuario.COORDENADOR);
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

    private void validarSenhaObrigatoria(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new RuntimeException("A senha é obrigatória");
        }
    }

    private String normalizarIdentificador(String identificador) {
        return identificador.trim().toLowerCase();
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
