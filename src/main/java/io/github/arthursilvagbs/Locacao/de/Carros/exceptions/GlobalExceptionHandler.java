package io.github.arthursilvagbs.Locacao.de.Carros.exceptions;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

   @ExceptionHandler(EntidadeNaoEncontradaException.class)
   public ResponseEntity<ErrorResponse> handlerEntidadeNaoEncontrada(
      EntidadeNaoEncontradaException e,
      HttpServletRequest request
   ) {
      ErrorResponse response = new ErrorResponse(
         404,
         "NOT FOUND",
         e.getMessage(),
         request.getRequestURI(),
         LocalDateTime.now()
      );

      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
   }

   @ExceptionHandler(RegistroDuplicadoException.class)
   public ResponseEntity<ErrorResponse> handlerRegistroDuplicado(
      RegistroDuplicadoException e,
      HttpServletRequest request
   ) {
      ErrorResponse response = new ErrorResponse(
         409,
         "CONFLICT",
         e.getMessage(),
         request.getRequestURI(),
         LocalDateTime.now()
      );

      return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
   }

   @ExceptionHandler(StatusInvalidoException.class)
   public ResponseEntity<ErrorResponse> handlerStatusInvalido(
      StatusInvalidoException e,
      HttpServletRequest request
   ) {
      ErrorResponse response = new ErrorResponse(
         400,
         "BAD REQUEST",
         e.getMessage(),
         request.getRequestURI(),
         LocalDateTime.now()
      );

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
   }

   @ExceptionHandler(DadosIncompativeisException.class)
   public ResponseEntity<ErrorResponse> handlerDadosIncompativeis(
      DadosIncompativeisException e,
      HttpServletRequest request
   ) {
      ErrorResponse response = new ErrorResponse(
         400,
         "BAD REQUEST",
         e.getMessage(),
         request.getRequestURI(),
         LocalDateTime.now()
      );

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
   }

   @ExceptionHandler(VeiculoNaoDisponivelException.class)
   public ResponseEntity<ErrorResponse> handlerVeiculoNaoDisponivel(
      VeiculoNaoDisponivelException e,
      HttpServletRequest request
   ) {
      ErrorResponse response = new ErrorResponse(
         404,
         "NOT FOUND",
         e.getMessage(),
         request.getRequestURI(),
         LocalDateTime.now()
      );

      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
   }

   @ExceptionHandler(ClienteSemLocacoesRegistradasException.class)
   public ResponseEntity<ErrorResponse> handlerClienteSemLocacoesRegistradas(
      ClienteSemLocacoesRegistradasException e,
      HttpServletRequest request
   ) {
      ErrorResponse response = new ErrorResponse(
         404,
         "NOT FOUND",
         e.getMessage(),
         request.getRequestURI(),
         LocalDateTime.now()
      );

      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValid(
      MethodArgumentNotValidException e,
      HttpServletRequest request
   ) {
      ErrorResponse response = new ErrorResponse(
         400,
         "BAD REQUEST",
         "Corpo da requisição inválido",
         request.getRequestURI(),
         LocalDateTime.now()
      );

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
   }

   @ExceptionHandler(HttpMessageNotReadableException.class)
   public ResponseEntity<ErrorResponse> handlerHttpMessageNotReadable(
      HttpMessageNotReadableException e,
      HttpServletRequest request
   ) {
      ErrorResponse response = new ErrorResponse(
         400,
         "BAD REQUEST",
         "Corpo da requisição inválido",
         request.getRequestURI(),
         LocalDateTime.now()
      );

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
   }

   @ExceptionHandler(BadCredentialsException.class)
   public ResponseEntity<ErrorResponse> handlerBadCredentials(
      BadCredentialsException e,
      HttpServletRequest request
   ) {
      ErrorResponse response = new ErrorResponse(
         401,
         "UNAUTHORIZED",
         e.getMessage(),
         request.getRequestURI(),
         LocalDateTime.now()
      );

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
   }

   @ExceptionHandler(AccessDeniedException.class)
   public ResponseEntity<ErrorResponse> handlerAccessDenied(
      AccessDeniedException e,
      HttpServletRequest request
   ) {
      ErrorResponse response = new ErrorResponse(
         403,
         "FORBIDDEN",
         e.getMessage(),
         request.getRequestURI(),
         LocalDateTime.now()
      );

      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
   }

}
