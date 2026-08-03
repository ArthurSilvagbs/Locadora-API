package io.github.arthursilvagbs.Locacao.de.Carros.service;

// DTO de entrada usado pra criar uma reserva/locação
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoCreateDTO;
// DTO de resposta devolvido pelo Service
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoResponseDTO;
// Entidades envolvidas no fluxo de locação
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Cliente;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FormaPagamento;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.CategoriaVeiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.StatusLocacao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.StatusVeiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;
// Exception lançada quando um registro não é encontrado
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
// Exception lançada quando o status atual não permite a operação pedida
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.StatusInvalidoException;
// Exception lançada quando o veículo já está locado/reservado
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.VeiculoNaoDisponivelException;
// Mapper que converte DTO <-> Entidade <-> DTO de resposta
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.LocacaoMapper;
// Repositories usados pelo Service; todos serão mockados, não usamos banco de verdade
import io.github.arthursilvagbs.Locacao.de.Carros.repository.ClienteRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.FilialLocadoraRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.LocacaoRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.VeiculoRepository;

// Anotações de teste do JUnit 5
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
// Liga o Mockito ao JUnit 5, habilitando @Mock e @InjectMocks
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// BigDecimal usado no valor da locação
import java.math.BigDecimal;
// LocalDateTime usado nas datas de retirada/devolução
import java.time.LocalDateTime;
// UUID pra gerar IDs falsos
import java.util.UUID;
// Optional representa "achei"/"não achei" no findById
import java.util.Optional;

// Métodos estáticos do AssertJ pras verificações
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
// Métodos estáticos do Mockito pra configurar mocks e verificar chamadas
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Liga o Mockito nesta classe: processa as anotações @Mock e @InjectMocks abaixo
@ExtendWith(MockitoExtension.class)
class LocacaoServiceTest {

    // Mock do repository de locação
    @Mock
    private LocacaoRepository repository;

    // Mock do mapper de locação
    @Mock
    private LocacaoMapper mapper;

    // Mock do repository de veículo (usado pra checar/alterar status e quilometragem)
    @Mock
    private VeiculoRepository veiculoRepository;

    // Mock do repository de cliente (usado só na criação da reserva)
    @Mock
    private ClienteRepository clienteRepository;

    // Mock do repository de filial (usado só na criação da reserva)
    @Mock
    private FilialLocadoraRepository filialLocadoraRepository;

    // Instância REAL do Service, com todos os mocks acima injetados
    @InjectMocks
    private LocacaoService service;

    // Método auxiliar do teste (não é do Service): monta uma Filial válida pra reutilizar nos cenários
    private FilialLocadora criarFilial() {
        return new FilialLocadora(
                "Filial Centro", "12345678000199", "SP", "Sao Paulo", "Rua X, 1", "1140028922", "filial@email.com"
        );
    }

    // Método auxiliar do teste: monta um Veiculo válido, já vinculado a uma filial, reutilizado nos cenários
    private Veiculo criarVeiculo(StatusVeiculo status) {
        Veiculo veiculo = new Veiculo(
                "9BWZZZ377VT004251", "ABC1D23", "12345678901", "Onix", "Chevrolet",
                LocalDateTime.of(2023, 1, 1, 0, 0), "Prata", CategoriaVeiculo.HATCH, 0.0, criarFilial()
        );
        // O construtor do Veiculo já define DISPONIVEL por padrão; aqui forçamos o status que o teste precisa
        veiculo.setStatusVeiculo(status);
        return veiculo;
    }

