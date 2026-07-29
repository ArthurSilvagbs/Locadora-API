package io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.*;

import java.time.LocalDateTime;

public record LocacaoCreateDTO(
        String clienteId,
        String veiculoId,
        String filialRetiradaId,
        String filialDevolucaoId,
        FormaPagamento formaPagamento,
        LocalDateTime dataRetirada,
        LocalDateTime dataDevolucao
) {
}
