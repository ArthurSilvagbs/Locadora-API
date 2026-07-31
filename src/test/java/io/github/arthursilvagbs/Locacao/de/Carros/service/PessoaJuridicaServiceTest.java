package io.github.arthursilvagbs.Locacao.de.Carros.service;

// DTO de entrada usado pra criar uma PessoaJuridica
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaCreateDTO;
// DTO de resposta devolvido pelo Service
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaResponseDTO;
// DTO usado pra atualizar uma PessoaJuridica existente
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaJuridica.PessoaJuridicaUpdateDTO;
// Entidade JPA que representa uma Pessoa Jurídica no banco
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaJuridica;
// Exception lançada quando um registro não é encontrado
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
// Exception lançada quando tentamos cadastrar CNPJ ou email já existentes
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
// Mapper que converte DTO <-> Entidade <-> DTO de resposta
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.PessoaJuridicaMapper;
// Repository que fala com o banco; será mockado, não usamos banco de verdade
import io.github.arthursilvagbs.Locacao.de.Carros.repository.PessoaJuridicaRepository;

// Anotações de teste do JUnit 5
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
// Liga o Mockito ao JUnit 5, habilitando @Mock e @InjectMocks
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// UUID pra gerar IDs falsos
import java.util.UUID;
// Optional representa "achei"/"não achei" no findById/findByX
import java.util.Optional;

// Métodos estáticos do AssertJ pras verificações
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
// Métodos estáticos do Mockito pra configurar mocks e verificar chamadas
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Liga o Mockito nesta classe: processa as anotações @Mock e @InjectMocks abaixo
@ExtendWith(MockitoExtension.class)
class PessoaJuridicaServiceTest {

    // Mock do repository: não bate no banco de verdade, nós decidimos as respostas
    @Mock
    private PessoaJuridicaRepository repository;

    // Mock do mapper: isola o teste só na lógica do Service
    @Mock
    private PessoaJuridicaMapper mapper;

    // Instância REAL do Service, com os mocks acima injetados nos campos do construtor
    @InjectMocks
    private PessoaJuridicaService service;

    // ---------------------------------------------------------------------
    // MÉTODO: criarPessoaJuridica
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("criarPessoaJuridica deve salvar e retornar o DTO quando CNPJ e email não existem")
    void criarPessoaJuridica_dadosValidos_retornaResponseDTO() {
        // ARRANGE: DTO de entrada, como se fosse o corpo de uma requisição de cadastro
        PessoaJuridicaCreateDTO dto = new PessoaJuridicaCreateDTO(
                "Locadora Silva LTDA",   // nome
                "contato@silva.com",     // email
                "1140028922",            // telefone
                "Av. Central, 500",      // endereco
                "12345678000199"         // cnpj
        );

        // Entidade que o mapper "fingirá" devolver ao receber o dto acima
        PessoaJuridica entidadeMapeada = new PessoaJuridica(
                dto.nome(), dto.email(), dto.telefone(), dto.endereco(), dto.cnpj()
        );

        // DTO de resposta esperado depois do mapeamento final
        PessoaJuridicaResponseDTO responseEsperado = new PessoaJuridicaResponseDTO(
                UUID.randomUUID(), dto.nome(), dto.email(), dto.telefone(), dto.endereco(), dto.cnpj(), null
        );

        // Ensina o mock: esse CNPJ ainda não existe no banco
        when(repository.existsByCnpj(dto.cnpj())).thenReturn(false);

        // Ensina o mock: esse email ainda não existe no banco
        when(repository.existsByEmail(dto.email())).thenReturn(false);

        // Ensina o mock: o mapper transforma o dto na entidade que criamos
        when(mapper.mapearParaPessoaJuridica(dto)).thenReturn(entidadeMapeada);

        // Ensina o mock: repository.save recebe a entidade mapeada e devolve ela mesma
        // (repare que aqui o Service usa o RETORNO do save, diferente do PessoaFisicaService)
        when(repository.save(entidadeMapeada)).thenReturn(entidadeMapeada);

        // Ensina o mock: o mapper transforma a entidade salva no response esperado
        when(mapper.mapearParaResponse(entidadeMapeada)).thenReturn(responseEsperado);

        // ACT: chama o método real do Service
        PessoaJuridicaResponseDTO resultado = service.criarPessoaJuridica(dto);

        // ASSERT: o resultado devolvido deve ser exatamente o response esperado
        assertThat(resultado).isEqualTo(responseEsperado);

        // Verifica que o save foi chamado exatamente uma vez com a entidade mapeada
        verify(repository, times(1)).save(entidadeMapeada);
    }

