package io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ManutencaoResponseDTO(
        UUID id,
        Veiculo veiculo,
        LocalDateTime dataManutencao,
        String descricao,
        int quilometragemVeiculo,
        BigDecimal valor
) {
}
