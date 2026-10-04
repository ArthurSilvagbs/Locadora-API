package io.github.arthursilvagbs.Locacao.de.Carros.security;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Role;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Usuario;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
   @Mock UsuarioRepository repository;
   @InjectMocks UserDetailsServiceImpl service;

   @Test
   void usuarioExistente_retornaIdentidadeComRole() {
      Usuario usuario = new Usuario("funcionario@email.com", "hash");
      usuario.setRoles(Role.FUNCIONARIO);
      when(repository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

      var detalhes = service.loadUserByUsername(usuario.getEmail());

      assertThat(detalhes.getUsername()).isEqualTo(usuario.getEmail());
      assertThat(detalhes.getPassword()).isEqualTo("hash");
      assertThat(detalhes.getAuthorities()).extracting("authority").containsExactly("ROLE_FUNCIONARIO");
   }

   @Test
   void usuarioInexistente_lancaExcecao() {
      when(repository.findByEmail("ausente@email.com")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.loadUserByUsername("ausente@email.com"))
         .isInstanceOf(UsernameNotFoundException.class);
   }
}
