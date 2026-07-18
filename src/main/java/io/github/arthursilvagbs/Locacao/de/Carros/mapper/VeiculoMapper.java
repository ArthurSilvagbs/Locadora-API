package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;

public class VeiculoMapper {

    public Veiculo mapearParaVeiculo(VeiculoCreateDTO dto) {
        return new Veiculo(
                dto.numeroChassi(),
                dto.placaVeiculo(),
                dto.renavam(),
                dto.modelo(),
                dto.marca(),
                dto.ano(),
                dto.cor(),
                dto.categoriaVeiculo(),
                dto.quilometragem()
        );
    }
}
