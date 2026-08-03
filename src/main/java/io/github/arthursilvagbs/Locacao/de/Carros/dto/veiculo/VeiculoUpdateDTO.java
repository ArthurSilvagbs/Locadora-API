package io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VeiculoUpdateDTO(

   @Size(min = 7, max = 7)
   @NotBlank(message = "O campo 'placaVeiculo' é obrigatório.")
   String placa,

   Double quilometragem,

   String cor
) {
}
