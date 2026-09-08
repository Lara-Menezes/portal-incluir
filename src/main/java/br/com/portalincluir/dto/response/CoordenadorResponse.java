package br.com.portalincluir.dto.response;

import br.com.portalincluir.enums.PerfilUsuario;
import br.com.portalincluir.enums.StatusCoordenador;
import br.com.portalincluir.model.Coordenador;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CoordenadorResponse {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String matricula;
    private String cargo;
    private String setor;
    private String identificadorAcesso;
    private PerfilUsuario perfil;
    private StatusCoordenador status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public CoordenadorResponse(Coordenador coordenador) {
        this.id = coordenador.getId();
        this.nome = coordenador.getNome();
        this.email = coordenador.getEmail();
        this.telefone = coordenador.getTelefone();
        this.dataNascimento = coordenador.getDataNascimento();
        this.matricula = coordenador.getMatricula();
        this.cargo = coordenador.getCargo();
        this.setor = coordenador.getSetor();
        this.identificadorAcesso = coordenador.getIdentificadorAcesso();
        this.perfil = coordenador.getPerfil();
        this.status = coordenador.getStatus();
        this.dataCriacao = coordenador.getDataCriacao();
        this.dataAtualizacao = coordenador.getDataAtualizacao();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getMatricula() { return matricula; }
    public String getCargo() { return cargo; }
    public String getSetor() { return setor; }
    public String getIdentificadorAcesso() { return identificadorAcesso; }
    public PerfilUsuario getPerfil() { return perfil; }
    public StatusCoordenador getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}
