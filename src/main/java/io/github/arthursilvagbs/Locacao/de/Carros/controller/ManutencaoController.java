package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.error.ErrorResponse;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.manutencao.ManutencaoUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.service.ManutencaoService;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manutencao")
@RequiredArgsConstructor
@Tag(name = "Manutenção", description = "Cadastro, consulta, atualização e exclusão de manutenções de veículos.")
@SecurityRequirement(name = "bearerAuth")
public class ManutencaoController {

   private final ManutencaoService service;

   @Operation(summary = "Cadastrar manutenção", description = "Registra uma manutenção e altera o status do veículo para em manutenção. Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "201",
         description = "Manutenção cadastrada",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = ManutencaoResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "400",
         description = "Dados de entrada inválidos ou status do veículo incompatível",
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
                       "path": "/manutencao",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               ),
               @ExampleObject(
                  name = "Veículo locado",
                  value = """
                     {
                       "status": 400,
                       "error": "BAD REQUEST",
                       "message": "O veículo está locado no momento.",
                       "path": "/manutencao",
                       "timestamp": "2026-09-29T12:40:00"
                     }
                     """
               ),
               @ExampleObject(
                  name = "Veículo em manutenção",
                  value = """
                     {
                       "status": 400,
                       "error": "BAD REQUEST",
                       "message": "O veículo já está em manutenção.",
                       "path": "/manutencao",
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
         description = "Veículo não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Veículo não encontrado.",
                 "path": "/manutencao",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @PostMapping
   public ResponseEntity<ManutencaoResponseDTO> criarManutencao(
      @Valid @RequestBody ManutencaoCreateDTO dto
   ) {
      ManutencaoResponseDTO response = service.criarManutencao(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @Operation(summary = "Buscar manutenção por ID", description = "Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Manutenção encontrada",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = ManutencaoResponseDTO.class))
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
         description = "Manutenção não encontrada",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Manutenção não encontrada.",
                 "path": "/manutencao/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @GetMapping("/{id}")
   public ResponseEntity<ManutencaoResponseDTO> buscarManutencaoPorId(
      @Parameter(description = "UUID da manutenção", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      ManutencaoResponseDTO response = service.buscarManutencaoPorId(id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Listar manutenções", description = "Retorna a primeira página, com até 10 manutenções, ordenadas pela data de manutenção decrescente. Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Página de manutenções retornada",
         useReturnTypeSchema = true
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
      )
   })
   @GetMapping
   public ResponseEntity<Page<ManutencaoResponseDTO>> buscarTodosPagindado() {
      Page<ManutencaoResponseDTO> responsePaginado = service.buscarTodosPaginado();
      return ResponseEntity.status(HttpStatus.OK).body(responsePaginado);
   }

   @Operation(summary = "Atualizar manutenção por ID", description = "Altera data, descrição e valor da manutenção. Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Manutenção atualizada",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = ManutencaoResponseDTO.class))
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
                 "path": "/manutencao/550e8400-e29b-41d4-a716-446655440000",
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
         description = "Manutenção não encontrada",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Manutenção não encontrada.",
                 "path": "/manutencao/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @PutMapping("/{id}")
   public ResponseEntity<ManutencaoResponseDTO> atualizarManutecaoPorId(
      @Valid @RequestBody ManutencaoUpdateDTO dto,
      @Parameter(description = "UUID da manutenção", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      ManutencaoResponseDTO response = service.atualizarManutencaoPorId(dto, id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Excluir manutenção por ID", description = "Exclui o registro da manutenção. Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "204",
         description = "Manutenção excluída",
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
         description = "Manutenção não encontrada",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Manutenção não encontrada.",
                 "path": "/manutencao/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deletarManutencaoPorId(
      @Parameter(description = "UUID da manutenção", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      service.deletarManutencaoPorId(id);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

}
