package io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FormaPagamento;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LocacaoUpdateDTO(


   @NotBlank(message = "Campo 'valorLocacao' é obrigatório.")
   BigDecimal valorLocacao,

   @NotBlank(message = "Campo 'filialRetirada' é obrigatório.")
   FilialLocadora filialRetirada,

   @NotBlank(message = "Campo 'filialDevolucao' é obrigatório.")
   FilialLocadora filialDevolucao,

   @NotBlank(message = "Campo 'formaPagamento' é obrigatório.")
   FormaPagamento formaPagamento,

   @NotBlank(message = "Campo 'dataRetirada' é obrigatório.")
   LocalDateTime dataRetirada,

   @NotBlank(message = "Campo 'dataDevolucao' é obrigatório.")
   LocalDateTime dataDevolucao
) {
}
