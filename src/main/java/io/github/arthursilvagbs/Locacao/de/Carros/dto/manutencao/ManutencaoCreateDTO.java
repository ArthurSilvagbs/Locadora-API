package io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ManutencaoCreateDTO(
        Veiculo veiculo,
        LocalDateTime dataManutencao,
        String descricao,
        BigDecimal valor
) {
}
