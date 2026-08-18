# Portal Incluir — Inclusão e Acompanhamento Educacional

Repositório do projeto **Portal Incluir**, desenvolvido na disciplina de **Projeto I** do curso de Análise e Desenvolvimento de Sistemas do IFPB Campus Monteiro.

O Portal Incluir é uma plataforma digital destinada à centralização e organização das informações relacionadas ao acompanhamento de estudantes com deficiência ou necessidades educacionais específicas.

> Em Projeto I, serão elaborados e documentados os artefatos necessários para especificar o sistema. A implementação será realizada posteriormente durante a disciplina de Projeto II.

---

## Sobre o Portal Incluir

Atualmente, as informações relacionadas ao acompanhamento dos estudantes estão distribuídas entre documentos Word, arquivos PDF, Google Drive, SUAP e diferentes meios de comunicação.

Essa fragmentação dificulta a localização das informações, provoca retrabalho, reduz a padronização dos documentos e torna mais lento o acompanhamento pedagógico e assistencial.

O **Portal Incluir** propõe reunir essas informações em um único ambiente, permitindo que usuários autorizados possam:

- cadastrar e consultar estudantes;
- registrar entrevistas e acompanhamentos;
- organizar documentos;
- elaborar planos de ação e PEIs;
- registrar adaptações pedagógicas;
- consultar o histórico dos estudantes;
- gerar documentos e relatórios;
- acessar funcionalidades conforme seu perfil;
- acompanhar ações relevantes por meio de registros de auditoria.

Mais informações estão disponíveis no [Documento de Visão](docs/visao.md).

---

## Objetivo

Desenvolver uma plataforma web segura, acessível e responsiva para apoiar o trabalho de coordenadores, professores e equipes de apoio no acompanhamento educacional dos estudantes atendidos.

A solução busca:

- centralizar as informações;
- reduzir o retrabalho;
- padronizar documentos e registros;
- facilitar a consulta ao histórico dos estudantes;
- melhorar a comunicação entre os profissionais;
- proteger as informações por meio de controle de acesso;
- contribuir para um acompanhamento educacional mais eficiente e inclusivo.

---

## Público-alvo

### Usuários diretos

- coordenador;
- substituto do coordenador;
- professores;
- equipe de apoio ou equipe multidisciplinar;
- profissionais da COPED e da CAEST;
- gestão de Tecnologia da Informação.

### Beneficiários indiretos

- estudantes com deficiência ou necessidades educacionais específicas;
- familiares e responsáveis;
- comunidade acadêmica;
- gestão institucional.

---

## Documentação do projeto

### Fase 1 — Descoberta e levantamento

| Artefato | Situação | Arquivo |
|---|---|---|
| Documento de Visão | Concluído | [docs/visao.md](docs/visao.md) |
| Stakeholders e personas | Em elaboração | — |
| Entrevistas e levantamento | Em elaboração | — |
| Requisitos | Em elaboração | — |

### Fase 2 — Especificação funcional

