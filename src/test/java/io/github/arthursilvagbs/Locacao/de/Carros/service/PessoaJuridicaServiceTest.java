package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaJuridica;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.PessoaJuridicaMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.PessoaJuridicaRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PessoaJuridicaServiceTest {

   @Mock
   private PessoaJuridicaRepository repository;

   @Mock
   private PessoaJuridicaMapper mapper;

   @InjectMocks
   private PessoaJuridicaService service;

   // ---------------------------------------------------------------------
   // MÉTODO: criarPessoaJuridica
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("criarPessoaJuridica deve salvar e retornar o DTO quando CNPJ e email não existem")
   void criarPessoaJuridica_dadosValidos_retornaResponseDTO() {
      PessoaJuridicaCreateDTO dto = new PessoaJuridicaCreateDTO(
         "Locadora Silva LTDA",
         "contato@silva.com",
         "1140028922",
         "Av. Central, 500",
         "12345678000199"
      );

      PessoaJuridica entidadeMapeada = new PessoaJuridica(
         dto.nome(), dto.email(), dto.telefone(), dto.endereco(), dto.cnpj()
      );
      PessoaJuridicaResponseDTO responseEsperado = new PessoaJuridicaResponseDTO(
         UUID.randomUUID(), dto.nome(), dto.email(), dto.telefone(), dto.endereco(), dto.cnpj(), null
      );

      when(repository.existsByCnpj(dto.cnpj())).thenReturn(false);
      when(repository.existsByEmail(dto.email())).thenReturn(false);
      when(mapper.mapearParaPessoaJuridica(dto)).thenReturn(entidadeMapeada);
      when(repository.save(entidadeMapeada)).thenReturn(entidadeMapeada);
      when(mapper.mapearParaResponse(entidadeMapeada)).thenReturn(responseEsperado);

      PessoaJuridicaResponseDTO resultado = service.criarPessoaJuridica(dto);

      assertThat(resultado).isEqualTo(responseEsperado);

      verify(repository, times(1)).save(entidadeMapeada);
   }

   @Test
   @DisplayName("criarPessoaJuridica deve lançar RegistroDuplicadoException quando o CNPJ já existe")
   void criarPessoaJuridica_cnpjDuplicado_lancaExcecao() {
      PessoaJuridicaCreateDTO dto = new PessoaJuridicaCreateDTO(
         "Locadora Silva LTDA",
         "contato@silva.com",
         "1140028922",
         "Av. Central, 500",
         "12345678000199"
      );

      when(repository.existsByCnpj(dto.cnpj())).thenReturn(true);

      assertThatThrownBy(() -> service.criarPessoaJuridica(dto))
         .isInstanceOf(RegistroDuplicadoException.class)
         .hasMessage("CNPJ já cadastrado.");

      verify(repository, never()).existsByEmail(anyString());
      verify(repository, never()).save(any());
   }

   @Test
   @DisplayName("criarPessoaJuridica deve lançar RegistroDuplicadoException quando o email já existe")
   void criarPessoaJuridica_emailDuplicado_lancaExcecao() {
      PessoaJuridicaCreateDTO dto = new PessoaJuridicaCreateDTO(
         "Locadora Silva LTDA",
         "contato@silva.com",
         "1140028922",
         "Av. Central, 500",
         "12345678000199"
      );

      when(repository.existsByCnpj(dto.cnpj())).thenReturn(false);
      when(repository.existsByEmail(dto.email())).thenReturn(true);

      assertThatThrownBy(() -> service.criarPessoaJuridica(dto))
         .isInstanceOf(RegistroDuplicadoException.class)
         .hasMessage("Email já cadastrado.");

      verify(repository, never()).save(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: buscarPessoaJuridicaPorId
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("buscarPessoaJuridicaPorId deve retornar o DTO quando o ID existe")
   void buscarPessoaJuridicaPorId_idExistente_retornaResponseDTO() {
      UUID id = UUID.randomUUID();

      PessoaJuridica entidade = new PessoaJuridica(
         "Locadora Silva LTDA",
         "contato@silva.com",
         "1140028922",
         "Av. Central, 500",
         "12345678000199"
      );
      PessoaJuridicaResponseDTO responseEsperado = new PessoaJuridicaResponseDTO(
         id,
         "Locadora Silva LTDA",
         "contato@silva.com",
         "1140028922",
         "Av. Central, 500",
         "12345678000199",
         null
      );

      when(repository.findById(id)).thenReturn(Optional.of(entidade));
      when(mapper.mapearParaResponse(entidade)).thenReturn(responseEsperado);

      PessoaJuridicaResponseDTO resultado = service.buscarPessoaJuridicaPorId(id.toString());

      assertThat(resultado).isEqualTo(responseEsperado);
   }

   @Test
   @DisplayName("buscarPessoaJuridicaPorId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
   void buscarPessoaJuridicaPorId_idInexistente_lancaExcecao() {
      UUID id = UUID.randomUUID();

      when(repository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarPessoaJuridicaPorId(id.toString()))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Cliente não encontrado.");

      verify(mapper, never()).mapearParaResponse(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: buscarPessoaJuridicaViaCnpj
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("buscarPessoaJuridicaPorId deve retornar o DTO quando o CNPJ existe")
   void buscarPessoaJuridicaPorCnpj_cnpjExistente_retornaResponseDTO() {
      String cnpj = "12345678000199";

      PessoaJuridica entidade = new PessoaJuridica(
         "Locadora Silva LTDA",
         "contato@silva.com",
         "1140028922",
         "Av. Central, 500",
         cnpj
      );
      PessoaJuridicaResponseDTO responseEsperado = new PessoaJuridicaResponseDTO(
         UUID.randomUUID(),
         entidade.getNome(),
         entidade.getEmail(),
         entidade.getTelefone(),
         entidade.getEndereco(),
         entidade.getCnpj(),
         entidade.getCreatedAt()
      );

      when(repository.findByCnpj(cnpj)).thenReturn(Optional.of(entidade));
      when(mapper.mapearParaResponse(entidade)).thenReturn(responseEsperado);

      PessoaJuridicaResponseDTO resultado = service.buscarPessoaJuridicaPorCnpj(cnpj);

      assertThat(resultado).isEqualTo(responseEsperado);
   }

   @Test
   @DisplayName("buscarPessoaJuridicaPorId deve lançar EntidadeNaoEncontradaException quando o CNPJ não existe")
   void buscarPessoaJuridicaPorCnpj_cnpjInexistente_lancaExcecao() {
      String cnpj = "1140028922";

      when(repository.findByCnpj(cnpj)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarPessoaJuridicaPorCnpj(cnpj))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Cliente não encontrado.");

      verify(mapper, never()).mapearParaResponse(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: atualizarPessoaJuridicaViaId
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("atualizarPessoaJuridicaViaId deve atualizar e retornar o DTO quando o ID existe")
   void atualizarPessoaJuridicaViaId_idExistente_atualizaERetorna() {
      UUID id = UUID.randomUUID();

      PessoaJuridica entidadeExistente = new PessoaJuridica(
         "Nome Antigo LTDA",
         "antigo@silva.com",
         "1140028922",
         "Endereco antigo",
         "12345678000199"
      );
      PessoaJuridicaUpdateDTO dtoAtualizacao = new PessoaJuridicaUpdateDTO(
         "Nome Novo LTDA",
         "novo@silva.com",
         "1140028900",
         "Endereco novo"
      );
      PessoaJuridicaResponseDTO responseEsperado = new PessoaJuridicaResponseDTO(
         id,
         "Nome Novo LTDA",
         "novo@silva.com",
         "1140028900",
         "Endereco novo",
         "12345678000199",
         null
      );

      when(repository.findById(id)).thenReturn(Optional.of(entidadeExistente));
      when(mapper.mapearParaResponse(any(PessoaJuridica.class))).thenReturn(responseEsperado);

      PessoaJuridicaResponseDTO resultado = service.atualizarPessoaJuridicaViaId(dtoAtualizacao, id.toString());

      assertThat(resultado).isEqualTo(responseEsperado);

      verify(repository, times(1)).save(entidadeExistente);
   }

   @Test
   @DisplayName("atualizarPessoaJuridicaViaId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
   void atualizarPessoaJuridicaViaId_idInexistente_lancaExcecao() {
      UUID id = UUID.randomUUID();

      PessoaJuridicaUpdateDTO dtoAtualizacao = new PessoaJuridicaUpdateDTO(
         "Nome Novo LTDA",
         "novo@silva.com",
         "1140028900",
         "Endereco novo"
      );

      when(repository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.atualizarPessoaJuridicaViaId(dtoAtualizacao, id.toString()))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Cliente não econtrado.");

      verify(repository, never()).save(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: atualizarPessoaJuridicaViaCnpj
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("atualizarPessoaJuridicaViaCnpj deve atualizar e retornar o DTO quando o CNPJ existe")
   void atualizarPessoaJuridicaViaCnpj_CnpjExistente_atualizaERetorna() {
      String cnpj = "1140028900";

      PessoaJuridica entidadeExistente = new PessoaJuridica(
         "Nome Antigo LTDA",
         "antigo@silva.com",
         "1140028922",
         "Endereco antigo",
         cnpj
      );
      PessoaJuridicaUpdateDTO dtoAtualizacao = new PessoaJuridicaUpdateDTO(
         "Nome Novo LTDA",
         "novo@silva.com",
         "1140028900",
         "Endereco novo"
      );
      PessoaJuridicaResponseDTO responseEsperado = new PessoaJuridicaResponseDTO(
         entidadeExistente.getIdClienteq(),
         entidadeExistente.getNome(),
         entidadeExistente.getEmail(),
         entidadeExistente.getTelefone(),
         entidadeExistente.getEndereco(),
         entidadeExistente.getCnpj(),
         entidadeExistente.getCreatedAt()
      );

      when(repository.findByCnpj(cnpj)).thenReturn(Optional.of(entidadeExistente));
      when(mapper.mapearParaResponse(entidadeExistente)).thenReturn(responseEsperado);

      PessoaJuridicaResponseDTO response = service.atualizarPessoaJuridicaViaCnpj(dtoAtualizacao, cnpj);

      assertThat(response).isEqualTo(responseEsperado);
   }

   @Test
   @DisplayName("atualizarPessoaJuridicaViaCnpj deve lançar EntidadeNaoEncontradaException quando o CNPJ não existe")
   void atualizarPessoaJuridicaViaCnpj_cnpjInexistente_lancaExcecao() {
      String cnpj = "1140028900";

      PessoaJuridicaUpdateDTO dto = new PessoaJuridicaUpdateDTO(
         "Novo nome Locadora Silva LTDA",
         "novolocadorasilva@email.com",
         "00912345678",
         "Novo Endereço"
      );

      when(repository.findByCnpj(cnpj)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.atualizarPessoaJuridicaViaCnpj(dto, cnpj))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Cliente não econtrado.");

      verify(repository, never()).save(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: deletarPessoaJuridicaViaId
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("deletarPessoaJuridicaViaId deve deletar quando o ID existe")
   void deletarPessoaJuridicaViaId_idExistente_deleta() {
      UUID id = UUID.randomUUID();

      PessoaJuridica entidadeExistente = new PessoaJuridica(
         "Locadora Silva LTDA",
         "contato@silva.com",
         "1140028922",
         "Av. Central, 500",
         "12345678000199"
      );

      when(repository.findById(id)).thenReturn(Optional.of(entidadeExistente));

      service.deletarPessoaJuridicaViaId(id.toString());

      verify(repository, times(1)).delete(entidadeExistente);
   }

   @Test
   @DisplayName("deletarPessoaJuridicaViaId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
   void deletarPessoaJuridicaViaId_idInexistente_lancaExcecao() {
      UUID id = UUID.randomUUID();

      when(repository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.deletarPessoaJuridicaViaId(id.toString()))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Cliente não encontrado.");

      verify(repository, never()).delete(any());
   }
}
