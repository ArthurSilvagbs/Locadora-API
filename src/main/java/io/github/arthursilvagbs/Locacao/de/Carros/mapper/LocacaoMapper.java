package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;
import org.springframework.stereotype.Component;

@Component
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

    public LocacaoResponseDTO mapearParaResponse(Locacao entidade) {
       return new LocacaoResponseDTO(
          entidade.getId(),
          entidade.getCliente(),
          entidade.getVeiculo(),
          entidade.getValorLocacao(),
          entidade.getFilialRetirada(),
          entidade.getFilialDevolucao(),
          entidade.getFormaPagamento(),
          entidade.getStatusLocacao(),
          entidade.getDataRetirada(),
          entidade.getDataDevolucao()
       );
    }
}
