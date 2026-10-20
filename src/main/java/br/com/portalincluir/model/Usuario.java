package br.com.portalincluir.model;

import br.com.portalincluir.enums.PerfilUsuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@org.hibernate.envers.Audited
@Table(name = "usuarios")
@Getter @Setter
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    @JsonIgnore
    @org.hibernate.envers.NotAudited
    @Column(nullable = false)
    private String senha;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfil;
    @OneToOne @JoinColumn(unique = true)
    private Coordenador coordenador;
    @OneToOne @JoinColumn(unique = true)
    private Professor professor;
    @OneToOne @JoinColumn(unique = true)
    private Administrador administrador;
    @OneToOne @JoinColumn(unique = true)
    private EquipeMultidisciplinar equipeMultidisciplinar;

    @PrePersist @PreUpdate
    void validarPerfil() {
        int vinculos = (coordenador != null ? 1 : 0) + (professor != null ? 1 : 0)
                + (administrador != null ? 1 : 0)
                + (equipeMultidisciplinar != null ? 1 : 0);
        if (perfil == null || vinculos != 1 || switch (perfil) {
            case COORDENADOR -> coordenador == null;
            case PROFESSOR -> professor == null;
            case ADMINISTRADOR -> administrador == null;
            case EQUIPE_MULTIDISCIPLINAR -> equipeMultidisciplinar == null;
        }) throw new IllegalStateException("O usuário deve possuir exatamente o vínculo do seu perfil");
    }
}
