package io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.br.CPF;

@Schema(description = "Dados necessários para cadastrar uma pessoa física.")
public record PessoaFisicaCreateDTO(

   @Schema(description = "Nome completo do cliente", example = "Maria Oliveira")
   @NotBlank(message = "Campo 'nome' é obrigatório.")
   String nome,

   @Schema(description = "E-mail de contato", example = "maria.oliveira@exemplo.com")
   @Email(message = "Formato de Email inválido.")
   @NotBlank(message = "Campo 'email' é obrigatório.")
   String email,

   @Schema(description = "Telefone de contato", example = "11999998888")
   String telefone,

   @Schema(description = "Endereço do cliente", example = "Rua das Flores, 120, São Paulo - SP")
   String endereco,

   @Schema(description = "CPF válido, sem pontuação", example = "52998224725")
   @CPF(message = "Formato de CPF inválido.")
   @NotBlank(message = "Campo 'CPF' é obrigatório.")
   String cpf
) {
}
