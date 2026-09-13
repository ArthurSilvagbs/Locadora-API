package io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ConfirmarRetiradaDTO(

   @NotBlank(message = "O campo 'idVeiculo' é obrigatório.")
   String idVeiculo
) {}
