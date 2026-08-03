package io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ManutencaoUpdateDTO(

   @NotBlank(message = "Campo 'dataManutencao' é obrigatório.")
   LocalDateTime dataManutencao,

   String descricao,

   @Positive(message = "Não é possível inserir um valor negativo para este campo.")
   @NotBlank(message = "Campo 'valor' é obrigatório.")
   BigDecimal valor
) {
}
