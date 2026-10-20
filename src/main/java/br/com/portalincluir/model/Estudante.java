package br.com.portalincluir.model;

import jakarta.persistence.*;
import br.com.portalincluir.enums.TipoNecessidadeEspecifica;
import br.com.portalincluir.enums.TipoCondicaoAssociada;
import br.com.portalincluir.enums.TipoAtendimentoCampus;
import br.com.portalincluir.enums.TipoAcessibilidadeFisica;

import java.util.HashSet;
import java.util.Set;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@org.hibernate.envers.Audited
@Entity
@Table(name = "estudantes", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_estudante_matricula",
                columnNames = "matricula"
        ),
        @UniqueConstraint(
                name = "uk_estudante_email",
                columnNames = "email"
        )
})
public class Estudante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false)
    @org.hibernate.annotations.ColumnDefault("0")
    private long versao;

    @Column(nullable = false)
    private boolean ativo = true;

    private LocalDate dataConclusao;
    private LocalDate dataLimiteRetencao;

    public LocalDate getDataConclusao() { return dataConclusao; }
    public LocalDate getDataLimiteRetencao() { return dataLimiteRetencao; }
    public boolean isConcluido() { return dataConclusao != null; }

    public void concluir(LocalDate data) {
        if (data == null || isConcluido()) {
            throw new IllegalStateException("Conclusao invalida ou ja registrada");
        }
        dataConclusao = data;
        dataLimiteRetencao = data.plusYears(5);
        ativo = false;
    }

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false, length = 150)
    private String curso;

    @Column(nullable = false, length = 30)
    private String matricula;

    @Column(nullable = false, length = 20)
    private String periodoIngresso;

    @Column(length = 20)
    private String telefone;

    @Column(length = 150)
    private String email;

    @Column(length = 150)
    private String nomeResponsavel;

    @Column(length = 20)
    private String telefoneResponsavel;

    @Column(length = 150)
    private String emailResponsavel;

    @Column(name = "possui_necessidade_especifica")
    private Boolean possuiNecessidadeEspecifica;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "estudante_necessidades",
            joinColumns = @JoinColumn(name = "estudante_id")
    )
    @Column(name = "tipo", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<TipoNecessidadeEspecifica> necessidadesEspecificas =
            new HashSet<>();

    @Column(name = "outra_necessidade_especifica", length = 500)
    private String outraNecessidadeEspecifica;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "estudante_condicoes_associadas",
            joinColumns = @JoinColumn(name = "estudante_id")
    )
    @Column(name = "tipo", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<TipoCondicaoAssociada> condicoesAssociadas =
            new HashSet<>();

    @Column(name = "outra_condicao_associada", length = 500)
    private String outraCondicaoAssociada;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "estudante_atendimentos",
            joinColumns = @JoinColumn(name = "estudante_id")
    )
    @Column(name = "tipo", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<TipoAtendimentoCampus> atendimentosCampus =
            new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "estudante_acessibilidades",
            joinColumns = @JoinColumn(name = "estudante_id")
    )
    @Column(name = "tipo", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<TipoAcessibilidadeFisica> acessibilidadesFisicas =
            new HashSet<>();

    @Column(name = "outra_acessibilidade_fisica", length = 500)
    private String outraAcessibilidadeFisica;

    @Column(name = "possui_outra_necessidade_atendimento")
    private Boolean possuiOutraNecessidadeAtendimento;

    @Column(name = "descricao_outra_necessidade_atendimento", length = 1000)
    private String descricaoOutraNecessidadeAtendimento;

}