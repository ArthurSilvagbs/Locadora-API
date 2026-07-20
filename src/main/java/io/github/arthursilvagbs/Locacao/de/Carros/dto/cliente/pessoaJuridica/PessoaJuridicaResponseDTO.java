package io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica;

import java.time.LocalDateTime;
import java.util.UUID;

public record PessoaJuridicaResponseDTO(
        UUID id,
        String nome,
        String email,
        String telefone,
        String endereco,
        String cnpj,
        LocalDateTime createdAt
) {
}
