package br.com.portalincluir.dto.response;

import java.util.Set;
import java.util.HashSet;
import br.com.portalincluir.model.Estudante;
import br.com.portalincluir.enums.TipoNecessidadeEspecifica;
import br.com.portalincluir.enums.TipoCondicaoAssociada;
import br.com.portalincluir.enums.TipoAtendimentoCampus;
import br.com.portalincluir.enums.TipoAcessibilidadeFisica;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstudanteResponse {

    private Long id;
    private boolean ativo;
    private LocalDate dataConclusao;
    private LocalDate dataLimiteRetencao;
    public LocalDate getDataConclusao() { return dataConclusao; }
    public LocalDate getDataLimiteRetencao() { return dataLimiteRetencao; }
    public boolean isConcluido() { return dataConclusao != null; }
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
    private Boolean possuiNecessidadeEspecifica;
    private Set<TipoNecessidadeEspecifica> necessidadesEspecificas;
    private String outraNecessidadeEspecifica;
    private Set<TipoCondicaoAssociada> condicoesAssociadas;
    private String outraCondicaoAssociada;
    private Set<TipoAtendimentoCampus> atendimentosCampus;
    private Set<TipoAcessibilidadeFisica> acessibilidadesFisicas;
    private String outraAcessibilidadeFisica;
    private Boolean possuiOutraNecessidadeAtendimento;
    private String descricaoOutraNecessidadeAtendimento;

    public EstudanteResponse(Estudante estudante) {
        this.id = estudante.getId();
        this.ativo = estudante.isAtivo();
        this.dataConclusao = estudante.getDataConclusao();
        this.dataLimiteRetencao = estudante.getDataLimiteRetencao();
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
        this.possuiNecessidadeEspecifica =
                estudante.getPossuiNecessidadeEspecifica();

        this.necessidadesEspecificas =
                new java.util.HashSet<>(estudante.getNecessidadesEspecificas());

        this.outraNecessidadeEspecifica =
                estudante.getOutraNecessidadeEspecifica();

        this.condicoesAssociadas =
                new java.util.HashSet<>(estudante.getCondicoesAssociadas());

        this.outraCondicaoAssociada =
                estudante.getOutraCondicaoAssociada();

        this.atendimentosCampus =
                new java.util.HashSet<>(estudante.getAtendimentosCampus());

        this.acessibilidadesFisicas =
                new java.util.HashSet<>(estudante.getAcessibilidadesFisicas());

        this.outraAcessibilidadeFisica =
                estudante.getOutraAcessibilidadeFisica();

        this.possuiOutraNecessidadeAtendimento =
                estudante.getPossuiOutraNecessidadeAtendimento();

        this.descricaoOutraNecessidadeAtendimento =
                estudante.getDescricaoOutraNecessidadeAtendimento();
    }
}