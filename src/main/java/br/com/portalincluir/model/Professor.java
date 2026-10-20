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
@Table(name = "professores")
public class Professor {

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
    private String disciplina;

    @Column(nullable = false, length = 120)
    private String setor;

    @Column(nullable = false)
    private boolean ativo = true;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

}
