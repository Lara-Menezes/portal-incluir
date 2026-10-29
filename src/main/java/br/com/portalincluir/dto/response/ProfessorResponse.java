package br.com.portalincluir.dto.response;

import br.com.portalincluir.model.Professor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessorResponse {

    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String matricula;
    private String disciplina;
    private String setor;

    private java.util.Set<String> cursosAtuacao;
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
        this.cursosAtuacao = new java.util.TreeSet<>(professor.getCursosAtuacao());
        this.dataCriacao = professor.getDataCriacao();
        this.dataAtualizacao = professor.getDataAtualizacao();
    }
}
