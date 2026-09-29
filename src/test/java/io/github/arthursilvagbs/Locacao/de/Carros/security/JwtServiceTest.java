package io.github.arthursilvagbs.Locacao.de.Carros.security;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Role;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Usuario;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {
   private final JwtService service = new JwtService();

   @BeforeEach
   void configurarChave() {
      ReflectionTestUtils.setField(service, "secret", "chave-de-teste-com-comprimento-suficiente-para-assinar-tokens-jwt-123456");
   }

   @Test
   void tokenGerado_autenticaSeuProprioUsuario() {
      UserDetailsImpl usuario = usuario("cliente@email.com");

      String token = service.generateToken(usuario);

      assertThat(service.extractUsername(token)).isEqualTo("cliente@email.com");
      assertThat(service.isTokenValid(token, usuario)).isTrue();
   }

   @Test
   void tokenDeOutroUsuario_naoAutentica() {
      String token = service.generateToken(usuario("cliente@email.com"));

      assertThat(service.isTokenValid(token, usuario("outro@email.com"))).isFalse();
   }

   @Test
   void tokenAdulterado_eRejeitado() {
      String token = service.generateToken(usuario("cliente@email.com"));
      int inicioAssinatura = token.lastIndexOf('.') + 1;
      char substituto = token.charAt(inicioAssinatura) == 'A' ? 'B' : 'A';
      String adulterado = token.substring(0, inicioAssinatura) + substituto + token.substring(inicioAssinatura + 1);

      assertThatThrownBy(() -> service.extractUsername(adulterado)).isInstanceOf(JwtException.class);
   }

   private UserDetailsImpl usuario(String email) {
      Usuario usuario = new Usuario(email, "hash");
      usuario.setRoles(Role.CLIENTE);
      return new UserDetailsImpl(usuario);
   }
}
