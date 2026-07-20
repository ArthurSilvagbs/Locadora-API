package io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica;

import java.time.LocalDateTime;
import java.util.UUID;

public record PessoaFisicaResponseDTO(
        UUID idCliente,
        String nome,
        String email,
        String telefone,
        String endereco,
        String cpf,
        LocalDateTime createdAt
) {
}
