package io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CNPJ;

public record FilialLocadoraCreateDTO(

   @NotBlank(message = "Campo 'nomeFilial' é obrigatório.")
   String nomeFilial,

   @CNPJ(message = "Formato de CNPJ inválido.")
   @NotBlank(message = "Campo 'cnpjFilial' é obrigatório.")
   String cnpjFilial,

   @NotBlank(message = "O campo 'uf' é obrigatório.")
   String uf,

   @NotBlank(message = "O campo 'cidade' é obrigatório.")
   String cidade,

   @NotBlank(message = "O campo 'uf' é obrigatório.")
   String endereco,

   String telefone,

   String email
) {
}
