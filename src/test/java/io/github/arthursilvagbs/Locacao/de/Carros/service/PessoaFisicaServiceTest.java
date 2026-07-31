package io.github.arthursilvagbs.Locacao.de.Carros.service;

// Import do DTO usado para criar uma PessoaFisica (o "corpo" que chegaria numa requisição de cadastro)
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaCreateDTO;
// Import do DTO de resposta (o que o service devolve depois de mapear a entidade)
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaResponseDTO;
// Import do DTO usado para atualizar uma PessoaFisica já existente
import io.github.arthursilvagbs.Locacao.de.Carros.dto.cliente.pessoaFisica.PessoaFisicaUpdateDTO;
// Import da entidade JPA que representa uma Pessoa Física no banco
import io.github.arthursilvagbs.Locacao.de.Carros.entity.PessoaFisica;
// Import da exception lançada quando um registro não é encontrado (ex: buscar por um ID que não existe)
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.EntidadeNaoEncontradaException;
// Import da exception lançada quando tentamos cadastrar algo duplicado (CPF ou email já existentes)
import io.github.arthursilvagbs.Locacao.de.Carros.exceptions.RegistroDuplicadoException;
// Import do mapper responsável por converter DTO <-> Entidade <-> DTO de resposta
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.PessoaFisicaMapper;
// Import do repository (camada que fala com o banco); vamos mockar ele, não usar um banco de verdade
import io.github.arthursilvagbs.Locacao.de.Carros.repository.PessoaFisicaRepository;

// Anotações de teste do JUnit 5: marcam método de teste e dão um nome legível a ele
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
// Permite ligar o Mockito ao JUnit 5, habilitando @Mock e @InjectMocks nesta classe
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Utilitário de UUID, usado para gerar IDs falsos nos testes
import java.util.UUID;
// Optional é o tipo que o Spring Data usa para representar "achei" ou "não achei" no findById/findByX
import java.util.Optional;

// Métodos estáticos do AssertJ, usados para fazer as verificações (assertThat...)
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
// Métodos estáticos do Mockito, usados para configurar comportamento dos mocks e verificar chamadas
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Liga o Mockito nesta classe de teste: ele vai processar as anotações @Mock e @InjectMocks abaixo
@ExtendWith(MockitoExtension.class)
class PessoaFisicaServiceTest {

    // Cria um mock (um "dublê") do repository: não vai bater no banco de verdade,
    // a gente é quem decide o que ele retorna em cada teste
    @Mock
    private PessoaFisicaRepository repository;

    // Cria um mock do mapper também, pelo mesmo motivo: isolar o teste só na lógica do Service
    @Mock
    private PessoaFisicaMapper mapper;

    // Cria uma instância REAL de PessoaFisicaService e injeta os mocks acima
    // (repository e mapper) nos campos correspondentes do construtor do Service
    @InjectMocks
    private PessoaFisicaService service;

    // ---------------------------------------------------------------------
    // MÉTODO: criarPessoaFisica
    // ---------------------------------------------------------------------

