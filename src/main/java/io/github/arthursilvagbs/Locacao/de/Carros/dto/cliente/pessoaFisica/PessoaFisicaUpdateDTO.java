package io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados alteráveis de uma pessoa física.")
public record PessoaFisicaUpdateDTO(

   @Schema(description = "Novo e-mail de contato", example = "maria.nova@exemplo.com")
   @Email(message = "Formato de Email inválido.")
   @NotBlank(message = "Campo 'email' é obrigatório.")
   String email,

   @Schema(description = "Novo telefone de contato", example = "11988887777")
   String telefone,

   @Schema(description = "Novo endereço", example = "Avenida Central, 45, São Paulo - SP")
   String endereco
) {
}
