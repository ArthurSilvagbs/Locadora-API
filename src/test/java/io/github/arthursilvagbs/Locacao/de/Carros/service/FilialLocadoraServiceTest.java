package io.github.arthursilvagbs.Locacao.de.Carros.service;

// DTO de entrada usado pra criar uma FilialLocadora
import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraCreateDTO;
// DTO de resposta devolvido pelo Service
import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraResponseDTO;
// DTO usado pra atualizar uma FilialLocadora existente
import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraUpdateDTO;
// Entidade JPA que representa uma FilialLocadora no banco
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
// Exception lançada quando um registro não é encontrado
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
// Exception lançada quando tentamos cadastrar um CNPJ já existente
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
// Mapper que converte DTO <-> Entidade <-> DTO de resposta
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.FilialLocadoraMapper;
// Repository que fala com o banco; será mockado, não usamos banco de verdade
import io.github.arthursilvagbs.Locacao.de.Carros.repository.FilialLocadoraRepository;

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
// Optional representa "achei"/"não achei" no findById
import java.util.Optional;

// Métodos estáticos do AssertJ pras verificações
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
// Métodos estáticos do Mockito pra configurar mocks e verificar chamadas
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Liga o Mockito nesta classe: processa as anotações @Mock e @InjectMocks abaixo
@ExtendWith(MockitoExtension.class)
class FilialLocadoraServiceTest {

    // Mock do repository: não bate no banco de verdade, nós decidimos as respostas
    @Mock
    private FilialLocadoraRepository repository;

    // Mock do mapper: isola o teste só na lógica do Service
    @Mock
    private FilialLocadoraMapper mapper;

    // Instância REAL do Service, com os mocks acima injetados nos campos do construtor
    @InjectMocks
    private FilialLocadoraService service;

    // ---------------------------------------------------------------------
    // MÉTODO: criarFilialLocadora
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("criarFilialLocadora deve salvar e retornar o DTO quando o CNPJ não existe")
    void criarFilialLocadora_dadosValidos_retornaResponseDTO() {
        // ARRANGE: DTO de entrada, como se fosse o corpo de uma requisição de cadastro
        FilialLocadoraCreateDTO dto = new FilialLocadoraCreateDTO(
                "Filial Centro",         // nomeFilial
                "12345678000199",        // cnpjFilial
                "SP",                    // uf
                "Sao Paulo",              // cidade
                "Rua X, 1",               // endereco
                "1140028922",             // telefone
                "filial@email.com"        // email
        );

        // Entidade que o mapper "fingirá" devolver ao receber o dto acima
        FilialLocadora entidadeMapeada = new FilialLocadora(
                dto.nomeFilial(), dto.cnpjFilial(), dto.uf(), dto.cidade(), dto.endereco(), dto.telefone(), dto.email()
        );

        // DTO de resposta que o mapper "fingirá" devolver no final do método
        FilialLocadoraResponseDTO responseEsperado = new FilialLocadoraResponseDTO(
                UUID.randomUUID(), dto.nomeFilial(), dto.cnpjFilial(), dto.uf(), dto.cidade(),
                dto.endereco(), dto.telefone(), dto.email(), null
        );

        // Ensina o mock: esse CNPJ ainda não existe no banco
        when(repository.existsByCnpjFilial(dto.cnpjFilial())).thenReturn(false);

        // Ensina o mock: quando o mapper receber o dto, devolva a entidade que criamos acima
        when(mapper.mapearParaFilialLocadora(dto)).thenReturn(entidadeMapeada);

        // Ensina o mock: quando o mapper receber a entidade mapeada, devolva o response esperado
        when(mapper.mapearParaResponse(entidadeMapeada)).thenReturn(responseEsperado);

        // ACT: chama o método real do Service
        FilialLocadoraResponseDTO resultado = service.criarFilialLocadora(dto);

        // ASSERT: o resultado devolvido deve ser exatamente o response esperado
        assertThat(resultado).isEqualTo(responseEsperado);

        // Verifica que o save foi chamado exatamente uma vez com a entidade mapeada
        verify(repository, times(1)).save(entidadeMapeada);
    }

