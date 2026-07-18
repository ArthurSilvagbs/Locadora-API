package io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica;

import io.github.arthursilvagbs.Locacao.de.Carros.entity.Locacao;

import java.util.List;
import java.util.UUID;

public record PessoaFisicaResponseDTO(
        UUID idCliente,
        String nome,
        String email,
        String telefone,
        String endereco,
        String cpf,
        List<Locacao> locacoes
) {
}
