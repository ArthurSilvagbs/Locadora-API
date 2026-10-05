package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.error.ErrorResponse;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.veiculo.*;
import io.github.arthursilvagbs.Locacao.de.Carros.service.VeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/veiculo")
@RequiredArgsConstructor
@Tag(name = "Veículos", description = "Cadastro, consulta, atualização, exclusão e disponibilidade de veículos.")
@SecurityRequirement(name = "bearerAuth")
public class VeiculoController {

   private final VeiculoService service;

   @Operation(summary = "Cadastrar veículo", description = "Disponível para funcionário, gerente e administrador. Número do chassi, placa e Renavam devem ser únicos.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "201",
         description = "Veículo cadastrado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "400",
         description = "Dados de entrada inválidos",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 400,
                 "error": "BAD REQUEST",
                 "message": "Corpo da requisição inválido",
                 "path": "/veiculo",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Acesso negado ou credenciais ausentes",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Filial não encontrada",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Filial não encontrada",
                 "path": "/veiculo",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      ),
      @ApiResponse(
         responseCode = "409",
         description = "Chassi, placa ou Renavam já cadastrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
               @ExampleObject(
                  name = "Chassi duplicado",
                  value = """
                     {
                       "status": 409,
                       "error": "CONFLICT",
                       "message": "Número do chassi já existente no sistema.",
                       "path": "/veiculo",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               ),
               @ExampleObject(
                  name = "Placa duplicada",
                  value = """
                     {
                       "status": 409,
                       "error": "CONFLICT",
                       "message": "Placa do veículo já existente no sistema.",
                       "path": "/veiculo",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               ),
               @ExampleObject(
                  name = "Renavam duplicado",
                  value = """
                     {
                       "status": 409,
                       "error": "CONFLICT",
                       "message": "Número do Renavam já existente no sistema.",
                       "path": "/veiculo",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               )
            }
         )
      )
   })
   @PostMapping
   public ResponseEntity<VeiculoResponseDTO> criarVeiculo(
      @Valid @RequestBody VeiculoCreateDTO dto
   ) {
      VeiculoResponseDTO response = service.cadastrarVeiculo(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @Operation(summary = "Buscar veículo por ID", description = "Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Veículo encontrado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Acesso negado ou credenciais ausentes",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Veículo não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Veículo não encontrado.",
                 "path": "/veiculo/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @GetMapping("/{id}")
   public ResponseEntity<VeiculoResponseDTO> buscarVeiculoPorId(
      @Parameter(description = "UUID do veículo", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      VeiculoResponseDTO response = service.buscarVeiculoPorId(id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Buscar veículo por número do chassi", description = "Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Veículo encontrado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Acesso negado ou credenciais ausentes",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Veículo não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Veículo não encontrado.",
                 "path": "/veiculo/num-chassi/9BWZZZ377VT004251",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @GetMapping("/num-chassi/{numChassi}")
   public ResponseEntity<VeiculoResponseDTO> buscarVeiculoPorNumChassi(
      @Parameter(description = "Número do chassi do veículo", example = "9BWZZZ377VT004251")
      @PathVariable String numChassi
   ) {
      VeiculoResponseDTO response = service.buscarVeiculoPorNumChassi(numChassi);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Buscar veículo por placa", description = "Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Veículo encontrado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Acesso negado ou credenciais ausentes",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Veículo não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Veículo não encontrado.",
                 "path": "/veiculo/placa/ABC1D23",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @GetMapping("/placa/{placa}")
   public ResponseEntity<VeiculoResponseDTO> buscarVeiculoPorPlaca(
      @Parameter(description = "Placa do veículo, sem pontuação", example = "ABC1D23")
      @PathVariable String placa
   ) {
      VeiculoResponseDTO response = service.buscarVeiculoPorPlaca(placa);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Buscar veículo por Renavam", description = "Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Veículo encontrado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Acesso negado ou credenciais ausentes",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Veículo não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Veículo não encontrado",
                 "path": "/veiculo/renavam/12345678901",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @GetMapping("/renavam/{renavam}")
   public ResponseEntity<VeiculoResponseDTO> buscarVeiculoPorRenavam(
      @Parameter(description = "Número do Renavam do veículo, sem pontuação", example = "12345678901")
      @PathVariable String renavam
   ) {
      VeiculoResponseDTO response = service.buscarVeiculoPorRenavam(renavam);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Listar categorias disponíveis por filial", description = "Consulta pública. Retorna a primeira página, com até 10 categorias disponíveis no período e o valor calculado para a locação.")
   @SecurityRequirements
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Página de categorias disponíveis retornada",
         useReturnTypeSchema = true
      ),
      @ApiResponse(
         responseCode = "400",
         description = "Parâmetros de data inválidos",
         content = @Content
      )
   })
   @GetMapping("/categorias-disponiveis-filial/{idFilial}")
   public ResponseEntity<Page<VeiculoCategoriasDisponiveisResponseDTO>> buscarCategoriaDisponiveisPorFilial(
      @Parameter(description = "UUID da filial", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String idFilial,

      @Parameter(description = "Data e hora de retirada no formato ISO 8601", example = "2026-10-01T10:00:00")
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataRetirada,

      @Parameter(description = "Data e hora de devolução no formato ISO 8601", example = "2026-10-03T10:00:00")
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataDevolucao
   ) {
      return ResponseEntity.status(HttpStatus.OK).body(service.buscarCategoriaDisponiveisPorFilial(idFilial, dataRetirada, dataDevolucao));
   }

   @Operation(summary = "Atualizar veículo por ID", description = "Altera placa, quilometragem e cor. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Veículo atualizado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "400",
         description = "Dados de entrada inválidos",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 400,
                 "error": "BAD REQUEST",
                 "message": "Corpo da requisição inválido",
                 "path": "/veiculo/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Acesso negado ou credenciais ausentes",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Veículo não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Veículo não encontrado.",
                 "path": "/veiculo/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @PutMapping("/{id}")
   public ResponseEntity<VeiculoResponseDTO> atualizarVeiculoPorId(
      @Valid @RequestBody VeiculoUpdateDTO dto,
      @Parameter(description = "UUID do veículo", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      VeiculoResponseDTO response = service.atualizarVeiculoPorId(dto, id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Atualizar veículo por número do chassi", description = "Altera placa, quilometragem e cor. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Veículo atualizado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "400",
         description = "Dados de entrada inválidos",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 400,
                 "error": "BAD REQUEST",
                 "message": "Corpo da requisição inválido",
                 "path": "/veiculo/num-chassi/9BWZZZ377VT004251",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Acesso negado ou credenciais ausentes",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Veículo não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Veículo não encontrado.",
                 "path": "/veiculo/num-chassi/9BWZZZ377VT004251",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @PutMapping("/num-chassi/{numChassi}")
   public ResponseEntity<VeiculoResponseDTO> atualizarVeiculoPorNumChassi(
      @Valid @RequestBody VeiculoUpdateDTO dto,
      @Parameter(description = "Número do chassi do veículo", example = "9BWZZZ377VT004251")
      @PathVariable String numChassi
   ) {
      VeiculoResponseDTO response = service.atualizarVeiculoPorNumChassi(dto, numChassi);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Atualizar veículo por placa", description = "Altera placa, quilometragem e cor. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Veículo atualizado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "400",
         description = "Dados de entrada inválidos",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 400,
                 "error": "BAD REQUEST",
                 "message": "Corpo da requisição inválido",
                 "path": "/veiculo/placa/ABC1D23",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Acesso negado ou credenciais ausentes",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Veículo não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Veículo não encontrado.",
                 "path": "/veiculo/placa/ABC1D23",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @PutMapping("/placa/{placa}")
   public ResponseEntity<VeiculoResponseDTO> atualizarVeiculoPorPlaca(
      @Valid @RequestBody VeiculoUpdateDTO dto,
      @Parameter(description = "Placa atual do veículo, sem pontuação", example = "ABC1D23")
      @PathVariable String placa
   ) {
      VeiculoResponseDTO response = service.atualizarVeiculoPorPlaca(dto, placa);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Transferir veículo entre filiais", description = "Move um veículo disponível para outra filial. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Veículo transferido",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "400",
         description = "Dados inválidos, veículo indisponível ou filial de destino igual à atual",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
               @ExampleObject(
                  name = "Veículo indisponível",
                  value = """
                     {
                       "status": 400,
                       "error": "BAD REQUEST",
                       "message": "O veículo não pode ser transferido por conta de seu status.",
                       "path": "/veiculo/550e8400-e29b-41d4-a716-446655440000/tranferir-filial",
                       "timestamp": "2026-10-04T12:40:00"
                     }
                     """
               ),
               @ExampleObject(
                  name = "Filial atual",
                  value = """
                     {
                       "status": 400,
                       "error": "BAD REQUEST",
                       "message": "O veículo já está na filial indicada.",
                       "path": "/veiculo/550e8400-e29b-41d4-a716-446655440000/tranferir-filial",
                       "timestamp": "2026-10-04T12:40:00"
                     }
                     """
               )
            }
         )
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Acesso negado ou credenciais ausentes",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Veículo ou filial não encontrada",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
               @ExampleObject(
                  name = "Veículo não encontrado",
                  value = """
                     {
                       "status": 404,
                       "error": "NOT FOUND",
                       "message": "Veículo não encontrado.",
                       "path": "/veiculo/550e8400-e29b-41d4-a716-446655440000/tranferir-filial",
                       "timestamp": "2026-10-04T12:40:00"
                     }
                     """
               ),
               @ExampleObject(
                  name = "Filial não encontrada",
                  value = """
                     {
                       "status": 404,
                       "error": "NOT FOUND",
                       "message": "Filial não encontrada.",
                       "path": "/veiculo/550e8400-e29b-41d4-a716-446655440000/tranferir-filial",
                       "timestamp": "2026-10-04T12:40:00"
                     }
                     """
               )
            }
         )
      )
   })
   @PutMapping("/{id}/tranferir-filial")
   public ResponseEntity<VeiculoResponseDTO> tranferirVeiculoDeFilial(
      @Parameter(description = "UUID do veículo", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable("id") String id,
      @Valid @RequestBody VeiculoTranferenciaFilialDTO dto
   ) {
      VeiculoResponseDTO response = service.tranferirVeiculoDeFilial(id, dto);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Excluir veículo por ID", description = "Exclui o cadastro do veículo. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "204",
         description = "Veículo excluído",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Acesso negado ou credenciais ausentes",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Veículo não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Veículo não encontrado.",
                 "path": "/veiculo/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deletarVeiculoId(
      @Parameter(description = "UUID do veículo", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      service.deletarVeiculoViaId(id);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

   @Operation(summary = "Excluir veículo por número do chassi", description = "Exclui o cadastro do veículo. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "204",
         description = "Veículo excluído",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Acesso negado ou credenciais ausentes",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Veículo não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Veículo não encontrado.",
                 "path": "/veiculo/num-chassi/9BWZZZ377VT004251",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @DeleteMapping("/num-chassi/{numChassi}")
   public ResponseEntity<Void> deletarVeiculoNumChassi(
      @Parameter(description = "Número do chassi do veículo", example = "9BWZZZ377VT004251")
      @PathVariable String numChassi
   ) {
      service.deletarVeiculoViaNumChassi(numChassi);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }
}
