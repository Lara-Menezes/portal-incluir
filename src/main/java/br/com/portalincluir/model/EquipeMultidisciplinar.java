package br.com.portalincluir.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@org.hibernate.envers.Audited
@Entity
@Table(name = "membros_equipe_multidisciplinar")
public class EquipeMultidisciplinar {

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

    @Column(nullable = false, length = 100)
    private String areaAtuacao;

    @Column(nullable = false, length = 120)
    private String cargo;

    @Column(nullable = false, length = 120)
    private String setor;

    @Column(length = 30)
    private String conselhoProfissional;

    @Column(length = 50)
    private String registroProfissional;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    public EquipeMultidisciplinar() {
    }

    @PrePersist
    private void antesDeSalvar() {
        this.dataCriacao = LocalDateTime.now();
    }

    @PreUpdate
    private void antesDeAtualizar() {
        this.dataAtualizacao = LocalDateTime.now();
    }

}
