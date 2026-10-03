package io.github.arthursilvagbs.Locacao.de.Carros.repository;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.CategoriaVeiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VeiculoRepository extends JpaRepository<Veiculo, UUID> {
   boolean existsByNumeroChassi(String numChassi);
   boolean existsByPlacaVeiculo(String placaVeiculo);
   boolean existsByRenavam(String renavam);
   Optional<Veiculo> findByNumeroChassi(String numChassi);
   Optional<Veiculo> findByPlacaVeiculo(String placa);
   Optional<Veiculo> findByRenavam(String renavam);


   // A ocupação só aumenta no início de uma reserva. Para reservas já em andamento,
   // avaliamos o início do período solicitado. Intervalos são [retirada, devolução).
   String CATEGORIAS_DISPONIVEIS = """
      SELECT DISTINCT v.categoriaVeiculo
      FROM Veiculo v
      WHERE v.filialAtual.idLocadora = :idFilialLocadora
      AND NOT EXISTS (
         SELECT marco.idLocacao
         FROM Locacao marco
         WHERE marco.filialRetirada.idLocadora = :idFilialLocadora
         AND marco.categoriaVeiculo = v.categoriaVeiculo
         AND marco.statusLocacao IN (PENDENTE_DE_RETIRADA, RETIRADO)
         AND marco.dataRetirada < :dataDevolucao
         AND marco.dataDevolucao > :dataRetirada
         AND (
            SELECT COUNT(l)
            FROM Locacao l
            WHERE l.filialRetirada.idLocadora = :idFilialLocadora
            AND l.categoriaVeiculo = v.categoriaVeiculo
            AND l.statusLocacao IN (PENDENTE_DE_RETIRADA, RETIRADO)
            AND l.dataRetirada <= CASE
               WHEN marco.dataRetirada < :dataRetirada THEN :dataRetirada
               ELSE marco.dataRetirada END
            AND l.dataDevolucao > CASE
               WHEN marco.dataRetirada < :dataRetirada THEN :dataRetirada
               ELSE marco.dataRetirada END
         ) >= (
            SELECT COUNT(frota)
            FROM Veiculo frota
            WHERE frota.filialAtual.idLocadora = :idFilialLocadora
            AND frota.categoriaVeiculo = v.categoriaVeiculo
         )
      )
      """;

   @Query(value = CATEGORIAS_DISPONIVEIS + " ORDER BY v.categoriaVeiculo",
      countQuery = "SELECT COUNT(DISTINCT total.categoriaVeiculo) " +
         "FROM Veiculo total WHERE total.filialAtual.idLocadora = :idFilialLocadora " +
         "AND total.categoriaVeiculo IN (" + CATEGORIAS_DISPONIVEIS + ")")
   Page<CategoriaVeiculo> buscarCategoriasVeiculoPorFilialPaginado(
      @Param("idFilialLocadora") UUID idFilialLocadora,
      @Param("dataRetirada") LocalDateTime dataRetirada,
      @Param("dataDevolucao") LocalDateTime dataDevolucao,
      Pageable pageable
   );


   @Query(CATEGORIAS_DISPONIVEIS + " ORDER BY v.categoriaVeiculo")
   List<CategoriaVeiculo> buscarCategoriasVeiculoPorFilial(
      @Param("idFilialLocadora") UUID idFilialLocadora,
      @Param("dataRetirada") LocalDateTime dataRetirada,
      @Param("dataDevolucao") LocalDateTime dataDevolucao
   );
}