| Artefato | Situação | Arquivo |
|---|---|---|
| Casos de uso detalhados | Em elaboração | — |
| Matriz de rastreabilidade | Em elaboração | — |
| Regras de negócio | Em elaboração | — |
| Protótipo de interface | Concluído | [Acessar no Figma](https://www.figma.com/design/vFjSKJomALDbPFoxTb5RvQ/Prototipa%C3%A7%C3%A3o---Portal-Incluir?m=auto&t=b3gqbFZ6KjZkovY8-6) |
| Apresentação do protótipo | Concluído | [Acessar no Figma Slides](https://www.figma.com/slides/qpQbdmK8aFWRGKVYal0dZ5/Apresenta%C3%A7%C3%A3o-Portal-Incluir---Fase-1?t=b3gqbFZ6KjZkovY8-6) |
| Validação do protótipo | Concluído | [docs/validacao-prototipo.md](docs/validacao-prototipo.md) |
| Testes de aceitação | Em elaboração | — |

### Fase 3 — Especificação técnica

| Artefato | Situação | Arquivo |
|---|---|---|
| Definições arquiteturais | Em elaboração | — |
| Diagrama de classes | Em elaboração | — |
| Diagrama de objetos | Em elaboração | — |
| Diagrama de componentes | Em elaboração | — |
| Diagrama de implantação | Em elaboração | — |
| Diagramas de sequência | Em elaboração | — |
| Diagramas de comunicação | Em elaboração | — |
| Diagramas de estados | Em elaboração | — |
| Diagramas de atividades | Em elaboração | — |

### Fase 4 — Planejamento e backlog

| Artefato | Situação | Arquivo |
|---|---|---|
| Histórias de usuário | Em elaboração | — |
| Story Map e definição do MVP | Em elaboração | — |
| Product Backlog | Em elaboração | — |
| Definition of Ready | Em elaboração | — |
| Definition of Done | Em elaboração | — |
| GitHub Projects | Em configuração | — |

> Os itens marcados como “Em elaboração” receberão seus respectivos links conforme forem adicionados ao repositório.

---

## Protótipo

O protótipo do Portal Incluir foi desenvolvido em alta fidelidade no Figma, a partir das entrevistas e reuniões de levantamento realizadas com o cliente.

Foram prototipadas inicialmente as seguintes telas:

- Login;
- Dashboard do coordenador;
- Gerenciar estudantes;
- Perfil do estudante;
- versões responsivas para dispositivos móveis.

**[Acessar protótipo do Portal Incluir no Figma](https://www.figma.com/design/vFjSKJomALDbPFoxTb5RvQ/Prototipa%C3%A7%C3%A3o---Portal-Incluir?m=auto&t=b3gqbFZ6KjZkovY8-6)**

---

## Validação

Os requisitos, casos de uso, escopo e protótipos foram apresentados ao cliente para avaliação.

Após a apresentação, foi disponibilizado um formulário de aceitação. O cliente confirmou que:

- participou das reuniões de levantamento;
- os requisitos representam as necessidades identificadas;
- os casos de uso representam as funcionalidades esperadas;
- os protótipos representam adequadamente a solução;
- o escopo está claro para a próxima etapa;
- os materiais estão adequados para prosseguir.

O processo completo está documentado em [Validação do Protótipo](docs/validacao-prototipo.md).

---

## Estrutura atual do repositório

```text
portal-incluir/
├── README.md
├── docs/
│   ├── visao.md
│   ├── validacao-prototipo.md
│   ├── personas.md
│   ├── entrevistas.md
│   ├── historias.md
│   ├── story-map.md
│   ├── definition-of-ready.md
│   ├── definition-of-done.md
│   ├── backlog-exemplo.md
│   └── adr/
│       ├── 001-escolha-de-stack.md
│       └── 002-banco-de-dados.md
└── src/
    └── README.md
```

> Alguns arquivos ainda contêm conteúdo do projeto-modelo IFgram e serão progressivamente revisados ou substituídos pelos artefatos reais do Portal Incluir.

---

## Organização da documentação

Toda a documentação produzida durante Projeto I será mantida em arquivos Markdown dentro do diretório `docs/`.

Imagens, diagramas e evidências poderão ser armazenados em subpastas de `docs/assets/`, sempre evitando a publicação de dados pessoais ou sensíveis.

O diretório `src/` será utilizado para o código-fonte durante a disciplina de Projeto II.

---

## Tecnologias previstas

| Área | Tecnologia | Finalidade |
|---|---|---|
| Frontend | React | Desenvolvimento da interface web responsiva |
| Backend | Java | Implementação das regras de negócio |
| Framework backend | Spring Boot | Desenvolvimento da API e dos serviços |
| Segurança | Spring Security | Autenticação e controle de acesso por perfil |
| Banco de dados | PostgreSQL | Armazenamento e persistência dos dados |
| Prototipação | Figma | Criação e validação das interfaces |
| Controle de versão | Git e GitHub | Versionamento do código e da documentação |
| Integração institucional | SUAP | Aproveitamento de informações institucionais |
| Acessibilidade | VLibras | Recurso complementar de acessibilidade |
| Notificações | Serviço de e-mail/SMTP | Envio de notificações do sistema |
| Hospedagem | A definir | Implantação da aplicação e do banco de dados |

> As integrações com o SUAP e o VLibras e a plataforma de hospedagem ainda serão avaliadas tecnicamente.

## Segurança, privacidade e acessibilidade

Como o Portal Incluir tratará informações sensíveis, o projeto deverá considerar desde sua especificação:

- proteção de dados pessoais;
- conformidade com a LGPD;
- acesso restrito por perfil;
- rastreabilidade das ações;
- utilização de dados fictícios durante testes e demonstrações;
- navegação por teclado;
- contraste e legibilidade;
- compatibilidade com tecnologias assistivas;
- responsividade para desktop e dispositivos móveis.

---

## Equipe

| Integrante | Responsabilidade |
|---|---|
| Nome do integrante 1 | Pedro Vitor Barbosa Florentino |
| Nome do integrante 2 | Lara Bezerra de Menezes |
| Nome do integrante 3 | Lívia Gonçalves de Freitas |

## Informações acadêmicas

- **Instituição:** Instituto Federal da Paraíba — Campus Monteiro
- **Curso:** Análise e Desenvolvimento de Sistemas
- **Disciplina:** Projeto I
- **Professor:** Marcia Cristina Rafael de Lima
- **Período:** 2026.1
