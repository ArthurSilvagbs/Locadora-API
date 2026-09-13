package io.github.arthursilvagbs.Locacao.de.Carros.service;

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
   public Page<CategoriaVeiculo> buscarCategoriaDisponiveisPorFilial(String idFilialLocadora) {
      UUID idFilial = UUID.fromString(idFilialLocadora);
      Pageable pg = PageRequest.of(0, 10);
      return repository.buscarCategoriasVeiculoPorFilial(idFilial, pg);
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

}
