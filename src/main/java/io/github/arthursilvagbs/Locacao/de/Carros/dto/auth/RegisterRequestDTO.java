package io.github.arthursilvagbs.Locacao.de.Carros.dto.auth;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDTO(

   @NotBlank
   @Email
   String email,

   @NotBlank
   String senha,

   @NotNull
   Role role

) {}
