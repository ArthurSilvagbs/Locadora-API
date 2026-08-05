package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaCreateDTO;
// Import do DTO de resposta (o que o service devolve depois de mapear a entidade)
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaResponseDTO;
// Import do DTO usado para atualizar uma PessoaFisica já existente
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaUpdateDTO;
// Import da entidade JPA que representa uma Pessoa Física no banco
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaFisica;
// Import da exception lançada quando um registro não é encontrado (ex: buscar por um ID que não existe)
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
// Import da exception lançada quando tentamos cadastrar algo duplicado (CPF ou email já existentes)
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
// Import do mapper responsável por converter DTO <-> Entidade <-> DTO de resposta
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.PessoaFisicaMapper;
// Import do repository (camada que fala com o banco); vamos mockar ele, não usar um banco de verdade
import io.github.arthursilvagbs.Locacao.de.Carros.repository.PessoaFisicaRepository;

// Anotações de teste do JUnit 5: marcam método de teste e dão um nome legível a ele
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
// Permite ligar o Mockito ao JUnit 5, habilitando @Mock e @InjectMocks nesta classe
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Utilitário de UUID, usado para gerar IDs falsos nos testes
import java.util.UUID;
// Optional é o tipo que o Spring Data usa para representar "achei" ou "não achei" no findById/findByX
import java.util.Optional;

