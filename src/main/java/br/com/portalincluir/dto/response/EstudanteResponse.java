package br.com.portalincluir.dto.response;

import br.com.portalincluir.model.Estudante;

import java.time.LocalDate;

public class EstudanteResponse {

    private Long id;
    private boolean ativo;
    private String nomeCompleto;
    private LocalDate dataNascimento;
    private String curso;
    private String matricula;
    private String periodoIngresso;
    private String telefone;
    private String email;
    private String nomeResponsavel;
    private String telefoneResponsavel;
    private String emailResponsavel;

    public EstudanteResponse(Estudante estudante) {
        this.id = estudante.getId();
        this.ativo = estudante.isAtivo();
        this.nomeCompleto = estudante.getNome();
        this.dataNascimento = estudante.getDataNascimento();
        this.curso = estudante.getCurso();
        this.matricula = estudante.getMatricula();
        this.periodoIngresso = estudante.getPeriodoIngresso();
        this.telefone = estudante.getTelefone();
        this.email = estudante.getEmail();
        this.nomeResponsavel = estudante.getNomeResponsavel();
        this.telefoneResponsavel = estudante.getTelefoneResponsavel();
        this.emailResponsavel = estudante.getEmailResponsavel();
    }

    public Long getId() {
        return id;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getCurso() {
        return curso;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getPeriodoIngresso() {return periodoIngresso;}

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public String getTelefoneResponsavel() {
        return telefoneResponsavel;
    }

    public String getEmailResponsavel() {
        return emailResponsavel;
    }
}