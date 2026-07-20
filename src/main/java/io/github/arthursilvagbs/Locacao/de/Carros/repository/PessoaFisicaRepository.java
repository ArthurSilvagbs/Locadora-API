package io.github.arthursilvagbs.Locacao.de.Carros.repository;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaFisica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, UUID> {

   Optional<PessoaFisica> findByCpf(String cpf);
   boolean existsByCpf(String cpf);
   boolean existsByEmail(String email);
}
