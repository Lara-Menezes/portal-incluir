package br.com.portalincluir.service;

import br.com.portalincluir.model.Usuario;
import br.com.portalincluir.enums.StatusCoordenador;
import br.com.portalincluir.repository.UsuarioRepository;
import br.com.portalincluir.dto.request.EditarUsuarioRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class GestaoUsuarioService {
    private final UsuarioRepository usuarios;
    public record Detalhe(Long id, String nome, String email, Object perfil, Long perfilId, boolean ativo, Map<String,Object> dados, Set<String> permissoesPadrao, Map<String,Boolean> ajustes, Set<String> permissoes, Set<String> cursosAtuacao, boolean root, boolean administracaoAprovada) {}
    public static Object profissional(Usuario u) {
        return switch(u.getPerfil()) {
            case COORDENADOR -> u.getCoordenador(); case PROFESSOR -> u.getProfessor();
            case ADMINISTRADOR -> u.getAdministrador(); case EQUIPE_MULTIDISCIPLINAR -> u.getEquipeMultidisciplinar();
        };
    }
    public static Detalhe detalhe(Usuario u) {
        BeanWrapper p = new BeanWrapperImpl(profissional(u));
        Map<String,Object> dados = new LinkedHashMap<>();
        for (String campo : List.of("nome", "matricula", "telefone", "dataNascimento", "setor", "cargo", "disciplina", "areaAtuacao", "conselhoProfissional", "registroProfissional"))
            if (p.isReadableProperty(campo)) dados.put(campo, p.getPropertyValue(campo));
        return new Detalhe(u.getId(), (String)p.getPropertyValue("nome"), u.getEmail(), u.getPerfil(), (Long)p.getPropertyValue("id"), PermissaoService.ativo(u), dados, u.isRoot() ? PermissaoService.CATALOGO : PermissaoService.padrao(u.getPerfil()), new TreeMap<>(u.getPermissoesIndividuais()), PermissaoService.efetivas(u), u.getProfessor() == null ? Set.of() : new TreeSet<>(u.getProfessor().getCursosAtuacao()), u.isRoot(), u.isAdministracaoAprovada());
    }
    @Transactional(readOnly=true) public List<Detalhe> listar() { return usuarios.findAll().stream().map(GestaoUsuarioService::detalhe).toList(); }
    private void protegerRoot(Usuario u) { if (u.isRoot()) throw new ResponseStatusException(HttpStatus.CONFLICT, "A conta root não pode ser alterada pela gestão de outras contas."); }
    private Usuario buscar(Long id) { return usuarios.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.")); }
    private void impedirPropriaConta(Long id, Long autor) { if (id.equals(autor)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Peça a outro responsável para alterar o acesso da sua conta."); }
    private void preservarGestao(Usuario alvo, boolean ativo, Set<String> permissoes) {
        if (ativo && permissoes.contains("permissoes.gerenciar") && permissoes.contains("permissoes") && permissoes.contains("usuarios")) return;
        boolean outro = usuarios.bloquearParaGestao().stream().anyMatch(u -> !u.getId().equals(alvo.getId()) && PermissaoService.ativo(u) && PermissaoService.efetivas(u).containsAll(Set.of("permissoes", "permissoes.gerenciar", "usuarios")));
        if (!outro) throw new ResponseStatusException(HttpStatus.CONFLICT, "Mantenha ao menos uma conta ativa com acesso à gestão de permissões.");
    }
    @Transactional public Detalhe editar(Long id, EditarUsuarioRequest r) {
        Usuario u = buscar(id); protegerRoot(u);
        String email = r.email().strip().toLowerCase(Locale.ROOT);
        if (usuarios.findByEmailIgnoreCase(email).filter(outro -> !outro.getId().equals(id)).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está em uso.");
        BeanWrapper p = new BeanWrapperImpl(profissional(u));
        String areaAnterior = u.getEquipeMultidisciplinar() == null ? null : u.getEquipeMultidisciplinar().getAreaAtuacao();
        String obrigatorio = u.getProfessor() == null ? r.cargo() : "curso";
        if (u.getProfessor() != null && r.cursosAtuacao() == null && u.getProfessor().getCursosAtuacao().isEmpty() && (r.disciplina() == null || r.disciplina().isBlank())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione o curso de atuacao do professor.");
        if (obrigatorio == null || obrigatorio.isBlank() || (u.getPerfil().name().equals("EQUIPE_MULTIDISCIPLINAR") && (r.areaAtuacao() == null || r.areaAtuacao().isBlank()))) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preencha os dados profissionais obrigatórios.");
        p.setPropertyValue("nome", r.nome().strip()); p.setPropertyValue("email", email); p.setPropertyValue("matricula", r.matricula().strip());
        p.setPropertyValue("telefone", r.telefone()); p.setPropertyValue("dataNascimento", r.dataNascimento()); p.setPropertyValue("setor", r.setor().strip());
        for (var entry : Map.of("cargo", Objects.toString(r.cargo(), ""), "disciplina", Objects.toString(r.disciplina(), ""), "areaAtuacao", Objects.toString(r.areaAtuacao(), ""), "conselhoProfissional", Objects.toString(r.conselhoProfissional(), ""), "registroProfissional", Objects.toString(r.registroProfissional(), "")).entrySet()) if (p.isWritableProperty(entry.getKey())) p.setPropertyValue(entry.getKey(), entry.getValue().strip());
        if (u.getProfessor() != null && r.cursosAtuacao() != null) u.getProfessor().setCursosAtuacao(br.com.portalincluir.config.CatalogoProfissional.cursos(r.cursosAtuacao()));
        if (u.getEquipeMultidisciplinar() != null && !Objects.equals(r.areaAtuacao(), areaAnterior)) u.getEquipeMultidisciplinar().setAreaAtuacao(br.com.portalincluir.config.CatalogoProfissional.area(r.areaAtuacao()));
        if (p.isWritableProperty("dataAtualizacao")) p.setPropertyValue("dataAtualizacao", LocalDateTime.now());
        u.setEmail(email); return detalhe(usuarios.saveAndFlush(u));
    }
    @Transactional public Detalhe situacao(Long id, boolean ativo, Long autor) {
        usuarios.bloquearParaGestao();
        Usuario u = buscar(id); protegerRoot(u);
        if (!ativo) impedirPropriaConta(id, autor);
        preservarGestao(u, ativo, PermissaoService.efetivas(u));
        BeanWrapper p = new BeanWrapperImpl(profissional(u));
        if (p.isWritableProperty("status")) p.setPropertyValue("status", ativo ? StatusCoordenador.ATIVO : StatusCoordenador.INATIVO);
        else p.setPropertyValue("ativo", ativo);
        return detalhe(usuarios.saveAndFlush(u));
    }
    @Transactional public void excluir(Long id, Long autor) {
        usuarios.bloquearParaGestao(); Usuario u = buscar(id); protegerRoot(u); impedirPropriaConta(id, autor); preservarGestao(u, false, Set.of());
        usuarios.delete(u); usuarios.flush();
        // Professional records are retained because plans, reports and audits refer to them.
    }
    @Transactional public Detalhe permissoes(Long id, Map<String,Boolean> ajustes, Long autor) {
        usuarios.bloquearParaGestao(); Usuario u = buscar(id); protegerRoot(u); impedirPropriaConta(id, autor);
        if (ajustes == null || ajustes.size() > PermissaoService.CATALOGO.size() || !PermissaoService.CATALOGO.containsAll(ajustes.keySet()) || ajustes.containsValue(null)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione permissões válidas.");
        if (!u.isAdministracaoAprovada() && ajustes.entrySet().stream().anyMatch(e -> Boolean.TRUE.equals(e.getValue()) && PermissaoService.ADMINISTRATIVAS.contains(e.getKey()))) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O acesso administrativo exige uma solicitação aprovada pelo root.");
        u.getPermissoesIndividuais().clear(); u.getPermissoesIndividuais().putAll(ajustes);
        Set<String> efetivas = PermissaoService.efetivas(u);
        if (efetivas.contains("usuarios.gerenciar") && !efetivas.contains("usuarios") || efetivas.contains("permissoes.gerenciar") && !efetivas.containsAll(Set.of("permissoes", "usuarios")) || efetivas.contains("estudantes.editar") && !efetivas.contains("estudantes.consultar")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Libere também a consulta necessária para usar esta ação.");
        for (var dependencia : Map.of("anamnese", "estudantes.editar", "permissoes", "usuarios", "documentos.gerenciar", "documentos").entrySet())
            if (efetivas.contains(dependencia.getKey()) && !efetivas.contains(dependencia.getValue())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Libere a consulta necessaria para usar esta acao.");
        if (efetivas.contains("anamnese") && !efetivas.contains("sensivel.consultar")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A anamnese exige acesso as informacoes de saude.");
        preservarGestao(u, PermissaoService.ativo(u), efetivas);
        return detalhe(usuarios.saveAndFlush(u));
    }
}
