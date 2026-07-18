package io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica;

public record PessoaFisicaCreateDTO(
        String nome,
        String email,
        String telefone,
        String endereco,
        String cpf
) {
}
