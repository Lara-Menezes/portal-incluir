package br.com.portalincluir.dto.response;

import br.com.portalincluir.model.Professor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProfessorResponse {

    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String matricula;
    private String disciplina;
    private String setor;

    private boolean ativo;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public ProfessorResponse(Professor professor) {
        this.id = professor.getId();
        this.nome = professor.getNome();
        this.email = professor.getEmail();
        this.telefone = professor.getTelefone();
        this.dataNascimento = professor.getDataNascimento();
        this.matricula = professor.getMatricula();
        this.disciplina = professor.getDisciplina();
        this.setor = professor.getSetor();
        this.ativo = professor.isAtivo();
        this.dataCriacao = professor.getDataCriacao();
        this.dataAtualizacao = professor.getDataAtualizacao();
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

    public String getDisciplina() {
        return disciplina;
    }

    public String getSetor() {
        return setor;
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
