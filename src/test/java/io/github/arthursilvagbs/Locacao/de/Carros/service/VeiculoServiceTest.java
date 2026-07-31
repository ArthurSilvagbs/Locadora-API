package io.github.arthursilvagbs.Locacao.de.Carros.service;

// DTO de entrada usado pra cadastrar um Veiculo
import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoCreateDTO;
// DTO de resposta devolvido pelo Service
import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoResponseDTO;
// DTO usado pra atualizar um Veiculo existente
import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoUpdateDTO;
// Enum de categoria do veículo, usado pra montar o DTO de criação
import io.github.arthursilvagbs.Locacao.de.Carros.entity.CategoriaVeiculo;
// Entidade da filial, necessária pra montar um Veiculo (todo veículo pertence a uma filial)
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
// Entidade JPA que representa um Veiculo no banco
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;
// Exception lançada quando um registro não é encontrado
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
// Exception lançada quando tentamos cadastrar chassi/placa/renavam já existentes
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
// Mapper que converte DTO <-> Entidade <-> DTO de resposta
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.VeiculoMapper;
// Repository da filial, usado pelo Service pra validar se a filial informada existe
import io.github.arthursilvagbs.Locacao.de.Carros.repository.FilialLocadoraRepository;
// Repository do veículo; será mockado, não usamos banco de verdade
import io.github.arthursilvagbs.Locacao.de.Carros.repository.VeiculoRepository;

// Anotações de teste do JUnit 5
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
// Liga o Mockito ao JUnit 5, habilitando @Mock e @InjectMocks
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// LocalDateTime usado no campo "ano" do veículo
import java.time.LocalDateTime;
// UUID pra gerar IDs falsos
import java.util.UUID;
// Optional representa "achei"/"não achei" no findById/findByX
import java.util.Optional;

