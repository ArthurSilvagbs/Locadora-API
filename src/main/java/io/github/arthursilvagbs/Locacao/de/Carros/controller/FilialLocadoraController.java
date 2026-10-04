package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.error.ErrorResponse;
import io.github.arthursilvagbs.Locacao.de.Carros.service.FilialLocadoraService;
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
@RequestMapping("/filial-locadora")
@RequiredArgsConstructor
@Tag(name = "Filial locadora", description = "Cadastro, consulta, atualização e exclusão de filiais da locadora.")
@SecurityRequirement(name = "bearerAuth")
public class FilialLocadoraController {

   private final FilialLocadoraService service;

   @Operation(summary = "Cadastrar filial da locadora", description = "Cria uma filial com CNPJ único. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "201",
         description = "Filial cadastrada",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = FilialLocadoraResponseDTO.class))
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
                 "path": "/filial-locadora",
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
         responseCode = "409",
         description = "CNPJ já cadastrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 409,
                 "error": "CONFLICT",
                 "message": "CNPJ já cadastrado.",
                 "path": "/filial-locadora",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @PostMapping
   public ResponseEntity<FilialLocadoraResponseDTO> criarFilialLocadora(
      @Valid @RequestBody FilialLocadoraCreateDTO dto
   ) {
      FilialLocadoraResponseDTO response = service.criarFilialLocadora(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @Operation(summary = "Buscar filial por ID", description = "Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Filial encontrada",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = FilialLocadoraResponseDTO.class))
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
                 "message": "Filial não encontrada.",
                 "path": "/filial-locadora/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @GetMapping("/{id}")
   public ResponseEntity<FilialLocadoraResponseDTO> buscarFilialPorId(
      @Parameter(description = "UUID da filial", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      FilialLocadoraResponseDTO response = service.buscarFilialLocadoraPorId(id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Listar filiais", description = "Retorna a primeira página, com até 10 filiais, ordenadas por nome. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Página de filiais retornada",
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
   public ResponseEntity<Page<FilialLocadoraResponseDTO>> buscarTodosPaginado() {
      Page<FilialLocadoraResponseDTO> responsePaginado = service.buscarTodosPaginado();
      return ResponseEntity.status(HttpStatus.OK).body(responsePaginado);
   }

   @Operation(summary = "Atualizar filial por ID", description = "Altera endereço, telefone e e-mail. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Filial atualizada",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = FilialLocadoraResponseDTO.class))
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
                 "path": "/filial-locadora/550e8400-e29b-41d4-a716-446655440000",
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
                 "message": "Filial não encontrada.",
                 "path": "/filial-locadora/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @PutMapping("/{id}")
   public ResponseEntity<FilialLocadoraResponseDTO> atualizarFilialPorId(
      @Valid @RequestBody FilialLocadoraUpdateDTO dto,
      @Parameter(description = "UUID da filial", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      FilialLocadoraResponseDTO response = service.atualizarFilialLocadoraPorId(dto, id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Excluir filial por ID", description = "Exclui o cadastro da filial. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "204",
         description = "Filial excluída",
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
         description = "Filial não encontrada",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "Filial não encontrada.",
                 "path": "/filial-locadora/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deletarFilialPorId(
      @Parameter(description = "UUID da filial", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      service.deletarFilialLocadoraPorId(id);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

}
