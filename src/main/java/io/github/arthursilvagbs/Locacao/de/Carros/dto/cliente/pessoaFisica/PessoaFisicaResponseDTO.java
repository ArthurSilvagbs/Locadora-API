package io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados retornados para uma pessoa física.")
public record PessoaFisicaResponseDTO(
        @Schema(description = "Identificador do cliente")
        UUID idCliente,
        @Schema(description = "Nome completo")
        String nome,
        @Schema(description = "E-mail")
        String email,
        @Schema(description = "Telefone")
        String telefone,
        @Schema(description = "Endereço")
        String endereco,
        @Schema(description = "CPF")
        String cpf,
        @Schema(description = "Data de cadastro")
        LocalDateTime createdAt
) {
}
