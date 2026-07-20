package io.github.arthursilvagbs.Locacao.de.Carros.mapper;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaFisica;
import org.springframework.stereotype.Component;

@Component
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

    public PessoaFisicaResponseDTO mapearParaResponse(PessoaFisica entidade) {
       return new PessoaFisicaResponseDTO(
          entidade.getIdClienteq(),
          entidade.getNome(),
          entidade.getEmail(),
          entidade.getTelefone(),
          entidade.getEndereco(),
          entidade.getCpf(),
          entidade.getCreatedAt()
       );
    }
}
