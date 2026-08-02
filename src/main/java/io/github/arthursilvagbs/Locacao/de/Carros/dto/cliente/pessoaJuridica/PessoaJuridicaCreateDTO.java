package io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

public record PessoaJuridicaCreateDTO(

   @NotBlank(message = "Campo 'nome' é obrigatório.")
   String nome,

   @Email(message = "Formato de Email inválido.")
   @NotBlank(message = "Campo 'email' é obrigatório.")
   String email,

   String telefone,

   String endereco,

   @CNPJ(message = "Formato de CNPJ inválido.")
   @NotBlank(message = "Campo 'CNPJ' é obrigatório.")
   String cnpj
) {
}
