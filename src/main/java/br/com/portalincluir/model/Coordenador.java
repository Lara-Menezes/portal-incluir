package br.com.portalincluir.model;

import br.com.portalincluir.enums.StatusCoordenador;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@org.hibernate.envers.Audited
@Entity
@Table(name = "coordenadores")
public class Coordenador {

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
    private String cargo;

    @Column(nullable = false, length = 120)
    private String setor;

    @Enumerated(EnumType.STRING)
    private StatusCoordenador status = StatusCoordenador.ATIVO;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

}
