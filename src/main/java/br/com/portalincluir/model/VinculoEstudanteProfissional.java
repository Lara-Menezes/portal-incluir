package br.com.portalincluir.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity @org.hibernate.envers.Audited @Getter @Setter
@Table(name="vinculos_estudante_profissional", uniqueConstraints={@UniqueConstraint(columnNames={"estudante_id", "professor_id"}), @UniqueConstraint(columnNames={"estudante_id", "membro_id"})})
public class VinculoEstudanteProfissional {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Version private long versao;
    @ManyToOne(optional=false) @JoinColumn(name="estudante_id", nullable=false) private Estudante estudante;
    @ManyToOne @JoinColumn(name="professor_id") private Professor professor;
    @ManyToOne @JoinColumn(name="membro_id") private EquipeMultidisciplinar membro;
    @Column(nullable=false) private boolean ativo = true;
    @Column(nullable=false, length=1000) private String motivo;
    @Column(length=1000) private String motivoEncerramento;
    @Column(nullable=false) private LocalDateTime dataInicio;
    private LocalDateTime dataEncerramento;
    @Column(nullable=false) private Long responsavelUsuarioId;
    @PrePersist @PreUpdate void validar() {
        if ((professor == null) == (membro == null)) throw new IllegalStateException("Selecione exatamente um profissional para o vínculo.");
    }
}
