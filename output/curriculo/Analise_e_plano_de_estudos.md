# Análise do currículo e plano de evolução para back-end Java júnior

Análise realizada em 10/09/2026, com base no currículo fornecido, no código local da Locadora-API e em três anúncios de vagas consultados nesta data. As experiências profissionais foram preservadas conforme seu relato; a leitura do projeto foi estática, sem executar build ou testes. A amostra de vagas orienta prioridades, mas não representa todo o mercado nem garante disponibilidade das posições.

## Avaliação do seu posicionamento

Você já tem material para se candidatar a vagas júnior: experiência profissional com sistema em produção, evolução de trainee para júnior, trabalho em equipe e um projeto Java que ultrapassa cadastros simples. A locadora demonstra regras de negócio, ciclo de locação, persistência, transações, validações e testes. O principal ganho agora é apresentar essas evidências com clareza e consolidar a qualidade do projeto.

Sua experiência profissional é em NestJS/TypeScript. Isso continua valioso para Java: APIs REST, DTOs, SQL, regras de negócio, testes, Git e revisão de código são conhecimentos transferíveis. O currículo deixa explícito onde você usa cada stack, sem transformar experiência de projeto em experiência profissional Java.

## O que mudou no currículo

- Título direcionado a Desenvolvedor Back-end Júnior com Java e Spring Boot.
- Resumo com contexto de produção e evidências, substituindo expressões como “domínio” e “APIs escaláveis”, que precisariam de exemplos para se sustentar.
- Experiências profissionais na primeira página e locadora como primeiro projeto na segunda página, seguida das competências técnicas.
- Inclusão de SQL, HTML, CSS, Spring Data JPA, Hibernate, Flyway, autenticação, autorização, JWT e testes automatizados. HTML e CSS ficam sob “Linguagens e web”, sem classificá-los como linguagens de programação.
- GitHub Actions descrito como CI: o workflow encontrado compila e testa, mas não faz deploy automático. Docker descrito como banco em Docker Compose, conforme a configuração encontrada.
- As quatro experiências foram mantidas separadas, com cargos, períodos e atividades. Os três projetos e MongoDB foram preservados, conforme o currículo original.
- Cursos Udemy classificados como cursos complementares. Removido o semestre da graduação para evitar informação que envelhece rapidamente; mantida a conclusão prevista em dezembro de 2026.
- Removidas promessas de faturamento, transferências e alertas automáticos da locadora: planos futuros não devem parecer entregas realizadas.
- PDF de duas páginas, texto selecionável, uma coluna, títulos convencionais e nenhuma tabela de layout. A extração de texto e a renderização foram verificadas. Isso facilita leitura, mas não constitui certificação de compatibilidade com todos os ATS.

## Conhecimentos observados no projeto

| Conhecimento | Evidência e limite |
| --- | --- |
| Java 21 e Spring Boot | Configuração Maven e classes da aplicação. |
| Spring MVC e APIs REST | Controllers, DTOs e organização por camadas. |
| JPA/Hibernate e PostgreSQL | Entidades, repositories e dependências. |
| Transações | Uso de `@Transactional` no fluxo de locação. |
| Validação e erros | Bean Validation e tratamento centralizado de exceções. |
| Flyway | Migrations V1 de schema e V2 de usuários. |
| JUnit e Mockito | Testes de services, com cenários de sucesso e erro; não foi calculada cobertura nem confirmada aprovação da suíte. |
| GitHub Actions | Workflow com build e testes Maven, Java 21 e PostgreSQL. |
| Docker Compose | Serviço PostgreSQL; não equivale a uma aplicação inteira publicada em containers. |
| Spring Security e JWT | Configuração, filtro e serviço de tokens implementados, com pendências no cadastro e nas regras de acesso. |

## Prioridade imediata no próprio projeto

