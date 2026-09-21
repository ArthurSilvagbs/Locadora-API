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

       if (entidade.getVeiculo() == null) {
          return new LocacaoResponseDTO(
             entidade.getIdLocacao(),
             entidade.getCliente().getIdCliente().toString(),
             null,
             entidade.getValorLocacao(),
             entidade.getFilialRetirada().getIdLocadora().toString(),
             entidade.getFilialDevolucao().getIdLocadora().toString(),
             entidade.getCategoriaVeiculo(),
             entidade.getFormaPagamento(),
             entidade.getStatusLocacao(),
             entidade.getDataRetirada(),
             entidade.getDataDevolucao()
          );
       }

       return new LocacaoResponseDTO(
          entidade.getIdLocacao(),
          entidade.getCliente().getIdCliente().toString(),
          entidade.getVeiculo().getIdVeiculo().toString(),
          entidade.getValorLocacao(),
          entidade.getFilialRetirada().getIdLocadora().toString(),
          entidade.getFilialDevolucao().getIdLocadora().toString(),
          entidade.getCategoriaVeiculo(),
          entidade.getFormaPagamento(),
          entidade.getStatusLocacao(),
          entidade.getDataRetirada(),
          entidade.getDataDevolucao()
       );
    }
}
