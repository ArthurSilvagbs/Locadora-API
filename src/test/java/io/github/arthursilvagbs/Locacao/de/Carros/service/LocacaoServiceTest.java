package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.ConfirmarRetiradaDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.ConfirmarDevolucaoDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.CategoriaVeiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Cliente;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FormaPagamento;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.StatusLocacao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.StatusVeiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.DadosIncompativeisException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.StatusInvalidoException;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.LocacaoMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.ClienteRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.FilialLocadoraRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.LocacaoRepository;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.VeiculoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocacaoServiceTest {

    @Mock
    private LocacaoRepository repository;

    @Mock
    private LocacaoMapper mapper;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private FilialLocadoraRepository filialLocadoraRepository;

    @InjectMocks
    private LocacaoService service;

    @Test
    void criarReservaDaCategoriaDeVeiculo_criaReservaComCategoriaEValorCalculado() {
        UUID clienteId = UUID.randomUUID();
        UUID filialRetiradaId = UUID.randomUUID();
        UUID filialDevolucaoId = UUID.randomUUID();
        LocalDateTime dataRetirada = LocalDateTime.of(2026, 9, 20, 10, 0);
        LocalDateTime dataDevolucao = LocalDateTime.of(2026, 9, 23, 10, 0);
        BigDecimal valorLocacao = BigDecimal.valueOf(360.0);
        LocacaoCreateDTO dto = new LocacaoCreateDTO(
            clienteId.toString(), filialRetiradaId.toString(), filialDevolucaoId.toString(),
            CategoriaVeiculo.HATCH, FormaPagamento.PIX, dataRetirada, dataDevolucao
        );
        Cliente cliente = new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1");
        FilialLocadora filialRetirada = criarFilial();
        FilialLocadora filialDevolucao = criarFilial();
        Locacao locacao = criarLocacao(cliente, filialRetirada, filialDevolucao, CategoriaVeiculo.HATCH, valorLocacao);
        LocacaoResponseDTO resposta = respostaDe(locacao);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(filialLocadoraRepository.findById(filialRetiradaId)).thenReturn(Optional.of(filialRetirada));
        when(filialLocadoraRepository.findById(filialDevolucaoId)).thenReturn(Optional.of(filialDevolucao));
        when(veiculoRepository.buscarCategoriasVeiculoPorFilial(
            filialRetirada.getIdLocadora(), dataRetirada, dataDevolucao))
            .thenReturn(List.of(CategoriaVeiculo.HATCH));
        when(mapper.mapearParaLocacao(dto, cliente, filialRetirada, filialDevolucao, CategoriaVeiculo.HATCH, valorLocacao))
            .thenReturn(locacao);
        when(repository.save(locacao)).thenReturn(locacao);
        when(mapper.mapearParaResponse(locacao)).thenReturn(resposta);

        LocacaoResponseDTO resultado = service.criarReservaDaCategoriaDeVeiculo(dto);

        assertThat(resultado).isEqualTo(resposta);
        assertThat(locacao.getVeiculo()).isNull();
        assertThat(locacao.getCategoriaVeiculo()).isEqualTo(CategoriaVeiculo.HATCH);
        assertThat(locacao.getValorLocacao()).isEqualByComparingTo(valorLocacao);
        assertThat(locacao.getStatusLocacao()).isEqualTo(StatusLocacao.PENDENTE_DE_RETIRADA);
        verify(repository).save(locacao);
        verify(veiculoRepository).buscarCategoriasVeiculoPorFilial(
            filialRetirada.getIdLocadora(), dataRetirada, dataDevolucao);
    }

    @Test
    void criarReservaDaCategoriaDeVeiculo_semCategoriasDisponiveis_naoSalva() {
        verificarReservaIndisponivel(List.of());
    }

    @Test
    void criarReservaDaCategoriaDeVeiculo_apenasOutraCategoriaDisponivel_naoSalva() {
        verificarReservaIndisponivel(List.of(CategoriaVeiculo.SUV));
    }

    private void verificarReservaIndisponivel(List<CategoriaVeiculo> categoriasDisponiveis) {
        UUID clienteId = UUID.randomUUID();
        UUID filialId = UUID.randomUUID();
        FilialLocadora filial = criarFilial();
        Cliente cliente = new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1");
        LocacaoCreateDTO dto = new LocacaoCreateDTO(
            clienteId.toString(), filialId.toString(), filialId.toString(),
            CategoriaVeiculo.HATCH, FormaPagamento.PIX,
            LocalDateTime.of(2026, 9, 20, 10, 0), LocalDateTime.of(2026, 9, 23, 10, 0)
        );
        Locacao locacao = criarLocacao(cliente, filial, filial, CategoriaVeiculo.HATCH, BigDecimal.valueOf(360));
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(filialLocadoraRepository.findById(filialId)).thenReturn(Optional.of(filial));
        when(mapper.mapearParaLocacao(dto, cliente, filial, filial, CategoriaVeiculo.HATCH, BigDecimal.valueOf(360.0)))
            .thenReturn(locacao);
        when(veiculoRepository.buscarCategoriasVeiculoPorFilial(
            filial.getIdLocadora(), dto.dataRetirada(), dto.dataDevolucao()))
            .thenReturn(categoriasDisponiveis);

        assertThatThrownBy(() -> service.criarReservaDaCategoriaDeVeiculo(dto))
            .isInstanceOf(DadosIncompativeisException.class)
            .hasMessage("Categoria indisponível nesta filial para o período informado.");
        verifyNoInteractions(repository);
        verify(mapper, never()).mapearParaResponse(locacao);
    }

    @Test
    void criarReservaDaCategoriaDeVeiculo_clienteInexistente_lancaExcecao() {
        UUID clienteId = UUID.randomUUID();
        LocacaoCreateDTO dto = new LocacaoCreateDTO(
            clienteId.toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            CategoriaVeiculo.HATCH, FormaPagamento.PIX,
            LocalDateTime.of(2026, 9, 20, 10, 0), LocalDateTime.of(2026, 9, 23, 10, 0)
        );

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criarReservaDaCategoriaDeVeiculo(dto))
            .isInstanceOf(EntidadeNaoEncontradaException.class)
            .hasMessage("Cliente não encontrado.");

        verifyNoInteractions(filialLocadoraRepository, veiculoRepository, repository, mapper);
    }

    @Test
    void confirmarRetirada_atribuiVeiculoEAtualizaStatus() {
        UUID locacaoId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID filialId = UUID.randomUUID();
        FilialLocadora filialRetirada = criarFilial(filialId);
        FilialLocadora filialDoVeiculo = criarFilial(UUID.fromString(filialId.toString()));
        Locacao locacao = criarLocacao(
            new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1"),
            filialRetirada,
            filialRetirada,
            CategoriaVeiculo.HATCH,
            BigDecimal.valueOf(360.0)
        );
        Veiculo veiculo = criarVeiculo(filialDoVeiculo, CategoriaVeiculo.HATCH, StatusVeiculo.DISPONIVEL);
        ConfirmarRetiradaDTO dto = new ConfirmarRetiradaDTO(veiculoId.toString());
        LocacaoResponseDTO resposta = respostaDe(locacao);

        when(repository.findById(locacaoId)).thenReturn(Optional.of(locacao));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));
        when(repository.save(locacao)).thenReturn(locacao);
        when(mapper.mapearParaResponse(locacao)).thenReturn(resposta);

        LocacaoResponseDTO resultado = service.confirmarRetirada(dto, locacaoId.toString());

        assertThat(resultado).isEqualTo(resposta);
        assertThat(locacao.getVeiculo()).isSameAs(veiculo);
        assertThat(locacao.getStatusLocacao()).isEqualTo(StatusLocacao.RETIRADO);
        assertThat(locacao.getKmRetirada()).isEqualTo(veiculo.getQuilometragem());
        assertThat(veiculo.getStatusVeiculo()).isEqualTo(StatusVeiculo.LOCADO);
        verify(veiculoRepository).save(veiculo);
        verify(repository).save(locacao);
    }

    @Test
    void confirmarRetirada_veiculoDeOutraFilial_lancaExcecao() {
        UUID locacaoId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        Locacao locacao = criarLocacao(
            new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1"),
            criarFilial(UUID.randomUUID()),
            criarFilial(),
            CategoriaVeiculo.HATCH,
            BigDecimal.valueOf(360.0)
        );
        Veiculo veiculo = criarVeiculo(
            criarFilial(UUID.randomUUID()),
            CategoriaVeiculo.HATCH,
            StatusVeiculo.DISPONIVEL
        );

        when(repository.findById(locacaoId)).thenReturn(Optional.of(locacao));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        assertThatThrownBy(() -> service.confirmarRetirada(
            new ConfirmarRetiradaDTO(veiculoId.toString()),
            locacaoId.toString()
        ))
            .isInstanceOf(DadosIncompativeisException.class)
            .hasMessage("O veículo não esta com o registro vinculado a esta filial, estando cadastrado em outra filial.");

        verify(veiculoRepository, never()).save(veiculo);
        verify(repository, never()).save(locacao);
    }

    @Test
    void confirmarRetirada_veiculoDeOutraCategoria_lancaExcecao() {
        UUID locacaoId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        FilialLocadora filial = criarFilial();
        Locacao locacao = criarLocacao(
            new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1"),
            filial,
            filial,
            CategoriaVeiculo.HATCH,
            BigDecimal.valueOf(360.0)
        );
        Veiculo veiculo = criarVeiculo(filial, CategoriaVeiculo.SUV, StatusVeiculo.DISPONIVEL);
        ConfirmarRetiradaDTO dto = new ConfirmarRetiradaDTO(veiculoId.toString());

        when(repository.findById(locacaoId)).thenReturn(Optional.of(locacao));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        assertThatThrownBy(() -> service.confirmarRetirada(dto, locacaoId.toString()))
            .isInstanceOf(DadosIncompativeisException.class);

        verify(veiculoRepository, never()).save(veiculo);
        verify(repository, never()).save(locacao);
    }

    @Test
    void confirmarRetirada_locacaoNaoPendente_lancaExcecao() {
        UUID locacaoId = UUID.randomUUID();
        Locacao locacao = criarLocacao(
            new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1"),
            criarFilial(),
            criarFilial(),
            CategoriaVeiculo.HATCH,
            BigDecimal.valueOf(360.0)
        );
        locacao.setStatusLocacao(StatusLocacao.RETIRADO);

        when(repository.findById(locacaoId)).thenReturn(Optional.of(locacao));

        assertThatThrownBy(() -> service.confirmarRetirada(
            new ConfirmarRetiradaDTO(UUID.randomUUID().toString()),
            locacaoId.toString()
        )).isInstanceOf(StatusInvalidoException.class);

        verifyNoInteractions(veiculoRepository);
    }

    @Test
    void confirmarRetirada_veiculoInexistente_lancaExcecao() {
        UUID locacaoId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        Locacao locacao = criarLocacao(
            new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1"),
            criarFilial(),
            criarFilial(),
            CategoriaVeiculo.HATCH,
            BigDecimal.valueOf(360.0)
        );

        when(repository.findById(locacaoId)).thenReturn(Optional.of(locacao));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.confirmarRetirada(
            new ConfirmarRetiradaDTO(veiculoId.toString()),
            locacaoId.toString()
        )).isInstanceOf(EntidadeNaoEncontradaException.class)
            .hasMessage("Veículo não encontrado.");

        verify(veiculoRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(repository, never()).save(locacao);
    }

    @Test
    void confirmarRetirada_veiculoIndisponivel_lancaExcecao() {
        UUID locacaoId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        FilialLocadora filial = criarFilial(UUID.randomUUID());
        Locacao locacao = criarLocacao(
            new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1"),
            filial,
            filial,
            CategoriaVeiculo.HATCH,
            BigDecimal.valueOf(360.0)
        );
        Veiculo veiculo = criarVeiculo(filial, CategoriaVeiculo.HATCH, StatusVeiculo.LOCADO);

        when(repository.findById(locacaoId)).thenReturn(Optional.of(locacao));
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculo));

        assertThatThrownBy(() -> service.confirmarRetirada(
            new ConfirmarRetiradaDTO(veiculoId.toString()),
            locacaoId.toString()
        )).isInstanceOf(StatusInvalidoException.class)
            .hasMessage("O veículo não estao disponível para retirada.");

        verify(veiculoRepository, never()).save(veiculo);
        verify(repository, never()).save(locacao);
    }

    @Test
    void confirmarDevolucao_emOutraFilial_atualizaLocalizacaoDoVeiculo() {
        UUID locacaoId = UUID.randomUUID();
        FilialLocadora filialRetirada = criarFilial(UUID.randomUUID());
        FilialLocadora filialDevolucao = criarFilial(UUID.randomUUID());
        Locacao locacao = criarLocacao(
            new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1"),
            filialRetirada,
            filialDevolucao,
            CategoriaVeiculo.HATCH,
            BigDecimal.valueOf(360.0)
        );
        Veiculo veiculo = criarVeiculo(filialRetirada, CategoriaVeiculo.HATCH, StatusVeiculo.LOCADO);
        locacao.setVeiculo(veiculo);
        locacao.setStatusLocacao(StatusLocacao.RETIRADO);
        LocacaoResponseDTO resposta = respostaDe(locacao);

        when(repository.findById(locacaoId)).thenReturn(Optional.of(locacao));
        when(repository.save(locacao)).thenReturn(locacao);
        when(mapper.mapearParaResponse(locacao)).thenReturn(resposta);

        LocacaoResponseDTO resultado = service.confirmarDevolucao(
            new ConfirmarDevolucaoDTO(250.0),
            locacaoId.toString()
        );

        assertThat(resultado).isEqualTo(resposta);
        assertThat(veiculo.getQuilometragem()).isEqualTo(1250.0);
        assertThat(veiculo.getStatusVeiculo()).isEqualTo(StatusVeiculo.DISPONIVEL);
        assertThat(veiculo.getFilialAtual()).isSameAs(filialDevolucao);
        assertThat(locacao.getKmDevolucao()).isEqualTo(1250.0);
        assertThat(locacao.getStatusLocacao()).isEqualTo(StatusLocacao.DEVOLVIDO);
        verify(veiculoRepository).save(veiculo);
        verify(repository).save(locacao);
    }

    @Test
    void cancelarLocacao_pendenteSemVeiculo_cancelaReservaSemAlterarVeiculo() {
        UUID locacaoId = UUID.randomUUID();
        Locacao locacao = criarLocacao(
            new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1"),
            criarFilial(),
            criarFilial(),
            CategoriaVeiculo.HATCH,
            BigDecimal.valueOf(360.0)
        );
        LocacaoResponseDTO resposta = respostaDe(locacao);

        when(repository.findById(locacaoId)).thenReturn(Optional.of(locacao));
        when(repository.save(locacao)).thenReturn(locacao);
        when(mapper.mapearParaResponse(locacao)).thenReturn(resposta);

        LocacaoResponseDTO resultado = service.cancelarLocacao(locacaoId.toString());

        assertThat(resultado).isEqualTo(resposta);
        assertThat(locacao.getStatusLocacao()).isEqualTo(StatusLocacao.CANCELADA);
        verify(repository).save(locacao);
        verifyNoInteractions(veiculoRepository);
    }

    @Test
    void cancelarLocacao_jaCancelada_lancaExcecao() {
        UUID locacaoId = UUID.randomUUID();
        Locacao locacao = criarLocacao(
            new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1"),
            criarFilial(),
            criarFilial(),
            CategoriaVeiculo.HATCH,
            BigDecimal.valueOf(360.0)
        );
        locacao.setStatusLocacao(StatusLocacao.CANCELADA);

        when(repository.findById(locacaoId)).thenReturn(Optional.of(locacao));

        assertThatThrownBy(() -> service.cancelarLocacao(locacaoId.toString()))
            .isInstanceOf(StatusInvalidoException.class)
            .hasMessage("A locação já está com o status 'CANCELADA'.");

        verify(repository, never()).save(locacao);
        verifyNoInteractions(veiculoRepository, mapper);
    }

    @Test
    void cancelarLocacao_retirada_lancaExcecao() {
        UUID locacaoId = UUID.randomUUID();
        Locacao locacao = criarLocacao(
            new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1"),
            criarFilial(),
            criarFilial(),
            CategoriaVeiculo.HATCH,
            BigDecimal.valueOf(360.0)
        );
        locacao.setStatusLocacao(StatusLocacao.RETIRADO);

        when(repository.findById(locacaoId)).thenReturn(Optional.of(locacao));

        assertThatThrownBy(() -> service.cancelarLocacao(locacaoId.toString()))
            .isInstanceOf(StatusInvalidoException.class)
            .hasMessage("Status de locação inválido.");

        verify(repository, never()).save(locacao);
        verifyNoInteractions(veiculoRepository, mapper);
    }

    @Test
    void buscarLocacaoPorId_existente_retornaDTO() {
        UUID id = UUID.randomUUID();
        Locacao locacao = criarLocacao(
            new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1"),
            criarFilial(), criarFilial(), CategoriaVeiculo.HATCH, BigDecimal.valueOf(360)
        );
        LocacaoResponseDTO resposta = respostaDe(locacao);
        when(repository.findById(id)).thenReturn(Optional.of(locacao));
        when(mapper.mapearParaResponse(locacao)).thenReturn(resposta);

        assertThat(service.buscarLocacaoPorId(id.toString())).isEqualTo(resposta);
        verify(mapper).mapearParaResponse(locacao);
    }

    @Test
    void buscarLocacaoPorId_inexistente_lancaExcecao() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarLocacaoPorId(id.toString()))
            .isInstanceOf(EntidadeNaoEncontradaException.class)
            .hasMessage("Locação não encontrada.");
        verifyNoInteractions(mapper);
    }

    @Test
    void confirmarDevolucao_locacaoNaoRetirada_lancaExcecaoSemAlterarVeiculo() {
        UUID id = UUID.randomUUID();
        Locacao locacao = criarLocacao(
            new Cliente("Arthur", "arthur@example.com", "11999999999", "Rua A, 1"),
            criarFilial(), criarFilial(), CategoriaVeiculo.HATCH, BigDecimal.valueOf(360)
        );
        when(repository.findById(id)).thenReturn(Optional.of(locacao));

        assertThatThrownBy(() -> service.confirmarDevolucao(new ConfirmarDevolucaoDTO(25.0), id.toString()))
            .isInstanceOf(StatusInvalidoException.class);
        verifyNoInteractions(veiculoRepository, mapper);
        verify(repository, never()).save(locacao);
    }

    @Test
    void confirmarRetirada_locacaoInexistente_lancaExcecao() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> service.confirmarRetirada(new ConfirmarRetiradaDTO(UUID.randomUUID().toString()), id.toString()))
            .isInstanceOf(EntidadeNaoEncontradaException.class);
        verifyNoInteractions(veiculoRepository, mapper);
    }

    @Test
    void calculoDiariaPorCategoria_calculaDiariaParaTodasAsCategorias() {
        assertThat(service.calculoDiariaPorCategoria(CategoriaVeiculo.HATCH)).isEqualByComparingTo("120.0");
        assertThat(service.calculoDiariaPorCategoria(CategoriaVeiculo.SEDAN)).isEqualByComparingTo("156.0");
        assertThat(service.calculoDiariaPorCategoria(CategoriaVeiculo.PICK_UP)).isEqualByComparingTo("216.0");
        assertThat(service.calculoDiariaPorCategoria(CategoriaVeiculo.SUV)).isEqualByComparingTo("204.0");
        assertThat(service.calculoDiariaPorCategoria(CategoriaVeiculo.MINI_VAN)).isEqualByComparingTo("192.0");
        assertThat(service.calculoDiariaPorCategoria(CategoriaVeiculo.VAN)).isEqualByComparingTo("240.0");
        assertThat(service.calculoDiariaPorCategoria(CategoriaVeiculo.FURGAO)).isEqualByComparingTo("264.0");
        assertThat(service.calculoDiariaPorCategoria(CategoriaVeiculo.BLINDADO)).isEqualByComparingTo("360.0");
    }

    private FilialLocadora criarFilial() {
        return new FilialLocadora("Filial Centro", "12345678000199", "SP", "Sao Paulo", "Rua A, 1", "11999999999", "filial@example.com");
    }

    private FilialLocadora criarFilial(UUID id) {
        return new FilialLocadora(
            id,
            "Filial Centro",
            "12345678000199",
            "SP",
            "Sao Paulo",
            "Rua A, 1",
            "11999999999",
            "filial@example.com",
            null,
            null,
            null,
            null,
            null
        );
    }

    private Locacao criarLocacao(
        Cliente cliente,
        FilialLocadora filialRetirada,
        FilialLocadora filialDevolucao,
        CategoriaVeiculo categoria,
        BigDecimal valorLocacao
    ) {
        return new Locacao(
            cliente,
            filialRetirada,
            filialDevolucao,
            categoria,
            FormaPagamento.PIX,
            LocalDateTime.of(2026, 9, 20, 10, 0),
            LocalDateTime.of(2026, 9, 23, 10, 0),
            valorLocacao
        );
    }

    private Veiculo criarVeiculo(FilialLocadora filial, CategoriaVeiculo categoria, StatusVeiculo status) {
        Veiculo veiculo = new Veiculo(
            "9BWZZZ377VT004251", "ABC1D23", "12345678901", "Onix", "Chevrolet",
            2023, "Prata", categoria, 1000.0, filial
        );
        veiculo.setStatusVeiculo(status);
        return veiculo;
    }

    private LocacaoResponseDTO respostaDe(Locacao locacao) {
        return new LocacaoResponseDTO(
            UUID.randomUUID(),
            UUID.randomUUID().toString(),
            locacao.getVeiculo() == null ? null : UUID.randomUUID().toString(),
            locacao.getValorLocacao(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            locacao.getCategoriaVeiculo(),
            locacao.getFormaPagamento(),
            locacao.getStatusLocacao(),
            locacao.getDataRetirada(),
            locacao.getDataDevolucao()
        );
    }
}
