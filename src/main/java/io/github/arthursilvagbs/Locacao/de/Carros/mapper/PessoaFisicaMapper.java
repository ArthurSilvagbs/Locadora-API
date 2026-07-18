package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaFisica;

public class PessoaFisicaMapper {

    public PessoaFisica mapearParaPessoaFisica(PessoaFisicaCreateDTO dto) {
        return new PessoaFisica(
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.endereco(),
                dto.cpf()
        );
    }
}
