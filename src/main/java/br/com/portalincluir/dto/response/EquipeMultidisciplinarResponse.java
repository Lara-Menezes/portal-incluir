package br.com.portalincluir.dto.response;

import br.com.portalincluir.model.EquipeMultidisciplinar;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EquipeMultidisciplinarResponse {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String matricula;
    private String areaAtuacao;
    private String cargo;
    private String setor;
    private String conselhoProfissional;
    private String registroProfissional;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public EquipeMultidisciplinarResponse(
            EquipeMultidisciplinar profissional
    ) {
        this.id = profissional.getId();
        this.nome = profissional.getNome();
        this.email = profissional.getEmail();
        this.telefone = profissional.getTelefone();
        this.dataNascimento = profissional.getDataNascimento();
        this.matricula = profissional.getMatricula();
        this.areaAtuacao = profissional.getAreaAtuacao();
        this.cargo = profissional.getCargo();
        this.setor = profissional.getSetor();
        this.conselhoProfissional = profissional.getConselhoProfissional();
        this.registroProfissional = profissional.getRegistroProfissional();
        this.ativo = profissional.isAtivo();
        this.dataCriacao = profissional.getDataCriacao();
        this.dataAtualizacao = profissional.getDataAtualizacao();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getAreaAtuacao() {
        return areaAtuacao;
    }

    public String getCargo() {
        return cargo;
    }

    public String getSetor() {
        return setor;
    }

    public String getConselhoProfissional() {
        return conselhoProfissional;
    }

    public String getRegistroProfissional() {
        return registroProfissional;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
}