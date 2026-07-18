package io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;

import java.util.List;

public record FilialLocadoraUpdateDTO(
        List<Locacao> locacoesRetiradas,
        List<Locacao> locacoesDevolucoes
) {
}
