package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.*;
import br.com.portalincluir.dto.response.UsuarioResponse;
import br.com.portalincluir.enums.StatusCoordenador;
import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.Locale;

@Service @RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final CoordenadorRepository coordenadores;
    private final ProfessorRepository professores;
    private final AdministradorRepository administradores;
    private final EquipeMultidisciplinarRepository equipe;
    private final PasswordEncoder encoder;

    @Transactional
    public UsuarioResponse cadastrar(CadastroUsuarioRequest r) {
        r.setEmail(r.getEmail().strip().toLowerCase(Locale.ROOT));
        r.setNome(r.getNome().strip());
        r.setMatricula(r.getMatricula().strip());
        if (usuarios.existsByEmailIgnoreCase(r.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        Usuario u = new Usuario();
        u.setEmail(r.getEmail());
        u.setSenha(encoder.encode(r.getSenha()));
        u.setPerfil(r.getPerfil());
        switch (r.getPerfil()) {
            case COORDENADOR -> {
                exigir(r.getCargo(), "Cargo"); exigir(r.getSetor(), "Setor");
                Coordenador p = new Coordenador(); BeanUtils.copyProperties(r, p);
                p.setDataCriacao(LocalDateTime.now());
                u.setCoordenador(coordenadores.save(p));
            }
            case PROFESSOR -> {
                exigir(r.getDisciplina(), "Disciplina"); exigir(r.getSetor(), "Setor");
                Professor p = new Professor(); BeanUtils.copyProperties(r, p);
                p.setDataCriacao(LocalDateTime.now());
                u.setProfessor(professores.save(p));
            }
            case ADMINISTRADOR -> {
                exigir(r.getCargo(), "Cargo"); exigir(r.getSetor(), "Setor");
                Administrador p = new Administrador(); BeanUtils.copyProperties(r, p);
                u.setAdministrador(administradores.save(p));
            }
            case EQUIPE_MULTIDISCIPLINAR -> {
                exigir(r.getCargo(), "Cargo"); exigir(r.getSetor(), "Setor"); exigir(r.getAreaAtuacao(), "Área de atuação");
                EquipeMultidisciplinar p = new EquipeMultidisciplinar(); BeanUtils.copyProperties(r, p);
                u.setEquipeMultidisciplinar(equipe.save(p));
            }
        }
        return resposta(usuarios.saveAndFlush(u));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse login(LoginRequest r) {
        Usuario u = usuarios.findByEmailIgnoreCase(r.email().strip())
                .orElseThrow(UsuarioService::credenciaisInvalidas);
        if (!encoder.matches(r.senha(), u.getSenha())) throw credenciaisInvalidas();
        boolean ativo = switch (u.getPerfil()) {
            case COORDENADOR -> u.getCoordenador().getStatus() == StatusCoordenador.ATIVO;
            case PROFESSOR -> u.getProfessor().isAtivo();
            case ADMINISTRADOR -> u.getAdministrador().isAtivo();
            case EQUIPE_MULTIDISCIPLINAR -> u.getEquipeMultidisciplinar().isAtivo();
        };
        if (!ativo) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Perfil inativo");
        return resposta(u);
    }

    private UsuarioResponse resposta(Usuario u) {
        return switch (u.getPerfil()) {
            case COORDENADOR -> new UsuarioResponse(u.getId(), u.getCoordenador().getNome(), u.getEmail(), u.getPerfil(), u.getCoordenador().getId());
            case PROFESSOR -> new UsuarioResponse(u.getId(), u.getProfessor().getNome(), u.getEmail(), u.getPerfil(), u.getProfessor().getId());
            case ADMINISTRADOR -> new UsuarioResponse(u.getId(), u.getAdministrador().getNome(), u.getEmail(), u.getPerfil(), u.getAdministrador().getId());
            case EQUIPE_MULTIDISCIPLINAR -> new UsuarioResponse(u.getId(), u.getEquipeMultidisciplinar().getNome(), u.getEmail(), u.getPerfil(), u.getEquipeMultidisciplinar().getId());
        };
    }
    private static ResponseStatusException credenciaisInvalidas() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
    }
    private void exigir(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, campo + " obrigatório");
    }
}
