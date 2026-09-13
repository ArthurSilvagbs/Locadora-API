package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LocacaoMapper {

    public Locacao mapearParaLocacao(
            LocacaoCreateDTO dto,
            Cliente cliente,
            FilialLocadora filialRetirada,
            FilialLocadora filialDevoculocao,
            CategoriaVeiculo categoriaVeiculo,
            BigDecimal valorLocacao
    ) {
        return new Locacao(
                cliente,
                filialRetirada,
                filialDevoculocao,
                categoriaVeiculo,
                dto.formaPagamento(),
                dto.dataRetirada(),
                dto.dataDevolucao(),
                valorLocacao
        );
    }

    public LocacaoResponseDTO mapearParaResponse(Locacao entidade) {
       return new LocacaoResponseDTO(
          entidade.getIdLocacao(),
          entidade.getCliente(),
          entidade.getVeiculo(),
          entidade.getValorLocacao(),
          entidade.getFilialRetirada(),
          entidade.getFilialDevolucao(),
          entidade.getCategoriaVeiculo(),
          entidade.getFormaPagamento(),
          entidade.getStatusLocacao(),
          entidade.getDataRetirada(),
          entidade.getDataDevolucao()
       );
    }
}
