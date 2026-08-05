package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.CategoriaVeiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Manutencao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.StatusVeiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.StatusInvalidoException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.ManutencaoMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.ManutencaoRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.VeiculoRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManutencaoServiceTest {

   @Mock
   private ManutencaoRepository repository;

   @Mock
   private ManutencaoMapper mapper;

   @Mock
   private VeiculoRepository veiculoRepository;

   @InjectMocks
   private ManutencaoService service;

   // Método auxiliar: monta um Veiculo válido com o status que o cenário do teste precisa
   private Veiculo criarVeiculo(StatusVeiculo status) {
      Veiculo veiculo = new Veiculo(
         "9BWZZZ377VT004251", "ABC1234", "00987654321", "Celta", "Chevrolet",
         2012, "prata", CategoriaVeiculo.HATCH, 10000.00, new FilialLocadora()
      );
      veiculo.setStatusVeiculo(status);
      return veiculo;
   }

   // Método auxiliar: monta uma Manutencao válida, vinculada ao veículo recebido
   private Manutencao criarManutencaoEntidade(Veiculo veiculo) {
      return new Manutencao(veiculo, LocalDateTime.of(2026, 3, 1, 9, 0), "Troca de óleo", BigDecimal.valueOf(250.00));
   }

   // ---------------------------------------------------------------------
   // MÉTODO: criarManutencao
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("criarManutencao deve criar a manutenção, bloquear o veículo e salvar com sucesso.")
   void criarManutencao_veiculoDisponivel_retornaResponseDTO() {
      UUID veiculoId = UUID.randomUUID();
      Veiculo veiculo = criarVeiculo(StatusVeiculo.DISPONIVEL);

      ManutencaoCreateDTO dto = new ManutencaoCreateDTO(
         veiculoId.toString(), LocalDateTime.of(2026, 3, 1, 9, 0), "Troca de óleo", BigDecimal.valueOf(250.00)
      );

      Manutencao manutencaoMapeada = new Manutencao(veiculo, dto.dataManutencao(), dto.descricao(), dto.valor());

      ManutencaoResponseDTO responseEsperado = new ManutencaoResponseDTO(
         manutencaoMapeada.getId(), veiculo, dto.dataManutencao(), dto.descricao(),
         manutencaoMapeada.getQuilimetragemVeiculo(), dto.valor()
      );

      when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
      when(mapper.mapearParaManutencao(dto, veiculo)).thenReturn(manutencaoMapeada);
      when(repository.save(manutencaoMapeada)).thenReturn(manutencaoMapeada);
      when(mapper.mapearParaResponse(manutencaoMapeada)).thenReturn(responseEsperado);

      ManutencaoResponseDTO response = service.criarManutencao(dto);

      assertThat(response).isEqualTo(responseEsperado);

      // O veículo precisa ficar bloqueado (EM_MANUTENCAO) pra não ser reservado enquanto está na oficina
      assertThat(veiculo.getStatusVeiculo()).isEqualTo(StatusVeiculo.EM_MANUTENCAO);
      verify(veiculoRepository, times(1)).save(veiculo);
      verify(repository, times(1)).save(manutencaoMapeada);
   }

   @Test
   @DisplayName("criarManutencao deve lançar exception quando o veículo não é encontrado.")
   void criarManutencao_veiculoNaoEncontrado_lancaExcecao() {
      UUID veiculoId = UUID.randomUUID();
      ManutencaoCreateDTO dto = new ManutencaoCreateDTO(
         veiculoId.toString(), LocalDateTime.of(2026, 3, 1, 9, 0), "Troca de óleo", BigDecimal.valueOf(250.00)
      );

      when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.criarManutencao(dto))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Veículo não encontrado.");

      verify(repository, never()).save(any());
   }

   @Test
   @DisplayName("criarManutencao deve lançar exception quando o veículo já está locado.")
   void criarManutencao_veiculoLocado_lancaExcecao() {
      UUID veiculoId = UUID.randomUUID();
      Veiculo veiculo = criarVeiculo(StatusVeiculo.LOCADO);
      ManutencaoCreateDTO dto = new ManutencaoCreateDTO(
         veiculoId.toString(), LocalDateTime.of(2026, 3, 1, 9, 0), "Troca de óleo", BigDecimal.valueOf(250.00)
      );
      Manutencao manutencaoMapeada = new Manutencao(veiculo, dto.dataManutencao(), dto.descricao(), dto.valor());

      when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
      when(mapper.mapearParaManutencao(dto, veiculo)).thenReturn(manutencaoMapeada);

      assertThatThrownBy(() -> service.criarManutencao(dto))
         .isInstanceOf(StatusInvalidoException.class)
         .hasMessage("O veículo está locado no momento.");

      verify(veiculoRepository, never()).save(any());
      verify(repository, never()).save(any());
   }

   @Test
   @DisplayName("criarManutencao deve lançar exception quando o veículo já está em manutenção.")
   void criarManutencao_veiculoJaEmManutencao_lancaExcecao() {
      UUID veiculoId = UUID.randomUUID();
      Veiculo veiculo = criarVeiculo(StatusVeiculo.EM_MANUTENCAO);
      ManutencaoCreateDTO dto = new ManutencaoCreateDTO(
         veiculoId.toString(), LocalDateTime.of(2026, 3, 1, 9, 0), "Troca de óleo", BigDecimal.valueOf(250.00)
      );
      Manutencao manutencaoMapeada = new Manutencao(veiculo, dto.dataManutencao(), dto.descricao(), dto.valor());

      when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
      when(mapper.mapearParaManutencao(dto, veiculo)).thenReturn(manutencaoMapeada);

      assertThatThrownBy(() -> service.criarManutencao(dto))
         .isInstanceOf(StatusInvalidoException.class)
         .hasMessage("O veículo já está em manutenção.");

      verify(veiculoRepository, never()).save(any());
      verify(repository, never()).save(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: buscarManutencaoPorId
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("buscarManutencaoPorId deve retornar a manutenção quando encontrada.")
   void buscarManutencaoPorId_sucesso_retornaResponseDTO() {
      UUID id = UUID.randomUUID();
      Veiculo veiculo = criarVeiculo(StatusVeiculo.EM_MANUTENCAO);
      Manutencao manutencao = criarManutencaoEntidade(veiculo);
      ManutencaoResponseDTO responseEsperado = new ManutencaoResponseDTO(
         manutencao.getId(), veiculo, manutencao.getDataManutencao(), manutencao.getDescricao(),
         manutencao.getQuilimetragemVeiculo(), manutencao.getValor()
      );

      when(repository.findById(id)).thenReturn(Optional.of(manutencao));
      when(mapper.mapearParaResponse(manutencao)).thenReturn(responseEsperado);

      ManutencaoResponseDTO response = service.buscarManutencaoPorId(id.toString());

      assertThat(response).isEqualTo(responseEsperado);
   }

   @Test
   @DisplayName("buscarManutencaoPorId deve lançar exception quando a manutenção não é encontrada.")
   void buscarManutencaoPorId_naoEncontrado_lancaExcecao() {
      UUID id = UUID.randomUUID();

      when(repository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarManutencaoPorId(id.toString()))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Manutenção não encontrada.");
   }

   // ---------------------------------------------------------------------
   // MÉTODO: buscarTodosPaginado
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("buscarTodosPaginado deve retornar a página de manutenções mapeada.")
   void buscarTodosPaginado_retornaPageDeResponseDTO() {
      Veiculo veiculo = criarVeiculo(StatusVeiculo.EM_MANUTENCAO);
      Manutencao manutencao = criarManutencaoEntidade(veiculo);
      ManutencaoResponseDTO responseEsperado = new ManutencaoResponseDTO(
         manutencao.getId(), veiculo, manutencao.getDataManutencao(), manutencao.getDescricao(),
         manutencao.getQuilimetragemVeiculo(), manutencao.getValor()
      );
      Pageable pageable = PageRequest.of(0, 10, Sort.by("dataManutencao").descending());
      Page<Manutencao> paginaManutencoes = new PageImpl<>(List.of(manutencao), pageable, 1);

      when(repository.findAll(pageable)).thenReturn(paginaManutencoes);
      when(mapper.mapearParaResponse(manutencao)).thenReturn(responseEsperado);

      Page<ManutencaoResponseDTO> resultado = service.buscarTodosPaginado();

      assertThat(resultado.getContent()).containsExactly(responseEsperado);
   }

   // ---------------------------------------------------------------------
   // MÉTODO: atualizarManutencaoPorId
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("atualizarManutencaoPorId deve atualizar e retornar a manutenção com sucesso.")
   void atualizarManutencaoPorId_sucesso_retornaResponseDTO() {
      UUID id = UUID.randomUUID();
      Veiculo veiculo = criarVeiculo(StatusVeiculo.EM_MANUTENCAO);
      Manutencao manutencao = criarManutencaoEntidade(veiculo);
      ManutencaoUpdateDTO dto = new ManutencaoUpdateDTO(
         LocalDateTime.of(2026, 3, 2, 9, 0), "Troca de óleo e filtro", BigDecimal.valueOf(320.00)
      );
      ManutencaoResponseDTO responseEsperado = new ManutencaoResponseDTO(
         manutencao.getId(), veiculo, dto.dataManutencao(), dto.descricao(),
         manutencao.getQuilimetragemVeiculo(), dto.valor()
      );

      when(repository.findById(id)).thenReturn(Optional.of(manutencao));
      when(mapper.mapearParaResponse(manutencao)).thenReturn(responseEsperado);

      ManutencaoResponseDTO response = service.atualizarManutencaoPorId(dto, id.toString());

      assertThat(response).isEqualTo(responseEsperado);
      // Confirma que os atributos editáveis foram de fato sobrescritos na entidade antes do save
      assertThat(manutencao.getDataManutencao()).isEqualTo(dto.dataManutencao());
      assertThat(manutencao.getDescricao()).isEqualTo(dto.descricao());
      assertThat(manutencao.getValor()).isEqualByComparingTo(dto.valor());
      verify(repository, times(1)).save(manutencao);
   }

   @Test
   @DisplayName("atualizarManutencaoPorId deve lançar exception quando a manutenção não é encontrada.")
   void atualizarManutencaoPorId_naoEncontrado_lancaExcecao() {
      UUID id = UUID.randomUUID();
      ManutencaoUpdateDTO dto = new ManutencaoUpdateDTO(
         LocalDateTime.of(2026, 3, 2, 9, 0), "Troca de óleo e filtro", BigDecimal.valueOf(320.00)
      );

      when(repository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.atualizarManutencaoPorId(dto, id.toString()))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Manutenção não encontrada.");

      verify(repository, never()).save(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: deletarManutencaoPorId
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("deletarManutencaoPorId deve deletar a manutenção com sucesso.")
   void deletarManutencaoPorId_sucesso() {
      UUID id = UUID.randomUUID();
      Veiculo veiculo = criarVeiculo(StatusVeiculo.EM_MANUTENCAO);
      Manutencao manutencao = criarManutencaoEntidade(veiculo);

      when(repository.findById(id)).thenReturn(Optional.of(manutencao));

      service.deletarManutencaoPorId(id.toString());

      verify(repository, times(1)).delete(manutencao);
   }

   @Test
   @DisplayName("deletarManutencaoPorId deve lançar exception quando a manutenção não é encontrada.")
   void deletarManutencaoPorId_naoEncontrado_lancaExcecao() {
      UUID id = UUID.randomUUID();

      when(repository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.deletarManutencaoPorId(id.toString()))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Manutenção não encontrada.");

      verify(repository, never()).delete(any());
   }
}
