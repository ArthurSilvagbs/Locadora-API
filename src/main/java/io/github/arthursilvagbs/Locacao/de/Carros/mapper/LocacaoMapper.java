package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Cliente;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;
import org.springframework.stereotype.Component;

@Component
public class LocacaoMapper {

    public Locacao mapearParaLocacao(
            LocacaoCreateDTO dto,
            Cliente cliente,
            Veiculo veiculo,
            FilialLocadora filialRetirada,
            FilialLocadora filialDevoculocao
    ) {
        return new Locacao(
                cliente,
                veiculo,
                filialRetirada,
                filialDevoculocao,
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
