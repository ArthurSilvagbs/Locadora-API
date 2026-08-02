package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.service.PessoaJuridicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pessoa-juridica")
@RequiredArgsConstructor
public class PessoaJuridicaController {

   private final PessoaJuridicaService service;

   @PostMapping
   public ResponseEntity<PessoaJuridicaResponseDTO> criarPessoaJuridica(
      @Valid @RequestBody PessoaJuridicaCreateDTO dto
   ) {
      PessoaJuridicaResponseDTO response = service.criarPessoaJuridica(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @GetMapping("/{id}")
   public ResponseEntity<PessoaJuridicaResponseDTO> buscarPessoaJuridicaPorId(
      @PathVariable String id
   ) {
      PessoaJuridicaResponseDTO response = service.buscarPessoaJuridicaPorId(id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @GetMapping("/cnpj/{cnpj}")
   public ResponseEntity<PessoaJuridicaResponseDTO> buscarPessoaJuridicaPorCnpj(
      @PathVariable String cnpj
   ) {
      PessoaJuridicaResponseDTO response = service.buscarPessoaJuridicaPorCnpj(cnpj);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @GetMapping
   public ResponseEntity<Page<PessoaJuridicaResponseDTO>> buscarTodosPaginado() {
      Page<PessoaJuridicaResponseDTO> responsePaginada = service.buscarTodosPaginado();
      return ResponseEntity.status(HttpStatus.OK).body(responsePaginada);
   }

   @PutMapping("/{id}")
   public ResponseEntity<PessoaJuridicaResponseDTO> atualizarPessoaJuridicaPorId(
      @Valid @RequestBody PessoaJuridicaUpdateDTO dto,
      @PathVariable String id
   ) {
      PessoaJuridicaResponseDTO response = service.atualizarPessoaJuridicaViaId(dto, id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @PutMapping("/cnpj/{cnpj}")
   public ResponseEntity<PessoaJuridicaResponseDTO> atualizarPessoaJuridicaPorCnpj(
      @Valid @RequestBody PessoaJuridicaUpdateDTO dto,
      @PathVariable String cnpj
   ) {
      PessoaJuridicaResponseDTO response = service.atualizarPessoaJuridicaViaCnpj(dto, cnpj);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deletarPessoaJuridicaPorId(
      @PathVariable String id
   ) {
      service.deletarPessoaJuridicaViaId(id);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }
}
