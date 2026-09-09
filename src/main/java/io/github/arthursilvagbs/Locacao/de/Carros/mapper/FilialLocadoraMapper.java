package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import org.springframework.stereotype.Component;

@Component
public class FilialLocadoraMapper {

    public FilialLocadora mapearParaFilialLocadora(FilialLocadoraCreateDTO dto) {
        return new FilialLocadora(
                dto.nomeFilial(),
                dto.cnpjFilial(),
                dto.uf(),
                dto.cidade(),
                dto.endereco(),
                dto.telefone(),
                dto.email()
        );
    }

    public FilialLocadoraResponseDTO mapearParaResponse(FilialLocadora entidade) {
       return new FilialLocadoraResponseDTO(
          entidade.getIdLocadora(),
          entidade.getNomeFilial(),
          entidade.getCnpjFilial(),
          entidade.getUf(),
          entidade.getCidade(),
          entidade.getEndereco(),
          entidade.getTelefone(),
          entidade.getEmail(),
          entidade.getCreatedAt()
       );
    }
}
