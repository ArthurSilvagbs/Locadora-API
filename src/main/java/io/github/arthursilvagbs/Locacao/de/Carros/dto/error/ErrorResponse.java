package io.github.arthursilvagbs.Locacao.de.Carros.dto.error;

import java.time.LocalDateTime;

public record ErrorResponse(
   int status,
   String error,
   String message,
   String path,
   LocalDateTime timestamp
) {
}
