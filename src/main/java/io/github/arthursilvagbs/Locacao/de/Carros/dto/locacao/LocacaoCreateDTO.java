package io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LocacaoCreateDTO(
        Cliente cliente,
        Veiculo veiculo,
        BigDecimal valorLocacao,
        FilialLocadora filialRetirada,
        FilialLocadora filialDevolucao,
        FormaPagamento formaPagamento,
        LocalDateTime dataRetirada,
        LocalDateTime dataDevolucao
) {
}
