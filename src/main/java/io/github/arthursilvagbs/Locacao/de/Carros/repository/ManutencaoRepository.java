package io.github.arthursilvagbs.Locacao.de.Carros.repository;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Manutencao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManutencaoRepository extends JpaRepository<Manutencao, UUID> {
}
