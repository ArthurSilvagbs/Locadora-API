package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaJuridica;
import org.springframework.stereotype.Component;

@Component
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

    public PessoaJuridicaResponseDTO mapearParaResponse(PessoaJuridica entidade) {
       return new PessoaJuridicaResponseDTO(
          entidade.getIdClienteq(),
          entidade.getNome(),
          entidade.getEmail(),
          entidade.getTelefone(),
          entidade.getEndereco(),
          entidade.getCnpj(),
          entidade.getCreatedAt()
       );
    }
}
