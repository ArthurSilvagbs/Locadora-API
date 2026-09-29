package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Cliente;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaFisica;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.ClienteSemLocacoesRegistradasException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.LocacaoMapper;
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
   private final LocacaoMapper locacaoMapper;

   @Transactional
   public PessoaFisicaResponseDTO criarPessoaFisica(PessoaFisicaCreateDTO dto) {
      if (repository.existsByCpf(dto.cpf())) {
         throw new RegistroDuplicadoException("CPF já cadastrado.");
      }
      if (repository.existsByEmail(dto.email())){
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

   @Transactional(readOnly = true)
   public Page<LocacaoResponseDTO> buscarLocacoesClientePorId(String id) {
      Pageable pg = PageRequest.of(0, 10, Sort.by("dataRetirada").descending());

      if (!repository.existsById(UUID.fromString(id))) {
         throw new EntidadeNaoEncontradaException("Cliente com o ID indicado não encontrado.");
      }

      Page<Locacao> listaDeLocacaoes = repository.buscarLocacoesPorId(UUID.fromString(id), pg);

      if (listaDeLocacaoes.isEmpty()) {
         throw new ClienteSemLocacoesRegistradasException("O cliente não possui nenhuma locação registrada");
      }

      return listaDeLocacaoes.map(locacaoMapper::mapearParaResponse);
   }

   @Transactional(readOnly = true)
   public Page<LocacaoResponseDTO> buscarLocacoesClientePorCpf(String cpf) {
      Pageable pg = PageRequest.of(0, 10, Sort.by("dataRetirada").descending());

      if (!repository.existsByCpf(cpf)) {
         throw new EntidadeNaoEncontradaException("Cliente não encontrado.");
      }

      Page<Locacao> listaLocacoes = repository.buscarLocacoesPorCpf(cpf, pg);

      if (listaLocacoes.isEmpty()) {
         throw new EntidadeNaoEncontradaException("Nenhuma locação deste cliente encontrada.");
      }

      return listaLocacoes.map(locacaoMapper::mapearParaResponse);
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
      entidade.setTelefone(dto.telefone());
      entidade.setEndereco(dto.endereco());
   }
}
