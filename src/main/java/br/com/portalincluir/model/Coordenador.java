package br.com.portalincluir.model;

import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.enums.StatusCoordenador;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "coordenadores", uniqueConstraints = {
        @UniqueConstraint(name = "uk_coordenador_identificador", columnNames = "identificador_acesso")
})
public class Coordenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String telefone;

    private LocalDate dataNascimento;

    @Column(nullable = false, unique = true, length = 30)
    private String matricula;

    @Column(nullable = false, length = 120)
    private String cargo;

    @Column(nullable = false, length = 120)
    private String setor;

    @Column(name = "identificador_acesso", nullable = false, length = 80)
    private String identificadorAcesso;

    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    private PerfilUsuario perfil = PerfilUsuario.COORDENADOR;

    @Enumerated(EnumType.STRING)
    private StatusCoordenador status = StatusCoordenador.ATIVO;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }
    public String getIdentificadorAcesso() { return identificadorAcesso; }
    public void setIdentificadorAcesso(String identificadorAcesso) { this.identificadorAcesso = identificadorAcesso; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }
    public StatusCoordenador getStatus() { return status; }
    public void setStatus(StatusCoordenador status) { this.status = status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
