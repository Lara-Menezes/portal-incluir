package br.com.portalincluir.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@org.hibernate.envers.Audited
@Table(name = "administradores")
@Getter @Setter
public class Administrador {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String nome;
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    @Column(nullable = false, unique = true, length = 30)
    private String matricula;
    private String cargo;
    private String setor;
    private boolean ativo = true;
}
