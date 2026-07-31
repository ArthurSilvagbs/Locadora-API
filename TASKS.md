# Locadora API — Tasks (Fase 3: Docker, Controllers, Exception Handler)

> Espelho local das tasks do ClickUp (lista "Locadora API — Docker, Controllers e Exception Handler").
> Ordem = prioridade de execução sugerida, não a ordem de criação no ClickUp.

## Imediato

### 21. [Bug] Corrigir `atualizarAtributosPessoaFisica`
`PessoaFisicaService` — o método `atualizarAtributosPessoaFisica` chama `setEmail` duas vezes e faz `entidade.setEndereco(dto.email())` — deveria ser `dto.endereco()`. Hoje, atualizar uma pessoa física sobrescreve o endereço com o valor do email. Correção direta.

## Fase 1 — Docker

### 1. Docker Compose — subir Postgres local
Criar `docker-compose.yml` na raiz com serviço Postgres (imagem oficial, sem containerizar a aplicação ainda).
- Definir `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`
- Mapear porta `5432:5432`
- Adicionar volume nomeado pra persistir dados
- Subir com `docker compose up -d` e validar com um client (DBeaver/psql) que conecta certo

### 2. Trocar SQLite por Postgres na aplicação
Apontar o projeto pro Postgres do Compose.
- Trocar dependência do driver no `pom.xml` (remove SQLite, adiciona `postgresql`)
- Ajustar `application.properties`/`application.yml` (`spring.datasource.url=jdbc:postgresql://localhost:5432/...`)
- Rodar a aplicação local (fora de container) e confirmar que conecta e as tabelas são criadas certo

### 3. Validar ciclo completo do Compose
Testar subir/derrubar o ambiente e confirmar persistência.
- `docker compose up -d` → roda app → mexe em algum dado → `docker compose down`
- Sobe de novo (`up -d`) e confirma que os dados persistiram (graças ao volume)
- Testar também `docker compose down -v` pra confirmar que reseta limpo (útil pra testes)

## Fase 2 — Fechar gaps de service que bloqueiam os controllers

### 22. LocacaoService — adicionar buscarPorId e buscarTodosPaginado
`LocacaoService` só tem os métodos de transição de estado (criar/retirada/devolução/cancelar), sem nenhuma busca. Bloqueia a task 8 (Controller de Locacao), que precisa exibir detalhe e listagem.

**Guia, não solução:**
- Seguir o mesmo padrão dos outros services (`buscarPorId` lançando `EntidadeNaoEncontradaException`, `buscarTodosPaginado` com `Page`)
- Pensar se listagem deveria aceitar filtro por status ou cliente desde já, ou fica simples por enquanto

### 16. Finalizar manutenção — devolver veículo pro status DISPONIVEL
`ManutencaoService` marca o veículo como `EM_MANUTENCAO` na criação, mas não existe método que reverta isso. Comparar com o ciclo do `LocacaoService` (retirada/devolução) como referência de padrão. Sem isso, o Controller de Manutenção (task 7) fica incompleto.

**Guia, não solução:**
- Que nome faz sentido pro método?
- O que precisa atualizar na entidade `Manutencao` além do status do veículo (data de conclusão? valor final?)
- Que exceção lançar se tentar finalizar uma manutenção que não está em andamento?

## Fase 3 — Controllers + Exception Handler (entrelaçados)

### 4. Controller — Cliente (PessoaFisica/PessoaJuridica)
Expor endpoints REST pra `PessoaFisicaService` e `PessoaJuridicaService`.
- CRUD básico (create/get/update/delete) usando os DTOs já existentes
- Definir se fica um controller único de Cliente ou dois separados (PF/PJ) — pensar no design antes de codar
- Validar `@RequestBody` com Bean Validation

### 5. Controller — Veiculo
Expor endpoints REST pra `VeiculoService`.
- CRUD usando `VeiculoCreateDTO`/`ResponseDTO`/`UpdateDTO`
- Endpoint de listagem com filtro por status/categoria (pensar se vale a pena já nessa fase ou deixar pro backlog)

### 6. Controller — FilialLocadora
Expor endpoints REST pra `FilialLocadoraService` usando os DTOs já existentes.

### 7. Controller — Manutencao
Expor endpoints REST pra `ManutencaoService` usando os DTOs já existentes. Conferir se o service já bloqueia o veículo corretamente (dependência com a task 16).

### 8. Controller — Locacao (ciclo completo)
Expor os 4 fluxos já implementados no `LocacaoService`.
- `POST` criar reserva (`criarReservaDoVeiculo`)
- `PATCH`/`POST` confirmar retirada (`confirmarRetirada`)
- `PATCH`/`POST` confirmar devolução (`confirmarDevolucao`)
- `PATCH`/`POST` cancelar (`cancelarLocacao`)
- Pensar no verbo HTTP e nomenclatura de rota certos pra cada transição de status (não é CRUD simples, é máquina de estado)

### 9. Global Exception Handler (@RestControllerAdvice)
Criar handler global, evoluindo junto com os controllers (não esperar todos ficarem prontos).
- Capturar as exceptions já existentes: `EntidadeNaoEncontradaException`, `RegistroDuplicadoException`, `StatusInvalidoException`, `VeiculoNaoDisponivelException`
- Definir formato padrão de corpo de erro (status HTTP, mensagem, timestamp)
- Mapear cada exception pro status HTTP certo (404, 409, 400, etc — pensar em cada caso)
- Cobrir também erro de validação de `@RequestBody` (`MethodArgumentNotValidException`) e um fallback genérico pra exceção não mapeada (sem vazar stacktrace)

