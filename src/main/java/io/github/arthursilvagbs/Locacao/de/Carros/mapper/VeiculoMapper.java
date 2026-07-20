package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
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

    public VeiculoResponseDTO mapearParaResponse(Veiculo entidade) {
       return new VeiculoResponseDTO(
          entidade.getIdVeiculo(),
          entidade.getNumeroChassi(),
          entidade.getPlacaVeiculo(),
          entidade.getRenavam(),
          entidade.getModelo(),
          entidade.getMarca(),
          entidade.getAno(),
          entidade.getCor(),
          entidade.getCategoriaVeiculo(),
          entidade.getQuilometragem(),
          entidade.getCreatedAt()
       );
    }

}
