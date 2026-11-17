package br.com.portalincluir.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity @Getter @Setter @org.hibernate.envers.Audited
@Table(name = "ocorrencias", indexes = {
    @Index(name = "idx_ocorrencia_estudante_data", columnList = "estudante_id,ocorrida_em"),
    @Index(name = "idx_ocorrencia_autor_data", columnList = "autor_id,ocorrida_em")
})
public class Ocorrencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "estudante_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Estudante estudante;
    @ManyToOne(optional = false) @JoinColumn(name = "autor_id", nullable = false) private Usuario autor;
    @Column(nullable = false, length = 150) private String autorNome;
    @Column(nullable = false, length = 5000) private String descricao;
    @Column(name = "ocorrida_em", nullable = false) private LocalDateTime ocorridaEm;
    @Column(nullable = false) private LocalDateTime registradaEm;
    @Column(length = 180) private String arquivoNome;
    @Column(length = 120) private String arquivoTipo;
    private Long arquivoTamanho;
}
