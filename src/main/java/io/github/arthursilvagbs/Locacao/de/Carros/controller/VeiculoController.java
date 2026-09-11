package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.VeiculoUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.service.VeiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/veiculo")
@RequiredArgsConstructor
public class VeiculoController {

   private final VeiculoService service;

   @PostMapping
   public ResponseEntity<VeiculoResponseDTO> criarVeiculo(
      @Valid @RequestBody VeiculoCreateDTO dto
   ) {
      VeiculoResponseDTO response = service.cadastrarVeiculo(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @GetMapping("/{id}")
   public ResponseEntity<VeiculoResponseDTO> buscarVeiculoPorId(
      @PathVariable String id
   ) {
      VeiculoResponseDTO response = service.buscarVeiculoPorId(id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @GetMapping("/num-chassi/{numChassi}")
   public ResponseEntity<VeiculoResponseDTO> buscarVeiculoPorNumChassi(
      @PathVariable String numChassi
   ) {
      VeiculoResponseDTO response = service.buscarVeiculoPorNumChassi(numChassi);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @GetMapping("/placa/{placa}")
   public ResponseEntity<VeiculoResponseDTO> buscarVeiculoPorPlaca(
      @PathVariable String placa
   ) {
      VeiculoResponseDTO response = service.buscarVeiculoPorPlaca(placa);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @GetMapping("/renavam/{renavam}")
   public ResponseEntity<VeiculoResponseDTO> buscarVeiculoPorRenavam(
      @PathVariable String renavam
   ) {
      VeiculoResponseDTO response = service.buscarVeiculoPorRenavam(renavam);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @PutMapping("/{id}")
   public ResponseEntity<VeiculoResponseDTO> atualizarVeiculoPorId(
      @Valid @RequestBody VeiculoUpdateDTO dto,
      @PathVariable String id
   ) {
      VeiculoResponseDTO response = service.atualizarVeiculoPorId(dto, id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @PutMapping("/num-chassi/{numChassi}")
   public ResponseEntity<VeiculoResponseDTO> atualizarVeiculoPorNumChassi(
      @Valid @RequestBody VeiculoUpdateDTO dto,
      @PathVariable String numChassi
   ) {
      VeiculoResponseDTO response = service.atualizarVeiculoPorNumChassi(dto, numChassi);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @PutMapping("/placa/{placa}")
   public ResponseEntity<VeiculoResponseDTO> atualizarVeiculoPorPlaca(
      @Valid @RequestBody VeiculoUpdateDTO dto,
      @PathVariable String placa
   ) {
      VeiculoResponseDTO response = service.atualizarVeiculoPorPlaca(dto, placa);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deletarVeiculoId(
      @PathVariable String id
   ) {
      service.deletarVeiculoViaId(id);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

   @DeleteMapping("/num-chassi/{numChassi}")
   public ResponseEntity<Void> deletarVeiculoNumChassi(
      @PathVariable String numChassi
   ) {
      service.deletarVeiculoViaNumChassi(numChassi);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }
}
