package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.*;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.StatusInvalidoException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.VeiculoNaoDisponivelException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.LocacaoMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.ClienteRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.FilialLocadoraRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.LocacaoRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocacaoService {

   private final LocacaoRepository repository;
   private final LocacaoMapper mapper;
   private final VeiculoRepository veiculoRepository;
   private final ClienteRepository clienteRepository;
   private final FilialLocadoraRepository filialLocadoraRepository;

   @Transactional
   public LocacaoResponseDTO criarReservaDoVeiculo(LocacaoCreateDTO dto) {
       Cliente cliente = clienteRepository.findById(UUID.fromString(dto.clienteId()))
               .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado."));
       Veiculo veiculo = veiculoRepository.findById(UUID.fromString(dto.veiculoId()))
               .orElseThrow(() -> new EntidadeNaoEncontradaException("Veículo não encontrado."));
       FilialLocadora filialRetirada = filialLocadoraRepository.findById(UUID.fromString(dto.filialRetiradaId()))
               .orElseThrow(() -> new EntidadeNaoEncontradaException("Filial não encontrada."));
       FilialLocadora filialDevolucao = filialLocadoraRepository.findById(UUID.fromString(dto.filialDevolucaoId()))
               .orElseThrow(() -> new EntidadeNaoEncontradaException("Filial não encontrada."));

       if (veiculo.getStatusVeiculo() == StatusVeiculo.LOCADO) {
           throw new VeiculoNaoDisponivelException("Veículo já está locado/reservado.");
       }

       long diferenciaDias = ChronoUnit.DAYS.between(dto.dataRetirada(), dto.dataDevolucao());
       BigDecimal valorDiariaVeiculo = calculoDiariaPorCategoria(veiculo.getCategoriaVeiculo());
       BigDecimal valorLocacao = valorDiariaVeiculo.multiply(new BigDecimal(diferenciaDias));

       veiculo.setStatusVeiculo(StatusVeiculo.LOCADO);
       veiculoRepository.save(veiculo);

       Locacao locacao = mapper.mapearParaLocacao(dto, cliente, veiculo, filialRetirada, filialDevolucao);
       locacao.setValorLocacao(valorLocacao);
       locacao.setStatusLocacao(StatusLocacao.PENDENTE_DE_RETIRADA);
       Locacao locacaoAtualizada = repository.save(locacao);

       return mapper.mapearParaResponse(locacaoAtualizada);
   }

   @Transactional
   public LocacaoResponseDTO confirmarRetirada(String locacaoId, Double kmPreRetirada) {
       Locacao locacao = repository.findById(UUID.fromString(locacaoId))
               .orElseThrow(() -> new EntidadeNaoEncontradaException("Locação não encontrada."));

       if (locacao.getStatusLocacao() != StatusLocacao.PENDENTE_DE_RETIRADA) {
           throw new StatusInvalidoException("Status de locação inválido.");
       }

       Veiculo veiculo = locacao.getVeiculo();
       veiculo.setQuilometragem(kmPreRetirada);
       veiculoRepository.save(veiculo);

       locacao.setStatusLocacao(StatusLocacao.RETIRADO);
       locacao.setKmRetirada(veiculo.getQuilometragem());
       Locacao locacaoAtualizada = repository.save(locacao);

       return mapper.mapearParaResponse(locacaoAtualizada);
   }

   @Transactional
   public LocacaoResponseDTO confirmarDevolucao(String locacaoId, Double kmPosDevolucao) {
       Locacao locacao = repository.findById(UUID.fromString(locacaoId))
               .orElseThrow(() -> new EntidadeNaoEncontradaException("Locação não encontrada."));

       if (locacao.getStatusLocacao() != StatusLocacao.RETIRADO) {
           throw new StatusInvalidoException("Status de locação inválido.");
       }

       Veiculo veiculo = locacao.getVeiculo();
       veiculo.setQuilometragem(veiculo.getQuilometragem() + kmPosDevolucao);
       veiculo.setStatusVeiculo(StatusVeiculo.DISPONIVEL);
       veiculoRepository.save(veiculo);

       locacao.setStatusLocacao(StatusLocacao.DEVOLVIDO);
       locacao.setKmDevolucao(veiculo.getQuilometragem());
       Locacao locacaoAtualizada = repository.save(locacao);

       return mapper.mapearParaResponse(locacaoAtualizada);
   }

   @Transactional
   public LocacaoResponseDTO cancelarLocacao(String locacaoId) {
       Locacao locacao = repository.findById(UUID.fromString(locacaoId))
               .orElseThrow(() -> new EntidadeNaoEncontradaException("Locação não encontrada."));

       if (locacao.getStatusLocacao() == StatusLocacao.CANCELADA) {
           throw new StatusInvalidoException("A locação já está com o status 'CANCELADA'.");
       }
       if (locacao.getStatusLocacao() != StatusLocacao.PENDENTE_DE_RETIRADA) {
           throw new StatusInvalidoException("Status de locação inválido.");
       }

       Veiculo veiculo = locacao.getVeiculo();
       veiculo.setStatusVeiculo(StatusVeiculo.DISPONIVEL);
       veiculoRepository.save(veiculo);

       locacao.setStatusLocacao(StatusLocacao.CANCELADA);
       Locacao locacaoAtualizada = repository.save(locacao);

       return mapper.mapearParaResponse(locacaoAtualizada);
   }

   private BigDecimal calculoDiariaPorCategoria(CategoriaVeiculo categoriaVeiculo) {
       final double DIARIA_BASE = 120.00;
       if (categoriaVeiculo == CategoriaVeiculo.HATCH) {
           return BigDecimal.valueOf(DIARIA_BASE);
       } else if (categoriaVeiculo == CategoriaVeiculo.SEDAN) {
           return BigDecimal.valueOf(DIARIA_BASE * 1.3);
       } else if (categoriaVeiculo == CategoriaVeiculo.PICK_UP) {
           return BigDecimal.valueOf(DIARIA_BASE * 1.8);
       } else if (categoriaVeiculo == CategoriaVeiculo.SUV) {
           return BigDecimal.valueOf(DIARIA_BASE * 1.7);
       } else if (categoriaVeiculo == CategoriaVeiculo.MINI_VAN) {
           return BigDecimal.valueOf(DIARIA_BASE * 1.6);
       } else if (categoriaVeiculo == CategoriaVeiculo.VAN) {
           return BigDecimal.valueOf(DIARIA_BASE * 2.0);
       } else if (categoriaVeiculo == CategoriaVeiculo.FURGAO) {
           return BigDecimal.valueOf(DIARIA_BASE * 2.2);
       } else if (categoriaVeiculo == CategoriaVeiculo.BLINDADO) {
           return BigDecimal.valueOf(DIARIA_BASE * 3.0);
       } else {
           throw new EntidadeNaoEncontradaException("Erro ao calcular valor da diária do veículo. Categoria inválida.");
       }
   }

}
