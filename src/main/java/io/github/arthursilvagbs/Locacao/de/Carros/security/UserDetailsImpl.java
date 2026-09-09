package io.github.arthursilvagbs.Locacao.de.Carros.security;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

   private final Usuario usuario;

   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
      return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRoles().name()));
   }

   @Override
   public @Nullable String getPassword() {
      return usuario.getSenha();
   }

   @Override
   public String getUsername() {
      return usuario.getEmail();
   }
}
