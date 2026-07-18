package io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica;

public record PessoaJuridicaCreateDTO(
        String nome,
        String email,
        String telefone,
        String endereco,
        String cnpj
) {
}
