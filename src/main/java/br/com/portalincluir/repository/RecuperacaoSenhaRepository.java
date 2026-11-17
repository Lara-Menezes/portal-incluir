package br.com.portalincluir.repository;

import br.com.portalincluir.model.RecuperacaoSenha;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RecuperacaoSenhaRepository extends JpaRepository<RecuperacaoSenha, Long> {
    Optional<RecuperacaoSenha> findByHashToken(String hashToken);
    List<RecuperacaoSenha> findByUsuarioIdAndUsadoEmIsNullAndRevogadoEmIsNull(Long usuarioId);
}
