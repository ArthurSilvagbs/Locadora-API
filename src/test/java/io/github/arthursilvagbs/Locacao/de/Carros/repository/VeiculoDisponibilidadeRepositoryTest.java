package io.github.arthursilvagbs.Locacao.de.Carros.repository;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Usa o PostgreSQL configurado no projeto/CI; os dados de cada teste sofrem rollback.
@SpringBootTest
@Transactional
class VeiculoDisponibilidadeRepositoryTest {
    @Autowired private EntityManager entityManager;
    @Autowired private VeiculoRepository repository;

    private FilialLocadora filial;
    private Cliente cliente;

    @BeforeEach
    void preparar() {
        filial = new FilialLocadora("Filial Teste", "98765432000199", "SP", "Sao Paulo",
            "Rua Teste, 1", "11999999999", "filial-teste@example.com");
        entityManager.persist(filial);
        cliente = new Cliente("Cliente Teste", UUID.randomUUID() + "@example.com", "11999999999", "Rua Teste, 1");
        entityManager.persist(cliente);
    }

    @Test
    void reservasConsecutivas_naoSomamOcupacaoSimultanea() {
        veiculo(CategoriaVeiculo.HATCH);
        veiculo(CategoriaVeiculo.HATCH);
        reserva(10, 12, CategoriaVeiculo.HATCH, StatusLocacao.PENDENTE_DE_RETIRADA);
        reserva(12, 14, CategoriaVeiculo.HATCH, StatusLocacao.PENDENTE_DE_RETIRADA);
        verificarHatch(true);
    }

    @Test
    void picoNoMeioDoPeriodo_esgotaCapacidade() {
        veiculo(CategoriaVeiculo.HATCH);
        veiculo(CategoriaVeiculo.HATCH);
        reserva(9, 13, CategoriaVeiculo.HATCH, StatusLocacao.RETIRADO);
        reserva(12, 15, CategoriaVeiculo.HATCH, StatusLocacao.PENDENTE_DE_RETIRADA);
        verificarHatch(false);
    }

    @Test
    void reservasIniciadasAntesDoPeriodo_contamNaOcupacaoInicial() {
        veiculo(CategoriaVeiculo.HATCH);
        reserva(8, 15, CategoriaVeiculo.HATCH, StatusLocacao.RETIRADO);
        verificarHatch(false);
    }

    @Test
    void devolucaoNoInicioERetiradaNoFim_naoOcupamPeriodo() {
        veiculo(CategoriaVeiculo.HATCH);
        reserva(8, 10, CategoriaVeiculo.HATCH, StatusLocacao.RETIRADO);
        reserva(14, 16, CategoriaVeiculo.HATCH, StatusLocacao.PENDENTE_DE_RETIRADA);
        verificarHatch(true);
    }

    @Test
    void reservasCanceladasDevolvidasEDeOutraCategoria_naoConsomemCapacidade() {
        veiculo(CategoriaVeiculo.HATCH);
        reserva(10, 14, CategoriaVeiculo.HATCH, StatusLocacao.CANCELADA);
        reserva(10, 14, CategoriaVeiculo.HATCH, StatusLocacao.DEVOLVIDO);
        reserva(10, 14, CategoriaVeiculo.SUV, StatusLocacao.PENDENTE_DE_RETIRADA);
        verificarHatch(true);
    }

    @Test
    void semFrota_naoRetornaCategoria() {
        verificarHatch(false);
    }

    @Test
    void semReservas_retornaCategoriasEPaginacaoComTotalCorreto() {
        veiculo(CategoriaVeiculo.HATCH);
        veiculo(CategoriaVeiculo.HATCH);
        veiculo(CategoriaVeiculo.SUV);
        entityManager.flush();
        var categorias = repository.buscarCategoriasVeiculoPorFilial(filial.getIdLocadora(), dia(10), dia(14));
        var primeira = repository.buscarCategoriasVeiculoPorFilialPaginado(
            filial.getIdLocadora(), dia(10), dia(14), PageRequest.of(0, 1));
        var segunda = repository.buscarCategoriasVeiculoPorFilialPaginado(
            filial.getIdLocadora(), dia(10), dia(14), PageRequest.of(1, 1));
        assertThat(categorias).containsExactly(CategoriaVeiculo.HATCH, CategoriaVeiculo.SUV);
        assertThat(primeira.getTotalElements()).isEqualTo(2);
        assertThat(segunda.getTotalElements()).isEqualTo(2);
        assertThat(primeira.getContent()).containsExactly(categorias.get(0));
        assertThat(segunda.getContent()).containsExactly(categorias.get(1));
    }

    private void verificarHatch(boolean disponivel) {
        entityManager.flush();
        var lista = repository.buscarCategoriasVeiculoPorFilial(filial.getIdLocadora(), dia(10), dia(14));
        var pagina = repository.buscarCategoriasVeiculoPorFilialPaginado(
            filial.getIdLocadora(), dia(10), dia(14), PageRequest.of(0, 10));
        assertThat(lista.contains(CategoriaVeiculo.HATCH)).isEqualTo(disponivel);
        assertThat(pagina.getContent()).containsExactlyElementsOf(lista);
        assertThat(pagina.getTotalElements()).isEqualTo(lista.size());
    }

    private void veiculo(CategoriaVeiculo categoria) {
        String identificador = UUID.randomUUID().toString().replace("-", "");
        long renavam = Math.floorMod(UUID.randomUUID().getLeastSignificantBits(), 100_000_000_000L);
        entityManager.persist(new Veiculo(
            identificador.substring(0, 17), identificador.substring(0, 7),
            String.format("%011d", renavam), "Onix", "Chevrolet", 2023, "Prata", categoria, 1000.0, filial));
    }

    private void reserva(int inicio, int fim, CategoriaVeiculo categoria, StatusLocacao status) {
        Locacao locacao = new Locacao(cliente, filial, filial, categoria, FormaPagamento.PIX,
            dia(inicio), dia(fim), BigDecimal.valueOf(120));
        locacao.setStatusLocacao(status);
        entityManager.persist(locacao);
    }

    private LocalDateTime dia(int dia) {
        return LocalDateTime.of(2026, 10, dia, 10, 0);
    }
}
