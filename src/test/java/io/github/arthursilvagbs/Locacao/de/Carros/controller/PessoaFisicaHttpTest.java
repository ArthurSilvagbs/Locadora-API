package io.github.arthursilvagbs.Locacao.de.Carros.controller;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.GlobalExceptionHandler;
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
import io.github.arthursilvagbs.Locacao.de.Carros.service.PessoaFisicaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PessoaFisicaHttpTest {
   private PessoaFisicaService service;
   private MockMvc mvc;

   @BeforeEach
   void configurar() {
      service = mock(PessoaFisicaService.class);
      mvc = MockMvcBuilders.standaloneSetup(new PessoaFisicaController(service))
         .setControllerAdvice(new GlobalExceptionHandler())
         .build();
   }

   @Test
   void cadastrarPessoaFisica_dadosValidos_retorna201ECliente() throws Exception {
      UUID id = UUID.randomUUID();
      PessoaFisicaResponseDTO resposta = new PessoaFisicaResponseDTO(
         id, "Maria", "maria@email.com", "11999999999", "Rua A", "52998224725", null
      );
      when(service.criarPessoaFisica(any())).thenReturn(resposta);

      mvc.perform(post("/pessoa-fisica")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"Maria","email":"maria@email.com","telefone":"11999999999",
                "endereco":"Rua A","cpf":"52998224725"}
               """))
         .andExpect(status().isCreated())
         .andExpect(jsonPath("$.idCliente").value(id.toString()))
         .andExpect(jsonPath("$.email").value("maria@email.com"));
   }

   @Test
   void cadastrarPessoaFisica_camposInvalidos_retorna400Padronizado() throws Exception {
      mvc.perform(post("/pessoa-fisica")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"\",\"email\":\"invalido\",\"cpf\":\"123\"}"))
         .andExpect(status().isBadRequest())
         .andExpect(jsonPath("$.status").value(400))
         .andExpect(jsonPath("$.error").value("BAD REQUEST"))
         .andExpect(jsonPath("$.message").value("Corpo da requisição inválido"))
         .andExpect(jsonPath("$.path").value("/pessoa-fisica"));
      verifyNoInteractions(service);
   }

   @Test
   void cadastrarPessoaFisica_jsonMalformado_retorna400Padronizado() throws Exception {
      mvc.perform(post("/pessoa-fisica")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{invalido"))
         .andExpect(status().isBadRequest())
         .andExpect(jsonPath("$.status").value(400))
         .andExpect(jsonPath("$.message").value("Corpo da requisição inválido"));
      verifyNoInteractions(service);
   }

   @Test
   void cadastrarPessoaFisica_cpfDuplicado_retorna409Padronizado() throws Exception {
      when(service.criarPessoaFisica(any())).thenThrow(new RegistroDuplicadoException("CPF já cadastrado."));

      mvc.perform(post("/pessoa-fisica")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"Maria","email":"maria@email.com","telefone":"11999999999",
                "endereco":"Rua A","cpf":"52998224725"}
               """))
         .andExpect(status().isConflict())
         .andExpect(jsonPath("$.status").value(409))
         .andExpect(jsonPath("$.error").value("CONFLICT"))
         .andExpect(jsonPath("$.message").value("CPF já cadastrado."))
         .andExpect(jsonPath("$.path").value("/pessoa-fisica"));
   }

   @Test
   void buscarPessoaFisica_clienteInexistente_retorna404Padronizado() throws Exception {
      UUID id = UUID.randomUUID();
      when(service.buscarPessoaFisicaPorId(id.toString()))
         .thenThrow(new EntidadeNaoEncontradaException("Cliente não encontrado."));

      mvc.perform(get("/pessoa-fisica/{id}", id))
         .andExpect(status().isNotFound())
         .andExpect(jsonPath("$.status").value(404))
         .andExpect(jsonPath("$.error").value("NOT FOUND"))
         .andExpect(jsonPath("$.message").value("Cliente não encontrado."))
         .andExpect(jsonPath("$.path").value("/pessoa-fisica/" + id));
   }
}
