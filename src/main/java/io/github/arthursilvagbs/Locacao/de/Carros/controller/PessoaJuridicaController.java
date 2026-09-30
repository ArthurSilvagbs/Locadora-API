package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.error.ErrorResponse;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.service.PessoaJuridicaService;
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
@RequestMapping("/pessoa-juridica")
@RequiredArgsConstructor
@Tag(name = "Pessoa Juridica", description = "Cadastro, consulta, atualização e histórico de locações de clientes pessoa jurídica.")
@SecurityRequirement(name = "bearerAuth")
public class PessoaJuridicaController {

   private final PessoaJuridicaService service;

   @Operation(summary = "Cadastrar pessoa jurídica", description = "Exige autenticação. Cria um cliente pessoa jurídica com CNPJ e eail únicos.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "201",
         description = "Cliente cadastrado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaJuridicaResponseDTO.class))
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
                 "path": "/pessoa-juridica",
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
         description = "CNPJ ou email já cadastrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
               @ExampleObject(value = """
                  {
                     "status": 409,
                     "error": "CONFLICT",
                     "message": "CNPJ já cadastrado.",
                     "path": "/pessoa-juridica",
                     "timestamp": "2026-09-29T12:40:00"
                  }
               """),
               @ExampleObject(value = """
                  {
                     "status": 409,
                     "error": "CONFLICT",
                     "message": "Email já cadastrado.",
                     "path": "/pessoa-juridica",
                     "timestamp": "2026-09-29T12:40:00"
                  }
               """)
            }
         )
      )
   })
   @PostMapping
   public ResponseEntity<PessoaJuridicaResponseDTO> criarPessoaJuridica(
      @Valid @RequestBody PessoaJuridicaCreateDTO dto
   ) {
      PessoaJuridicaResponseDTO response = service.criarPessoaJuridica(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @Operation(summary = "Buscar pessoa jurídica por ID", description = "Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Cliente encontrado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaJuridicaResponseDTO.class))
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
         description = "Cliente não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 404,
                  "error": "NOT FOUND",
                  "message": "Cliente não encontrado.",
                  "path": "/pessoa-juridica/550e8400-e29b-41d4-a716-446655440000",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @GetMapping("/{id}")
   public ResponseEntity<PessoaJuridicaResponseDTO> buscarPessoaJuridicaPorId(
      @Parameter(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      PessoaJuridicaResponseDTO response = service.buscarPessoaJuridicaPorId(id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Buscar pessoa jurídica por CNPJ", description = "Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Cliente encontrado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaJuridicaResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Sem acesso ou sem credenciais",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Cliente não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 404,
                  "error": "NOT FOUND",
                  "message": "Cliente não encontrado.",
                  "path": "/pessoa-juridica/cnpj/12345678000195",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @GetMapping("/cnpj/{cnpj}")
   public ResponseEntity<PessoaJuridicaResponseDTO> buscarPessoaJuridicaPorCnpj(
      @Parameter(description = "CNPJ do cliente, sem pontuação", example = "12345678000195")
      @PathVariable String cnpj
   ) {
      PessoaJuridicaResponseDTO response = service.buscarPessoaJuridicaPorCnpj(cnpj);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Listar pessoas jurídicas", description = "Retorna a primeira página, com até 10 clientes, ordenados por CNPJ. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Página de clientes retornada",
         useReturnTypeSchema = true
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Sem acesso ou sem credenciais",
         content = @Content
      )
   })
   @GetMapping
   public ResponseEntity<Page<PessoaJuridicaResponseDTO>> buscarTodosPaginado() {
      Page<PessoaJuridicaResponseDTO> responsePaginada = service.buscarTodosPaginado();
      return ResponseEntity.status(HttpStatus.OK).body(responsePaginada);
   }

   @Operation(summary = "Listar locações por ID do cliente", description = "Retorna até 10 locações da primeira página, ordenadas pela data de retirada decrescente. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Página de locações retornada",
         useReturnTypeSchema = true
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Sem acesso ou sem credenciais",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Cliente não encontrado ou sem locações",
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
                 "message": "Cliente com o ID indicado não encontrado.",
                 "path": "/pessoa-juridica/locacoes/id/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """
               ),
               @ExampleObject(
                  name = "Cliente sem locações",
                  value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "O cliente não possui nenhuma locação registrada",
                 "path": "/pessoa-juridica/locacoes/id/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """
               )
            }
         )
      )
   })
   @GetMapping("/locacoes/id/{idCliente}")
   public ResponseEntity<Page<LocacaoResponseDTO>> buscarLocacoesPorPessoaJuridicaId(
      @Parameter(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable("idCliente") String idCliente
   ) {
      Page<LocacaoResponseDTO> responsePaginada = service.buscarLocacoesClientePorId(idCliente);
      return ResponseEntity.status(HttpStatus.OK).body(responsePaginada);
   }

   @Operation(summary = "Listar locações por CNPJ", description = "Retorna até 10 locações da primeira página, ordenadas pela data de retirada decrescente. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Página de locações retornada",
         useReturnTypeSchema = true
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Sem acesso ou sem credenciais",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Cliente não encontrado ou sem locações",
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
                 "message": "Cliente com o CNPJ indicado não encontrado.",
                 "path": "/pessoa-juridica/locacoes/cnpj/12345678000195",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """
               ),
               @ExampleObject(
                  name = "Cliente sem locações",
                  value = """
               {
                 "status": 404,
                 "error": "NOT FOUND",
                 "message": "O cliente não possui nenhuma locação registrada",
                 "path": "/pessoa-juridica/locacoes/cnpj/12345678000195",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """
               )
            }
         )
      )
   })
   @GetMapping("/locacoes/cnpj/{cnpj}")
   public ResponseEntity<Page<LocacaoResponseDTO>> buscarLocacoesPorPessoaJuridicaCnpj(
      @Parameter(description = "CNPJ do cliente, sem pontuação", example = "12345678000195")
      @PathVariable("cnpj") String cnpj
   ) {
      Page<LocacaoResponseDTO> responsePaginada = service.buscarLocacoesClientePorCnpj(cnpj);
      return ResponseEntity.status(HttpStatus.OK).body(responsePaginada);
   }

   @Operation(summary = "Atualizar pessoa jurídica por ID", description = "Altera e-mail, telefone e endereço. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Cliente atualizado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaJuridicaResponseDTO.class))
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
                 "path": "/pessoa-juridica/550e8400-e29b-41d4-a716-446655440000",
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
         description = "Sem acesso ou sem credenciais",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Cliente não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 404,
                  "error": "NOT FOUND",
                  "message": "Cliente não encontrado.",
                  "path": "/pessoa-juridica/550e8400-e29b-41d4-a716-446655440000",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @PutMapping("/{id}")
   public ResponseEntity<PessoaJuridicaResponseDTO> atualizarPessoaJuridicaPorId(
      @Valid @RequestBody PessoaJuridicaUpdateDTO dto,
      @Parameter(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      PessoaJuridicaResponseDTO response = service.atualizarPessoaJuridicaViaId(dto, id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Atualizar pessoa jurídica por CNPJ", description = "Altera e-mail, telefone e endereço. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Cliente atualizado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaJuridicaResponseDTO.class))
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
                 "path": "/pessoa-juridica/cnpj/12345678000195",
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
         description = "Sem acesso ou sem credenciais",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Cliente não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 404,
                  "error": "NOT FOUND",
                  "message": "Cliente não encontrado.",
                  "path": "/pessoa-juridica/cnpj/12345678000195",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @PutMapping("/cnpj/{cnpj}")
   public ResponseEntity<PessoaJuridicaResponseDTO> atualizarPessoaJuridicaPorCnpj(
      @Valid @RequestBody PessoaJuridicaUpdateDTO dto,
      @Parameter(description = "CNPJ do cliente, sem pontuação", example = "12345678000195")
      @PathVariable String cnpj
   ) {
      PessoaJuridicaResponseDTO response = service.atualizarPessoaJuridicaViaCnpj(dto, cnpj);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Excluir pessoa jurídica por ID", description = "Exclui o cadastro do cliente. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "204",
         description = "Cliente excluído",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "401",
         description = "Token inválido",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "403",
         description = "Sem acesso ou sem credenciais",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Cliente não encontrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 404,
                  "error": "NOT FOUND",
                  "message": "Cliente não encontrado.",
                  "path": "/pessoa-juridica/550e8400-e29b-41d4-a716-446655440000",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deletarPessoaJuridicaPorId(
      @Parameter(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      service.deletarPessoaJuridicaViaId(id);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }
}
