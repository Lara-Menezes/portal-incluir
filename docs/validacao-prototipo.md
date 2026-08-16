# Validação do Protótipo: IFgram

## Informações gerais

- **Ferramenta:** Figma (protótipo navegável)
- **Acesso ao protótipo:** [Ver no Figma →](https://figma.com/proto/ifgram-prototipo)
  > Link fictício: substitua pelo link real.
- **Sessões realizadas:** 2 sessões, com 3 participantes cada
- **Perfil dos participantes:** estudantes do IFPB Campus Monteiro (ADS e Técnico em Informática)
- **Metodologia:** teste de usabilidade com tarefas definidas + entrevista pós-teste

---

## Tarefas testadas

1. Criar uma conta no IFgram
2. Fazer login
3. Publicar uma foto com legenda
4. Encontrar e seguir um usuário
5. Curtir uma publicação no feed

---

## Resultados por tarefa

### Tarefa 1: Criar conta

| Participante | Concluiu? | Tempo | Dificuldades |
|---|---|---|---|
| P1 | Sim | 1min 20s | Não entendeu que o e-mail precisava ser @ifpb.edu.br |
| P2 | Sim | 58s | Nenhuma |
| P3 | Sim | 1min 45s | Ficou em dúvida sobre o campo "nome de usuário" |
| P4 | Sim | 1min 05s | Nenhuma |
| P5 | Não | - | Não encontrou o botão de confirmação após o cadastro |
| P6 | Sim | 2min 10s | Leu o e-mail de confirmação mas não entendeu que precisava clicar no link |

**Conclusão:** 5 de 6 concluíram. Principal problema: o fluxo de confirmação de e-mail não estava claro no protótipo.

---

### Tarefa 2: Login

Todos os 6 participantes concluíram sem dificuldades. Tempo médio: 25 segundos.

---

### Tarefa 3: Publicar foto

| Participante | Concluiu? | Dificuldades |
|---|---|---|
| P1 | Sim | Não encontrou o botão de nova publicação de imediato |
| P2 | Sim | Nenhuma |
| P3 | Sim | Nenhuma |
| P4 | Sim | Tentou arrastar a foto direto para a tela |
| P5 | Sim | Nenhuma |
| P6 | Não | Não entendeu que precisava selecionar uma imagem antes de publicar |

**Conclusão:** Botão de nova publicação pouco visível. O ícone (+) na barra inferior não foi associado à ação de publicar por 2 participantes.

---

### Tarefa 4: Seguir usuário

Todos concluíram. 2 participantes foram pela busca; 4 foram pelo perfil sugerido na tela inicial. O botão "Seguir" foi reconhecido por todos imediatamente.

---

### Tarefa 5: Curtir publicação

Todos concluíram sem dificuldades. O ícone de coração foi reconhecido por todos.

---

## Problemas identificados e ações tomadas

| # | Problema | Severidade | Ação tomada |
|---|----------|-----------|-------------|
| 1 | Fluxo de confirmação de e-mail confuso | Alta | Adicionado texto explicativo após o cadastro: "Acesse seu e-mail e clique no link de confirmação antes de entrar." |
| 2 | Botão de nova publicação (ícone +) pouco visível | Média | Substituído por botão com rótulo "Publicar" na barra inferior |
| 3 | Campo "nome de usuário" sem explicação | Baixa | Adicionado placeholder: "Ex: lucas.mendes (será seu @)" |
| 4 | Usuário tentou arrastar foto diretamente | Baixa | Mantido comportamento atual; drag-and-drop pode ser avaliado no futuro |

---

## Conclusão geral

O protótipo validou os fluxos principais com taxa de sucesso acima de 80% em todas as tarefas críticas. As correções de maior impacto (confirmação de e-mail e botão de publicação) foram aplicadas antes da entrega da Fase 2.

O fluxo de busca e perfil não foi testado por limitação de tempo: fica como pendência para a validação da Fase 4.
