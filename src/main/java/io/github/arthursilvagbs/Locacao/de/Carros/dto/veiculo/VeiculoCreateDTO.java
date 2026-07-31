package io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.CategoriaVeiculo;

import java.time.LocalDateTime;
import java.util.UUID;

public record VeiculoCreateDTO(
        String numeroChassi,
        String placaVeiculo,
        String renavam,
        String modelo,
        String marca,
        LocalDateTime ano,
        String cor,
        CategoriaVeiculo categoriaVeiculo,
        Double quilometragem,
        String filialAtualId
) {
}
