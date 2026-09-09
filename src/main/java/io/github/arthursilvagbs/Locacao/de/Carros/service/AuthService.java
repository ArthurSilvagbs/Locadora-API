package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.auth.AuthResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.auth.LoginRequestDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.auth.RegisterRequestDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Usuario;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.UsuarioMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.UsuarioRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.security.JwtService;
import io.github.arthursilvagbs.Locacao.de.Carros.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

   private final UsuarioRepository repository;
   private final UsuarioMapper mapper;
   private final PasswordEncoder passwordEncoder;
   private final JwtService jwtService;
   private final AuthenticationManager authenticationManager;

   public AuthResponseDTO registrar(RegisterRequestDTO dto) {
      if (repository.findByEmail(dto.email()).isPresent()) {
         throw new RegistroDuplicadoException("Email já cadastrado.");
      }

      Usuario usuario = mapper.MapearParaUsuario(dto);
      repository.save(usuario);
      String token = jwtService.generateToken(new UserDetailsImpl(usuario));
      return new AuthResponseDTO(token);
   }

   public AuthResponseDTO login(LoginRequestDTO dto) {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.senha()));

      Usuario usuario = repository.findByEmail(dto.email())
         .orElseThrow(() -> new IllegalStateException("Usuario não encontrado após autenticação."));

      String token = jwtService.generateToken(new UserDetailsImpl(usuario));
      return new AuthResponseDTO(token);
   }

}

