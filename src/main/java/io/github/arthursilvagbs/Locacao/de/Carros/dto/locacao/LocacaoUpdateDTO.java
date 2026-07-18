package io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FormaPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LocacaoUpdateDTO(
        BigDecimal valorLocacao,
        FilialLocadora filialRetirada,
        FilialLocadora filialDevolucao,
        FormaPagamento formaPagamento,
        LocalDateTime dataRetirada,
        LocalDateTime dataDevolucao
) {
}
