from pathlib import Path
from reportlab.platypus import SimpleDocTemplate, Paragraph, PageBreak
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.pagesizes import A4
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from pypdf import PdfReader
from html import escape

out=Path('output/curriculo'); out.mkdir(parents=True,exist_ok=True)
pdfmetrics.registerFont(TTFont('ArialCV',r'C:\Windows\Fonts\arial.ttf'))
pdfmetrics.registerFont(TTFont('ArialCVBold',r'C:\Windows\Fonts\arialbd.ttf'))
pdfmetrics.registerFontFamily('ArialCV',normal='ArialCV',bold='ArialCVBold')
rows=[
('title','Arthur Gabriel Borges Silva'),
('subtitle','Desenvolvedor Back-end Júnior | Java e Spring Boot'),
('contact','Brasília, DF | (61) 99115-2822 | arthurborges2607@gmail.com'),
('contact','linkedin.com/in/arthurgbsilva | github.com/ArthurSilvagbs'),
('heading','Resumo profissional'),
('body','Desenvolvedor Full Stack Júnior com experiência em sistema empresarial em produção, utilizando NestJS, TypeScript e PostgreSQL. Foco em back-end Java e Spring Boot, com projetos de APIs REST, persistência com JPA/Hibernate, migrations com Flyway e testes unitários. Vivência com Git, pull requests, code review, GitHub Actions e sprints semanais.'),
('heading','Competências técnicas'),
('body','<b>Linguagens e web:</b> Java 21, TypeScript, JavaScript, SQL, HTML e CSS.'),
('body','<b>Back-end:</b> Spring Boot, Spring MVC, Spring Data JPA, Hibernate, APIs REST, DTOs, Bean Validation, tratamento de exceções, NestJS e TypeORM.'),
('body','<b>Segurança:</b> fundamentos de Spring Security, autenticação, autorização e JSON Web Token (JWT).'),
('body','<b>Dados e qualidade:</b> PostgreSQL, MySQL, Flyway, testes automatizados com JUnit e Mockito, Jest, Postman e Swagger.'),
('body','<b>Ferramentas:</b> Git, GitHub, GitHub Actions (CI), Maven, Docker, Docker Compose e Jira. <b>Front-end:</b> React e Next.js.'),
('heading','Experiência profissional'),
('job','Desenvolvedor Full Stack Júnior | Soluction Eventos'),
('meta','ago/2026 - atual | Brasília, DF'),
('bullet','Desenvolvimento e manutenção de APIs REST, endpoints, services, DTOs e regras de negócio com NestJS, TypeScript, TypeORM e PostgreSQL em sistema de gestão de eventos governamentais em produção.'),
('bullet','Atuação em módulos de licitação, produção de eventos, compras, financeiro, auditoria e relatoria; desenvolvimento de telas e integração com APIs em Next.js/React.'),
('bullet','Criação de testes unitários e colaboração com Git/GitHub, pull requests, code review e GitHub Actions, em sprints semanais acompanhadas no Jira.'),
('job','Desenvolvedor Full Stack Trainee | Soluction Eventos'),
('meta','fev/2026 - jul/2026 | Brasília, DF'),
('bullet','Implementação de funcionalidades e correção de bugs sob orientação de desenvolvedores sêniores, com NestJS, TypeScript, TypeORM e Next.js/React; participação em sprints e cerimônias ágeis.'),
('job','Experiência anterior | SS Inovatec'),
('body','Projetista Orçamentista / Técnico (out/2024 - jan/2026) e Auxiliar Técnico (jul/2024 - out/2024). Análise de requisitos técnicos, projetos de infraestrutura e diagnóstico de falhas de rede.'),
('heading','Projetos em Java'),
('job','Locadora-API | API REST de gestão de locação de veículos'),
('contact','github.com/ArthurSilvagbs/Locadora-API'),
('bullet','Desenvolvimento com Java 21, Spring Boot, JPA/Hibernate e PostgreSQL para clientes, veículos, filiais, manutenção e fluxo de reserva, retirada, devolução e cancelamento.'),
('bullet','Arquitetura em camadas, DTOs, validações, transações e tratamento centralizado de exceções; versionamento do banco de dados com migrations Flyway.'),
('bullet','Testes unitários com JUnit e Mockito; pipeline de build e testes com Maven no GitHub Actions e banco PostgreSQL em Docker Compose. Implementação de autenticação JWT com Spring Security em andamento.'),
('job','Alerta Rua | Projeto acadêmico em grupo na UCB'),
('body','Desenvolvimento do back-end em Java e Spring Boot para sistema de alertas de risco de alagamento e enchente. Repositório privado.'),
('heading','Formação acadêmica'),
('body','<b>Análise e Desenvolvimento de Sistemas</b> | Universidade Católica de Brasília'),
('body','jan/2024 - dez/2026 (conclusão prevista)'),
('heading','Cursos complementares e idiomas'),
('body','<b>Udemy:</b> Java COMPLETO: Do Zero ao Profissional + Projetos; Java COMPLETO: Programação Orientada a Objetos + Projetos; NestJS para REST API com TypeORM, Autenticação JWT e Testes.'),
('body','<b>Idiomas:</b> Português nativo | Inglês intermediário.'),
]
rows=rows[:6]+[
('heading','Experiência profissional'),
('job','Desenvolvedor Full Stack Júnior | Soluction Eventos'),
('meta','ago/2026 - atual | Brasília, DF'),
('bullet','Desenvolvimento e manutenção de sistema de gestão interna em produção, com dezenas de módulos integrados para o setor de eventos governamentais.'),
('bullet','Implementação de endpoints, services, DTOs e regras de negócio em NestJS, TypeScript, TypeORM e PostgreSQL nos módulos de licitação, produção de eventos, compras, financeiro, auditoria e relatoria.'),
('bullet','Desenvolvimento de telas em Next.js/React, incluindo formulários, tabelas, filtros, modais e consumo de APIs REST.'),
('bullet','Criação de testes unitários e apoio em automações do fluxo de desenvolvimento.'),
('bullet','Uso diário de Git/GitHub, branches, pull requests, GitHub Actions e code review em equipe; entregas em sprints semanais com demandas acompanhadas no Jira.'),
('job','Desenvolvedor Full Stack Trainee | Soluction Eventos'),
('meta','fev/2026 - jul/2026 | Brasília, DF'),
('bullet','Atuação com NestJS, TypeScript, TypeORM e Next.js/React nos módulos do sistema de gestão interna.'),
('bullet','Implementação de funcionalidades pontuais e correção de bugs sob orientação de desenvolvedores sêniores.'),
('bullet','Apoio na criação de telas e componentes em Next.js/React, incluindo formulários e listagens.'),
('bullet','Participação em sprints semanais e cerimônias ágeis, com demandas acompanhadas no Jira.'),
('job','Projetista Orçamentista / Técnico | SS Inovatec'),
('meta','out/2024 - jan/2026 | Brasília, DF'),
('bullet','Análise de requisitos e dimensionamento de infraestrutura a partir de documentação técnica e especificações de projetos.'),
('bullet','Diagnóstico de gargalos e planejamento de soluções técnicas para clientes.'),
('job','Auxiliar Técnico | SS Inovatec'),
('meta','jul/2024 - out/2024 | Brasília, DF'),
('bullet','Troubleshooting e diagnóstico de falhas em infraestrutura e redes, incluindo conectividade e configuração de protocolos TCP/IP.'),
('bullet','Resolução de incidentes e acompanhamento de chamados técnicos.'),
('break',''),
('heading','Projetos'),
('job','Locadora-API | API REST de gestão de locadora de veículos'),
('contact','Java 21, Spring Boot, PostgreSQL | github.com/ArthurSilvagbs/Locadora-API'),
('bullet','Desenvolvimento de cadastros de clientes, veículos e filiais, controle de manutenção e fluxo de reserva, retirada, devolução e cancelamento de locações.'),
('bullet','Persistência com Spring Data JPA/Hibernate, arquitetura em camadas, DTOs, validações, transações e tratamento centralizado de exceções com @RestControllerAdvice.'),
('bullet','Versionamento do banco de dados com migrations Flyway e testes unitários automatizados com JUnit e Mockito para regras de negócio e cenários de erro.'),
('bullet','Pipeline de integração contínua (CI) com build e testes Maven no GitHub Actions; PostgreSQL em Docker Compose.'),
('bullet','Implementação de autenticação JWT com Spring Security em andamento, com estudos de autorização e proteção de endpoints.'),
('job','Alerta Rua | Sistema de alerta de alagamentos e enchentes'),
('meta','Java, Spring Boot | Projeto acadêmico em grupo na UCB | Repositório privado'),
('bullet','Desenvolvimento do back-end para aplicação que notifica cidadãos sobre áreas de risco, com regiões demarcadas por polígono/raio e alertas push, integrando dados da Defesa Civil e institutos de meteorologia.'),
('job','ByteBankAPI | API REST de simulação bancária'),
('contact','Java, Spring Boot, Hibernate, PostgreSQL | github.com/ArthurSilvagbs/ByteBank-SpringBoot'),
('bullet','Projeto com foco em boas práticas de engenharia de software e arquitetura em camadas.'),
('heading','Competências técnicas'),
('body','<b>Linguagens e web:</b> Java, TypeScript, JavaScript, SQL, HTML e CSS.'),
('body','<b>Back-end:</b> Spring Boot, Spring MVC, Spring Data JPA, Hibernate, NestJS, TypeORM e APIs REST.'),
('body','<b>Segurança:</b> fundamentos de Spring Security, autenticação, autorização e JSON Web Token (JWT).'),
('body','<b>Banco de dados:</b> PostgreSQL, MySQL, MongoDB e migrations com Flyway.'),
('body','<b>Testes:</b> JUnit, Mockito, Jest, testes unitários automatizados e testes de API com Postman.'),
('body','<b>Ferramentas:</b> Git, GitHub, GitHub Actions (CI), Docker, Docker Compose, Maven, Swagger e Jira. <b>Front-end:</b> React e Next.js.'),
('heading','Formação acadêmica'),
('body','<b>Análise e Desenvolvimento de Sistemas</b> | Universidade Católica de Brasília'),
('body','jan/2024 - dez/2026 (conclusão prevista)'),
('heading','Cursos complementares'),
('body','Java COMPLETO: Do Zero ao Profissional + Projetos | Udemy'),
('body','Java COMPLETO: Programação Orientada a Objetos + Projetos | Udemy'),
('body','NestJS para REST API com TypeORM, Autenticação JWT e Testes | Udemy'),
('heading','Idiomas'),
('body','Português nativo | Inglês intermediário.'),
]
styles={}
for kind,size,leading,before,after in [('title',20,24,0,4),('subtitle',12,15,0,5),('contact',9.5,12,0,3),('heading',12,15,11,5),('body',10.2,13.4,0,3),('job',11,14,8,3),('meta',9.8,12.5,0,4),('bullet',10.5,14,0,4)]:
    styles[kind]=ParagraphStyle(kind,fontName='ArialCVBold' if kind in ['title','heading','job'] else 'ArialCV',fontSize=size,leading=leading,spaceBefore=before,spaceAfter=after,keepWithNext=kind in ['heading','job','meta'],leftIndent=8 if kind=='bullet' else 0)
