package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;

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
}
