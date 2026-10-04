package io.github.arthursilvagbs.Locacao.de.Carros.repository;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaFisica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, UUID> {

   Optional<PessoaFisica> findByCpf(String cpf);
   boolean existsByCpf(String cpf);
   boolean existsByEmail(String email);

   @Query("""
      SELECT l
      FROM PessoaFisica p
      JOIN p.locacoes l
      WHERE p.idCliente = :id
      ORDER BY l.dataRetirada DESC
      """)
   Page<Locacao> buscarLocacoesPorId(@Param("id") UUID id, Pageable pg);

   @Query("""
      SELECT l
      FROM PessoaFisica p
      JOIN p.locacoes l
      WHERE p.cpf = :cpf
      ORDER BY l.dataRetirada DESC
      """)
   Page<Locacao> buscarLocacoesPorCpf(@Param("cpf") String cpf, Pageable pg);
}
