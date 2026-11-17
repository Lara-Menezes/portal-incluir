package br.com.portalincluir.dto.response;

import br.com.portalincluir.model.EquipeMultidisciplinar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}