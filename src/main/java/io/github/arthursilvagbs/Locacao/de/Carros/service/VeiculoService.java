package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoCategoriasDisponiveisResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.CategoriaVeiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.VeiculoMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.FilialLocadoraRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VeiculoService {

   private final VeiculoRepository repository;
   private final FilialLocadoraRepository filialLocadoraRepository;
   private final VeiculoMapper mapper;

   @Transactional
   public VeiculoResponseDTO cadastrarVeiculo(VeiculoCreateDTO dto) {
      if (repository.existsByNumeroChassi(dto.numeroChassi())) {
         throw new RegistroDuplicadoException("Número do chassi já existente no sistema.");
      }
      if (repository.existsByPlacaVeiculo(dto.placaVeiculo())) {
         throw new RegistroDuplicadoException("Placa do veículo já existente no sistema.");
      }
      if (repository.existsByRenavam(dto.renavam())) {
         throw new RegistroDuplicadoException("Número do Renavam já existente no sistema.");
      }

      FilialLocadora filialLocadora = filialLocadoraRepository.findById(UUID.fromString(dto.filialAtualId()))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Filial não encontrada"));

      Veiculo veiculo = mapper.mapearParaVeiculo(dto, filialLocadora);
      repository.save(veiculo);
      return mapper.mapearParaResponse(veiculo);
   }

   @Transactional(readOnly = true)
   public VeiculoResponseDTO buscarVeiculoPorId(String id) {
      Veiculo veiculo = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Veículo não encontrado."));
      return mapper.mapearParaResponse(veiculo);
   }

   @Transactional(readOnly = true)
   public VeiculoResponseDTO buscarVeiculoPorNumChassi(String numChassi) {
      Veiculo veiculo = repository.findByNumeroChassi(numChassi)
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Veículo não encontrado."));
      return mapper.mapearParaResponse(veiculo);
   }

   @Transactional(readOnly = true)
   public VeiculoResponseDTO buscarVeiculoPorPlaca(String placa) {
      Veiculo veiculo = repository.findByPlacaVeiculo(placa)
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Veículo não encontrado."));
      return mapper.mapearParaResponse(veiculo);
   }

   @Transactional(readOnly = true)
   public VeiculoResponseDTO buscarVeiculoPorRenavam(String renavam) {
      Veiculo veiculo = repository.findByRenavam(renavam)
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Veículo não encontrado"));
      return mapper.mapearParaResponse(veiculo);
   }

   @Transactional(readOnly = true)
   public Page<VeiculoResponseDTO> buscarTodosPaginado() {
      Pageable pg = PageRequest.of(0, 10, Sort.by("marca"));
      Page<Veiculo> listaVeiculo = repository.findAll(pg);
      return listaVeiculo.map(mapper::mapearParaResponse);
   }

   @Transactional(readOnly = true)
   public Page<VeiculoCategoriasDisponiveisResponseDTO> buscarCategoriaDisponiveisPorFilial(String idFilialLocadora, LocalDateTime dataRetirada, LocalDateTime dataDevolucao) {
      UUID idFilial = UUID.fromString(idFilialLocadora);
      Pageable pg = PageRequest.of(0, 10);

      long diferenciaDias = ChronoUnit.DAYS.between(dataRetirada, dataDevolucao);

      Page<CategoriaVeiculo> lista = repository.buscarCategoriasVeiculoPorFilial(idFilial, dataRetirada, dataDevolucao, pg);

      return lista.map(categoria -> {
         BigDecimal diaria = calculoDiariaPorCategoria(categoria);
         BigDecimal valorLocacao = diaria.multiply(BigDecimal.valueOf(diferenciaDias));

         return new VeiculoCategoriasDisponiveisResponseDTO(categoria, valorLocacao);
      });
   }

   @Transactional
   public VeiculoResponseDTO atualizarVeiculoPorId(VeiculoUpdateDTO dto, String id) {
      Veiculo veiculo = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Veículo não encontrado."));
      atualizarAtributosVeiculo(veiculo, dto);
      repository.save(veiculo);
      return mapper.mapearParaResponse(veiculo);
   }

   @Transactional
   public VeiculoResponseDTO atualizarVeiculoPorNumChassi(VeiculoUpdateDTO dto, String numChassi) {
      Veiculo veiculo = repository.findByNumeroChassi(numChassi)
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Veículo não encontrado."));
      atualizarAtributosVeiculo(veiculo, dto);
      repository.save(veiculo);
      return mapper.mapearParaResponse(veiculo);
   }

   @Transactional
   public VeiculoResponseDTO atualizarVeiculoPorPlaca(VeiculoUpdateDTO dto, String placa) {
      Veiculo veiculo = repository.findByPlacaVeiculo(placa)
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Veículo não encontrado."));
      atualizarAtributosVeiculo(veiculo, dto);
      repository.save(veiculo);
      return mapper.mapearParaResponse(veiculo);
   }

   @Transactional
   public void deletarVeiculoViaId(String id) {
      Veiculo veiculo = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Veículo não encontrado."));
      repository.delete(veiculo);
   }

   @Transactional
   public void deletarVeiculoViaNumChassi(String numChassi) {
      Veiculo veiculo = repository.findByNumeroChassi(numChassi)
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Veículo não encontrado."));
      repository.delete(veiculo);
   }

   // METODOS AUXILIARES
   private void atualizarAtributosVeiculo(Veiculo entidade, VeiculoUpdateDTO dto) {
      entidade.setQuilometragem(dto.quilometragem());
      entidade.setCor(dto.cor());
      entidade.setPlacaVeiculo(dto.placa());
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