**1. Concluir segurança e provar seu funcionamento.** O método `AuthService.registrar` recebe um `PasswordEncoder`, mas não chama `encode`; `UsuarioMapper` copia a senha recebida diretamente para a entidade. Portanto, o cadastro observado persiste a senha sem aplicar o hash configurado. A existência de um bean BCrypt não resolve isso sozinha. Corrija esse fluxo e teste que a senha salva difere da original e que `matches` a valida. A documentação explica o papel do [PasswordEncoder](https://docs.spring.io/spring-security/reference/7.0/features/authentication/password-storage.html).

Também existe um `requestMatchers("/")` sem regra associada antes de `anyRequest`, que precisa ser revisado. A configuração lida não restringe endpoints por papel; não encontrei `hasRole` ou `@PreAuthorize`. O cadastro aceita `role` do cliente e a encaminha à entidade: ao concluir autorização, defina no servidor o papel permitido no cadastro público para impedir escolha de privilégios. Teste login, senha inválida, token expirado, ausência de token e usuário sem permissão. Use como referência a [autorização de requisições do Spring Security](https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html).

Por isso, o currículo inclui esses conhecimentos como fundamentos e descreve a implementação no projeto como em andamento. Depois de corrigir e validar, você poderá substituir esse trecho por uma entrega concreta de autenticação JWT e autorização por perfis.

**2. Acrescentar testes de integração.** A suíte observada concentra-se em services com mocks e há um teste de contexto. A próxima entrega deve atravessar HTTP, segurança, persistência e migrations com PostgreSQL real de teste. Aprenda MockMvc e Testcontainers; valide reserva, retirada, devolução, restrições do banco e códigos HTTP. O [guia oficial de Spring Boot com Testcontainers](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html) é um ponto de partida. Não basta aumentar a quantidade de testes que apenas repetem o comportamento existente: o teste de cadastro atual, por exemplo, aceita a senha original no objeto de usuário.

**3. Aprofundar SQL, JPA e concorrência.** Pratique JOIN, agregações, índices, EXPLAIN, constraints, lazy loading, problema N+1 e limites de transação. Na locadora, use a pergunta “o que acontece quando duas pessoas reservam o mesmo carro ao mesmo tempo?” para estudar isolamento e bloqueio otimista ou pessimista. `@Transactional` não demonstra, por si só, que esse conflito foi resolvido. Como exercício, confira também datas de reserva, disponibilidade de veículo em manutenção e o significado de quilometragem final versus distância percorrida na devolução.

**4. Tornar o portfólio fácil de executar e avaliar.** Não encontrei README na raiz. Escreva instruções de execução, variáveis de ambiente sem valores secretos, migrações, testes, endpoints e exemplos de chamadas. Acrescente documentação OpenAPI ao projeto, um Dockerfile para a API e Compose com aplicação e banco. Só descreva o ambiente inteiro como containerizado depois de validar essa execução.

## Roteiro sugerido para seis semanas

| Período | Foco | Entrega observável |
| --- | --- | --- |
| Semana 1 | Segurança e HTTP | Cadastro com hash, regras de acesso, respostas 401/403 e testes de cenários negados. |
| Semana 2 | Testes de integração | Fluxo de autenticação e locação com PostgreSQL e migrations em ambiente de teste. |
| Semana 3 | SQL e JPA | Consultas explicadas, um índice justificado e teste de conflito entre reservas concorrentes. |
| Semana 4 | Documentação e Docker | README, exemplos de API, OpenAPI e execução reproduzível de aplicação e banco. |
| Semanas 5 e 6 | Deploy e operação | Publicar uma versão de demonstração, configurar secrets, logs e health check; entender como atualizar e reverter uma versão. |

Em paralelo, revise fundamentos Java: interfaces, composição, collections, generics, `equals/hashCode`, exceções, streams, `Optional`, `BigDecimal` e `java.time`. Use o próprio código para explicar decisões em voz alta. Pratique inglês técnico e uma apresentação de cinco minutos sobre sua atuação na Soluction e sobre uma regra de negócio da locadora.

AWS básica pode vir junto do deploy: entenda aplicação, banco gerenciado, permissões e logs antes de perseguir certificação. Microsserviços, mensageria e Kubernetes podem entrar depois, conforme as vagas-alvo. Minha recomendação é primeiro demonstrar que você consegue construir, testar e operar bem essa API.

## Comparação com vagas consultadas

Na [vaga Java júnior da Minsait](https://minsait.gupy.io/jobs/11952340), aparecem Java, Spring Boot, APIs REST, SQL Server, Git, metodologias ágeis, AWS e Docker. TypeScript/React e Jira são diferenciais que se conectam ao seu histórico. AWS e arquitetura distribuída aparecem como lacunas a desenvolver; não devem ser adicionadas ao currículo como conhecimentos atuais sem prática.

Na [vaga júnior Java e React da Compass](https://compass.gupy.io/jobs/12091249), aparecem Java 17+, Spring Boot, testes automatizados, Git e boas práticas, com NestJS como diferencial. A posição também exige tecnologias mobile; o anúncio mostra valor na combinação de stacks, mas não implica aderência integral do seu perfil.

Na [vaga Fullstack Backend Júnior da Confitec](https://confitec.gupy.io/job/eyJqb2JJZCI6MTIyODg5NzksInNvdXJjZSI6Imd1cHlfcG9ydGFsIn0=), aparecem Java/Spring Boot e APIs, com AWS entre os diferenciais e atuação sobre saúde e performance dos serviços. Há requisitos adicionais em Python que você não relatou. Não é necessário estudar toda tecnologia presente em cada anúncio; selecione vagas cujo núcleo combine com seu perfil.

## Como usar a versão otimizada para ATS

Envie o PDF quando esse formato for aceito. Use o arquivo Markdown como texto editável para copiar os blocos nos formulários e adaptar o resumo a uma vaga específica. Confira nome, contato, cargos, datas, formação e habilidades depois de qualquer importação. A [orientação da própria Gupy](https://www.gupy.io/blog-do-emprego/boas-praticas-curriculo-gupy) destaca a importância do preenchimento correto dos campos do cadastro.

Use os termos da vaga quando correspondem ao que você realmente sabe, por exemplo “Spring Data JPA”, “testes unitários”, “PostgreSQL” e “integração contínua”. Não esconda palavras-chave, não repita tecnologias artificialmente e não invente métricas. Otimização de ATS melhora clareza e aderência textual; não garante entrevista ou aprovação.

O melhor próximo ganho na experiência profissional é substituir um dos tópicos abrangentes por uma entrega sua: qual problema existia, o que você implementou e o que mudou para o usuário. Só acrescente percentuais ou volumes se você tiver dados verificáveis. Preserve informações confidenciais do sistema empresarial.

Você pode começar a se candidatar enquanto segue esse roteiro. O objetivo dos estudos é aumentar a consistência entre o currículo, a demonstração do projeto e suas respostas na entrevista.
