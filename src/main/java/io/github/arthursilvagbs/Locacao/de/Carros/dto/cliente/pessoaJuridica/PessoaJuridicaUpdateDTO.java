package io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PessoaJuridicaUpdateDTO(

   @NotBlank(message = "Campo 'nome' é obrigatório.")
   String nome,

   @Email(message = "Formato de Email inválido.")
   @NotBlank(message = "Campo 'email' é obrigatório.")
   String email,

   String telefone,

   String endereco
) {
}
