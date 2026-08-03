package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.ConfirmarDevolucaoDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.ConfirmarRetiradaDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.service.LocacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locacao")
@RequiredArgsConstructor
public class LocacaoController {

   private final LocacaoService service;

   @PostMapping
   public ResponseEntity<LocacaoResponseDTO> reservaLocacao(
      @Valid @RequestBody LocacaoCreateDTO dto
   ) {
      LocacaoResponseDTO response = service.criarReservaDoVeiculo(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @PutMapping("/retirada/{id}")
   public ResponseEntity<LocacaoResponseDTO> confirmarRetirada(
      @Valid @RequestBody ConfirmarRetiradaDTO dto,
      @PathVariable String id
   ) {
      LocacaoResponseDTO response = service.confirmarRetirada(dto,id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @PutMapping("/devolucao/{id}")
   public ResponseEntity<LocacaoResponseDTO> confirmarDevolucao(
      @Valid @RequestBody ConfirmarDevolucaoDTO dto,
      @PathVariable String id
   ) {
      LocacaoResponseDTO response = service.confirmarDevolucao(dto,id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @PutMapping("/{id}")
   public ResponseEntity<LocacaoResponseDTO> cancelarLocacao(
      @PathVariable String id
   ) {
      LocacaoResponseDTO response = service.cancelarLocacao(id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }
}