    // ---------------------------------------------------------------------
    // MÉTODO: criarReservaDoVeiculo
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("criarReservaDoVeiculo deve calcular o valor, bloquear o veículo e salvar a locação quando ele está disponível")
    void criarReservaDoVeiculo_veiculoDisponivel_retornaResponseDTO() {
        // ARRANGE: IDs que vamos usar no DTO de entrada, como strings (é assim que chegam numa requisição)
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID filialRetiradaId = UUID.randomUUID();
        UUID filialDevolucaoId = UUID.randomUUID();

        // DTO de entrada com datas de retirada e devolução com 3 dias de diferença
        LocacaoCreateDTO dto = new LocacaoCreateDTO(
                clienteId.toString(),
                veiculoId.toString(),
                filialRetiradaId.toString(),
                filialDevolucaoId.toString(),
                FormaPagamento.PIX,
                LocalDateTime.of(2026, 1, 1, 10, 0),  // dataRetirada
                LocalDateTime.of(2026, 1, 4, 10, 0)   // dataDevolucao (3 dias depois)
        );

        // Entidades que os repositories "fingirão" ter encontrado pra cada ID do DTO
        Cliente cliente = new Cliente("Arthur Silva", "arthur@email.com", "11999999999", "Rua A, 123");
        Veiculo veiculo = criarVeiculo(StatusVeiculo.DISPONIVEL); // veículo livre, então a reserva deve seguir em frente
        FilialLocadora filialRetirada = criarFilial();
        FilialLocadora filialDevolucao = criarFilial();

        // Entidade Locacao que o mapper "fingirá" montar a partir do dto + entidades encontradas
        Locacao locacaoMapeada = new Locacao(
                cliente, veiculo, filialRetirada, filialDevolucao, dto.formaPagamento(), dto.dataRetirada(), dto.dataDevolucao()
        );

        // DTO de resposta esperado no final do método
        LocacaoResponseDTO responseEsperado = new LocacaoResponseDTO(
                UUID.randomUUID(), cliente, veiculo, BigDecimal.valueOf(360.00), filialRetirada, filialDevolucao,
                dto.formaPagamento(), StatusLocacao.PENDENTE_DE_RETIRADA, dto.dataRetirada(), dto.dataDevolucao()
        );

        // Ensina os mocks: cada repository encontra a entidade correspondente ao ID do DTO
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
        when(filialLocadoraRepository.findById(filialRetiradaId)).thenReturn(Optional.of(filialRetirada));
        when(filialLocadoraRepository.findById(filialDevolucaoId)).thenReturn(Optional.of(filialDevolucao));

        // Ensina o mock: o mapper monta a entidade Locacao a partir do dto e das entidades encontradas
        when(mapper.mapearParaLocacao(dto, cliente, veiculo, filialRetirada, filialDevolucao)).thenReturn(locacaoMapeada);

        // Ensina o mock: repository.save devolve a MESMA locação recebida (é o retorno que o Service usa
        // pra chamar o mapper depois — sem esse stub, o mock devolveria null e o mapper receberia null)
        when(repository.save(locacaoMapeada)).thenReturn(locacaoMapeada);

        // Ensina o mock: o mapper transforma a locação salva no response esperado
        when(mapper.mapearParaResponse(locacaoMapeada)).thenReturn(responseEsperado);

        // ACT: chama o método real do Service
        LocacaoResponseDTO resultado = service.criarReservaDoVeiculo(dto);

        // ASSERT: o resultado devolvido deve ser exatamente o response esperado
        assertThat(resultado).isEqualTo(responseEsperado);

        // O Service calcula: HATCH -> fator 1.0 * 120.00 = 120.00 por dia, vezes 3 dias = 360.00
        // Como o valor é setado DENTRO da locacaoMapeada antes do save, conferimos ali
        assertThat(locacaoMapeada.getValorLocacao()).isEqualByComparingTo(BigDecimal.valueOf(360.00));

        // Verifica que o veículo foi marcado como LOCADO e salvo, bloqueando ele pra outras reservas
        assertThat(veiculo.getStatusVeiculo()).isEqualTo(StatusVeiculo.LOCADO);
        verify(veiculoRepository, times(1)).save(veiculo);

        // Verifica que a locação foi persistida com o status inicial correto
        assertThat(locacaoMapeada.getStatusLocacao()).isEqualTo(StatusLocacao.PENDENTE_DE_RETIRADA);
        verify(repository, times(1)).save(locacaoMapeada);
    }

