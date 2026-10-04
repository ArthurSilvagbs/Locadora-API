package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaFisica;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.ClienteSemLocacoesRegistradasException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.LocacaoMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.PessoaFisicaMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.PessoaFisicaRepository;
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
class PessoaFisicaConsultasTest {
   @Mock PessoaFisicaRepository repository;
   @Mock PessoaFisicaMapper mapper;
   @Mock LocacaoMapper locacaoMapper;
   @InjectMocks PessoaFisicaService service;

   @Test
   void buscarTodosPaginado_mapeiaPrimeiraPaginaOrdenadaPorCpf() {
      PessoaFisica cliente = mock(PessoaFisica.class);
      PessoaFisicaResponseDTO dto = mock(PessoaFisicaResponseDTO.class);
      when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(cliente)));
      when(mapper.mapearParaResponse(cliente)).thenReturn(dto);

      Page<PessoaFisicaResponseDTO> resultado = service.buscarTodosPaginado();

      assertThat(resultado.getContent()).containsExactly(dto);
      ArgumentCaptor<Pageable> pagina = ArgumentCaptor.forClass(Pageable.class);
      verify(repository).findAll(pagina.capture());
      assertThat(pagina.getValue().getPageNumber()).isZero();
      assertThat(pagina.getValue().getPageSize()).isEqualTo(10);
      assertThat(pagina.getValue().getSort().getOrderFor("cpf").isAscending()).isTrue();
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
      assertThat(pagina.getValue().getPageSize()).isEqualTo(10);
      assertThat(pagina.getValue().getSort().getOrderFor("dataRetirada").isDescending()).isTrue();
   }

   @Test
   void buscarLocacoesClientePorId_clienteAusente_lancaExcecaoSemConsultarLocacoes() {
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
   void buscarLocacoesClientePorCpf_clienteComLocacoes_retornaPaginaMapeada() {
      String cpf = "52998224725";
      Locacao locacao = mock(Locacao.class);
      LocacaoResponseDTO dto = mock(LocacaoResponseDTO.class);
      when(repository.existsByCpf(cpf)).thenReturn(true);
      when(repository.buscarLocacoesPorCpf(eq(cpf), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(locacao)));
      when(locacaoMapper.mapearParaResponse(locacao)).thenReturn(dto);

      assertThat(service.buscarLocacoesClientePorCpf(cpf).getContent()).containsExactly(dto);
      verify(repository).buscarLocacoesPorCpf(eq(cpf), any(Pageable.class));
   }

   @Test
   void buscarLocacoesClientePorCpf_clienteAusente_lancaExcecaoSemConsultarLocacoes() {
      String cpf = "52998224725";
      assertThatThrownBy(() -> service.buscarLocacoesClientePorCpf(cpf))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Cliente não encontrado.");
      verify(repository, never()).buscarLocacoesPorCpf(any(), any());
   }

   @Test
   void buscarLocacoesClientePorCpf_semLocacoes_lancaExcecao() {
      String cpf = "52998224725";
      when(repository.existsByCpf(cpf)).thenReturn(true);
      when(repository.buscarLocacoesPorCpf(eq(cpf), any(Pageable.class))).thenReturn(Page.empty());

      assertThatThrownBy(() -> service.buscarLocacoesClientePorCpf(cpf))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Nenhuma locação deste cliente encontrada.");
   }
}
