package io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.CategoriaVeiculo;

import java.time.LocalDateTime;

public record VeiculoCreateDTO(
        String numeroChassi,
        String placaVeiculo,
        String renavam,
        String modelo,
        String marca,
        LocalDateTime ano,
        String cor,
        CategoriaVeiculo categoriaVeiculo,
        Integer quilometragem
) {
}
