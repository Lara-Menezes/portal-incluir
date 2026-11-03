package br.com.portalincluir.service;
import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import br.com.portalincluir.config.CatalogoProfissional;
import br.com.portalincluir.enums.PerfilUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor @Transactional
public class VinculoEstudanteService {
    private final VinculoEstudanteProfissionalRepository vinculos;
    private final UsuarioRepository usuarios;
    private final PoliticaHistoricoService politica;
    private final EstudanteRepository estudantes;
    private final EscopoEstudanteService escopo;
    public record Profissional(Long usuarioId, Long perfilId, String nome, PerfilUsuario perfil, String areaAtuacao, String disciplina, String setor, Set<String> cursosAtuacao, boolean mesmoCurso, boolean ativo, boolean vinculado) {}
    public record Vinculo(Long id, long versao, Profissional profissional, boolean ativo, String motivo, String motivoEncerramento, LocalDateTime dataInicio, LocalDateTime dataEncerramento) {}
    private Profissional profissional(Usuario u, String curso, boolean vinculado) {
        boolean professor = u.getPerfil() == PerfilUsuario.PROFESSOR;
        var p = new org.springframework.beans.BeanWrapperImpl(GestaoUsuarioService.profissional(u));
        Set<String> cursos = professor ? new TreeSet<>(u.getProfessor().getCursosAtuacao()) : Set.of();
        return new Profissional(u.getId(), (Long)p.getPropertyValue("id"), (String)p.getPropertyValue("nome"), u.getPerfil(), professor ? "Docência" : u.getEquipeMultidisciplinar().getAreaAtuacao(), professor ? u.getProfessor().getDisciplina() : null, (String)p.getPropertyValue("setor"), cursos, professor && CatalogoProfissional.atuaNoCurso(cursos, curso), PermissaoService.ativo(u), vinculado);
    }
    private boolean corresponde(Usuario u, VinculoEstudanteProfissional v) {
        return u.getPerfil() == PerfilUsuario.PROFESSOR && v.getProfessor() != null && u.getProfessor().getId().equals(v.getProfessor().getId()) || u.getPerfil() == PerfilUsuario.EQUIPE_MULTIDISCIPLINAR && v.getMembro() != null && u.getEquipeMultidisciplinar().getId().equals(v.getMembro().getId());
    }
    private Vinculo resposta(VinculoEstudanteProfissional v, List<Usuario> contas) {
        Profissional p = contas.stream().filter(u -> corresponde(u, v)).findFirst().map(u -> profissional(u, v.getEstudante().getCurso(), v.isAtivo())).orElseGet(() -> {
            boolean professor = v.getProfessor() != null;
            return new Profissional(null, professor ? v.getProfessor().getId() : v.getMembro().getId(), professor ? v.getProfessor().getNome() : v.getMembro().getNome(), professor ? PerfilUsuario.PROFESSOR : PerfilUsuario.EQUIPE_MULTIDISCIPLINAR, professor ? "Docência" : v.getMembro().getAreaAtuacao(), professor ? v.getProfessor().getDisciplina() : null, professor ? v.getProfessor().getSetor() : v.getMembro().getSetor(), professor ? new TreeSet<>(v.getProfessor().getCursosAtuacao()) : Set.of(), false, false, v.isAtivo());
        });
        return new Vinculo(v.getId(), v.getVersao(), p, v.isAtivo(), v.getMotivo(), v.getMotivoEncerramento(), v.getDataInicio(), v.getDataEncerramento());
    }
    @Transactional(readOnly=true) public List<Vinculo> listar(Long estudanteId) {
        escopo.exigir(estudanteId);
        var contas = usuarios.findAll();
        return vinculos.findByEstudanteIdOrderByAtivoDescDataInicioDesc(estudanteId).stream().map(v -> resposta(v, contas)).toList();
    }
    @Transactional(readOnly=true) public List<Profissional> candidatos(Long estudanteId) {
        escopo.exigirGestao();
        var estudante = estudantes.findById(estudanteId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        politica.exigirDisponivel(estudante);
        var atuais = vinculos.findByEstudanteIdOrderByAtivoDescDataInicioDesc(estudanteId);
        return usuarios.findAll().stream().filter(u -> u.getPerfil() == PerfilUsuario.PROFESSOR || u.getPerfil() == PerfilUsuario.EQUIPE_MULTIDISCIPLINAR).filter(PermissaoService::ativo)
            .map(u -> profissional(u, estudante.getCurso(), atuais.stream().anyMatch(v -> v.isAtivo() && corresponde(u, v))))
            .sorted(Comparator.comparing(Profissional::mesmoCurso).reversed().thenComparing(Profissional::nome, String.CASE_INSENSITIVE_ORDER)).toList();
    }
    public Vinculo associar(Long estudanteId, Long profissionalId, String motivo, Long autor) {
        escopo.exigirGestao();
        Estudante estudante = politica.bloquear(estudanteId); politica.exigirEditavel(estudante);
        if (!estudante.isAtivo()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ative o estudante antes de associar profissionais.");
        Usuario profissional = usuarios.findById(profissionalId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado."));
        if (!PermissaoService.ativo(profissional)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Este profissional está inativo.");
        Optional<VinculoEstudanteProfissional> anterior;
        if (profissional.getPerfil() == PerfilUsuario.PROFESSOR) anterior = vinculos.findByEstudanteIdAndProfessorId(estudanteId, profissional.getProfessor().getId());
        else if (profissional.getPerfil() == PerfilUsuario.EQUIPE_MULTIDISCIPLINAR) anterior = vinculos.findByEstudanteIdAndMembroId(estudanteId, profissional.getEquipeMultidisciplinar().getId());
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione um professor ou integrante da equipe multidisciplinar.");
        if (anterior.filter(VinculoEstudanteProfissional::isAtivo).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Este profissional já está associado ao estudante.");
        var v = anterior.orElseGet(VinculoEstudanteProfissional::new);
        v.setEstudante(estudante); v.setProfessor(profissional.getProfessor()); v.setMembro(profissional.getEquipeMultidisciplinar()); v.setAtivo(true); v.setMotivo(motivo.strip()); v.setDataInicio(politica.agora()); v.setDataEncerramento(null); v.setMotivoEncerramento(null); v.setResponsavelUsuarioId(autor);
        return resposta(vinculos.saveAndFlush(v), List.of(profissional));
    }
    public Vinculo encerrar(Long estudanteId, Long id, long versao, String motivo, Long autor) {
        escopo.exigirGestao();
        var estudante = politica.bloquear(estudanteId); politica.exigirEditavel(estudante);
        var v = vinculos.buscarComBloqueio(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vínculo não encontrado."));
        if (!v.getEstudante().getId().equals(estudanteId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vínculo não encontrado para este estudante.");
        if (v.getVersao() != versao || !v.isAtivo()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Este vínculo foi alterado. Atualize os dados antes de continuar.");
        v.setAtivo(false); v.setDataEncerramento(politica.agora()); v.setMotivoEncerramento(motivo.strip()); v.setResponsavelUsuarioId(autor);
        return resposta(vinculos.saveAndFlush(v), usuarios.findAll());
    }
}
