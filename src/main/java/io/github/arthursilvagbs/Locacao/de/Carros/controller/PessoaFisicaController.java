package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaCreateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaUpdateDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.locacao.LocacaoResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.dto.error.ErrorResponse;
import io.github.arthursilvagbs.Locacao.de.Carros.service.PessoaFisicaService;
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
@RequestMapping("/pessoa-fisica")
@RequiredArgsConstructor
@Tag(name = "Pessoa física", description = "Cadastro, consulta, atualização e histórico de locações de clientes pessoa física.")
@SecurityRequirement(name = "bearerAuth")
public class PessoaFisicaController {

   private final PessoaFisicaService service;

   @Operation(summary = "Cadastrar pessoa física", description = "Exige autenticação. Cria um cliente pessoa física com CPF e e-mail únicos.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "201",
         description = "Cliente cadastrado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaFisicaResponseDTO.class))
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
                 "path": "/pessoa-fisica",
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
         description = "CPF ou e-mail já cadastrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
               @ExampleObject(value = """
               {
                 "status": 409,
                 "error": "CONFLICT",
                 "message": "CPF já cadastrado.",
                 "path": "/pessoa-fisica",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """),
               @ExampleObject(value = """
               {
                 "status": 409,
                 "error": "CONFLICT",
                 "message": "Email já cadastrado.",
                 "path": "/pessoa-fisica",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """)
            }
         )
      )
   })
   @PostMapping
   public ResponseEntity<PessoaFisicaResponseDTO> criarPessoaFisica(
      @Valid @RequestBody PessoaFisicaCreateDTO dto
   ) {
      PessoaFisicaResponseDTO response = service.criarPessoaFisica(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @Operation(summary = "Buscar pessoa física por ID", description = "Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Cliente encontrado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaFisicaResponseDTO.class))
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
                  "path": "/pessoa-fisica/550e8400-e29b-41d4-a716-446655440000",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @GetMapping("/{id}")
   public ResponseEntity<PessoaFisicaResponseDTO> encontrarPessoaFisicaPorId(
      @Parameter(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      PessoaFisicaResponseDTO response = service.buscarPessoaFisicaPorId(id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Buscar pessoa física por CPF", description = "Disponível para funcionário, gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Cliente encontrado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaFisicaResponseDTO.class))
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
                  "message": "Cliente não encontado",
                  "path": "/pessoa-fisica/cpf/12345678900",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @GetMapping("/cpf/{cpf}")
   public ResponseEntity<PessoaFisicaResponseDTO> encontrarPessoaFisicaPorCpf(
      @Parameter(description = "CPF do cliente, sem pontuação", example = "52998224725")
      @PathVariable String cpf
   ) {
      PessoaFisicaResponseDTO response = service.buscarPessoaFisicaPorCpf(cpf);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Listar pessoas físicas", description = "Retorna a primeira página, com até 10 clientes, ordenados por CPF. Disponível para gerente e administrador.")
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
   public ResponseEntity<Page<PessoaFisicaResponseDTO>> buscarTodosPaginado() {
      Page<PessoaFisicaResponseDTO> responsePaginado = service.buscarTodosPaginado();
      return ResponseEntity.status(HttpStatus.OK).body(responsePaginado);
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
                 "path": "/pessoa-fisica/locacoes/id/550e8400-e29b-41d4-a716-446655440000",
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
                 "path": "/pessoa-fisica/locacoes/id/550e8400-e29b-41d4-a716-446655440000",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """
               )
            }
         )
      )
   })
   @GetMapping("locacoes/id/{idCliente}")
   public ResponseEntity<Page<LocacaoResponseDTO>> buscarLocacaoesPorPessoaFisicaId(
      @Parameter(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable("idCliente") String idCliente
   ) {
      Page<LocacaoResponseDTO> responsePaginada = service.buscarLocacoesClientePorId(idCliente);
      return ResponseEntity.status(HttpStatus.OK).body(responsePaginada);
   }

   @Operation(summary = "Listar locações por CPF", description = "Retorna até 10 locações da primeira página, ordenadas pela data de retirada decrescente. Disponível para gerente e administrador.")
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
                 "path": "/pessoa-fisica/locacoes/cpf/12345678900",
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
                 "path": "/pessoa-fisica/locacoes/cpf/12345678900",
                 "timestamp": "2026-09-29T12:40:00"
               }
               """
               )
            }
         )
      )
   })
   @GetMapping("locacoes/cpf/{cpf}")
   public ResponseEntity<Page<LocacaoResponseDTO>> buscarLocacoesPorPessoaFisicaCpf(
      @Parameter(description = "CPF do cliente, sem pontuação", example = "52998224725")
      @PathVariable("cpf") String cpf
   ) {
      Page<LocacaoResponseDTO> responsePaginada = service.buscarLocacoesClientePorCpf(cpf);
      return ResponseEntity.status(HttpStatus.OK).body(responsePaginada);
   }

   @Operation(summary = "Atualizar pessoa física por ID", description = "Altera e-mail, telefone e endereço. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Cliente atualizado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaFisicaResponseDTO.class))
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
                 "path": "/pessoa-fisica/550e8400-e29b-41d4-a716-446655440000",
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
         description = "Cliente não econtrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 404,
                  "error": "NOT FOUND",
                  "message": "Cliente não encontrado.",
                  "path": "/pessoa-fisica/550e8400-e29b-41d4-a716-446655440000",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @PutMapping("/{id}")
   public ResponseEntity<PessoaFisicaResponseDTO> atualizarPessoaFisicaPorId(
      @Valid @RequestBody PessoaFisicaUpdateDTO dto,
      @Parameter(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      PessoaFisicaResponseDTO response = service.atualizarPessoaFisicaPorId(dto, id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Atualizar pessoa física por CPF", description = "Altera e-mail, telefone e endereço. Disponível para gerente e administrador.")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Cliente atualizado",
         content = @Content(mediaType = "application/json", schema = @Schema(implementation = PessoaFisicaResponseDTO.class))
      ),
      @ApiResponse(
         responseCode = "400",
         description = "Dados de entrada inválidos",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "object"),
            examples = @ExampleObject(value = """
               {
                  "status": 400,
                  "error": "BAD REQUEST",
                  "message": "Corpo da requisição inválido",
                  "path": "/pessoa-fisica/cpf/12345678900",
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
         description = "Cliente não econtrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 404,
                  "error": "NOT FOUND",
                  "message": "Cliente não encontrado.",
                  "path": "/pessoa-fisica/cpf/12345678900",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @PutMapping("/cpf/{cpf}")
   public ResponseEntity<PessoaFisicaResponseDTO> atualizarPessoaFisicaPorCpf(
      @Valid @RequestBody PessoaFisicaUpdateDTO dto,
      @Parameter(description = "CPF do cliente, sem pontuação", example = "52998224725")
      @PathVariable String cpf
   ) {
      PessoaFisicaResponseDTO response = service.atualizarPessoaFisicaPorCpf(dto, cpf);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Excluir pessoa física por ID", description = "Exclui o cadastro do cliente. Disponível para gerente e administrador.")
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
         description = "Cliente não econtrado",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 404,
                  "error": "NOT FOUND",
                  "message": "Cliente não encontrado.",
                  "path": "/pessoa-fisica/550e8400-e29b-41d4-a716-446655440000",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """)
         )
      )
   })
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deletarPessoaFisicaPorId(
      @Parameter(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable String id
   ) {
      service.deletarPessoaFisicaPorId(id);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

}