    @Test
    @DisplayName("criarReservaDoVeiculo deve lançar VeiculoNaoDisponivelException quando o veículo já está locado")
    void criarReservaDoVeiculo_veiculoLocado_lancaExcecao() {
        // ARRANGE: IDs usados no DTO
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID filialRetiradaId = UUID.randomUUID();
        UUID filialDevolucaoId = UUID.randomUUID();

        // DTO de entrada, com datas quaisquer (não chegam a ser usadas, a exception acontece antes)
        LocacaoCreateDTO dto = new LocacaoCreateDTO(
                clienteId.toString(), veiculoId.toString(), filialRetiradaId.toString(), filialDevolucaoId.toString(),
                FormaPagamento.PIX, LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 4, 10, 0)
        );

        // Entidades que os repositories "fingirão" ter encontrado
        Cliente cliente = new Cliente("Arthur Silva", "arthur@email.com", "11999999999", "Rua A, 123");
        Veiculo veiculo = criarVeiculo(StatusVeiculo.LOCADO); // veículo já está locado por outra reserva
        FilialLocadora filialRetirada = criarFilial();
        FilialLocadora filialDevolucao = criarFilial();

        // Ensina os mocks: todas as entidades existem, então o Service chega até a checagem de status
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
        when(filialLocadoraRepository.findById(filialRetiradaId)).thenReturn(Optional.of(filialRetirada));
        when(filialLocadoraRepository.findById(filialDevolucaoId)).thenReturn(Optional.of(filialDevolucao));

        // ACT + ASSERT: chama o método e confere a exception lançada
        assertThatThrownBy(() -> service.criarReservaDoVeiculo(dto))
                .isInstanceOf(VeiculoNaoDisponivelException.class)
                .hasMessage("Veículo já está locado/reservado.");

        // Confirma que nada foi salvo, nem veículo nem locação, já que a operação foi barrada
        verify(veiculoRepository, never()).save(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("criarReservaDoVeiculo deve lançar EntidadeNaoEncontradaException quando o cliente não existe")
    void criarReservaDoVeiculo_clienteInexistente_lancaExcecao() {
        // ARRANGE: IDs usados no DTO
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID filialRetiradaId = UUID.randomUUID();
        UUID filialDevolucaoId = UUID.randomUUID();

        // DTO de entrada, com datas quaisquer
        LocacaoCreateDTO dto = new LocacaoCreateDTO(
                clienteId.toString(), veiculoId.toString(), filialRetiradaId.toString(), filialDevolucaoId.toString(),
                FormaPagamento.PIX, LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 4, 10, 0)
        );

        // Ensina o mock: não existe cliente pra esse ID (Optional vazio)
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // ACT + ASSERT: o Service deve lançar a exception assim que o cliente não é encontrado
        assertThatThrownBy(() -> service.criarReservaDoVeiculo(dto))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Cliente não encontrado.");

        // Confirma que o Service nem chegou a buscar o veículo, já que parou no cliente primeiro
        verify(veiculoRepository, never()).findById(any());
    }

    // ---------------------------------------------------------------------
    // MÉTODO: cancelarLocacao
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("cancelarLocacao deve liberar o veículo e cancelar a locação quando ela está PENDENTE_DE_RETIRADA")
    void cancelarLocacao_statusValido_retornaResponseDTO() {
        // ARRANGE: monta uma locação pendente de retirada, ainda cancelável
        UUID locacaoId = UUID.randomUUID();
        Cliente cliente = new Cliente("Arthur Silva", "arthur@email.com", "11999999999", "Rua A, 123");
        Veiculo veiculo = criarVeiculo(StatusVeiculo.LOCADO);
        FilialLocadora filial = criarFilial();
        Locacao locacao = new Locacao(
                cliente, veiculo, filial, filial, FormaPagamento.PIX,
                LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 4, 10, 0)
        );
        // Status padrão do construtor já é PENDENTE_DE_RETIRADA, então o cancelamento deve ser permitido

        // DTO de resposta esperado após o cancelamento
        LocacaoResponseDTO responseEsperado = new LocacaoResponseDTO(
                locacaoId, cliente, veiculo, locacao.getValorLocacao(), filial, filial,
                FormaPagamento.PIX, StatusLocacao.CANCELADA, locacao.getDataRetirada(), locacao.getDataDevolucao()
        );

        // Ensina o mock: encontrou a locação pra esse ID
        when(repository.findById(locacaoId)).thenReturn(Optional.of(locacao));

