package br.com.portalincluir.service;

import br.com.portalincluir.dto.request.*;
import br.com.portalincluir.enums.*;
import br.com.portalincluir.model.*;
import br.com.portalincluir.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock UsuarioRepository usuarios;
    @Mock CoordenadorRepository coordenadores;
    @Mock ProfessorRepository professores;
    @Mock AdministradorRepository administradores;
    @Mock EquipeMultidisciplinarRepository equipe;
    UsuarioService service;

    @BeforeEach void configurar() {
        service = new UsuarioService(usuarios, coordenadores, professores, administradores, equipe,
                new BCryptPasswordEncoder());
    }

    private CadastroUsuarioRequest cadastro(PerfilUsuario perfil) {
        var r = new CadastroUsuarioRequest();
        r.setNome(" Maria "); r.setEmail(" MARIA@EXEMPLO.COM "); r.setSenha("senha123");
        r.setPerfil(perfil); r.setMatricula("123"); r.setCargo("Coordenação");
        r.setSetor("CLAI"); r.setDisciplina("Português"); r.setAreaAtuacao("Pedagogia");
        r.setCursosAtuacao(java.util.Set.of(br.com.portalincluir.config.CatalogoProfissional.CURSOS.get(0)));
        return r;
    }

    @Test void criaCadaPerfilComContaESenhaCodificada() {
        when(coordenadores.save(any())).thenAnswer(i -> i.getArgument(0));
        when(professores.save(any())).thenAnswer(i -> i.getArgument(0));
        when(administradores.save(any())).thenAnswer(i -> i.getArgument(0));
        when(equipe.save(any())).thenAnswer(i -> i.getArgument(0));
        when(usuarios.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        for (PerfilUsuario perfil : PerfilUsuario.values()) {
            var response = service.cadastrar(cadastro(perfil));
            assertEquals(perfil, response.perfil());
            assertEquals("Maria", response.nome());
            assertEquals("maria@exemplo.com", response.email());
        }
        var captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarios, times(4)).saveAndFlush(captor.capture());
        captor.getAllValues().forEach(u -> {
            assertNotEquals("senha123", u.getSenha());
            assertTrue(new BCryptPasswordEncoder().matches("senha123", u.getSenha()));
        });
    }

    @Test void rejeitaEmailDuplicadoAntesDeCriarPerfil() {
        when(usuarios.existsByEmailIgnoreCase("maria@exemplo.com")).thenReturn(true);
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> service.cadastrar(cadastro(PerfilUsuario.COORDENADOR))).getStatusCode().value());
        verifyNoInteractions(coordenadores, professores, administradores, equipe);
    }

    @Test void rejeitaCamposEspecificosAusentes() {
        var r = cadastro(PerfilUsuario.PROFESSOR); r.setCursosAtuacao(java.util.Set.of());
        assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.cadastrar(r)).getStatusCode().value());
        verifyNoInteractions(professores);
    }

    @Test void loginValidaSenhaEStatus() {
        var p = new Professor(); p.setId(10L); p.setNome("Maria");
        var u = new Usuario(); u.setId(1L); u.setEmail("maria@exemplo.com");
        u.setPerfil(PerfilUsuario.PROFESSOR); u.setProfessor(p);
        u.setSenha(new BCryptPasswordEncoder().encode("senha123"));
        when(usuarios.findByEmailIgnoreCase("maria@exemplo.com")).thenReturn(Optional.of(u));
        assertEquals(10L, service.login(new LoginRequest("maria@exemplo.com", "senha123")).perfilId());
        assertEquals(401, assertThrows(ResponseStatusException.class,
                () -> service.login(new LoginRequest("maria@exemplo.com", "errada"))).getStatusCode().value());
        p.setAtivo(false);
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> service.login(new LoginRequest("maria@exemplo.com", "senha123"))).getStatusCode().value());
    }

    @Test void estudanteNaoPossuiPerfilDeLogin() {
        assertThrows(IllegalArgumentException.class, () -> PerfilUsuario.valueOf("ESTUDANTE"));
    }
}
