package io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;

import java.util.List;
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
        List<Locacao> locacoesRetiradas,
        List<Locacao> locacoesDevolucoes
) {
}