// Métodos estáticos do AssertJ, usados para fazer as verificações (assertThat...)
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
// Métodos estáticos do Mockito, usados para configurar comportamento dos mocks e verificar chamadas
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PessoaFisicaServiceTest {
   @Mock
   private PessoaFisicaRepository repository;

   @Mock
   private PessoaFisicaMapper mapper;

   @InjectMocks
   private PessoaFisicaService service;

   // ---------------------------------------------------------------------
   // MÉTODO: criarPessoaFisica
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("criarPessoaFisica deve salvar e retornar o DTO quando CPF e email não existem")
   void criarPessoaFisica_dadosValidos_retornaResponseDTO() {
      PessoaFisicaCreateDTO dto = new PessoaFisicaCreateDTO(
         "Arthur Silva",
         "arthur@email.com",
         "11999999999",
         "Rua A, 123",
         "12345678900"
      );
      PessoaFisica entidadeMapeada = new PessoaFisica(
         dto.nome(), dto.email(), dto.telefone(), dto.endereco(), dto.cpf()
      );
      PessoaFisicaResponseDTO responseEsperado = new PessoaFisicaResponseDTO(
         UUID.randomUUID(), dto.nome(), dto.email(), dto.telefone(), dto.endereco(), dto.cpf(), null
      );

      when(repository.existsByCpf(dto.cpf())).thenReturn(false);
      when(repository.existsByEmail(dto.email())).thenReturn(false);
      when(mapper.mapearParaPessoaFisica(dto)).thenReturn(entidadeMapeada);
      when(mapper.mapearParaResponse(entidadeMapeada)).thenReturn(responseEsperado);

      PessoaFisicaResponseDTO resultado = service.criarPessoaFisica(dto);

      assertThat(resultado).isEqualTo(responseEsperado);

      verify(repository, times(1)).save(entidadeMapeada);
   }

   @Test
   @DisplayName("criarPessoaFisica deve lançar RegistroDuplicadoException quando o CPF já existe")
   void criarPessoaFisica_cpfDuplicado_lancaExcecao() {
      PessoaFisicaCreateDTO dto = new PessoaFisicaCreateDTO(
         "Arthur Silva",
         "arthur@email.com",
         "11999999999",
         "Rua A, 123",
         "12345678900"
      );

      when(repository.existsByCpf(dto.cpf())).thenReturn(true);

      assertThatThrownBy(() -> service.criarPessoaFisica(dto))
         .isInstanceOf(RegistroDuplicadoException.class)
         .hasMessage("CPF já cadastrado.");

      verify(repository, never()).existsByEmail(anyString());
      verify(repository, never()).save(any());
   }

   @Test
   @DisplayName("criarPessoaFisica deve lançar RegistroDuplicadoException quando o email já existe")
   void criarPessoaFisica_emailDuplicado_lancaExcecao() {
      PessoaFisicaCreateDTO dto = new PessoaFisicaCreateDTO(
         "Arthur Silva",
         "arthur@email.com",
         "11999999999",
         "Rua A, 123",
         "12345678900"
      );

      when(repository.existsByCpf(dto.cpf())).thenReturn(false);
      when(repository.existsByEmail(dto.email())).thenReturn(true);

      assertThatThrownBy(() -> service.criarPessoaFisica(dto))
         .isInstanceOf(RegistroDuplicadoException.class)
         .hasMessage("Email já cadastrado.");

      verify(repository, never()).save(any());
   }

   @Test
   @DisplayName("buscarPessoaFisicaPorId deve retornar o DTO quando o ID existe")
   void buscarPessoaFisicaPorId_idExistente_retornaResponseDTO() {
      UUID id = UUID.randomUUID();
      PessoaFisica entidade = new PessoaFisica(
         "Arthur Silva",
         "arthur@email.com",
         "11999999999",
         "Rua A, 123",
         "12345678900"
      );
      PessoaFisicaResponseDTO responseEsperado = new PessoaFisicaResponseDTO(
         id,
         "Arthur Silva",
         "arthur@email.com",
         "11999999999",
         "Rua A, 123",
         "12345678900",
         null
      );

      when(repository.findById(id)).thenReturn(Optional.of(entidade));
      when(mapper.mapearParaResponse(entidade)).thenReturn(responseEsperado);
      PessoaFisicaResponseDTO resultado = service.buscarPessoaFisicaPorId(id.toString());

      assertThat(resultado).isEqualTo(responseEsperado);
   }

   @Test
   @DisplayName("buscarPessoaFisicaPorId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
   void buscarPessoaFisicaPorId_idInexistente_lancaExcecao() {
      UUID id = UUID.randomUUID();
      when(repository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarPessoaFisicaPorId(id.toString()))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Cliente não encontrado.");

      verify(mapper, never()).mapearParaResponse(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: buscarPessoaFisicaPorCpf
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("buscarPessoaFisicaPorCpf deve retornar o DTO quando o CPF existe.")
   void buscarPessoaFisicaPorCpf_cpfExistente_retornaDTO() {
      PessoaFisica entidade = new PessoaFisica(
         "Arthur Silva",
         "arthur@email.com",
         "11999999999",
         "Rua A, 123",
         "12345678900"
      );
      String cpf = entidade.getCpf();
      PessoaFisicaResponseDTO dto = new PessoaFisicaResponseDTO(
         UUID.randomUUID(),
         entidade.getNome(),
         entidade.getEmail(),
         entidade.getTelefone(),
         entidade.getEndereco(),
         entidade.getCpf(),
         entidade.getCreatedAt()
      );

      when(repository.findByCpf(cpf)).thenReturn(Optional.of(entidade));
      when(mapper.mapearParaResponse(entidade)).thenReturn(dto);
      PessoaFisicaResponseDTO resultado = service.buscarPessoaFisicaPorCpf(cpf);

      assertThat(resultado).isEqualTo(dto);
   }

   @Test
   @DisplayName("buscarPessoaFisicaPorCpf deve lançar EntidadeNaoEncontradaException quando o ID não existe")
   void buscarPessoaFisicaPorCpf_idInexistente_lancaExcecao() {
      String cpf = "12345678900";
      when(repository.findByCpf(cpf)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarPessoaFisicaPorCpf(cpf))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Cliente não encontrado.");

      verify(mapper, never()).mapearParaResponse(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: atualizarPessoaFisicaPorId
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("atualizarPessoaFisicaPorId deve atualizar e retornar o DTO quando o ID existe")
   void atualizarPessoaFisicaPorId_idExistente_atualizaERetorna() {
      UUID id = UUID.randomUUID();

      PessoaFisica entidadeExistente = new PessoaFisica(
         "Arthur Silva",
         "antigo@email.com",
         "11999999999",
         "Endereco antigo",
         "12345678900"
      );
      PessoaFisicaUpdateDTO dtoAtualizacao = new PessoaFisicaUpdateDTO(
         "novo@email.com",
         "11888888888",
         "Endereco novo"
      );
      PessoaFisicaResponseDTO responseEsperado = new PessoaFisicaResponseDTO(
         id, "Arthur Silva",
         "novo@email.com",
         "11888888888",
         "Endereco novo",
         "12345678900",
         null
      );

      when(repository.findById(id)).thenReturn(Optional.of(entidadeExistente));
      when(mapper.mapearParaResponse(any(PessoaFisica.class))).thenReturn(responseEsperado);

      PessoaFisicaResponseDTO resultado = service.atualizarPessoaFisicaPorId(dtoAtualizacao, id.toString());

      assertThat(resultado).isEqualTo(responseEsperado);

      verify(repository, times(1)).save(entidadeExistente);
   }

   @Test
   @DisplayName("atualizarPessoaFisicaPorId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
   void atualizarPessoaFisicaPorId_idInexistente_lancaExcecao() {
      UUID id = UUID.randomUUID();

      PessoaFisicaUpdateDTO dtoAtualizacao = new PessoaFisicaUpdateDTO(
         "novo@email.com",
         "11888888888",
         "Endereco novo"
      );

      when(repository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.atualizarPessoaFisicaPorId(dtoAtualizacao, id.toString()))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Cliente não econtrado.");

      verify(repository, never()).save(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: atualizarPessoaFisicaPorCpf
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("atualizarPessoaFisicaPorCpf deve atualizar a entidade e retornar o DTO quando o ID é existente.")
   void atualizarPessoaFisicaPorCpf_idExistente_atualizaERetorna() {
      String cpf = "12345678900";

      PessoaFisica entidade = new PessoaFisica(
         "Arthur Silva",
         "arthur@email.com",
         cpf,
         "Rua A, 123",
         "12345678900"
      );
      PessoaFisicaUpdateDTO updateDto = new PessoaFisicaUpdateDTO(
         "arthursilva@email.com",
         "22988888888",
         "Rua B, 321"
      );
      PessoaFisicaResponseDTO responseEsperado = new PessoaFisicaResponseDTO(
         UUID.randomUUID(),
         entidade.getNome(),
         entidade.getEmail(),
         entidade.getTelefone(),
         entidade.getEndereco(),
         entidade.getCpf(),
         entidade.getCreatedAt()
      );

      when(repository.findByCpf(cpf)).thenReturn(Optional.of(entidade));
      when(mapper.mapearParaResponse(entidade)).thenReturn(responseEsperado);

      PessoaFisicaResponseDTO resultado = service.atualizarPessoaFisicaPorCpf(updateDto, cpf);

      assertThat(responseEsperado).isEqualTo(resultado);

      verify(repository, times(1)).save(entidade);
   }

   // ---------------------------------------------------------------------
   // MÉTODO: deletarPessoaFisicaPorId
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("deletarPessoaFisicaPorId deve deletar quando o ID existe")
   void deletarPessoaFisicaPorId_idExistente_deleta() {
      UUID id = UUID.randomUUID();

      PessoaFisica entidadeExistente = new PessoaFisica(
         "Arthur Silva",
         "arthur@email.com",
         "11999999999",
         "Rua A, 123",
         "12345678900"
      );

      when(repository.findById(id)).thenReturn(Optional.of(entidadeExistente));

      service.deletarPessoaFisicaPorId(id.toString());

      verify(repository, times(1)).delete(entidadeExistente);
   }

   @Test
   @DisplayName("deletarPessoaFisicaPorId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
   void deletarPessoaFisicaPorId_idInexistente_lancaExcecao() {
      UUID id = UUID.randomUUID();

      when(repository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.deletarPessoaFisicaPorId(id.toString()))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Cliente não encontrado");

      verify(repository, never()).delete(any());
   }
}
