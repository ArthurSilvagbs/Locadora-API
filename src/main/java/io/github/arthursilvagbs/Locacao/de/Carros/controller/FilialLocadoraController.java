package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.service.FilialLocadoraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/filial-locadora")
@RequiredArgsConstructor
public class FilialLocadoraController {

   private final FilialLocadoraService service;

   @PostMapping
   public ResponseEntity<FilialLocadoraResponseDTO> criarFilialLocadora(
      @Valid @RequestBody FilialLocadoraCreateDTO dto
   ) {
      FilialLocadoraResponseDTO response = service.criarFilialLocadora(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @GetMapping("/{id}")
   public ResponseEntity<FilialLocadoraResponseDTO> buscarFilialPorId(
      @PathVariable String id
   ) {
      FilialLocadoraResponseDTO response = service.buscarFilialLocadoraPorId(id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @GetMapping
   public ResponseEntity<Page<FilialLocadoraResponseDTO>> buscarTodosPaginado() {
      Page<FilialLocadoraResponseDTO> responsePaginado = service.buscarTodosPaginado();
      return ResponseEntity.status(HttpStatus.OK).body(responsePaginado);
   }

   @PutMapping("/{id}")
   public ResponseEntity<FilialLocadoraResponseDTO> atualizarFilialPorId(
      @Valid @RequestBody FilialLocadoraUpdateDTO dto,
      @PathVariable String id
   ) {
      FilialLocadoraResponseDTO response = service.atualizarFilialLocadoraPorId(dto, id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deletarFilialPorId(
      @PathVariable String id
   ) {
      service.deletarFilialLocadoraPorId(id);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

}
