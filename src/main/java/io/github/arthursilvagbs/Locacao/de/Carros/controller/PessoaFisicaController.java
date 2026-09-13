package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.service.PessoaFisicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/pessoa-fisica")
@RequiredArgsConstructor
public class PessoaFisicaController {

   private final PessoaFisicaService service;

   @PostMapping
   public ResponseEntity<PessoaFisicaResponseDTO> criarPessoaFisica(
      @Valid @RequestBody PessoaFisicaCreateDTO dto
   ) {
      PessoaFisicaResponseDTO response = service.criarPessoaFisica(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @GetMapping("/{id}")
   public ResponseEntity<PessoaFisicaResponseDTO> encontrarPessoaFisicaPorId(
      @PathVariable String id
   ) {
      PessoaFisicaResponseDTO response = service.buscarPessoaFisicaPorId(id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @GetMapping("/cpf/{cpf}")
   public ResponseEntity<PessoaFisicaResponseDTO> encontrarPessoaFisicaPorCpf(
      @PathVariable String cpf
   ) {
      PessoaFisicaResponseDTO response = service.buscarPessoaFisicaPorCpf(cpf);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @GetMapping
   public ResponseEntity<Page<PessoaFisicaResponseDTO>> buscarTodosPaginado() {
      Page<PessoaFisicaResponseDTO> responsePaginado = service.buscarTodosPaginado();
      return ResponseEntity.status(HttpStatus.OK).body(responsePaginado);
   }

   @PutMapping("/{id}")
   public ResponseEntity<PessoaFisicaResponseDTO> atualizarPessoaFisicaPorId(
      @Valid @RequestBody PessoaFisicaUpdateDTO dto,
      @PathVariable String id
   ) {
      PessoaFisicaResponseDTO response = service.atualizarPessoaFisicaPorId(dto, id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @PutMapping("/cpf/{cpf}")
   public ResponseEntity<PessoaFisicaResponseDTO> atualizarPessoaFisicaPorCpf(
      @Valid @RequestBody PessoaFisicaUpdateDTO dto,
      @PathVariable String cpf
   ) {
      PessoaFisicaResponseDTO response = service.atualizarPessoaFisicaPorCpf(dto, cpf);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deletarPessoaFisicaPorId(@PathVariable String id) {
      service.deletarPessoaFisicaPorId(id);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

}