    // @Test marca este método como um caso de teste que o JUnit vai executar
    @Test
    // @DisplayName só dá um nome legível pro teste, aparece no relatório de execução
    @DisplayName("criarPessoaFisica deve salvar e retornar o DTO quando CPF e email não existem")
    void criarPessoaFisica_dadosValidos_retornaResponseDTO() {
        // ARRANGE (preparação): cria o DTO de entrada, como se fosse o corpo de uma requisição
        PessoaFisicaCreateDTO dto = new PessoaFisicaCreateDTO(
                "Arthur Silva",       // nome
                "arthur@email.com",   // email
                "11999999999",        // telefone
                "Rua A, 123",         // endereco
                "12345678900"         // cpf
        );

        // Cria a entidade que o mapper "fingirá" devolver quando receber o dto acima
        // (isso simula o que o mapper faria de verdade, sem depender da implementação real dele)
        PessoaFisica entidadeMapeada = new PessoaFisica(
                dto.nome(), dto.email(), dto.telefone(), dto.endereco(), dto.cpf()
        );

        // Cria o DTO de resposta que o mapper "fingirá" devolver no final do método
        PessoaFisicaResponseDTO responseEsperado = new PessoaFisicaResponseDTO(
                UUID.randomUUID(), dto.nome(), dto.email(), dto.telefone(), dto.endereco(), dto.cpf(), null
        );

        // Ensina o mock: quando chamarem repository.existsByCpf com esse CPF, devolva false
        // (ou seja: "não existe ninguém com esse CPF ainda")
        when(repository.existsByCpf(dto.cpf())).thenReturn(false);

        // Ensina o mock: quando chamarem repository.existsByEmail com esse email, devolva false
        // ("não existe ninguém com esse email ainda")
        when(repository.existsByEmail(dto.email())).thenReturn(false);

        // Ensina o mock: quando o mapper receber o dto, devolva a entidade que criamos acima
        when(mapper.mapearParaPessoaFisica(dto)).thenReturn(entidadeMapeada);

        // Ensina o mock: quando o mapper receber a entidade mapeada, devolva o response que criamos acima
        when(mapper.mapearParaResponse(entidadeMapeada)).thenReturn(responseEsperado);

        // ACT (ação): chama o método real do Service que estamos testando
        PessoaFisicaResponseDTO resultado = service.criarPessoaFisica(dto);

        // ASSERT (verificação): confirma que o resultado devolvido é exatamente o response esperado
        assertThat(resultado).isEqualTo(responseEsperado);

        // Verifica que o repository.save foi chamado exatamente uma vez, passando a entidade mapeada
        // (isso garante que o Service realmente tentou persistir o dado)
        verify(repository, times(1)).save(entidadeMapeada);
    }

    // Segundo teste do mesmo método, agora testando o caminho de ERRO: CPF duplicado
    @Test
    @DisplayName("criarPessoaFisica deve lançar RegistroDuplicadoException quando o CPF já existe")
    void criarPessoaFisica_cpfDuplicado_lancaExcecao() {
        // ARRANGE: monta um DTO de entrada qualquer, os valores em si não importam pro teste
        PessoaFisicaCreateDTO dto = new PessoaFisicaCreateDTO(
                "Arthur Silva", "arthur@email.com", "11999999999", "Rua A, 123", "12345678900"
        );

        // Ensina o mock: quando perguntarem se esse CPF já existe, responda que SIM (true)
        // isso força o Service a cair no primeiro "if" e lançar a exception
        when(repository.existsByCpf(dto.cpf())).thenReturn(true);

        // ACT + ASSERT juntos: assertThatThrownBy executa o código dentro do lambda
        // e verifica o tipo/mensagem da exception lançada
        assertThatThrownBy(() -> service.criarPessoaFisica(dto))
                // confirma que a exception lançada é do tipo RegistroDuplicadoException
                .isInstanceOf(RegistroDuplicadoException.class)
                // confirma que a mensagem da exception é a que o Service define pra esse caso
                .hasMessage("CPF já cadastrado.");

        // Verifica que, como a validação de CPF já falhou, o Service NUNCA chegou a checar o email
        // (prova que o "if" do CPF interrompeu o fluxo antes de continuar)
        verify(repository, never()).existsByEmail(anyString());

        // Verifica que o save nunca foi chamado, já que o cadastro deveria ter sido bloqueado
        verify(repository, never()).save(any());
    }

