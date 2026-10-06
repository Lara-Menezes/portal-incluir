package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.EquipeMultidisciplinarRequest;
import br.com.portalincluir.dto.response.EquipeMultidisciplinarResponse;
import br.com.portalincluir.model.EquipeMultidisciplinar;
import br.com.portalincluir.repository.EquipeMultidisciplinarRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class EquipeMultidisciplinarService {

    private final EquipeMultidisciplinarRepository repository;

    public EquipeMultidisciplinarService(
            EquipeMultidisciplinarRepository repository
    ) {
        this.repository = repository;
    }

    public EquipeMultidisciplinarResponse criar(
            EquipeMultidisciplinarRequest request
    ) {
        validarDuplicidade(request, null);

        EquipeMultidisciplinar profissional =
                new EquipeMultidisciplinar();

        preencherDados(profissional, request);

        return salvar(profissional);
    }

    public List<EquipeMultidisciplinarResponse> listar(Boolean ativo) {
        List<EquipeMultidisciplinar> profissionais = ativo == null
                ? repository.findAll()
                : repository.findByAtivo(ativo);

        return profissionais.stream()
                .map(EquipeMultidisciplinarResponse::new)
                .toList();
    }

    public EquipeMultidisciplinarResponse buscarPorId(Long id) {
        return new EquipeMultidisciplinarResponse(buscarEntidade(id));
    }

    public EquipeMultidisciplinarResponse atualizar(
            Long id,
            EquipeMultidisciplinarRequest request
    ) {
        EquipeMultidisciplinar profissional = buscarEntidade(id);

        validarDuplicidade(request, id);
        preencherDados(profissional, request);

        return salvar(profissional);
    }

    public EquipeMultidisciplinarResponse inativar(Long id) {
        EquipeMultidisciplinar profissional = buscarEntidade(id);
        profissional.setAtivo(false);

        return salvar(profissional);
    }

    public EquipeMultidisciplinarResponse reativar(Long id) {
        EquipeMultidisciplinar profissional = buscarEntidade(id);
        profissional.setAtivo(true);

        return salvar(profissional);
    }

    public void excluir(Long id) {
        EquipeMultidisciplinar profissional = buscarEntidade(id);

        try {
            repository.delete(profissional);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O profissional possui registros vinculados e não pode ser excluído",
                    exception
            );
        }
    }

    private EquipeMultidisciplinar buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profissional não encontrado"
                ));
    }

    private void validarDuplicidade(
            EquipeMultidisciplinarRequest request,
            Long id
    ) {
        String email = normalizarEmail(request.getEmail());
        String matricula = request.getMatricula().trim();

        boolean emailExistente = id == null
                ? repository.existsByEmailIgnoreCase(email)
                : repository.existsByEmailIgnoreCaseAndIdNot(email, id);

        if (emailExistente) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "E-mail já cadastrado"
            );
        }

        boolean matriculaExistente = id == null
                ? repository.existsByMatricula(matricula)
                : repository.existsByMatriculaAndIdNot(matricula, id);

        if (matriculaExistente) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Matrícula já cadastrada"
            );
        }
    }

    private void preencherDados(
            EquipeMultidisciplinar profissional,
            EquipeMultidisciplinarRequest request
    ) {
        profissional.setNome(request.getNome().trim());
        profissional.setEmail(normalizarEmail(request.getEmail()));
        profissional.setTelefone(normalizarOpcional(request.getTelefone()));
        profissional.setDataNascimento(request.getDataNascimento());
        profissional.setMatricula(request.getMatricula().trim());
        profissional.setAreaAtuacao(request.getAreaAtuacao().trim());
        profissional.setCargo(request.getCargo().trim());
        profissional.setSetor(request.getSetor().trim());

        profissional.setConselhoProfissional(
                normalizarOpcional(request.getConselhoProfissional())
        );

        profissional.setRegistroProfissional(
                normalizarOpcional(request.getRegistroProfissional())
        );
    }

    private EquipeMultidisciplinarResponse salvar(
            EquipeMultidisciplinar profissional
    ) {
        try {
            return new EquipeMultidisciplinarResponse(
                    repository.saveAndFlush(profissional)
            );
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não foi possível salvar: verifique duplicidades e restrições cadastrais",
                    exception
            );
        }
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}