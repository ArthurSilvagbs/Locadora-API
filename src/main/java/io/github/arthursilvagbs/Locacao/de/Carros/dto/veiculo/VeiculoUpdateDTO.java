package io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Manutencao;

import java.util.List;

public record VeiculoUpdateDTO(
        Integer quilometragem,
        List<Manutencao> manutencoes,
        List<Locacao> locacoes
) {
}
