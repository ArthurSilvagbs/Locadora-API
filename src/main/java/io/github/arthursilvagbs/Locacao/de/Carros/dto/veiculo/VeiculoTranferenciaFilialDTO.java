package io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo;

import jakarta.validation.constraints.NotBlank;

public record VeiculoTranferenciaFilialDTO(
   @NotBlank
   String idNovaFilialLocadora
) {
}
