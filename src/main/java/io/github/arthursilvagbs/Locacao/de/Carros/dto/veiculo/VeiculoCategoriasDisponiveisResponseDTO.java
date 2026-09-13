package io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.CategoriaVeiculo;

import java.math.BigDecimal;

public record VeiculoCategoriasDisponiveisResponseDTO(
   CategoriaVeiculo categoriaVeiculo,
   BigDecimal valorLocacao
) {
}
