package io.github.arthursilvagbs.Locacao.de.Carros.repository;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

   Optional<Usuario> findByEmail(String email);
}
