package io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record LocacaoCreateDTO(

   @NotBlank(message = "Campo 'clienteId' é obrigatório.")
   String clienteId,

   @NotBlank(message = "Campo 'veiculoId' é obrigatório.")
   String veiculoId,

   @NotBlank(message = "Campo 'filialRetiradaId' é obrigatório.")
   String filialRetiradaId,

   @NotBlank(message = "Campo 'filialDevolucaoId' é obrigatório.")
   String filialDevolucaoId,

   @NotNull(message = "Campo 'formaPagamento' é obrigatório.")
   FormaPagamento formaPagamento,

   @NotNull(message = "Campo 'dataRetirada' é obrigatório.")
   LocalDateTime dataRetirada,

   @NotNull(message = "Campo 'dataDevolucao' é obrigatório.")
   LocalDateTime dataDevolucao
) {
}
