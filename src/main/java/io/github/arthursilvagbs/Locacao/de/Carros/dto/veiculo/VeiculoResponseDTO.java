package io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.CategoriaVeiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Manutencao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record VeiculoResponseDTO(
        UUID idVeiculo,
        String numeroChassi,
        String placaVeiculo,
        String renavam,
        String modelo,
        String marca,
        LocalDateTime ano,
        String cor,
        CategoriaVeiculo categoriaVeiculo,
        Double quilometragem,
        LocalDateTime createdAt
) {
}
