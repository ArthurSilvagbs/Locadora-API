package io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora;

public record FilialLocadoraCreateDTO(
        String nomeFilial,
        String cnpjFilial,
        String uf,
        String cidade,
        String endereco,
        String telefone,
        String email
) {
}
