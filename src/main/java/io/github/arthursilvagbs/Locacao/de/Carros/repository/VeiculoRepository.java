package io.github.arthursilvagbs.Locacao.de.Carros.repository;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.CategoriaVeiculo;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface VeiculoRepository extends JpaRepository<Veiculo, UUID> {
   boolean existsByNumeroChassi(String numChassi);
   boolean existsByPlacaVeiculo(String placaVeiculo);
   boolean existsByRenavam(String renavam);
   Optional<Veiculo> findByNumeroChassi(String numChassi);
   Optional<Veiculo> findByPlacaVeiculo(String placa);
   Optional<Veiculo> findByRenavam(String renavam);


   @Query("""
      SELECT DISTINCT v.categoriaVeiculo
      FROM Veiculo v
      WHERE v.filialAtual.idLocadora = :idFilialLocadora
      AND v.statusVeiculo = DISPONIVEL
      """)
   Page<CategoriaVeiculo> buscarCategoriasVeiculoPorFilial(
      @Param("idFilialLocadora") UUID idFilialLocadora,
      Pageable pageable
   );
}