// Métodos estáticos do AssertJ pras verificações
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
// Métodos estáticos do Mockito pra configurar mocks e verificar chamadas
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Liga o Mockito nesta classe: processa as anotações @Mock e @InjectMocks abaixo
@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    // Mock do repository de veículo: não bate no banco de verdade, nós decidimos as respostas
    @Mock
    private VeiculoRepository repository;

    // Mock do repository de filial: o Service usa ele pra validar a filialAtualId recebida no DTO
    @Mock
    private FilialLocadoraRepository filialLocadoraRepository;

    // Mock do mapper: isola o teste só na lógica do Service
    @Mock
    private VeiculoMapper mapper;

    // Instância REAL do Service, com os mocks acima injetados nos campos do construtor
    @InjectMocks
    private VeiculoService service;

    // ---------------------------------------------------------------------
    // MÉTODO: cadastrarVeiculo
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("cadastrarVeiculo deve salvar e retornar o DTO quando chassi, placa e renavam não existem")
    void cadastrarVeiculo_dadosValidos_retornaResponseDTO() {
        // ARRANGE: UUID da filial que o veículo vai pertencer
        UUID filialId = UUID.randomUUID();

        // DTO de entrada, como se fosse o corpo de uma requisição de cadastro de veículo
        VeiculoCreateDTO dto = new VeiculoCreateDTO(
                "9BWZZZ377VT004251",     // numeroChassi
                "ABC1D23",               // placaVeiculo
                "12345678901",           // renavam
                "Onix",                  // modelo
                "Chevrolet",             // marca
                LocalDateTime.of(2023, 1, 1, 0, 0), // ano
                "Prata",                 // cor
                CategoriaVeiculo.HATCH,  // categoriaVeiculo
                0.0,                       // quilometragem
                filialId.toString()      // filialAtualId
        );

        // Entidade de filial que o repository de filial "fingirá" ter encontrado
        FilialLocadora filial = new FilialLocadora(
                "Filial Centro", "12345678000199", "SP", "Sao Paulo", "Rua X, 1", "1140028922", "filial@email.com"
        );

        // Entidade que o mapper "fingirá" devolver ao montar o Veiculo a partir do dto + filial
        Veiculo entidadeMapeada = new Veiculo(
                dto.numeroChassi(), dto.placaVeiculo(), dto.renavam(), dto.modelo(), dto.marca(),
                dto.ano(), dto.cor(), dto.categoriaVeiculo(), dto.quilometragem(), filial
        );

        // DTO de resposta esperado após o mapeamento final
        VeiculoResponseDTO responseEsperado = new VeiculoResponseDTO(
                UUID.randomUUID(), dto.numeroChassi(), dto.placaVeiculo(), dto.renavam(), dto.modelo(),
                dto.marca(), dto.ano(), dto.cor(), dto.categoriaVeiculo(), dto.quilometragem(), null
        );

        // Ensina o mock: esse número de chassi ainda não existe no banco
        when(repository.existsByNumeroChassi(dto.numeroChassi())).thenReturn(false);

        // Ensina o mock: essa placa ainda não existe no banco
        when(repository.existsByPlacaVeiculo(dto.placaVeiculo())).thenReturn(false);

        // Ensina o mock: esse renavam ainda não existe no banco
        when(repository.existsByRenavam(dto.renavam())).thenReturn(false);

        // Ensina o mock: a filial informada no DTO existe e é essa que criamos
        when(filialLocadoraRepository.findById(filialId)).thenReturn(Optional.of(filial));

        // Ensina o mock: o mapper monta a entidade Veiculo a partir do dto e da filial encontrada
        when(mapper.mapearParaVeiculo(dto, filial)).thenReturn(entidadeMapeada);

        // Ensina o mock: o mapper transforma a entidade mapeada no response esperado
        when(mapper.mapearParaResponse(entidadeMapeada)).thenReturn(responseEsperado);

        // ACT: chama o método real do Service
        VeiculoResponseDTO resultado = service.cadastrarVeiculo(dto);

        // ASSERT: o resultado devolvido deve ser exatamente o response esperado
        assertThat(resultado).isEqualTo(responseEsperado);

        // Verifica que o save foi chamado exatamente uma vez com a entidade mapeada
        verify(repository, times(1)).save(entidadeMapeada);
    }

    @Test
    @DisplayName("cadastrarVeiculo deve lançar RegistroDuplicadoException quando o chassi já existe")
    void cadastrarVeiculo_chassiDuplicado_lancaExcecao() {
        // ARRANGE: DTO de entrada qualquer, o conteúdo não importa pra esse teste
        VeiculoCreateDTO dto = new VeiculoCreateDTO(
                "9BWZZZ377VT004251", "ABC1D23", "12345678901", "Onix", "Chevrolet",
                LocalDateTime.of(2023, 1, 1, 0, 0), "Prata", CategoriaVeiculo.HATCH, (double) 0, UUID.randomUUID().toString()
        );

        // Ensina o mock: esse chassi já existe, forçando o primeiro "if" a barrar o cadastro
        when(repository.existsByNumeroChassi(dto.numeroChassi())).thenReturn(true);

        // ACT + ASSERT: chama o método e confere a exception lançada
        assertThatThrownBy(() -> service.cadastrarVeiculo(dto))
                .isInstanceOf(RegistroDuplicadoException.class)
                .hasMessage("Número do chassi já existente no sistema.");

        // Confirma que as validações seguintes (placa, renavam) nunca rodaram
        verify(repository, never()).existsByPlacaVeiculo(anyString());
        verify(repository, never()).existsByRenavam(anyString());

        // Confirma que o save nunca foi chamado
        verify(repository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // MÉTODO: buscarVeiculoPorId
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("buscarVeiculoPorId deve retornar o DTO quando o ID existe")
    void buscarVeiculoPorId_idExistente_retornaResponseDTO() {
        // ARRANGE: UUID que vamos simular como existente no banco
        UUID id = UUID.randomUUID();

        // Filial usada apenas pra conseguir montar um Veiculo completo (é obrigatória no construtor)
        FilialLocadora filial = new FilialLocadora(
                "Filial Centro", "12345678000199", "SP", "Sao Paulo", "Rua X, 1", "1140028922", "filial@email.com"
        );

        // Entidade que o repository "encontrará" para esse ID
        Veiculo entidade = new Veiculo(
                "9BWZZZ377VT004251", "ABC1D23", "12345678901", "Onix", "Chevrolet",
                LocalDateTime.of(2023, 1, 1, 0, 0), "Prata", CategoriaVeiculo.HATCH, 0.0, filial
        );

        // DTO de resposta esperado após o mapeamento
        VeiculoResponseDTO responseEsperado = new VeiculoResponseDTO(
                id, "9BWZZZ377VT004251", "ABC1D23", "12345678901", "Onix", "Chevrolet",
                LocalDateTime.of(2023, 1, 1, 0, 0), "Prata", CategoriaVeiculo.HATCH, 0.0, null
        );

        // Ensina o mock: encontrou a entidade pra esse ID
        when(repository.findById(id)).thenReturn(Optional.of(entidade));

        // Ensina o mock: o mapper converte a entidade encontrada no response esperado
        when(mapper.mapearParaResponse(entidade)).thenReturn(responseEsperado);

        // ACT: chama o método passando o ID como String (Service faz UUID.fromString por dentro)
        VeiculoResponseDTO resultado = service.buscarVeiculoPorId(id.toString());

        // ASSERT: resultado deve bater com o response esperado
        assertThat(resultado).isEqualTo(responseEsperado);
    }

    @Test
    @DisplayName("buscarVeiculoPorId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
    void buscarVeiculoPorId_idInexistente_lancaExcecao() {
        // ARRANGE: UUID que vamos simular como inexistente
        UUID id = UUID.randomUUID();

        // Ensina o mock: não encontrou nada pra esse ID (Optional vazio)
        when(repository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT: o Service deve traduzir isso numa exception de negócio
        assertThatThrownBy(() -> service.buscarVeiculoPorId(id.toString()))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Veículo não encontrado.");

        // Confirma que o mapper nunca foi chamado, já que não havia entidade pra mapear
        verify(mapper, never()).mapearParaResponse(any());
    }

    // ---------------------------------------------------------------------
    // MÉTODO: atualizarVeiculoPorId
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("atualizarVeiculoPorId deve atualizar e retornar o DTO quando o ID existe")
    void atualizarVeiculoPorId_idExistente_atualizaERetorna() {
        // ARRANGE: ID que vamos simular como existente
        UUID id = UUID.randomUUID();

        // Filial obrigatória pra montar a entidade Veiculo
        FilialLocadora filial = new FilialLocadora(
                "Filial Centro", "12345678000199", "SP", "Sao Paulo", "Rua X, 1", "1140028922", "filial@email.com"
        );

        // Entidade "atual", como se já estivesse salva antes da atualização
        Veiculo entidadeExistente = new Veiculo(
                "9BWZZZ377VT004251", "ABC1D23", "12345678901", "Onix", "Chevrolet",
                LocalDateTime.of(2023, 1, 1, 0, 0), "Prata", CategoriaVeiculo.HATCH, 0.0, filial
        );

        // DTO com os dados novos que o usuário quer aplicar (só quilometragem, cor e placa são editáveis)
        VeiculoUpdateDTO dtoAtualizacao = new VeiculoUpdateDTO(1500.0, "Preto", "XYZ9A87");

        // DTO de resposta esperado depois da atualização e do mapeamento
        VeiculoResponseDTO responseEsperado = new VeiculoResponseDTO(
                id, "9BWZZZ377VT004251", "XYZ9A87", "12345678901", "Onix", "Chevrolet",
                LocalDateTime.of(2023, 1, 1, 0, 0), "Preto", CategoriaVeiculo.HATCH, 1500.0, null
        );

        // Ensina o mock: o repository encontra a entidade existente pra esse ID
        when(repository.findById(id)).thenReturn(Optional.of(entidadeExistente));

        // Ensina o mock: depois de atualizada, o mapper devolve o response esperado
        // any(Veiculo.class) porque é a mesma instância, só com os campos já alterados
        when(mapper.mapearParaResponse(any(Veiculo.class))).thenReturn(responseEsperado);

        // ACT: chama a atualização passando o DTO novo e o ID como String
        VeiculoResponseDTO resultado = service.atualizarVeiculoPorId(dtoAtualizacao, id.toString());

        // ASSERT: o resultado deve ser o response esperado
        assertThat(resultado).isEqualTo(responseEsperado);

        // Verifica que o save foi chamado com a mesma instância vinda do findById
        verify(repository, times(1)).save(entidadeExistente);
    }

    @Test
    @DisplayName("atualizarVeiculoPorId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
    void atualizarVeiculoPorId_idInexistente_lancaExcecao() {
        // ARRANGE: ID que vamos simular como inexistente
        UUID id = UUID.randomUUID();

        // DTO de atualização qualquer, o conteúdo não importa pra esse teste
        VeiculoUpdateDTO dtoAtualizacao = new VeiculoUpdateDTO(1500.0, "Preto", "XYZ9A87");

        // Ensina o mock: não existe entidade pra esse ID
        when(repository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT: o Service deve lançar a exception antes de tentar atualizar
        assertThatThrownBy(() -> service.atualizarVeiculoPorId(dtoAtualizacao, id.toString()))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Veículo não encontrado.");

        // Garante que, sem entidade encontrada, o save nunca é chamado
        verify(repository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // MÉTODO: deletarVeiculoViaId
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("deletarVeiculoViaId deve deletar quando o ID existe")
    void deletarVeiculoViaId_idExistente_deleta() {
        // ARRANGE: ID que vamos simular como existente
        UUID id = UUID.randomUUID();

        // Filial obrigatória pra montar a entidade Veiculo
        FilialLocadora filial = new FilialLocadora(
                "Filial Centro", "12345678000199", "SP", "Sao Paulo", "Rua X, 1", "1140028922", "filial@email.com"
        );

        // Entidade que o repository "encontrará" pra esse ID
        Veiculo entidadeExistente = new Veiculo(
                "9BWZZZ377VT004251", "ABC1D23", "12345678901", "Onix", "Chevrolet",
                LocalDateTime.of(2023, 1, 1, 0, 0), "Prata", CategoriaVeiculo.HATCH, 0.0, filial
        );

        // Ensina o mock: o findById encontra a entidade
        when(repository.findById(id)).thenReturn(Optional.of(entidadeExistente));

        // ACT: chama o método de deleção (void, não retorna nada)
        service.deletarVeiculoViaId(id.toString());

        // ASSERT: já que não há retorno, confirmamos o comportamento via verify
        // o delete deve ter sido chamado exatamente uma vez com a entidade encontrada
        verify(repository, times(1)).delete(entidadeExistente);
    }

    @Test
    @DisplayName("deletarVeiculoViaId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
    void deletarVeiculoViaId_idInexistente_lancaExcecao() {
        // ARRANGE: ID que vamos simular como inexistente
        UUID id = UUID.randomUUID();

        // Ensina o mock: não existe entidade pra esse ID
        when(repository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT: o Service deve lançar a exception ao invés de tentar deletar algo inexistente
        assertThatThrownBy(() -> service.deletarVeiculoViaId(id.toString()))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Veículo não encontrado.");

        // Garante que o delete nunca foi chamado
        verify(repository, never()).delete(any());
    }
}
