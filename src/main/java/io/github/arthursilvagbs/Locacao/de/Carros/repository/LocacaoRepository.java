package io.github.arthursilvagbs.Locacao.de.Carros.repository;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocacaoRepository extends JpaRepository<Locacao, UUID> {
}
