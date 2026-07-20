package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.FilialLocadoraMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.FilialLocadoraRepository;
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
public class FilialLocadoraService {
   private final FilialLocadoraRepository repository;
   private final FilialLocadoraMapper mapper;

   @Transactional
   public FilialLocadoraResponseDTO criarFilialLocadora(FilialLocadoraCreateDTO dto) {
      if (repository.existsByCnpjFilial(dto.cnpjFilial())) {
         throw new RegistroDuplicadoException("CNPJ já cadastrado.");
      }
      FilialLocadora filial = mapper.mapearParaFilialLocadora(dto);
      repository.save(filial);
      return mapper.mapearParaResponse(filial);
   }

   @Transactional(readOnly = true)
   public FilialLocadoraResponseDTO buscarFilialLocadoraPorId(String id) {
      FilialLocadora filial = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Filial não encontrada."));
      return mapper.mapearParaResponse(filial);
   }

   @Transactional(readOnly = true)
   public Page<FilialLocadoraResponseDTO> buscarTodosPaginado() {
      Pageable pg = PageRequest.of(0, 10, Sort.by("nomeFilial").ascending());
      Page<FilialLocadora> listaFiliais = repository.findAll(pg);
      return listaFiliais.map(mapper::mapearParaResponse);
   }

   @Transactional
   public FilialLocadoraResponseDTO atualizarFilialLocadoraPorId(FilialLocadoraUpdateDTO dto, String id) {
      FilialLocadora entidade = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Filial não encontrada."));
      atualizarAtributosFilialLocadora(entidade, dto);
      repository.save(entidade);
      return mapper.mapearParaResponse(entidade);
   }

   @Transactional
   public void deletarFilialLocadoraPorId(String id) {
      FilialLocadora entidade = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Filial não encontrada."));
      repository.delete(entidade);
   }

   // METODOS AUXILIARES
   private void atualizarAtributosFilialLocadora(FilialLocadora entidade, FilialLocadoraUpdateDTO dto) {
      entidade.setEndereco(dto.endereco());
      entidade.setTelefone(dto.telefone());
      entidade.setEmail(dto.email());
   }
}
