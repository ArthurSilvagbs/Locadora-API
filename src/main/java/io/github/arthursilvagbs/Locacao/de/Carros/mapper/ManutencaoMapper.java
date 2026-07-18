package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Manutencao;

public class ManutencaoMapper {
    public Manutencao mapearParaManutencao(ManutencaoCreateDTO dto) {
        return new Manutencao(
                dto.veiculo(),
                dto.dataManutencao(),
                dto.descricao(),
                dto.valor()
        );
    }

}
