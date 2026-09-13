package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.auth.AuthResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.auth.LoginRequestDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.auth.RegisterRequestDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Role;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Usuario;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.UsuarioMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.UsuarioRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.security.JwtService;
import io.github.arthursilvagbs.Locacao.de.Carros.security.UserDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

   @Mock
   private UsuarioRepository repository;

   @Mock
   private UsuarioMapper mapper;

   @Mock
   private PasswordEncoder passwordEncoder;

   @Mock
   private JwtService jwtService;

   @Mock
   private AuthenticationManager authenticationManager;

   @InjectMocks
   private AuthService service;

   @Test
   @DisplayName("registrar deve salvar o usuário e retornar seu token quando o e-mail está disponível")
   void registrar_emailDisponivel_salvaUsuarioERetornaToken() {
      RegisterRequestDTO dto = new RegisterRequestDTO("ana@email.com", "senha-segura", Role.CLIENTE);
      Usuario usuario = new Usuario(dto.email(), dto.senha(), dto.role());

      when(repository.findByEmail(dto.email())).thenReturn(Optional.empty());
      when(mapper.MapearParaUsuario(dto)).thenReturn(usuario);
      when(passwordEncoder.encode(dto.senha())).thenReturn("senha-com-hash");
      when(jwtService.generateToken(any(UserDetailsImpl.class))).thenReturn("token-gerado");

      AuthResponseDTO resposta = service.registrar(dto);

      assertThat(resposta.token()).isEqualTo("token-gerado");
      verify(repository).save(usuario);
      verify(passwordEncoder).encode(dto.senha());
      assertThat(usuario.getSenha()).isEqualTo("senha-com-hash");

      ArgumentCaptor<UserDetailsImpl> userDetailsCaptor = ArgumentCaptor.forClass(UserDetailsImpl.class);
      verify(jwtService).generateToken(userDetailsCaptor.capture());
      assertThat(userDetailsCaptor.getValue().getUsername()).isEqualTo(dto.email());
      assertThat(userDetailsCaptor.getValue().getPassword()).isEqualTo("senha-com-hash");
   }

   @Test
   @DisplayName("registrar deve rejeitar e-mail já cadastrado sem mapear, salvar ou gerar token")
   void registrar_emailJaCadastrado_lancaExcecao() {
      RegisterRequestDTO dto = new RegisterRequestDTO("ana@email.com", "senha-segura", Role.CLIENTE);
      when(repository.findByEmail(dto.email())).thenReturn(Optional.of(new Usuario()));

      assertThatThrownBy(() -> service.registrar(dto))
         .isInstanceOf(RegistroDuplicadoException.class)
         .hasMessage("Email já cadastrado.");

      verify(mapper, never()).MapearParaUsuario(any());
      verify(repository, never()).save(any());
      verify(jwtService, never()).generateToken(any());
   }

   @Test
   @DisplayName("login deve autenticar com e-mail e senha, e retornar o token do usuário")
   void login_credenciaisValidas_retornaToken() {
      LoginRequestDTO dto = new LoginRequestDTO("ana@email.com", "senha-segura");
      Usuario usuario = new Usuario(dto.email(), dto.senha(), Role.CLIENTE);
      when(repository.findByEmail(dto.email())).thenReturn(Optional.of(usuario));
      when(jwtService.generateToken(any(UserDetailsImpl.class))).thenReturn("token-gerado");

      AuthResponseDTO resposta = service.login(dto);

      assertThat(resposta.token()).isEqualTo("token-gerado");

      ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationCaptor =
         ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
      verify(authenticationManager).authenticate(authenticationCaptor.capture());
      assertThat(authenticationCaptor.getValue().getPrincipal()).isEqualTo(dto.email());
      assertThat(authenticationCaptor.getValue().getCredentials()).isEqualTo(dto.senha());
      verify(repository).findByEmail(dto.email());
   }

   @Test
   @DisplayName("login deve informar estado inválido quando o usuário não é encontrado após autenticar")
   void login_usuarioAusenteAposAutenticacao_lancaExcecao() {
      LoginRequestDTO dto = new LoginRequestDTO("ana@email.com", "senha-segura");
      when(repository.findByEmail(dto.email())).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.login(dto))
         .isInstanceOf(IllegalStateException.class)
         .hasMessage("Usuario não encontrado após autenticação.");

      verify(jwtService, never()).generateToken(any());
   }

   @Test
   @DisplayName("login não deve consultar o usuário nem gerar token se a autenticação falhar")
   void login_credenciaisInvalidas_interrompeFluxo() {
      LoginRequestDTO dto = new LoginRequestDTO("ana@email.com", "senha-invalida");
      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
         .thenThrow(new BadCredentialsException("Credenciais inválidas"));

      assertThatThrownBy(() -> service.login(dto))
         .isInstanceOf(BadCredentialsException.class)
         .hasMessage("Credenciais inválidas");

      verify(repository, never()).findByEmail(any());
      verify(jwtService, never()).generateToken(any());
   }
}
