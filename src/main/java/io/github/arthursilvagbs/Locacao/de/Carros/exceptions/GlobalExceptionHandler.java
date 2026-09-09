package io.github.arthursilvagbs.Locacao.de.Carros.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

   @ExceptionHandler(EntidadeNaoEncontradaException.class)
   public ResponseEntity<String> handlerEntidadeNaoEncontrada(EntidadeNaoEncontradaException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
   }

   @ExceptionHandler(RegistroDuplicadoException.class)
   public ResponseEntity<String> handlerRegistroDuplicado(RegistroDuplicadoException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
   }

   @ExceptionHandler(StatusInvalidoException.class)
   public ResponseEntity<String> handlerStatusInvalido(StatusInvalidoException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
   }

   @ExceptionHandler(VeiculoNaoDisponivelException.class)
   public ResponseEntity<String> handlerVeiculoNaoDisponivel(VeiculoNaoDisponivelException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
   }

}