    // Terceiro teste do mesmo método: caminho de ERRO com email duplicado (CPF passa, email não)
    @Test
    @DisplayName("criarPessoaFisica deve lançar RegistroDuplicadoException quando o email já existe")
    void criarPessoaFisica_emailDuplicado_lancaExcecao() {
        // ARRANGE: mesmo DTO de sempre
        PessoaFisicaCreateDTO dto = new PessoaFisicaCreateDTO(
                "Arthur Silva", "arthur@email.com", "11999999999", "Rua A, 123", "12345678900"
        );

        // CPF não existe ainda, então essa validação passa (não lança nada)
        when(repository.existsByCpf(dto.cpf())).thenReturn(false);

        // Email já existe, então essa validação deve barrar o cadastro
        when(repository.existsByEmail(dto.email())).thenReturn(true);

        // ACT + ASSERT: chama o método e verifica a exception esperada
        assertThatThrownBy(() -> service.criarPessoaFisica(dto))
                .isInstanceOf(RegistroDuplicadoException.class)
                .hasMessage("Email já cadastrado.");

        // Garante que, mesmo passando pela validação de CPF, o save nunca foi chamado
        verify(repository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // MÉTODO: buscarPessoaFisicaPorId
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("buscarPessoaFisicaPorId deve retornar o DTO quando o ID existe")
    void buscarPessoaFisicaPorId_idExistente_retornaResponseDTO() {
        // ARRANGE: gera um UUID aleatório que vamos "fingir" que existe no banco
        UUID id = UUID.randomUUID();

        // Cria a entidade que o repository "fingirá" ter encontrado
        PessoaFisica entidade = new PessoaFisica(
                "Arthur Silva", "arthur@email.com", "11999999999", "Rua A, 123", "12345678900"
        );

        // Cria o DTO de resposta esperado após o mapeamento
        PessoaFisicaResponseDTO responseEsperado = new PessoaFisicaResponseDTO(
                id, "Arthur Silva", "arthur@email.com", "11999999999", "Rua A, 123", "12345678900", null
        );

        // Ensina o mock: ao buscar por esse UUID, devolva um Optional preenchido com a entidade
        // (Optional.of significa "achou", diferente de Optional.empty() que significa "não achou")
        when(repository.findById(id)).thenReturn(Optional.of(entidade));

        // Ensina o mock: o mapper transforma essa entidade no DTO de resposta esperado
        when(mapper.mapearParaResponse(entidade)).thenReturn(responseEsperado);

        // ACT: chama o método do Service passando o ID como String
        // (o Service internamente faz UUID.fromString(id), por isso passamos id.toString())
        PessoaFisicaResponseDTO resultado = service.buscarPessoaFisicaPorId(id.toString());

        // ASSERT: confere se o resultado bate com o esperado
        assertThat(resultado).isEqualTo(responseEsperado);
    }

    @Test
    @DisplayName("buscarPessoaFisicaPorId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
    void buscarPessoaFisicaPorId_idInexistente_lancaExcecao() {
        // ARRANGE: gera um UUID que vamos "fingir" que NÃO existe no banco
        UUID id = UUID.randomUUID();

        // Ensina o mock: ao buscar por esse UUID, devolva um Optional vazio
        // (é assim que o Spring Data representa "não encontrei nada")
        when(repository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT: o service deve traduzir esse Optional vazio em uma exception de negócio
        assertThatThrownBy(() -> service.buscarPessoaFisicaPorId(id.toString()))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Cliente não encontrado.");

        // Confirma que o mapper nunca chegou a ser chamado, já que não havia entidade para mapear
        verify(mapper, never()).mapearParaResponse(any());
    }

    // ---------------------------------------------------------------------
    // MÉTODO: atualizarPessoaFisicaPorId
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("atualizarPessoaFisicaPorId deve atualizar e retornar o DTO quando o ID existe")
    void atualizarPessoaFisicaPorId_idExistente_atualizaERetorna() {
        // ARRANGE: ID que vamos simular como existente
        UUID id = UUID.randomUUID();

        // Entidade "atual", como se já estivesse salva no banco antes da atualização
        PessoaFisica entidadeExistente = new PessoaFisica(
                "Arthur Silva", "antigo@email.com", "11999999999", "Endereco antigo", "12345678900"
        );

        // DTO com os novos dados que o usuário quer aplicar (update não permite trocar CPF, repare)
        PessoaFisicaUpdateDTO dtoAtualizacao = new PessoaFisicaUpdateDTO(
                "novo@email.com", "11888888888", "Endereco novo"
        );

        // DTO de resposta esperado após a atualização ser aplicada e mapeada
        PessoaFisicaResponseDTO responseEsperado = new PessoaFisicaResponseDTO(
                id, "Arthur Silva", "novo@email.com", "11888888888", "Endereco novo", "12345678900", null
        );

        // Ensina o mock: o repository encontra a entidade existente para esse ID
        when(repository.findById(id)).thenReturn(Optional.of(entidadeExistente));

        // Ensina o mock: depois de atualizada, o mapper transforma a entidade no response esperado
        // usamos any(PessoaFisica.class) porque a entidade que chega aqui é a mesma instância,
        // só que com campos alterados pelo próprio Service antes de chamar o mapper
        when(mapper.mapearParaResponse(any(PessoaFisica.class))).thenReturn(responseEsperado);

        // ACT: chama o método de atualização passando o DTO novo e o ID como String
        PessoaFisicaResponseDTO resultado = service.atualizarPessoaFisicaPorId(dtoAtualizacao, id.toString());

        // ASSERT: o resultado deve ser o response esperado
        assertThat(resultado).isEqualTo(responseEsperado);

        // Verifica que o repository.save foi chamado com a MESMA instância de entidade
        // que veio do findById (o Service atualiza o objeto em memória e depois salva)
        verify(repository, times(1)).save(entidadeExistente);
    }

    @Test
    @DisplayName("atualizarPessoaFisicaPorId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
    void atualizarPessoaFisicaPorId_idInexistente_lancaExcecao() {
        // ARRANGE: ID que vamos simular como inexistente
        UUID id = UUID.randomUUID();

        // DTO de atualização qualquer, não importa o conteúdo pra esse teste
        PessoaFisicaUpdateDTO dtoAtualizacao = new PessoaFisicaUpdateDTO(
                "novo@email.com", "11888888888", "Endereco novo"
        );

        // Ensina o mock: não existe nenhuma entidade com esse ID
        when(repository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT: o service deve lançar a exception antes de tentar atualizar qualquer coisa
        assertThatThrownBy(() -> service.atualizarPessoaFisicaPorId(dtoAtualizacao, id.toString()))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Cliente não econtrado."); // atenção: texto exato usado no Service (com o typo "econtrado")

        // Garante que, sem entidade encontrada, o save nunca é chamado
        verify(repository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // MÉTODO: deletarPessoaFisicaPorId
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("deletarPessoaFisicaPorId deve deletar quando o ID existe")
    void deletarPessoaFisicaPorId_idExistente_deleta() {
        // ARRANGE: ID que vamos simular como existente
        UUID id = UUID.randomUUID();

        // Entidade que o repository "encontrará" para esse ID
        PessoaFisica entidadeExistente = new PessoaFisica(
                "Arthur Silva", "arthur@email.com", "11999999999", "Rua A, 123", "12345678900"
        );

        // Ensina o mock: o findById encontra a entidade
        when(repository.findById(id)).thenReturn(Optional.of(entidadeExistente));

        // ACT: chama o método de deleção (esse método é void, não retorna nada)
        service.deletarPessoaFisicaPorId(id.toString());

        // ASSERT: como não há retorno, verificamos comportamento (interação com o mock)
        // confirma que o repository.delete foi chamado exatamente uma vez com a entidade encontrada
        verify(repository, times(1)).delete(entidadeExistente);
    }

    @Test
    @DisplayName("deletarPessoaFisicaPorId deve lançar EntidadeNaoEncontradaException quando o ID não existe")
    void deletarPessoaFisicaPorId_idInexistente_lancaExcecao() {
        // ARRANGE: ID que vamos simular como inexistente
        UUID id = UUID.randomUUID();

        // Ensina o mock: não existe nenhuma entidade com esse ID
        when(repository.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT: o service deve lançar a exception ao invés de tentar deletar algo inexistente
        assertThatThrownBy(() -> service.deletarPessoaFisicaPorId(id.toString()))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Cliente não encontrado");

        // Garante que o delete nunca foi chamado, já que não havia entidade para deletar
        verify(repository, never()).delete(any());
    }
}