    @Test
    @DisplayName("criarFilialLocadora deve lançar RegistroDuplicadoException quando o CNPJ já existe")
    void criarFilialLocadora_cnpjDuplicado_lancaExcecao() {
        // ARRANGE: DTO de entrada qualquer, o conteúdo não importa pra esse teste
        FilialLocadoraCreateDTO dto = new FilialLocadoraCreateDTO(
                "Filial Centro", "12345678000199", "SP", "Sao Paulo", "Rua X, 1", "1140028922", "filial@email.com"
        );

        // Ensina o mock: esse CNPJ já existe, forçando o "if" a barrar o cadastro
        when(repository.existsByCnpjFilial(dto.cnpjFilial())).thenReturn(true);

        // ACT + ASSERT: chama o método e confere a exception lançada
        assertThatThrownBy(() -> service.criarFilialLocadora(dto))
                .isInstanceOf(RegistroDuplicadoException.class)
                .hasMessage("CNPJ já cadastrado.");

        // Confirma que o save nunca foi chamado, já que o cadastro deveria ter sido bloqueado
        verify(repository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // MÉTODO: buscarFilialLocadoraPorId
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("buscarFilialLocadoraPorId deve retornar o DTO quando o ID existe")
    void buscarFilialLocadoraPorId_idExistente_retornaResponseDTO() {
        // ARRANGE: UUID que vamos simular como existente no banco
        UUID id = UUID.randomUUID();

        // Entidade que o repository "encontrará" para esse ID
        FilialLocadora entidade = new FilialLocadora(
                "Filial Centro", "12345678000199", "SP", "Sao Paulo", "Rua X, 1", "1140028922", "filial@email.com"
        );

        // DTO de resposta esperado após o mapeamento
        FilialLocadoraResponseDTO responseEsperado = new FilialLocadoraResponseDTO(
                id, "Filial Centro", "12345678000199", "SP", "Sao Paulo", "Rua X, 1", "1140028922", "filial@email.com", null
        );

        // Ensina o mock: encontrou a entidade pra esse ID
        when(repository.findById(id)).thenReturn(Optional.of(entidade));

        // Ensina o mock: o mapper converte a entidade encontrada no response esperado
        when(mapper.mapearParaResponse(entidade)).thenReturn(responseEsperado);

        // ACT: chama o método passando o ID como String (Service faz UUID.fromString por dentro)
        FilialLocadoraResponseDTO resultado = service.buscarFilialLocadoraPorId(id.toString());

        // ASSERT: resultado deve bater com o response esperado
        assertThat(resultado).isEqualTo(responseEsperado);
    }

    @Test
    @DisplayName("buscarFilialLocadoraPorId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
    void buscarFilialLocadoraPorId_idInexistente_lancaExcecao() {
        // ARRANGE: UUID que vamos simular como inexistente
        UUID id = UUID.randomUUID();

        // Ensina o mock: não encontrou nada pra esse ID (Optional vazio)
        when(repository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT: o Service deve traduzir isso numa exception de negócio
        assertThatThrownBy(() -> service.buscarFilialLocadoraPorId(id.toString()))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Filial não encontrada.");

        // Confirma que o mapper nunca foi chamado, já que não havia entidade pra mapear
        verify(mapper, never()).mapearParaResponse(any());
    }

    // ---------------------------------------------------------------------
    // MÉTODO: atualizarFilialLocadoraPorId
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("atualizarFilialLocadoraPorId deve atualizar e retornar o DTO quando o ID existe")
    void atualizarFilialLocadoraPorId_idExistente_atualizaERetorna() {
        // ARRANGE: ID que vamos simular como existente
        UUID id = UUID.randomUUID();

        // Entidade "atual", como se já estivesse salva antes da atualização
        FilialLocadora entidadeExistente = new FilialLocadora(
                "Filial Centro", "12345678000199", "SP", "Sao Paulo", "Endereco antigo", "1140028922", "antigo@email.com"
        );

        // DTO com os dados novos que o usuário quer aplicar (só endereco, telefone e email são editáveis)
        FilialLocadoraUpdateDTO dtoAtualizacao = new FilialLocadoraUpdateDTO(
                "Endereco novo", "1140028900", "novo@email.com"
        );

        // DTO de resposta esperado depois da atualização e do mapeamento
        FilialLocadoraResponseDTO responseEsperado = new FilialLocadoraResponseDTO(
                id, "Filial Centro", "12345678000199", "SP", "Sao Paulo", "Endereco novo", "1140028900", "novo@email.com", null
        );

        // Ensina o mock: o repository encontra a entidade existente pra esse ID
        when(repository.findById(id)).thenReturn(Optional.of(entidadeExistente));

        // Ensina o mock: depois de atualizada, o mapper devolve o response esperado
        // any(FilialLocadora.class) porque é a mesma instância, só com os campos já alterados
        when(mapper.mapearParaResponse(any(FilialLocadora.class))).thenReturn(responseEsperado);

        // ACT: chama a atualização passando o DTO novo e o ID como String
        FilialLocadoraResponseDTO resultado = service.atualizarFilialLocadoraPorId(dtoAtualizacao, id.toString());

        // ASSERT: o resultado deve ser o response esperado
        assertThat(resultado).isEqualTo(responseEsperado);

        // Verifica que o save foi chamado com a mesma instância vinda do findById
        verify(repository, times(1)).save(entidadeExistente);
    }

    @Test
    @DisplayName("atualizarFilialLocadoraPorId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
    void atualizarFilialLocadoraPorId_idInexistente_lancaExcecao() {
        // ARRANGE: ID que vamos simular como inexistente
        UUID id = UUID.randomUUID();

        // DTO de atualização qualquer, o conteúdo não importa pra esse teste
        FilialLocadoraUpdateDTO dtoAtualizacao = new FilialLocadoraUpdateDTO(
                "Endereco novo", "1140028900", "novo@email.com"
        );

        // Ensina o mock: não existe entidade pra esse ID
        when(repository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT: o Service deve lançar a exception antes de tentar atualizar
        assertThatThrownBy(() -> service.atualizarFilialLocadoraPorId(dtoAtualizacao, id.toString()))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Filial não encontrada.");

        // Garante que, sem entidade encontrada, o save nunca é chamado
        verify(repository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // MÉTODO: deletarFilialLocadoraPorId
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("deletarFilialLocadoraPorId deve deletar quando o ID existe")
    void deletarFilialLocadoraPorId_idExistente_deleta() {
        // ARRANGE: ID que vamos simular como existente
        UUID id = UUID.randomUUID();

        // Entidade que o repository "encontrará" pra esse ID
        FilialLocadora entidadeExistente = new FilialLocadora(
                "Filial Centro", "12345678000199", "SP", "Sao Paulo", "Rua X, 1", "1140028922", "filial@email.com"
        );

        // Ensina o mock: o findById encontra a entidade
        when(repository.findById(id)).thenReturn(Optional.of(entidadeExistente));

        // ACT: chama o método de deleção (void, não retorna nada)
        service.deletarFilialLocadoraPorId(id.toString());

        // ASSERT: já que não há retorno, confirmamos o comportamento via verify
        // o delete deve ter sido chamado exatamente uma vez com a entidade encontrada
        verify(repository, times(1)).delete(entidadeExistente);
    }

    @Test
    @DisplayName("deletarFilialLocadoraPorId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
    void deletarFilialLocadoraPorId_idInexistente_lancaExcecao() {
        // ARRANGE: ID que vamos simular como inexistente
        UUID id = UUID.randomUUID();

        // Ensina o mock: não existe entidade pra esse ID
        when(repository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT: o Service deve lançar a exception ao invés de tentar deletar algo inexistente
        assertThatThrownBy(() -> service.deletarFilialLocadoraPorId(id.toString()))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Filial não encontrada.");

        // Garante que o delete nunca foi chamado
        verify(repository, never()).delete(any());
    }
}
