package io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora;

import java.time.LocalDateTime;
import java.util.UUID;

public record FilialLocadoraResponseDTO(
        UUID id,
        String nomeFilial,
        String cnpjFilial,
        String uf,
        String cidade,
        String endereco,
        String telefone,
        String email,
        LocalDateTime createdAt
) {
}
