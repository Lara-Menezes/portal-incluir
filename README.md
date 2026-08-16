# IFgram: Repositório de Referência · Projeto I · ADS · IFPB Campus Monteiro

Este repositório é um **exemplo de referência** para a disciplina Projeto I do curso de Análise e Desenvolvimento de Sistemas. Ele contém todos os artefatos esperados nas 4 entregas do semestre, usando o projeto fictício **IFgram** como caso de uso.

> **Para os alunos:** use este repositório para entender o nível de qualidade e organização esperado. Não copie: crie os seus próprios artefatos a partir do problema que o seu grupo escolheu.

---

## Sobre o IFgram

O **IFgram** é uma rede social de fotos para a comunidade do IFPB Campus Monteiro. O problema, o público-alvo e os épicos do projeto estão descritos no [Documento de Visão](docs/visao.md).

---

## Índice de artefatos

### Fase 1: Descoberta

| Artefato | Arquivo |
|----------|---------|
| Documento de Visão | [docs/visao.md](docs/visao.md) |
| Personas | [docs/personas.md](docs/personas.md) |
| Entrevistas | [docs/entrevistas.md](docs/entrevistas.md) |

### Fase 2: Definição

| Artefato | Arquivo |
|----------|---------|
| Histórias de Usuário | [docs/historias.md](docs/historias.md) |
| Story Map | [docs/story-map.md](docs/story-map.md) |
| Validação do Protótipo | [docs/validacao-prototipo.md](docs/validacao-prototipo.md) |

### Fase 3: Decisão Técnica

| Artefato | Arquivo |
|----------|---------|
| ADR 001: Escolha de Stack | [docs/adr/001-escolha-de-stack.md](docs/adr/001-escolha-de-stack.md) |
| ADR 002: Banco de Dados | [docs/adr/002-banco-de-dados.md](docs/adr/002-banco-de-dados.md) |

### Fase 4: Backlog Final

| Artefato | Arquivo / Link |
|----------|---------|
| Definition of Ready | [docs/definition-of-ready.md](docs/definition-of-ready.md) |
| Definition of Done | [docs/definition-of-done.md](docs/definition-of-done.md) |
| Backlog de exemplo (campos do board) | [docs/backlog-exemplo.md](docs/backlog-exemplo.md) |
| GitHub Projects (board) | [Acessar board](https://github.com/users/ppereiradev/projects/4) |

---

## Estrutura do repositório

```
docs/
  ├── visao.md                     # Documento de Visão do produto
  ├── personas.md                  # Personas do projeto
  ├── entrevistas.md               # Entrevistas com usuários (simuladas)
  ├── historias.md                 # Histórias de usuário por épico
  ├── story-map.md                 # Link para o Story Map no Miro + definição do MVP
  ├── validacao-prototipo.md       # Resultado da validação do protótipo
  ├── definition-of-ready.md       # Critérios de Definition of Ready
  ├── definition-of-done.md        # Critérios de Definition of Done
  ├── backlog-exemplo.md           # Histórias com campos do GitHub Projects preenchidos
  └── adr/
       ├── 001-escolha-de-stack.md # ADR sobre a stack tecnológica
       └── 002-banco-de-dados.md   # ADR sobre o banco de dados
README.md                          # Este arquivo
```

---

## GitHub Projects

O backlog do projeto está no GitHub Projects:  
**[Acessar board](https://github.com/users/ppereiradev/projects/4)**

O board usa os seguintes campos customizados:

| Campo | Tipo | Valores |
|-------|------|---------|
| Épico | Single select | Autenticação, Publicações, Feed, Interações, Perfil, Busca |
| MoSCoW | Single select | Must, Should, Could, Won't |
| Size | Single select | P, M, G, GG |
| Sprint | Iteration | Sprint 1, Sprint 2, … |
