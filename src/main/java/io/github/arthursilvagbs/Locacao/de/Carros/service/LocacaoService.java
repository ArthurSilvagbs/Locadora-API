package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.LocacaoMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.LocacaoRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class LocacaoService {

   private final LocacaoRepository repository;
   private final LocacaoMapper mapper;
   private final VeiculoRepository veiculoRepository;
   private final VeiculoService veiculoService;

   private BigDecimal calcularValorLocacao(String ) {

   }
}