## Backlog — ordenado por impacto

### 18. Listar veículos disponíveis (com filtro por categoria/filial)
Não existe busca de veículos disponíveis pra locação — é a funcionalidade mais básica de uma locadora real.

**Guia, não solução:**
- Query nova no repository (nome de método) ou filtro em memória? Qual a diferença de performance entre as duas?
- Filtro por categoria e/ou filial deve ser opcional ou obrigatório?

### 10. Validar sobreposição de datas na reserva
`criarReservaDoVeiculo` só checa status atual do veículo, não checa se já existe outra locação daquele veículo com datas sobrepostas. Risco de double-booking real.

### 20. Histórico de locações por cliente
Não existe método pra listar todas as locações de um cliente específico. Funcionalidade comum de tela de histórico.

**Guia, não solução:** essa query nasce no repository ou é composta no service?

### [Feature] Bloqueio de cliente inadimplente
Cliente com locação vencida sem devolução ou pendência não deveria conseguir criar nova reserva.

**Guia, não solução:** essa checagem entra no `LocacaoService.criarReservaDoVeiculo`, ou você criaria um método separado tipo `validarClienteApto`?

### 26. [Feature] Relatório de ocupação da frota
Endpoint tipo "quantos veículos DISPONIVEL vs LOCADO vs EM_MANUTENCAO" — contagem agrupada por status.

**Guia, não solução:** dá pra fazer com `Page`/`List` filtrado em memória, ou vale pesquisar `@Query` com `GROUP BY` no Spring Data? Qual escala melhor conforme a frota cresce?

### 28. [Feature] Faturamento total por filial/período
Somar `valorLocacao` de locações concluídas num intervalo de datas, agrupado por filial.

**Guia, não solução:** agregação SQL (`@Query` com `SUM`) ou buscar a lista e somar em Java? Qual mais correto numa API que pode crescer bastante?

### 24. [Feature] Histórico de manutenções por veículo
Listar todas as manutenções que um veículo específico já passou.

**Guia, não solução:** query nasce no `ManutencaoRepository` ou é composta no service?

### 19. Proteger exclusão de Veiculo com histórico de locação
`deletarVeiculoViaId` apaga direto, sem checar se existem locações vinculadas (mesmo já devolvidas). Risco de perder histórico ou quebrar integridade.

**Guia, não solução:** bloquear exclusão com exceção, ou trocar por "inativação" (soft delete)? Pensar no trade-off de cada abordagem.

### 25. [Feature] Transferência de veículo entre filiais (one-way)
Veículo tem `filialAtual` fixa desde o cadastro. Permitir que o carro "mude" de filial — cenário real de locação one-way.

**Guia, não solução:**
- Isso deveria acontecer automaticamente em `confirmarDevolucao` quando `filialRetirada != filialDevolucao`, ou é uma ação manual separada no VeiculoService?
- Se for automático, onde no fluxo atual de `LocacaoService` isso se encaixa?

### 11. Multa por atraso na devolução
`confirmarDevolucao` não compara data real de devolução com a planejada.

### 12. Franquia de km / cobrança de excedente
Hoje só soma km rodado, sem limite contratado nem cobrança de excedente.

### 14. Taxa de cancelamento por proximidade da retirada
Hoje `cancelarLocacao` não cobra taxa. Regra real de locadora costuma cobrar se cancelar muito perto da data de retirada.

### 27. [Feature] Alerta de manutenção preventiva por quilometragem
Marcar veículo pra manutenção obrigatória a cada X km rodado (ex: a cada 10.000km), usando o campo `quilometragem`.

**Guia, não solução:**
- Verificação sob demanda (método chamado manualmente) ou algo automático (`@Scheduled`, conceito novo — pode ficar pra depois)?
- Onde fica esse "X km limite" — constante, campo na entidade, configuração?

### 15. Renovação/extensão de locação em andamento
Não existe forma de estender uma locação já `RETIRADO` sem cancelar e criar outra.

### 17. Paginação dinâmica (Veiculo e Manutencao)
`buscarTodosPaginado` tem página/tamanho/ordenação fixos no código. Pesquisar como receber `Pageable` como parâmetro vindo do controller (Spring resolve isso automaticamente).

**Guia, não solução:** pensar na assinatura do método de service depois dessa mudança.

### 13. Refatorar calculoDiariaPorCategoria (if/else → switch/map)
Cadeia de if/else if em `LocacaoService` pode virar switch expression (Java 21) ou Map de multiplicadores por categoria. Refatoração de qualidade, não bloqueia nada.

### 23. FilialLocadoraService — busca por CNPJ (consistência com outros services)
Todo service com campo único de negócio tem busca por esse campo além do ID (chassi/placa/renavam em Veiculo, CPF em PessoaFisica, CNPJ em PessoaJuridica). `FilialLocadora` só busca por ID, apesar de ter `cnpjFilial` único.

---

## Pendente de sincronizar no ClickUp

As tasks marcadas com número já existem na lista do ClickUp ("Locadora API — Docker, Controllers e Exception Handler"). As sem número (bloqueio de cliente inadimplente) e as tasks 25-28 ainda **não foram criadas lá** — a API bateu rate limit durante a sessão. Sincronizar quando o limite liberar.
