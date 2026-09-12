package io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ConfirmarDevolucaoDTO(

   @Positive(message = "A quilometragem deve ser positiva.")
   @NotNull(message = "Campo 'kmPosDevolucao' é obrigatório.")
   Double kmPosDevolucao
) {}
