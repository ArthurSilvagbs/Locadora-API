package io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LocacaoResponseDTO(
        UUID id,
        String idCliente,
        String idVeiculo,
        BigDecimal valorLocacao,
        String idFilialRetirada,
        String idFilialDevolucao,
        CategoriaVeiculo categoriaVeiculo,
        FormaPagamento formaPagamento,
        StatusLocacao statusLocacao,
        LocalDateTime dataRetirada,
        LocalDateTime dataDevolucao
) {
}
