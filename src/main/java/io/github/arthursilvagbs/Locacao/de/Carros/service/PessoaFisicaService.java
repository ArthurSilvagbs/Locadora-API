package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaFisica;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.PessoaFisicaMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.PessoaFisicaRepository;
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
public class PessoaFisicaService {

   private final PessoaFisicaRepository repository;
   private final PessoaFisicaMapper mapper;

   @Transactional
   public PessoaFisicaResponseDTO criarPessoaFisica(PessoaFisicaCreateDTO dto) {
      if (!repository.existsByCpf(dto.cpf())) {
         throw new RegistroDuplicadoException("CPF já cadastrado.");
      }
      if (!repository.existsByEmail(dto.email())){
         throw new RegistroDuplicadoException("Email já cadastrado.");
      }
      PessoaFisica cliente = mapper.mapearParaPessoaFisica(dto);
      repository.save(cliente);
      return mapper.mapearParaResponse(cliente);
   }

   @Transactional(readOnly = true)
   public PessoaFisicaResponseDTO buscarPessoaFisicaPorId(String id) {
      PessoaFisica pessoaFisica = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado."));
      return mapper.mapearParaResponse(pessoaFisica);
   }

   @Transactional(readOnly = true)
   public PessoaFisicaResponseDTO buscarPessoaFisicaPorCpf(String cpf) {
      PessoaFisica pessoaFisica = repository.findByCpf(cpf)
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado."));
      return mapper.mapearParaResponse(pessoaFisica);
   }

   @Transactional(readOnly = true)
   public Page<PessoaFisicaResponseDTO> buscarTodosPaginado() {
      Pageable pg = PageRequest.of(0, 10, Sort.by("cpf").ascending());
      Page<PessoaFisica> listaPessoaFisica = repository.findAll(pg);
      return listaPessoaFisica.map(mapper::mapearParaResponse);
   }

   @Transactional
   public PessoaFisicaResponseDTO atualizarPessoaFisicaPorId(PessoaFisicaUpdateDTO dto, String id) {
      PessoaFisica entidade = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não econtrado."));
      atualizarAtributosPessoaFisica(entidade, dto);
      repository.save(entidade);
      return mapper.mapearParaResponse(entidade);
   }

   @Transactional
   public PessoaFisicaResponseDTO atualizarPessoaFisicaPorCpf(PessoaFisicaUpdateDTO dto, String cpf) {
      PessoaFisica entidade = repository.findByCpf(cpf)
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não econtrado."));
      atualizarAtributosPessoaFisica(entidade, dto);
      repository.save(entidade);
      return mapper.mapearParaResponse(entidade);
   }

   @Transactional
   public void deletarPessoaFisicaPorId(String id) {
      PessoaFisica entidade = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado"));
      repository.delete(entidade);
   }

   // METODOS AUXILIARES
   private void atualizarAtributosPessoaFisica(PessoaFisica entidade, PessoaFisicaUpdateDTO dto) {
      entidade.setEmail(dto.email());
      entidade.setEmail(dto.email());
      entidade.setEndereco(dto.email());
   }
}
