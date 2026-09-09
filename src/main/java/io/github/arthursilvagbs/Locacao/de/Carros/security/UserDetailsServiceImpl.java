package io.github.arthursilvagbs.Locacao.de.Carros.security;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Usuario;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

   private final UsuarioRepository repository;

   @Override
   public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
      Usuario usuario = repository.findByEmail(email).orElseThrow(
         () -> new UsernameNotFoundException("Usuario não encontrado.")
      );

      return new UserDetailsImpl(usuario);
   }

}
