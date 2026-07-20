package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaJuridica;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.PessoaJuridicaMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.PessoaJuridicaRepository;
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
public class PessoaJuridicaService {
   private final PessoaJuridicaRepository repository;
   private final PessoaJuridicaMapper mapper;

   @Transactional
   public PessoaJuridicaResponseDTO criarPessoaJuridica(PessoaJuridicaCreateDTO dto) {
      if (!repository.existsByCnpj(dto.cnpj())) {
         throw new RegistroDuplicadoException("CNPJ já cadastrado.");
      }
      if (!repository.existsByEmail(dto.email())) {
         throw new RegistroDuplicadoException("Email já cadastrado.");
      }
      PessoaJuridica pessoaJuridica = repository.save(mapper.mapearParaPessoaJuridica(dto));
      return mapper.mapearParaResponse(pessoaJuridica);
   }

   @Transactional(readOnly = true)
   public PessoaJuridicaResponseDTO buscarPessoaJuridicaPorId(String id) {
      PessoaJuridica pessoaJuridica = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado."));
      return mapper.mapearParaResponse(pessoaJuridica);
   }

   @Transactional(readOnly = true)
   public PessoaJuridicaResponseDTO buscarPessoaJuridicaPorCnpj(String cnpj) {
      PessoaJuridica pessoaJuridica = repository.findByCnpj(cnpj)
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado."));
      return mapper.mapearParaResponse(pessoaJuridica);
   }

   @Transactional(readOnly = true)
   public Page<PessoaJuridicaResponseDTO> buscarTodosPaginado() {
      Pageable pg = PageRequest.of(0, 10, Sort.by("cnpj").ascending());
      Page<PessoaJuridica> listaPessoaJuridica = repository.findAll(pg);
      return listaPessoaJuridica.map(mapper::mapearParaResponse);
   }

   @Transactional
   public PessoaJuridicaResponseDTO atualizarPessoaJuridicaViaId(PessoaJuridicaUpdateDTO dto, String id) {
      PessoaJuridica entidade = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não econtrado."));
      atualizarAtributos(entidade, dto);
      repository.save(entidade);
      return mapper.mapearParaResponse(entidade);
   }

   @Transactional
   public PessoaJuridicaResponseDTO atualizarPessoaJuridicaViaCnpj(PessoaJuridicaUpdateDTO dto, String cnpj) {
      PessoaJuridica entidade = repository.findByCnpj(cnpj)
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não econtrado."));
      atualizarAtributos(entidade, dto);
      repository.save(entidade);
      return mapper.mapearParaResponse(entidade);
   }

   @Transactional
   public void deletarPessoaJuridicaViaId(String id) {
      PessoaJuridica entidade = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado."));
      repository.delete(entidade);
   }


   // METODOS AUXILIARES
   private void atualizarAtributos(PessoaJuridica entidade, PessoaJuridicaUpdateDTO dto) {
      entidade.setNome(dto.nome());
      entidade.setEmail(dto.email());
      entidade.setTelefone(dto.telefone());
      entidade.setEndereco(dto.endereco());
   }
}
