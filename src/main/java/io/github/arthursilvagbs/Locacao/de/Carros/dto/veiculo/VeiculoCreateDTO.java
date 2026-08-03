package io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.CategoriaVeiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record VeiculoCreateDTO(

   @Size(min = 17, max = 17)
   @NotBlank(message = "O campo 'numeroChassi' é obrigatório.")
   String numeroChassi,

   @Size(min = 7, max = 7)
   @NotBlank(message = "O campo 'placaVeiculo' é obrigatório.")
   String placaVeiculo,

   @Size(min = 9, max = 11)
   @NotBlank(message = "O campo 'renavem' é obrigatório.")
   String renavam,

   @NotBlank(message = "O campo 'modelo' é obrigatório.")
   String modelo,

   @NotBlank(message = "O campo 'marca' é obrigatório.")
   String marca,

   @NotBlank(message = "O campo 'ano' é obrigatório.")
   LocalDateTime ano,

   String cor,

   @NotBlank(message = "O campo 'categoriaVeiculo' é obrigatório.")
   CategoriaVeiculo categoriaVeiculo,

   Double quilometragem,

   @NotBlank(message = "O campo 'filialAtualId' é obrigatório.")
   String filialAtualId
) {
}
