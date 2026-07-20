package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Manutencao;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.ManutencaoMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.ManutencaoRepository;
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
public class ManutencaoService {
   private final ManutencaoRepository repository;
   private final ManutencaoMapper mapper;

   @Transactional
   public ManutencaoResponseDTO criarManutencao(ManutencaoCreateDTO dto) {
      Manutencao manutencao = mapper.mapearParaManutencao(dto);
      repository.save(manutencao);
      return mapper.mapearParaResponse(manutencao);
   }

   @Transactional(readOnly = true)
   public ManutencaoResponseDTO buscarManutencaoPorId(String id) {
      Manutencao manutencao = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Manutenção não encontrada."));
      return mapper.mapearParaResponse(manutencao);
   }

   @Transactional(readOnly = true)
   public Page<ManutencaoResponseDTO> buscarTodosPaginado() {
      Pageable pg = PageRequest.of(0, 10, Sort.by("dataManutencao").descending());
      Page<Manutencao> listaManutencoes = repository.findAll(pg);
      return listaManutencoes.map(mapper::mapearParaResponse);
   }

   @Transactional
   public ManutencaoResponseDTO atualizarManutencaoPorId(ManutencaoUpdateDTO dto, String id) {
      Manutencao entidade = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Manutenção não encontrada."));
      atualizarAtributosManutencao(entidade, dto);
      repository.save(entidade);
      return mapper.mapearParaResponse(entidade);
   }

   @Transactional
   public void deletarManutencaoPorId(String id) {
      Manutencao entidade = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Manutenção não encontrada."));
      repository.delete(entidade);
   }

   // METODOS AUXILIARES
   private void atualizarAtributosManutencao(Manutencao entidade, ManutencaoUpdateDTO dto) {
      entidade.setDataManutencao(dto.dataManutencao());
      entidade.setDescricao(dto.descricao());
      entidade.setValor(dto.valor());
   }
}
