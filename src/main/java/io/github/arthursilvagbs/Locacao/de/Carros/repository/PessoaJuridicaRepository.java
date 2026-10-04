package io.github.arthursilvagbs.Locacao.de.Carros.repository;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaJuridica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PessoaJuridicaRepository extends JpaRepository<PessoaJuridica, UUID> {

   boolean existsByEmail(String email);
   boolean existsByCnpj(String cnpj);
   Optional<PessoaJuridica> findByCnpj(String cnpj);

   @Query("""
      SELECT l
      FROM PessoaJuridica p
      JOIN p.locacoes l
      WHERE p.idCliente = :id
      ORDER BY l.dataRetirada DESC
      """)
   Page<Locacao> buscarLocacoesPorId(@Param("id") UUID id, Pageable pg);

   @Query("""
      SELECT l
      FROM PessoaJuridica p
      JOIN p.locacoes l
      WHERE p.cnpj = :cnpj
      ORDER BY l.dataRetirada DESC
      """)
   Page<Locacao> buscarLocacaoPorCnpj(@Param("cnpj") String cnpj, Pageable pg);
}
