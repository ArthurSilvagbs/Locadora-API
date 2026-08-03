package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.service.ManutencaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manutencao")
@RequiredArgsConstructor
public class ManutencaoController {

   private final ManutencaoService service;

   @PostMapping
   public ResponseEntity<ManutencaoResponseDTO> criarManutencao(
      @Valid @RequestBody ManutencaoCreateDTO dto
   ) {
      ManutencaoResponseDTO response = service.criarManutencao(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @GetMapping("/{id}")
   public ResponseEntity<ManutencaoResponseDTO> buscarManutencaoPorId(
      @PathVariable String id
   ) {
      ManutencaoResponseDTO response = service.buscarManutencaoPorId(id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @GetMapping
   public ResponseEntity<Page<ManutencaoResponseDTO>> buscarTodosPagindado() {
      Page<ManutencaoResponseDTO> responsePaginado = service.buscarTodosPaginado();
      return ResponseEntity.status(HttpStatus.OK).body(responsePaginado);
   }

   @PutMapping("/{id}")
   public ResponseEntity<ManutencaoResponseDTO> atualizarManutecaoPorId(
      @Valid @RequestBody ManutencaoUpdateDTO dto,
      @PathVariable String id
   ) {
      ManutencaoResponseDTO response = service.atualizarManutencaoPorId(dto, id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deletarManutencaoPorId(
      @PathVariable String id
   ) {
      service.deletarManutencaoPorId(id);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

}
