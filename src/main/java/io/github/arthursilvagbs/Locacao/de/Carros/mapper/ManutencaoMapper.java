package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Manutencao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;
import org.springframework.stereotype.Component;

@Component
public class ManutencaoMapper {
    public Manutencao mapearParaManutencao(ManutencaoCreateDTO dto, Veiculo veiculo) {
        return new Manutencao(
                veiculo,
                dto.dataManutencao(),
                dto.descricao(),
                dto.valor()
        );
    }

    public ManutencaoResponseDTO mapearParaResponse(Manutencao entidade) {
       return new ManutencaoResponseDTO(
          entidade.getId(),
          entidade.getVeiculo(),
          entidade.getDataManutencao(),
          entidade.getDescricao(),
          entidade.getQuilimetragemVeiculo(),
          entidade.getValor()
       );
    }

}
