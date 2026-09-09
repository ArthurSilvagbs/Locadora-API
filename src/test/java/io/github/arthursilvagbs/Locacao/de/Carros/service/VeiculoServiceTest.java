package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.CategoriaVeiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.VeiculoMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.FilialLocadoraRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VeiculoServiceTest {

   @Mock
   private VeiculoRepository repository;

   @Mock
   private FilialLocadoraRepository filialLocadoraRepository;

   @Mock
   private VeiculoMapper mapper;

   @InjectMocks
   private VeiculoService service;

   // Método auxiliar: monta um Veiculo válido, reutilizado nos testes de busca/atualização/deleção
   private Veiculo criarVeiculo() {
      return new Veiculo(
         "9BWZZZ377VT004251", "ABC1234", "00987654321", "Celta", "Chevrolet",
         2012, "prata", CategoriaVeiculo.HATCH, 10000.00, new FilialLocadora()
      );
   }

   // Método auxiliar: monta o VeiculoResponseDTO correspondente a um Veiculo, refletindo o estado atual dele
   private VeiculoResponseDTO responseDe(Veiculo veiculo) {
      return new VeiculoResponseDTO(
         veiculo.getIdVeiculo(), veiculo.getNumeroChassi(), veiculo.getPlacaVeiculo(), veiculo.getRenavam(),
         veiculo.getModelo(), veiculo.getMarca(), veiculo.getAno(), veiculo.getCor(),
         veiculo.getCategoriaVeiculo(), veiculo.getQuilometragem(), veiculo.getCreatedAt()
      );
   }

   @Test
   @DisplayName("cadastrarVeiculo deve cadastrar Veiculo com sucesso.")
   void cadatrarVeiculo_sucesso_retornaResponseDTO() {
      String numChassi = "9BWZZZ377VT004251";
      String placaVeiculo = "ABC1234";
      String renavan = "00987654321";
      UUID idFilialLocadora = UUID.randomUUID();

      FilialLocadora filialLocadora = new FilialLocadora();

      Veiculo entidade = new Veiculo(
         numChassi,
         placaVeiculo,
         renavan,
         "Celta",
         "Chevrolet",
         2012,
         "prata",
         CategoriaVeiculo.HATCH,
         10000.00,
         filialLocadora
      );

      VeiculoCreateDTO dto = new VeiculoCreateDTO(
         numChassi,
         placaVeiculo,
         renavan,
         "Celta",
         "Chevrolet",
         2012,
         "prata",
         CategoriaVeiculo.HATCH,
         10000.00,
         idFilialLocadora.toString()
      );

      VeiculoResponseDTO responseEsperado = new VeiculoResponseDTO(
         entidade.getIdVeiculo(),
         entidade.getNumeroChassi(),
         entidade.getPlacaVeiculo(),
         entidade.getRenavam(),
         entidade.getModelo(),
         entidade.getMarca(),
         entidade.getAno(),
         entidade.getCor(),
         entidade.getCategoriaVeiculo(),
         entidade.getQuilometragem(),
         entidade.getCreatedAt()
      );

      when(repository.existsByNumeroChassi(numChassi)).thenReturn(false);
      when(repository.existsByPlacaVeiculo(placaVeiculo)).thenReturn(false);
      when(repository.existsByRenavam(renavan)).thenReturn(false);
      when(filialLocadoraRepository.findById(idFilialLocadora)).thenReturn(Optional.of(filialLocadora));
      when(mapper.mapearParaVeiculo(dto, filialLocadora)).thenReturn(entidade);
      when(mapper.mapearParaResponse(entidade)).thenReturn(responseEsperado);

      VeiculoResponseDTO response = service.cadastrarVeiculo(dto);

      assertThat(response).isEqualTo(responseEsperado);

      verify(repository, times(1)).save(entidade);
   }

   @Test
   @DisplayName("cadastrarVeiculo deve retornar exception RegistroDuplicadoException na verificação de duplicidade do numChassi.")
   void cadastrarVeiculo_exception_NumChassiDuplicado() {
      String numChassi = "9BWZZZ377VT004251";
      String placa = "ABC1234";

      VeiculoCreateDTO dto = new VeiculoCreateDTO(
         numChassi,
         placa,
         "00987654321",
         "Celta",
         "Chevrolet",
         2012,
         "prata",
         CategoriaVeiculo.HATCH,
         10000.00,
         UUID.randomUUID().toString()
      );

      when(repository.existsByNumeroChassi(numChassi)).thenReturn(true);

      assertThatThrownBy(() -> service.cadastrarVeiculo(dto))
         .isInstanceOf(RegistroDuplicadoException.class)
         .hasMessage("Número do chassi já existente no sistema.");

      verify(repository, never()).existsByPlacaVeiculo(placa);
   }

   @Test
   @DisplayName("cadastrarVeiculo deve retornar exception RegistroDuplicadoException na verificação de duplicidade da placa do veículo.")
   void cadastrarVeiculo_exception_PlacaDuplicada() {
      String numChassi = "9BWZZZ377VT004251";
      String placa = "ABC1234";
      String renavam = "00987654321";

      VeiculoCreateDTO dto = new VeiculoCreateDTO(
         numChassi,
         placa,
         renavam,
         "Celta",
         "Chevrolet",
         2012,
         "prata",
         CategoriaVeiculo.HATCH,
         10000.00,
         UUID.randomUUID().toString()
      );

      when(repository.existsByNumeroChassi(numChassi)).thenReturn(false);
      when(repository.existsByPlacaVeiculo(placa)).thenReturn(true);

      assertThatThrownBy(() -> service.cadastrarVeiculo(dto))
         .isInstanceOf(RegistroDuplicadoException.class)
         .hasMessage("Placa do veículo já existente no sistema.");

      verify(repository, never()).existsByRenavam(placa);
   }

   @Test
   @DisplayName("cadastrarVeiculo deve retornar exception RegistroDuplicadoException na verificação de duplicidade do Renavam.")
   void cadastrarVeiculo_exception_RenavamDuplicado() {
      String numChassi = "9BWZZZ377VT004251";
      String placa = "ABC1234";
      String renavam = "00987654321";
      UUID filialLocadoraId = UUID.randomUUID();

      VeiculoCreateDTO dto = new VeiculoCreateDTO(
         numChassi,
         placa,
         renavam,
         "Celta",
         "Chevrolet",
         2012,
         "prata",
         CategoriaVeiculo.HATCH,
         10000.00,
         UUID.randomUUID().toString()
      );

      when(repository.existsByNumeroChassi(numChassi)).thenReturn(false);
      when(repository.existsByPlacaVeiculo(placa)).thenReturn(false);
      when(repository.existsByRenavam(renavam)).thenReturn(true);

      assertThatThrownBy(() -> service.cadastrarVeiculo(dto))
         .isInstanceOf(RegistroDuplicadoException.class)
         .hasMessage("Número do Renavam já existente no sistema.");

      verify(filialLocadoraRepository, never()).findById(filialLocadoraId);
   }

   @Test
   @DisplayName("cadastrarVeiculo deve retornar exception para filial não encontrada.")
   void cadastrarVeiculo_exception_FilialNaoEcontrada() {
      String numChassi = "9BWZZZ377VT004251";
      String placa = "ABC1234";
      String renavam = "00987654321";
      UUID filialLocadoraId = UUID.randomUUID();

      VeiculoCreateDTO dto = new VeiculoCreateDTO(
         numChassi,
         placa,
         renavam,
         "Celta",
         "Chevrolet",
         2012,
         "prata",
         CategoriaVeiculo.HATCH,
         10000.00,
         filialLocadoraId.toString()
      );

      when(repository.existsByNumeroChassi(numChassi)).thenReturn(false);
      when(repository.existsByPlacaVeiculo(placa)).thenReturn(false);
      when(repository.existsByRenavam(renavam)).thenReturn(false);
      when(filialLocadoraRepository.findById(filialLocadoraId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.cadastrarVeiculo(dto))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Filial não encontrada");
   }

   // ---------------------------------------------------------------------
   // MÉTODO: buscarVeiculoPorId
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("buscarVeiculoPorId deve retornar o veículo quando encontrado.")
   void buscarVeiculoPorId_sucesso_retornaResponseDTO() {
      UUID id = UUID.randomUUID();
      Veiculo veiculo = criarVeiculo();
      VeiculoResponseDTO responseEsperado = responseDe(veiculo);

      when(repository.findById(id)).thenReturn(Optional.of(veiculo));
      when(mapper.mapearParaResponse(veiculo)).thenReturn(responseEsperado);

      VeiculoResponseDTO response = service.buscarVeiculoPorId(id.toString());

      assertThat(response).isEqualTo(responseEsperado);
   }

   @Test
   @DisplayName("buscarVeiculoPorId deve lançar exception quando o veículo não é encontrado.")
   void buscarVeiculoPorId_naoEncontrado_lancaExcecao() {
      UUID id = UUID.randomUUID();

      when(repository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarVeiculoPorId(id.toString()))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Veículo não encontrado.");
   }

   // ---------------------------------------------------------------------
   // MÉTODO: buscarVeiculoPorNumChassi
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("buscarVeiculoPorNumChassi deve retornar o veículo quando encontrado.")
   void buscarVeiculoPorNumChassi_sucesso_retornaResponseDTO() {
      String numChassi = "9BWZZZ377VT004251";
      Veiculo veiculo = criarVeiculo();
      VeiculoResponseDTO responseEsperado = responseDe(veiculo);

      when(repository.findByNumeroChassi(numChassi)).thenReturn(Optional.of(veiculo));
      when(mapper.mapearParaResponse(veiculo)).thenReturn(responseEsperado);

      VeiculoResponseDTO response = service.buscarVeiculoPorNumChassi(numChassi);

      assertThat(response).isEqualTo(responseEsperado);
   }

   @Test
   @DisplayName("buscarVeiculoPorNumChassi deve lançar exception quando o veículo não é encontrado.")
   void buscarVeiculoPorNumChassi_naoEncontrado_lancaExcecao() {
      String numChassi = "9BWZZZ377VT004251";

      when(repository.findByNumeroChassi(numChassi)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarVeiculoPorNumChassi(numChassi))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Veículo não encontrado.");
   }

   // ---------------------------------------------------------------------
   // MÉTODO: buscarVeiculoPorPlaca
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("buscarVeiculoPorPlaca deve retornar o veículo quando encontrado.")
   void buscarVeiculoPorPlaca_sucesso_retornaResponseDTO() {
      String placa = "ABC1234";
      Veiculo veiculo = criarVeiculo();
      VeiculoResponseDTO responseEsperado = responseDe(veiculo);

      when(repository.findByPlacaVeiculo(placa)).thenReturn(Optional.of(veiculo));
      when(mapper.mapearParaResponse(veiculo)).thenReturn(responseEsperado);

      VeiculoResponseDTO response = service.buscarVeiculoPorPlaca(placa);

      assertThat(response).isEqualTo(responseEsperado);
   }

   @Test
   @DisplayName("buscarVeiculoPorPlaca deve lançar exception quando o veículo não é encontrado.")
   void buscarVeiculoPorPlaca_naoEncontrado_lancaExcecao() {
      String placa = "ABC1234";

      when(repository.findByPlacaVeiculo(placa)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarVeiculoPorPlaca(placa))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Veículo não encontrado.");
   }

   // ---------------------------------------------------------------------
   // MÉTODO: buscarVeiculoPorRenavam
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("buscarVeiculoPorRenavam deve retornar o veículo quando encontrado.")
   void buscarVeiculoPorRenavam_sucesso_retornaResponseDTO() {
      String renavam = "00987654321";
      Veiculo veiculo = criarVeiculo();
      VeiculoResponseDTO responseEsperado = responseDe(veiculo);

      when(repository.findByRenavam(renavam)).thenReturn(Optional.of(veiculo));
      when(mapper.mapearParaResponse(veiculo)).thenReturn(responseEsperado);

      VeiculoResponseDTO response = service.buscarVeiculoPorRenavam(renavam);

      assertThat(response).isEqualTo(responseEsperado);
   }

   @Test
   @DisplayName("buscarVeiculoPorRenavam deve lançar exception quando o veículo não é encontrado.")
   void buscarVeiculoPorRenavam_naoEncontrado_lancaExcecao() {
      String renavam = "00987654321";

      when(repository.findByRenavam(renavam)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarVeiculoPorRenavam(renavam))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Veículo não encontrado");
   }

   // ---------------------------------------------------------------------
   // MÉTODO: buscarTodosPaginado
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("buscarTodosPaginado deve retornar a página de veículos mapeada.")
   void buscarTodosPaginado_retornaPageDeResponseDTO() {
      Veiculo veiculo = criarVeiculo();
      VeiculoResponseDTO responseEsperado = responseDe(veiculo);
      Pageable pageable = PageRequest.of(0, 10, Sort.by("marca"));
      Page<Veiculo> paginaVeiculos = new PageImpl<>(List.of(veiculo), pageable, 1);

      when(repository.findAll(pageable)).thenReturn(paginaVeiculos);
      when(mapper.mapearParaResponse(veiculo)).thenReturn(responseEsperado);

      Page<VeiculoResponseDTO> resultado = service.buscarTodosPaginado();

      assertThat(resultado.getContent()).containsExactly(responseEsperado);
   }

   // ---------------------------------------------------------------------
   // MÉTODO: atualizarVeiculoPorId
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("atualizarVeiculoPorId deve atualizar e retornar o veículo com sucesso.")
   void atualizarVeiculoPorId_sucesso_retornaResponseDTO() {
      UUID id = UUID.randomUUID();
      Veiculo veiculo = criarVeiculo();
      VeiculoUpdateDTO dto = new VeiculoUpdateDTO("XYZ9876", 20000.00, "preto");
      VeiculoResponseDTO responseEsperado = responseDe(veiculo);

      when(repository.findById(id)).thenReturn(Optional.of(veiculo));
      when(mapper.mapearParaResponse(veiculo)).thenReturn(responseEsperado);

      VeiculoResponseDTO response = service.atualizarVeiculoPorId(dto, id.toString());

      assertThat(response).isEqualTo(responseEsperado);
      // Confirma que os atributos editáveis foram de fato sobrescritos na entidade antes do save
      assertThat(veiculo.getPlacaVeiculo()).isEqualTo("XYZ9876");
      assertThat(veiculo.getQuilometragem()).isEqualTo(20000.00);
      assertThat(veiculo.getCor()).isEqualTo("preto");
      verify(repository, times(1)).save(veiculo);
   }

   @Test
   @DisplayName("atualizarVeiculoPorId deve lançar exception quando o veículo não é encontrado.")
   void atualizarVeiculoPorId_naoEncontrado_lancaExcecao() {
      UUID id = UUID.randomUUID();
      VeiculoUpdateDTO dto = new VeiculoUpdateDTO("XYZ9876", 20000.00, "preto");

      when(repository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.atualizarVeiculoPorId(dto, id.toString()))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Veículo não encontrado.");

      verify(repository, never()).save(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: atualizarVeiculoPorNumChassi
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("atualizarVeiculoPorNumChassi deve atualizar e retornar o veículo com sucesso.")
   void atualizarVeiculoPorNumChassi_sucesso_retornaResponseDTO() {
      String numChassi = "9BWZZZ377VT004251";
      Veiculo veiculo = criarVeiculo();
      VeiculoUpdateDTO dto = new VeiculoUpdateDTO("XYZ9876", 20000.00, "preto");
      VeiculoResponseDTO responseEsperado = responseDe(veiculo);

      when(repository.findByNumeroChassi(numChassi)).thenReturn(Optional.of(veiculo));
      when(mapper.mapearParaResponse(veiculo)).thenReturn(responseEsperado);

      VeiculoResponseDTO response = service.atualizarVeiculoPorNumChassi(dto, numChassi);

      assertThat(response).isEqualTo(responseEsperado);
      verify(repository, times(1)).save(veiculo);
   }

   @Test
   @DisplayName("atualizarVeiculoPorNumChassi deve lançar exception quando o veículo não é encontrado.")
   void atualizarVeiculoPorNumChassi_naoEncontrado_lancaExcecao() {
      String numChassi = "9BWZZZ377VT004251";
      VeiculoUpdateDTO dto = new VeiculoUpdateDTO("XYZ9876", 20000.00, "preto");

      when(repository.findByNumeroChassi(numChassi)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.atualizarVeiculoPorNumChassi(dto, numChassi))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Veículo não encontrado.");

      verify(repository, never()).save(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: atualizarVeiculoPorPlaca
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("atualizarVeiculoPorPlaca deve atualizar e retornar o veículo com sucesso.")
   void atualizarVeiculoPorPlaca_sucesso_retornaResponseDTO() {
      String placa = "ABC1234";
      Veiculo veiculo = criarVeiculo();
      VeiculoUpdateDTO dto = new VeiculoUpdateDTO("XYZ9876", 20000.00, "preto");
      VeiculoResponseDTO responseEsperado = responseDe(veiculo);

      when(repository.findByPlacaVeiculo(placa)).thenReturn(Optional.of(veiculo));
      when(mapper.mapearParaResponse(veiculo)).thenReturn(responseEsperado);

      VeiculoResponseDTO response = service.atualizarVeiculoPorPlaca(dto, placa);

      assertThat(response).isEqualTo(responseEsperado);
      verify(repository, times(1)).save(veiculo);
   }

   @Test
   @DisplayName("atualizarVeiculoPorPlaca deve lançar exception quando o veículo não é encontrado.")
   void atualizarVeiculoPorPlaca_naoEncontrado_lancaExcecao() {
      String placa = "ABC1234";
      VeiculoUpdateDTO dto = new VeiculoUpdateDTO("XYZ9876", 20000.00, "preto");

      when(repository.findByPlacaVeiculo(placa)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.atualizarVeiculoPorPlaca(dto, placa))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Veículo não encontrado.");

      verify(repository, never()).save(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: deletarVeiculoViaId
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("deletarVeiculoViaId deve deletar o veículo com sucesso.")
   void deletarVeiculoViaId_sucesso() {
      UUID id = UUID.randomUUID();
      Veiculo veiculo = criarVeiculo();

      when(repository.findById(id)).thenReturn(Optional.of(veiculo));

      service.deletarVeiculoViaId(id.toString());

      verify(repository, times(1)).delete(veiculo);
   }

   @Test
   @DisplayName("deletarVeiculoViaId deve lançar exception quando o veículo não é encontrado.")
   void deletarVeiculoViaId_naoEncontrado_lancaExcecao() {
      UUID id = UUID.randomUUID();

      when(repository.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.deletarVeiculoViaId(id.toString()))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Veículo não encontrado.");

      verify(repository, never()).delete(any());
   }

   // ---------------------------------------------------------------------
   // MÉTODO: deletarVeiculoViaNumChassi
   // ---------------------------------------------------------------------

   @Test
   @DisplayName("deletarVeiculoViaNumChassi deve deletar o veículo com sucesso.")
   void deletarVeiculoViaNumChassi_sucesso() {
      String numChassi = "9BWZZZ377VT004251";
      Veiculo veiculo = criarVeiculo();

      when(repository.findByNumeroChassi(numChassi)).thenReturn(Optional.of(veiculo));

      service.deletarVeiculoViaNumChassi(numChassi);

      verify(repository, times(1)).delete(veiculo);
   }

   @Test
   @DisplayName("deletarVeiculoViaNumChassi deve lançar exception quando o veículo não é encontrado.")
   void deletarVeiculoViaNumChassi_naoEncontrado_lancaExcecao() {
      String numChassi = "9BWZZZ377VT004251";

      when(repository.findByNumeroChassi(numChassi)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.deletarVeiculoViaNumChassi(numChassi))
         .isInstanceOf(EntidadeNaoEncontradaException.class)
         .hasMessage("Veículo não encontrado.");

      verify(repository, never()).delete(any());
   }
}
