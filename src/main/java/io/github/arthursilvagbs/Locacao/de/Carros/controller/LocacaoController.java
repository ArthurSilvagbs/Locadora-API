package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.error.ErrorResponse;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.ConfirmarDevolucaoDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.ConfirmarRetiradaDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.service.LocacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locacao")
@RequiredArgsConstructor
@Tag(name = "Locação", description = "Reserva, consulta, retirada, devolução e cancelamento de locações.")
@SecurityRequirement(name = "bearerAuth")
public class LocacaoController {

   private final LocacaoService service;

   @Operation(summary = "Reservar locação", description = "Exige autenticação. Cria uma reserva para a categoria de veículo informada, pendente de retirada.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "201",
         description = "Reserva criada",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocacaoResponseDTO.class))
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
                 "path": "/locacao",
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
         description = "Cliente ou filial não encontrada",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
               @ExampleObject(
                  name = "Cliente não encontrado",
                  value = """
                     {
                       "status": 404,
                       "error": "NOT FOUND",
                       "message": "Cliente não encontrado.",
                       "path": "/locacao",
                       "timestamp": "2026-09-29T12:40:00"
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
                       "path": "/locacao",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               )
            }
         )
      )
   })
   @PostMapping
   public ResponseEntity<LocacaoResponseDTO> reservaLocacao(
      @Valid @RequestBody LocacaoCreateDTO dto
   ) {
      LocacaoResponseDTO response = service.criarReservaDaCategoriaDeVeiculo(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @Operation(summary = "Buscar locação por ID", description = "Exige autenticação.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Locação encontrada",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocacaoResponseDTO.class))
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
         description = "Locação não encontrada",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Locação não encontrada.",
                 "path": "/locacao/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @GetMapping("/{id}")
   public ResponseEntity<LocacaoResponseDTO> buscarLocacaoPorId(
      @Parameter(description = "UUID da locação", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable("id") String id
   ) {
      LocacaoResponseDTO response = service.buscarLocacaoPorId(id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Confirmar retirada da locação", description = "Vincula um veículo disponível da categoria e filial da reserva, registrando sua quilometragem de retirada. Exige autenticação.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Retirada confirmada",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocacaoResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "400",
         description = "Dados inválidos, status incompatível ou veículo incompatível com a reserva",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
               @ExampleObject(
                  name = "Dados inválidos",
                  value = """
                     {
                       "status": 400,
                       "error": "BAD REQUEST",
                       "message": "Corpo da requisição inválido",
                       "path": "/locacao/retirada/550e8400-e29b-41d4-a716-446655440000",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               ),
               @ExampleObject(
                  name = "Status inválido",
                  value = """
                     {
                       "status": 400,
                       "error": "BAD REQUEST",
                       "message": "Status de locação inválido.",
                       "path": "/locacao/retirada/550e8400-e29b-41d4-a716-446655440000",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               ),
               @ExampleObject(
                  name = "Categoria incompatível",
                  value = """
                     {
                       "status": 400,
                       "error": "BAD REQUEST",
                       "message": "O veículo selecionado não pertence a categoria registrada na locação.",
                       "path": "/locacao/retirada/550e8400-e29b-41d4-a716-446655440000",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               ),
               @ExampleObject(
                  name = "Filial incompatível",
                  value = """
                     {
                       "status": 400,
                       "error": "BAD REQUEST",
                       "message": "O veículo não esta com o registro vinculado a esta filial, estando cadastrado em outra filial.",
                       "path": "/locacao/retirada/550e8400-e29b-41d4-a716-446655440000",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               ),
               @ExampleObject(
                  name = "Veículo indisponível",
                  value = """
                     {
                       "status": 400,
                       "error": "BAD REQUEST",
                       "message": "O veículo não estao disponível para retirada.",
                       "path": "/locacao/retirada/550e8400-e29b-41d4-a716-446655440000",
                       "timestamp": "2026-09-29T12:40:00"
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
         description = "Locação ou veículo não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
               @ExampleObject(
                  name = "Locação não encontrada",
                  value = """
                     {
                       "status": 404,
                       "error": "NOT FOUND",
                       "message": "Locação não encontrada.",
                       "path": "/locacao/retirada/550e8400-e29b-41d4-a716-446655440000",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               ),
               @ExampleObject(
                  name = "Veículo não encontrado",
                  value = """
                     {
                       "status": 404,
                       "error": "NOT FOUND",
                       "message": "Veículo não encontrado.",
                       "path": "/locacao/retirada/550e8400-e29b-41d4-a716-446655440000",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               )
            }
         )
      )
   })
   @PutMapping("/retirada/{id}")
   public ResponseEntity<LocacaoResponseDTO> confirmarRetirada(
      @Valid @RequestBody ConfirmarRetiradaDTO dto,
      @Parameter(description = "UUID da locação", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      LocacaoResponseDTO response = service.confirmarRetirada(dto,id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Confirmar devolução da locação", description = "Registra a quilometragem percorrida e torna o veículo disponível novamente. Exige autenticação.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Devolução confirmada",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocacaoResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "400",
         description = "Dados de entrada inválidos ou status da locação incompatível",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
               @ExampleObject(
                  name = "Dados inválidos",
                  value = """
                     {
                       "status": 400,
                       "error": "BAD REQUEST",
                       "message": "Corpo da requisição inválido",
                       "path": "/locacao/devolucao/550e8400-e29b-41d4-a716-446655440000",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               ),
               @ExampleObject(
                  name = "Status inválido",
                  value = """
                     {
                       "status": 400,
                       "error": "BAD REQUEST",
                       "message": "Status de locação inválido.",
                       "path": "/locacao/devolucao/550e8400-e29b-41d4-a716-446655440000",
                       "timestamp": "2026-09-29T12:40:00"
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
         description = "Locação não encontrada",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
               @ExampleObject(value = """
                  {
                     "status": 404,
                     "error": "NOT FOUND",
                     "message": "Locação não encontrada.",
                     "path": "/locacao/devolucao/550e8400-e29b-41d4-a716-446655440000",
                     "timestamp": "2026-09-29T12:40:00"
                  }
               """),
               @ExampleObject(value = """
                  {
                     "status": 404,
                     "error": "NOT FOUND",
                     "message": "Filial não econtrada.",
                     "path": "/locacao/devolucao/550e8400-e29b-41d4-a716-446655440000",
                     "timestamp": "2026-09-29T12:40:00"
                  }
               """)
            }
         )
      )
   })
   @PutMapping("/devolucao/{id}")
   public ResponseEntity<LocacaoResponseDTO> confirmarDevolucao(
      @Valid @RequestBody ConfirmarDevolucaoDTO dto,
      @Parameter(description = "UUID da locação", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      LocacaoResponseDTO response = service.confirmarDevolucao(dto, id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Cancelar locação", description = "Cancela uma locação pendente de retirada. Exige autenticação.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Locação cancelada",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocacaoResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "400",
         description = "Locação já cancelada ou com status incompatível",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
               @ExampleObject(
                  name = "Já cancelada",
                  value = """
                     {
                       "status": 400,
                       "error": "BAD REQUEST",
                       "message": "A locação já está com o status 'CANCELADA'.",
                       "path": "/locacao/cancelar/550e8400-e29b-41d4-a716-446655440000",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               ),
               @ExampleObject(
                  name = "Status inválido",
                  value = """
                     {
                       "status": 400,
                       "error": "BAD REQUEST",
                       "message": "Status de locação inválido.",
                       "path": "/locacao/cancelar/550e8400-e29b-41d4-a716-446655440000",
                       "timestamp": "2026-09-29T12:40:00"
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
         description = "Locação não encontrada",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Locação não encontrada.",
                 "path": "/locacao/cancelar/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @PutMapping("cancelar/{id}")
   public ResponseEntity<LocacaoResponseDTO> cancelarLocacao(
      @Parameter(description = "UUID da locação", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      LocacaoResponseDTO response = service.cancelarLocacao(id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }
}