        // Ensina o mock: repository.save devolve a MESMA locação recebida (é o retorno que o Service usa
        // pra chamar o mapper depois — sem esse stub, o mock devolveria null e o mapper receberia null)
        when(repository.save(locacao)).thenReturn(locacao);

        // Ensina o mock: o mapper transforma a locação cancelada no response esperado
        when(mapper.mapearParaResponse(locacao)).thenReturn(responseEsperado);

        // ACT: chama o método real do Service
        LocacaoResponseDTO resultado = service.cancelarLocacao(locacaoId.toString());

        // ASSERT: o resultado deve ser o response esperado
        assertThat(resultado).isEqualTo(responseEsperado);

        // Confirma que o veículo foi liberado, já que a locação não vai mais acontecer
        assertThat(veiculo.getStatusVeiculo()).isEqualTo(StatusVeiculo.DISPONIVEL);
        verify(veiculoRepository, times(1)).save(veiculo);

        // Confirma que a locação foi marcada como cancelada
        assertThat(locacao.getStatusLocacao()).isEqualTo(StatusLocacao.CANCELADA);
        verify(repository, times(1)).save(locacao);
    }

    @Test
    @DisplayName("cancelarLocacao deve lançar StatusInvalidoException quando a locação já está CANCELADA")
    void cancelarLocacao_jaCancelada_lancaExcecao() {
        // ARRANGE: monta uma locação que já foi cancelada antes
        UUID locacaoId = UUID.randomUUID();
        Cliente cliente = new Cliente("Arthur Silva", "arthur@email.com", "11999999999", "Rua A, 123");
        Veiculo veiculo = criarVeiculo(StatusVeiculo.DISPONIVEL);
        FilialLocadora filial = criarFilial();
        Locacao locacao = new Locacao(
                cliente, veiculo, filial, filial, FormaPagamento.PIX,
                LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 4, 10, 0)
        );
        // Forçamos o status pra CANCELADA, simulando que ela já foi cancelada antes
        locacao.setStatusLocacao(StatusLocacao.CANCELADA);

        // Ensina o mock: encontrou a locação pra esse ID
        when(repository.findById(locacaoId)).thenReturn(Optional.of(locacao));

        // ACT + ASSERT: o Service deve barrar um cancelamento duplicado com uma mensagem específica
        assertThatThrownBy(() -> service.cancelarLocacao(locacaoId.toString()))
                .isInstanceOf(StatusInvalidoException.class)
                .hasMessage("A locação já está com o status 'CANCELADA'.");

        // Confirma que nada foi salvo de novo
        verify(veiculoRepository, never()).save(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("cancelarLocacao deve lançar StatusInvalidoException quando a locação já foi RETIRADO")
    void cancelarLocacao_statusInvalido_lancaExcecao() {
        // ARRANGE: monta uma locação que já foi retirada (não faz mais sentido cancelar, só devolver)
        UUID locacaoId = UUID.randomUUID();
        Cliente cliente = new Cliente("Arthur Silva", "arthur@email.com", "11999999999", "Rua A, 123");
        Veiculo veiculo = criarVeiculo(StatusVeiculo.LOCADO);
        FilialLocadora filial = criarFilial();
        Locacao locacao = new Locacao(
                cliente, veiculo, filial, filial, FormaPagamento.PIX,
                LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 4, 10, 0)
        );
        // Forçamos o status pra RETIRADO
        locacao.setStatusLocacao(StatusLocacao.RETIRADO);

        // Ensina o mock: encontrou a locação pra esse ID
        when(repository.findById(locacaoId)).thenReturn(Optional.of(locacao));

        // ACT + ASSERT: o Service deve barrar porque o status não é PENDENTE_DE_RETIRADA nem CANCELADA
        assertThatThrownBy(() -> service.cancelarLocacao(locacaoId.toString()))
                .isInstanceOf(StatusInvalidoException.class)
                .hasMessage("Status de locação inválido.");

        // Confirma que nada foi salvo
        verify(veiculoRepository, never()).save(any());
        verify(repository, never()).save(any());
    }
}
