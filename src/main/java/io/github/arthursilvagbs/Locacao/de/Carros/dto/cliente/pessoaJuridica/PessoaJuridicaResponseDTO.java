package io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;

import java.util.List;
import java.util.UUID;

public record PessoaJuridicaResponseDTO(
        UUID id,
        String nome,
        String email,
        String telefone,
        String endereco,
        String cnpj,
        List<Locacao> locacoes
) {
}