    @Test
    @DisplayName("criarPessoaJuridica deve lançar RegistroDuplicadoException quando o CNPJ já existe")
    void criarPessoaJuridica_cnpjDuplicado_lancaExcecao() {
        // ARRANGE: DTO de entrada qualquer, o conteúdo não importa pra esse teste
        PessoaJuridicaCreateDTO dto = new PessoaJuridicaCreateDTO(
                "Locadora Silva LTDA", "contato@silva.com", "1140028922", "Av. Central, 500", "12345678000199"
        );

        // Ensina o mock: esse CNPJ já existe, forçando o primeiro "if" a barrar o cadastro
        when(repository.existsByCnpj(dto.cnpj())).thenReturn(true);

        // ACT + ASSERT: chama o método e confere a exception lançada
        assertThatThrownBy(() -> service.criarPessoaJuridica(dto))
                .isInstanceOf(RegistroDuplicadoException.class)
                .hasMessage("CNPJ já cadastrado.");

        // Confirma que a validação de email nunca rodou (o "if" do CNPJ interrompeu antes)
        verify(repository, never()).existsByEmail(anyString());

        // Confirma que o save nunca foi chamado
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("criarPessoaJuridica deve lançar RegistroDuplicadoException quando o email já existe")
    void criarPessoaJuridica_emailDuplicado_lancaExcecao() {
        // ARRANGE: DTO de entrada padrão
        PessoaJuridicaCreateDTO dto = new PessoaJuridicaCreateDTO(
                "Locadora Silva LTDA", "contato@silva.com", "1140028922", "Av. Central, 500", "12345678000199"
        );

        // CNPJ não existe ainda, então essa validação passa
        when(repository.existsByCnpj(dto.cnpj())).thenReturn(false);

        // Email já existe, deve barrar o cadastro
        when(repository.existsByEmail(dto.email())).thenReturn(true);

        // ACT + ASSERT: confere o tipo e a mensagem da exception
        assertThatThrownBy(() -> service.criarPessoaJuridica(dto))
                .isInstanceOf(RegistroDuplicadoException.class)
                .hasMessage("Email já cadastrado.");

        // Confirma que, mesmo passando pela validação de CNPJ, o save nunca foi chamado
        verify(repository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // MÉTODO: buscarPessoaJuridicaPorId
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("buscarPessoaJuridicaPorId deve retornar o DTO quando o ID existe")
    void buscarPessoaJuridicaPorId_idExistente_retornaResponseDTO() {
        // ARRANGE: UUID que vamos simular como existente no banco
        UUID id = UUID.randomUUID();

        // Entidade que o repository "encontrará" para esse ID
        PessoaJuridica entidade = new PessoaJuridica(
                "Locadora Silva LTDA", "contato@silva.com", "1140028922", "Av. Central, 500", "12345678000199"
        );

        // DTO de resposta esperado após o mapeamento
        PessoaJuridicaResponseDTO responseEsperado = new PessoaJuridicaResponseDTO(
                id, "Locadora Silva LTDA", "contato@silva.com", "1140028922", "Av. Central, 500", "12345678000199", null
        );

        // Ensina o mock: encontrou a entidade pra esse ID
        when(repository.findById(id)).thenReturn(Optional.of(entidade));

        // Ensina o mock: o mapper converte a entidade encontrada no response esperado
        when(mapper.mapearParaResponse(entidade)).thenReturn(responseEsperado);

        // ACT: chama o método passando o ID como String (Service faz UUID.fromString por dentro)
        PessoaJuridicaResponseDTO resultado = service.buscarPessoaJuridicaPorId(id.toString());

        // ASSERT: resultado deve bater com o response esperado
        assertThat(resultado).isEqualTo(responseEsperado);
    }

    @Test
    @DisplayName("buscarPessoaJuridicaPorId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
    void buscarPessoaJuridicaPorId_idInexistente_lancaExcecao() {
        // ARRANGE: UUID que vamos simular como inexistente
        UUID id = UUID.randomUUID();

        // Ensina o mock: não encontrou nada pra esse ID (Optional vazio)
        when(repository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT: o Service deve traduzir isso numa exception de negócio
        assertThatThrownBy(() -> service.buscarPessoaJuridicaPorId(id.toString()))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Cliente não encontrado.");

        // Confirma que o mapper nunca foi chamado, já que não havia entidade pra mapear
        verify(mapper, never()).mapearParaResponse(any());
    }

    // ---------------------------------------------------------------------
    // MÉTODO: atualizarPessoaJuridicaViaId
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("atualizarPessoaJuridicaViaId deve atualizar e retornar o DTO quando o ID existe")
    void atualizarPessoaJuridicaViaId_idExistente_atualizaERetorna() {
        // ARRANGE: ID que vamos simular como existente
        UUID id = UUID.randomUUID();

        // Entidade "atual", como se já estivesse salva antes da atualização
        PessoaJuridica entidadeExistente = new PessoaJuridica(
                "Nome Antigo LTDA", "antigo@silva.com", "1140028922", "Endereco antigo", "12345678000199"
        );

        // DTO com os dados novos que o usuário quer aplicar (CNPJ não é atualizável, repare)
        PessoaJuridicaUpdateDTO dtoAtualizacao = new PessoaJuridicaUpdateDTO(
                "Nome Novo LTDA", "novo@silva.com", "1140028900", "Endereco novo"
        );

        // DTO de resposta esperado depois da atualização e do mapeamento
        PessoaJuridicaResponseDTO responseEsperado = new PessoaJuridicaResponseDTO(
                id, "Nome Novo LTDA", "novo@silva.com", "1140028900", "Endereco novo", "12345678000199", null
        );

        // Ensina o mock: o repository encontra a entidade existente pra esse ID
        when(repository.findById(id)).thenReturn(Optional.of(entidadeExistente));

        // Ensina o mock: depois de atualizada, o mapper devolve o response esperado
        // any(PessoaJuridica.class) porque é a mesma instância, só com os campos já alterados
        when(mapper.mapearParaResponse(any(PessoaJuridica.class))).thenReturn(responseEsperado);

        // ACT: chama a atualização passando o DTO novo e o ID como String
        PessoaJuridicaResponseDTO resultado = service.atualizarPessoaJuridicaViaId(dtoAtualizacao, id.toString());

        // ASSERT: o resultado deve ser o response esperado
        assertThat(resultado).isEqualTo(responseEsperado);

        // Verifica que o save foi chamado com a mesma instância vinda do findById
        verify(repository, times(1)).save(entidadeExistente);
    }

    @Test
    @DisplayName("atualizarPessoaJuridicaViaId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
    void atualizarPessoaJuridicaViaId_idInexistente_lancaExcecao() {
        // ARRANGE: ID que vamos simular como inexistente
        UUID id = UUID.randomUUID();

        // DTO de atualização qualquer, o conteúdo não importa pra esse teste
        PessoaJuridicaUpdateDTO dtoAtualizacao = new PessoaJuridicaUpdateDTO(
                "Nome Novo LTDA", "novo@silva.com", "1140028900", "Endereco novo"
        );

        // Ensina o mock: não existe entidade pra esse ID
        when(repository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT: o Service deve lançar a exception antes de tentar atualizar
        assertThatThrownBy(() -> service.atualizarPessoaJuridicaViaId(dtoAtualizacao, id.toString()))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Cliente não econtrado."); // texto exato do Service (com o typo "econtrado")

        // Garante que, sem entidade encontrada, o save nunca é chamado
        verify(repository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // MÉTODO: deletarPessoaJuridicaViaId
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("deletarPessoaJuridicaViaId deve deletar quando o ID existe")
    void deletarPessoaJuridicaViaId_idExistente_deleta() {
        // ARRANGE: ID que vamos simular como existente
        UUID id = UUID.randomUUID();

        // Entidade que o repository "encontrará" pra esse ID
        PessoaJuridica entidadeExistente = new PessoaJuridica(
                "Locadora Silva LTDA", "contato@silva.com", "1140028922", "Av. Central, 500", "12345678000199"
        );

        // Ensina o mock: o findById encontra a entidade
        when(repository.findById(id)).thenReturn(Optional.of(entidadeExistente));

        // ACT: chama o método de deleção (void, não retorna nada)
        service.deletarPessoaJuridicaViaId(id.toString());

        // ASSERT: já que não há retorno, confirmamos o comportamento via verify
        // o delete deve ter sido chamado exatamente uma vez com a entidade encontrada
        verify(repository, times(1)).delete(entidadeExistente);
    }

    @Test
    @DisplayName("deletarPessoaJuridicaViaId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
    void deletarPessoaJuridicaViaId_idInexistente_lancaExcecao() {
        // ARRANGE: ID que vamos simular como inexistente
        UUID id = UUID.randomUUID();

        // Ensina o mock: não existe entidade pra esse ID
        when(repository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT: o Service deve lançar a exception ao invés de tentar deletar algo inexistente
        assertThatThrownBy(() -> service.deletarPessoaJuridicaViaId(id.toString()))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Cliente não encontrado.");

        // Garante que o delete nunca foi chamado
        verify(repository, never()).delete(any());
    }
}
