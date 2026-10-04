package io.github.arthursilvagbs.Locacao.de.Carros.security;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Role;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Usuario;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
   @Mock JwtService jwtService;
   @Mock UserDetailsServiceImpl userDetailsService;
   @Mock FilterChain chain;
   @InjectMocks JwtAuthenticationFilter filter;

   @AfterEach
   void limparContexto() {
      SecurityContextHolder.clearContext();
   }

   @Test
   void semToken_continuaCadeiaSemAutenticar() throws Exception {
      MockHttpServletRequest request = new MockHttpServletRequest();
      MockHttpServletResponse response = new MockHttpServletResponse();

      filter.doFilter(request, response, chain);

      verify(chain).doFilter(request, response);
      verifyNoInteractions(jwtService, userDetailsService);
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
   }

   @Test
   void tokenValido_autenticaComRolesEContinuaCadeia() throws Exception {
      MockHttpServletRequest request = new MockHttpServletRequest();
      request.addHeader("Authorization", "Bearer token-valido");
      MockHttpServletResponse response = new MockHttpServletResponse();
      Usuario entidade = new Usuario("cliente@email.com", "hash");
      entidade.setRoles(Role.CLIENTE);
      UserDetailsImpl usuario = new UserDetailsImpl(entidade);
      when(jwtService.extractUsername("token-valido")).thenReturn("cliente@email.com");
      when(userDetailsService.loadUserByUsername("cliente@email.com")).thenReturn(usuario);
      when(jwtService.isTokenValid("token-valido", usuario)).thenReturn(true);

      filter.doFilter(request, response, chain);

      assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isSameAs(usuario);
      assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
         .extracting("authority").containsExactly("ROLE_CLIENTE");
      verify(chain).doFilter(request, response);
   }

   @Test
   void tokenInvalido_retorna401SemContinuarCadeia() throws Exception {
      MockHttpServletRequest request = new MockHttpServletRequest();
      request.addHeader("Authorization", "Bearer token-invalido");
      MockHttpServletResponse response = new MockHttpServletResponse();
      when(jwtService.extractUsername("token-invalido")).thenThrow(new JwtException("Token inválido"));

      filter.doFilter(request, response, chain);

      assertThat(response.getStatus()).isEqualTo(401);
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
      verify(chain, never()).doFilter(request, response);
   }
}
