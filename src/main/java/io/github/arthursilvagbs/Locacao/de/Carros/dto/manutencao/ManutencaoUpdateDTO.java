package io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ManutencaoUpdateDTO(
        LocalDateTime dataManutencao,
        String descricao,
        BigDecimal valor
) {
}
