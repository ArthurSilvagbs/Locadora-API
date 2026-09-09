package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.auth.AuthResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.auth.LoginRequestDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.auth.RegisterRequestDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

   private final AuthService service;

   @PostMapping("/registrar")
   public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody RegisterRequestDTO dto) {
      return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
   }

   @PostMapping("/login")
   public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
      return ResponseEntity.ok(service.login(dto));
   }
}
