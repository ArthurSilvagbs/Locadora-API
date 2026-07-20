# Regras de negócio — Locadora de Carros

Rascunho de fluxos e regras pra sair do CRUD básico. Ideia: escolher 3-4 pra virar o "coração" do projeto e deixar o resto como próxima iteração.

## Disponibilidade e reserva do veículo

- Cliente pesquisa carros disponíveis por categoria, filial e intervalo de datas — o sistema precisa saber dizer "esse veículo está livre nesse período" cruzando com outras locações ativas.
- O que acontece se o veículo estiver em manutenção programada dentro do período pedido? Ele devia nem aparecer na busca.
- Dá pra reservar um veículo sem retirar na hora (reserva futura) ou a locação só existe no ato da retirada?

## Ciclo de vida da locação (as transições de status)

- Quem pode mover PENDENTE_DE_RETIRADA → JA_RETIRADO? O que precisa ser validado nesse momento (ex: conferir km atual do carro, checklist de avarias)?
- JA_RETIRADO → DEVOLVIDO: precisa calcular km rodado, comparar com franquia, cobrar excedente?
- Devolução atrasada (depois de `dataDevolucao`): existe multa por dia de atraso? Como isso muda o `valorLocacao` final?
- Devolução em filial diferente da retirada (`filialRetirada` ≠ `filialDevolucao`): isso já é modelado — faz sentido ter uma taxa extra por isso?
- Cancelamento: cliente pode cancelar uma locação PENDENTE_DE_RETIRADA? Tem prazo mínimo antes da retirada pra cancelar sem multa?

## Precificação

- Como o `valorLocacao` é calculado — diária × categoria do veículo × número de dias? Isso hoje é passado pronto no DTO, mas seria mais "de negócio" se o sistema calculasse.
- Forma de pagamento influencia o preço (desconto à vista, juros parcelado)?
- Cliente PJ tem tabela de preço diferente de PF (ex: frota corporativa)?

## Elegibilidade do cliente

- Pessoa física precisa ter idade mínima e CNH válida (categoria compatível com o veículo) pra poder alugar — isso é uma regra que vale a pena validar no momento da locação, não só cadastro.
- Cliente com locação em aberto e atrasada pode abrir uma segunda locação? Provavelmente não — isso é uma regra de bloqueio.
- Limite de locações simultâneas por cliente.

## Manutenção

- Veículo entra em manutenção automaticamente ao bater uma quilometragem (ex: a cada 10.000km) ou por tempo desde a última revisão?
- Enquanto `Manutencao` está aberta pro veículo, ele fica indisponível pra novas locações — isso cruza direto com a busca de disponibilidade.
- Devolução com avaria grave dispara criação automática de uma manutenção?

## Filial

- Cada filial tem sua própria frota "fisicamente ali", ou o sistema só rastreia via `filialRetirada`/`filialDevolucao` das locações? Isso decide como você calcula "veículos disponíveis nessa filial agora".
- Transferência de veículo entre filiais (fora do fluxo de locação) é algo que existe no seu domínio?

## Sugestão de foco inicial

Pra não travar, escolher 3-4 fluxos pra virar o "coração" do projeto:

1. Disponibilidade por data
2. Cálculo automático do valor
3. Transições de status com validação
4. Elegibilidade do cliente

Isso já é bem mais que CRUD e dá pra explicar numa entrevista com propriedade.
