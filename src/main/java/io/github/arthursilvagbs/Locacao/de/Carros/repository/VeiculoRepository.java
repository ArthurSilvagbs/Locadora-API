package io.github.arthursilvagbs.Locacao.de.Carros.repository;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VeiculoRepository extends JpaRepository<Veiculo, UUID> {
   boolean existsByNumeroChassi(String numChassi);
   boolean existsByPlacaVeiculo(String placaVeiculo);
   boolean existsByRenavam(String renavam);
   Optional<Veiculo> findByNumeroChassi(String numChassi);
   Optional<Veiculo> findByPlacaVeiculo(String placa);
   Optional<Veiculo> findByRenavam(String renavam);
}
