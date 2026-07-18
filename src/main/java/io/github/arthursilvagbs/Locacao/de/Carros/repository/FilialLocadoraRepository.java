package io.github.arthursilvagbs.Locacao.de.Carros.repository;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FilialLocadoraRepository extends JpaRepository<FilialLocadora, UUID> {
}
