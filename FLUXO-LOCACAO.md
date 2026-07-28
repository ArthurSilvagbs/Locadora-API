# Fluxo de locação de veículo

Mapeamento do fluxo completo do cliente (estilo Localiza) e os métodos principais que sustentam ele.

## Fluxo do usuário

1. Cliente define filial de retirada e data de retirada (validação: data de retirada não pode ser anterior a hoje).
2. Cliente define filial de devolução e data de devolução (validação: data de devolução não pode ser anterior à data de retirada).
3. Sistema retorna os veículos disponíveis naquela filial, nesse intervalo de datas — já com o preço calculado por veículo.
4. Cliente escolhe um veículo da lista.
5. Confirmação do pedido.
6. Pagamento (escolha da forma de pagamento) — locação é efetivada aqui.
7. (Bônus) Disparo de e-mail confirmando a locação.

Validações puramente lógicas (não tocam banco): data de retirada ≥ hoje, data de devolução ≥ data de retirada.
Validações de estado (dependem do banco): disponibilidade do veículo no período, existência da filial.

**Princípio de dupla verificação:** o front valida pra dar feedback rápido (UX). O back sempre revalida tudo — nunca confia no que vem do client, já que a API pode ser chamada diretamente sem passar pelo front.

## Métodos principais

### 1. `buscarVeiculosDisponiveis(filialRetiradaId, dataRetirada, dataDevolucao)`
- Provável dono: `VeiculoService` (quem ele retorna é veículo).
- Valida as datas (lógica pura).
- Busca veículos da filial informada.
- Cruza com `Locacao` pra excluir veículos com conflito de datas.
- Retorna a lista já com valor calculado por veículo.

### 2. `calcularValorLocacao(veiculo, dataRetirada, dataDevolucao)`
- Modelo: categoria do veículo × dias × multiplicador de marca (premium ou não).
- Reaproveitado tanto na listagem (mostrar preço) quanto na criação da locação (recalcular do zero, nunca confiar em valor vindo do client).
- Pendência: onde mora a informação de "marca premium" (hoje `marca` é uma `String` livre, sem essa info).

### 3. `locarVeiculo(LocacaoCreateDTO)`
- Dono: `LocacaoService`.
- Recebe cliente, veículo escolhido, filiais, datas, forma de pagamento.
- Revalida que o veículo ainda está disponível nesse período (dupla checagem, pode ter passado tempo desde a listagem).
- Recalcula o valor do zero.
- Cria a `Locacao` com status inicial `PENDENTE_DE_RETIRADA`.

## Próximos passos (futuro, não bloqueia o MVP)

- Lock/fila pra evitar duas locações concorrentes no mesmo veículo/período (já feito algo parecido no bytebank — revisitar quando o grosso do projeto estiver fechado).
- Envio de e-mail como efeito colateral, fora da transação principal — não deve travar a criação da locação se falhar.
