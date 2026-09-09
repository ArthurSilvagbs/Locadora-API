package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.auth.RegisterRequestDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

   public Usuario MapearParaUsuario(RegisterRequestDTO dto) {
      return new Usuario(
         dto.email(),
         dto.senha(),
         dto.role()
      );
   }
}
