package io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FormaPagamento;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LocacaoUpdateDTO(


   @NotNull(message = "Campo 'valorLocacao' é obrigatório.")
   BigDecimal valorLocacao,

   @NotNull(message = "Campo 'filialRetirada' é obrigatório.")
   FilialLocadora filialRetirada,

   @NotNull(message = "Campo 'filialDevolucao' é obrigatório.")
   FilialLocadora filialDevolucao,

   @NotNull(message = "Campo 'formaPagamento' é obrigatório.")
   FormaPagamento formaPagamento,

   @NotNull(message = "Campo 'dataRetirada' é obrigatório.")
   LocalDateTime dataRetirada,

   @NotNull(message = "Campo 'dataDevolucao' é obrigatório.")
   LocalDateTime dataDevolucao
) {
}
