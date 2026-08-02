package io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record PessoaFisicaCreateDTO(

   @NotBlank(message = "Campo 'nome' é obrigatório.")
   String nome,

   @Email(message = "Formato de Email inválido.")
   @NotBlank(message = "Campo 'email' é obrigatório.")
   String email,

   String telefone,

   String endereco,

   @CPF(message = "Formato de CPF inválido.")
   @NotBlank(message = "Campo 'CPF' é obrigatório.")
   String cpf
) {
}
