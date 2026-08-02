package io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora;

import jakarta.validation.constraints.NotBlank;

public record FilialLocadoraUpdateDTO(

   @NotBlank(message = "O campo 'endereco' é obrigatório")
   String endereco,
   String telefone,
   String email
) {
}