doc=SimpleDocTemplate(str(out/'Arthur_Silva_Curriculo_Backend_Java.pdf'),pagesize=A4,rightMargin=42,leftMargin=42,topMargin=36,bottomMargin=36,title='Arthur Gabriel Borges Silva - Desenvolvedor Back-end Java Júnior',author='Arthur Gabriel Borges Silva')
story=[]
for kind,value in rows:
    if kind=='break':
        story.append(PageBreak())
        continue
    value=value.replace('github.com/ArthurSilvagbs/Locadora-API','<link href="https://github.com/ArthurSilvagbs/Locadora-API">github.com/ArthurSilvagbs/Locadora-API</link>') if kind=='contact' else value
    story.append(Paragraph(('- ' if kind=='bullet' else '')+value,styles[kind]))
doc.build(story)
import re
md='\n\n'.join(('# ' if kind=='title' else '## ' if kind=='heading' else '### ' if kind=='job' else '- ' if kind=='bullet' else '')+re.sub('<[^>]+>','',value) for kind,value in rows)+'\n'
(out/'Arthur_Silva_Curriculo_Backend_Java.md').write_text(md,encoding='utf-8')
r=PdfReader(out/'Arthur_Silva_Curriculo_Backend_Java.pdf')
text='\n'.join(p.extract_text() for p in r.pages)
(Path('tmp/curriculo')/'extracted.txt').write_text(text,encoding='utf-8')
for term in ['Flyway','Mockito','GitHub Actions','SQL','HTML','CSS','JWT','Soluction','dez/2026']:
    assert term in text,term
print('Pages:',len(r.pages),'Words:',len(text.split()))
