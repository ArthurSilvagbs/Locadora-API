package io.github.arthursilvagbs.Locacao.de.Carros.repository;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

}
