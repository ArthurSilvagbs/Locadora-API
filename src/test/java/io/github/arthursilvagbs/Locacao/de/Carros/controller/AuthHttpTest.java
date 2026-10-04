package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.auth.AuthResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.service.AuthService;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthHttpTest {
   private AuthService service;
   private MockMvc mvc;

   @BeforeEach
   void configurar() {
      service = mock(AuthService.class);
      mvc = MockMvcBuilders.standaloneSetup(new AuthController(service))
         .setControllerAdvice(new GlobalExceptionHandler())
         .build();
   }

   @Test
   void login_credenciaisValidas_retornaToken() throws Exception {
      when(service.login(any())).thenReturn(new AuthResponseDTO("token-gerado"));

      mvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"cliente@email.com\",\"senha\":\"senha\"}"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.token").value("token-gerado"));
   }

   @Test
   void login_credenciaisInvalidas_retorna401Padronizado() throws Exception {
      when(service.login(any())).thenThrow(new BadCredentialsException("Credenciais inválidas"));

      mvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"cliente@email.com\",\"senha\":\"errada\"}"))
         .andExpect(status().isUnauthorized())
         .andExpect(jsonPath("$.status").value(401))
         .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
         .andExpect(jsonPath("$.message").value("Credenciais inválidas"))
         .andExpect(jsonPath("$.path").value("/auth/login"));
   }
}
