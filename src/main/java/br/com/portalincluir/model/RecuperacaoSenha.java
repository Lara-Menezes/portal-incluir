package br.com.portalincluir.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity @Table(name = "recuperacoes_senha") @Getter @Setter
@org.hibernate.envers.Audited
public class RecuperacaoSenha {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Version private long versao;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false) private Usuario usuario;
    @Column(nullable = false, unique = true, length = 64)
    @org.hibernate.envers.NotAudited private String hashToken;
    @Column(nullable = false, length = 150)
    @org.hibernate.envers.NotAudited private String emailDestino;
    @Column(nullable = false) private long versaoCredenciais;
    @Column(nullable = false) private Instant criadoEm;
    @Column(nullable = false) private Instant expiraEm;
    private Instant usadoEm;
    private Instant revogadoEm;
}
