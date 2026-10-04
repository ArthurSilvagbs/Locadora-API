package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaJuridica;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.ClienteSemLocacoesRegistradasException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.LocacaoMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.PessoaJuridicaMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.PessoaJuridicaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PessoaJuridicaConsultasTest {
   @Mock PessoaJuridicaRepository repository;
   @Mock PessoaJuridicaMapper mapper;
   @Mock LocacaoMapper locacaoMapper;
   @InjectMocks PessoaJuridicaService service;

   @Test
   void buscarTodosPaginado_mapeiaPrimeiraPaginaOrdenadaPorCnpj() {
      PessoaJuridica cliente = mock(PessoaJuridica.class);
      PessoaJuridicaResponseDTO dto = mock(PessoaJuridicaResponseDTO.class);
      when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(cliente)));
      when(mapper.mapearParaResponse(cliente)).thenReturn(dto);

      assertThat(service.buscarTodosPaginado().getContent()).containsExactly(dto);
      ArgumentCaptor<Pageable> pagina = ArgumentCaptor.forClass(Pageable.class);
      verify(repository).findAll(pagina.capture());
      assertThat(pagina.getValue().getPageNumber()).isZero();
      assertThat(pagina.getValue().getPageSize()).isEqualTo(10);
      assertThat(pagina.getValue().getSort().getOrderFor("cnpj").isAscending()).isTrue();
   }

   @Test
   void buscarTodosPaginado_semClientes_retornaPaginaVazia() {
      when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());
      assertThat(service.buscarTodosPaginado()).isEmpty();
      verify(mapper, never()).mapearParaResponse(any());
   }

   @Test
   void buscarLocacoesClientePorId_clienteComLocacoes_retornaPaginaMapeada() {
      UUID id = UUID.randomUUID();
      Locacao locacao = mock(Locacao.class);
      LocacaoResponseDTO dto = mock(LocacaoResponseDTO.class);
      when(repository.existsById(id)).thenReturn(true);
      when(repository.buscarLocacoesPorId(eq(id), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(locacao)));
      when(locacaoMapper.mapearParaResponse(locacao)).thenReturn(dto);

      assertThat(service.buscarLocacoesClientePorId(id.toString()).getContent()).containsExactly(dto);
      ArgumentCaptor<Pageable> pagina = ArgumentCaptor.forClass(Pageable.class);
      verify(repository).buscarLocacoesPorId(eq(id), pagina.capture());
      assertThat(pagina.getValue().getSort().getOrderFor("dataRetirada").isDescending()).isTrue();
   }

   @Test
   void buscarLocacoesClientePorId_clienteAusente_lancaExcecao() {
      UUID id = UUID.randomUUID();
      assertThatThrownBy(() -> service.buscarLocacoesClientePorId(id.toString()))
         .isInstanceOf(EntidadeNaoEncontradaException.class);
      verify(repository, never()).buscarLocacoesPorId(any(), any());
   }

   @Test
   void buscarLocacoesClientePorId_semLocacoes_lancaExcecao() {
      UUID id = UUID.randomUUID();
      when(repository.existsById(id)).thenReturn(true);
      when(repository.buscarLocacoesPorId(eq(id), any(Pageable.class))).thenReturn(Page.empty());
      assertThatThrownBy(() -> service.buscarLocacoesClientePorId(id.toString()))
         .isInstanceOf(ClienteSemLocacoesRegistradasException.class);
   }

   @Test
   void buscarLocacoesClientePorCnpj_clienteComLocacoes_retornaPaginaMapeada() {
      String cnpj = "12345678000199";
      Locacao locacao = mock(Locacao.class);
      LocacaoResponseDTO dto = mock(LocacaoResponseDTO.class);
      when(repository.existsByCnpj(cnpj)).thenReturn(true);
      when(repository.buscarLocacaoPorCnpj(eq(cnpj), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(locacao)));
      when(locacaoMapper.mapearParaResponse(locacao)).thenReturn(dto);

      assertThat(service.buscarLocacoesClientePorCnpj(cnpj).getContent()).containsExactly(dto);
      verify(repository).buscarLocacaoPorCnpj(eq(cnpj), any(Pageable.class));
   }

   @Test
   void buscarLocacoesClientePorCnpj_clienteAusente_lancaExcecao() {
      String cnpj = "12345678000199";
      assertThatThrownBy(() -> service.buscarLocacoesClientePorCnpj(cnpj))
         .isInstanceOf(EntidadeNaoEncontradaException.class);
      verify(repository, never()).buscarLocacaoPorCnpj(any(), any());
   }

   @Test
   void buscarLocacoesClientePorCnpj_semLocacoes_lancaExcecao() {
      String cnpj = "12345678000199";
      when(repository.existsByCnpj(cnpj)).thenReturn(true);
      when(repository.buscarLocacaoPorCnpj(eq(cnpj), any(Pageable.class))).thenReturn(Page.empty());
      assertThatThrownBy(() -> service.buscarLocacoesClientePorCnpj(cnpj))
         .isInstanceOf(ClienteSemLocacoesRegistradasException.class);
   }
}
