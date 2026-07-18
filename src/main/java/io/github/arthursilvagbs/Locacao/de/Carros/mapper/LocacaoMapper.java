package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;

public class LocacaoMapper {

    public Locacao mapearParaLocacao(LocacaoCreateDTO dto) {
        return new Locacao(
                dto.cliente(),
                dto.veiculo(),
                dto.valorLocacao(),
                dto.filialRetirada(),
                dto.filialDevolucao(),
                dto.formaPagamento(),
                dto.dataRetirada(),
                dto.dataDevolucao()
        );
    }
}
