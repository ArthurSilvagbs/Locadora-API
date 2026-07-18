package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaJuridica;

public class PessoaJuridicaMapper {

    public PessoaJuridica mapearParaPessoaJuridica(PessoaJuridicaCreateDTO dto) {
        return new PessoaJuridica(
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.endereco(),
                dto.cnpj()
        );
    }
}
