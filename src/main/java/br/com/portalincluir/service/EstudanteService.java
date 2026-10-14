package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.EstudanteRequest;
import br.com.portalincluir.dto.response.EstudanteResponse;
import br.com.portalincluir.model.Estudante;
import br.com.portalincluir.repository.EstudanteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@org.springframework.transaction.annotation.Transactional
@Service
public class EstudanteService {

    private final EstudanteRepository estudanteRepository;

    private final PoliticaHistoricoService politica;
    private final br.com.portalincluir.repository.PlanoAcaoRepository planos;
    private final br.com.portalincluir.repository.AdaptacaoPedagogicaRepository adaptacoes;

    public EstudanteService(EstudanteRepository estudanteRepository, PoliticaHistoricoService politica,
            br.com.portalincluir.repository.PlanoAcaoRepository planos,
            br.com.portalincluir.repository.AdaptacaoPedagogicaRepository adaptacoes) {
        this.politica = politica;
        this.planos = planos;
        this.adaptacoes = adaptacoes;
        this.estudanteRepository = estudanteRepository;
    }

    public EstudanteResponse concluir(Long id, java.time.LocalDate data) {
        if (data == null || data.isAfter(politica.hoje()) || !politica.hoje().isBefore(data.plusYears(5))) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Data de conclusao invalida ou prazo ja encerrado");
        }
        Estudante estudante = politica.bloquear(id);
        politica.exigirEditavel(estudante);
        estudante.concluir(data);
        // Cria uma revisao dos filhos preexistentes na mesma transacao da conclusao.
        planos.findByEstudanteId(id).forEach(p -> p.setDataAtualizacao(politica.agora()));
        adaptacoes.findByEstudanteId(id).forEach(a -> a.setDataAtualizacao(politica.agora()));
        return new EstudanteResponse(estudanteRepository.save(estudante));
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
        estudante.setPeriodoIngresso(request.getPeriodoIngresso());
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
                .filter(politica::disponivel)
                .map(EstudanteResponse::new)
                .toList();
    }

    // Buscar por ID
    public EstudanteResponse buscarPorId(Long id) {
        Estudante estudante = buscarEntidadePorId(id);
        politica.exigirDisponivel(estudante);

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
                .filter(politica::disponivel)
                .map(EstudanteResponse::new)
                .toList();
    }

    // Listar estudantes inativos
    public List<EstudanteResponse> listarInativos() {
        return estudanteRepository.findByAtivo(false)
                .stream()
                .filter(politica::disponivel)
                .map(EstudanteResponse::new)
                .toList();
    }

    // Buscar estudantes por nome
    public List<EstudanteResponse> buscarPorNome(String nome) {
        return estudanteRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .filter(politica::disponivel)
                .map(EstudanteResponse::new)
                .toList();
    }

    // Atualizar
    public EstudanteResponse atualizar(Long id, EstudanteRequest request) {

        Estudante estudante = buscarEntidadePorId(id);
        politica.exigirEditavel(estudante);

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
        estudante.setPeriodoIngresso(request.getPeriodoIngresso());
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
        politica.exigirEditavel(estudante);

        throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,
                "Use inativacao ou conclusao; exclusao definitiva ocorre pela politica de retencao");
    }

    // Inativar
    public EstudanteResponse inativar(Long id) {

        Estudante estudante = buscarEntidadePorId(id);
        politica.exigirEditavel(estudante);

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
        politica.exigirEditavel(estudante);

        if (estudante.isAtivo()) {
            throw new RuntimeException("O estudante já está ativo");
        }

        estudante.setAtivo(true);

        Estudante atualizado = estudanteRepository.save(estudante);

        return new EstudanteResponse(atualizado);
    }
}