package io.github.arthursilvagbs.Locacao.de.Carros.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

   private final JwtAuthenticationFilter jwtAuthenticationFilter;

   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }

   @Bean
   public AuthenticationManager authenticationManager(
      UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder
   ) {
      DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
      authenticationProvider.setPasswordEncoder(passwordEncoder);
      return new ProviderManager(authenticationProvider);
   }

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      return http
         .csrf(csrf -> csrf.disable())
         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
         .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            //VeiculoController
            .requestMatchers(HttpMethod.POST, "/veiculo").hasAnyRole("ADMIN", "GERENTE", "FUNCIONARIO")
            .requestMatchers(HttpMethod.GET, "/veiculo/**").hasAnyRole("ADMIN", "GERENTE", "FUNCIONARIO")
            .requestMatchers("/veiculo/**").hasAnyRole("ADMIN", "GERENTE")
            //VeiculoController
            //PessoaFisicaController
            .requestMatchers(HttpMethod.POST, "/pessoa-fisica").authenticated()
            .requestMatchers(HttpMethod.GET, "/pessoa-fisica/*").hasAnyRole("ADMIN", "GERENTE", "FUNCIONARIO")
            .requestMatchers(HttpMethod.GET, "/pessoa-fisica/cpf/*").hasAnyRole("ADMIN", "GERENTE", "FUNCIONARIO")
            .requestMatchers("/pessoa-fisica/**").hasAnyRole("ADMIN", "GERENTE")
            //PessoaFisicaController
            //PessoaJuridicaController
            .requestMatchers(HttpMethod.POST, "/pessoa-juridica").authenticated()
            .requestMatchers(HttpMethod.GET, "/pessoa-juridica/*").hasAnyRole("ADMIN", "GERENTE", "FUNCIONARIO")
            .requestMatchers(HttpMethod.GET, "/pessoa-juridica/cnpj/*").hasAnyRole("ADMIN", "GERENTE", "FUNCIONARIO")
            .requestMatchers("/pessoa-juridica/**").hasAnyRole("ADMIN", "GERENTE")
            //PessoaJuridicaController
            //FilialLocadoraController
            .requestMatchers(HttpMethod.GET, "/filial-locadora/*").hasAnyRole("ADMIN", "GERENTE", "FUNCIONARIO")
            .requestMatchers("/filial-locadora/**").hasAnyRole("ADMIN", "GERENTE")
            //FilialLocadoraController
            //ManutencaoController
            .requestMatchers("/manutencao/**").hasAnyRole("ADMIN", "GERENTE", "FUNCIONARIO")
            //ManutencaoController
            //LocacaoController
            .requestMatchers("/locacao/**").authenticated()
            //LocacaoController
         )
         .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
         .build();
   }
}
