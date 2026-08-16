# Backlog de Exemplo: GitHub Projects

Este documento mostra como popular o GitHub Projects. Cada seção abaixo representa um Issue no board, com os campos customizados e o checklist de tasks técnicas que ficam dentro do issue.

---

## Visão geral do backlog

| ID | Épico | MoSCoW | Size | Sprint |
|----|-------|--------|------|--------|
| HU001 | Autenticação | Must | G | Sprint 1 |
| HU002 | Autenticação | Must | P | Sprint 1 |
| HU003 | Autenticação | Must | P | Sprint 1 |
| HU004 | Publicações | Must | G | Sprint 1 |
| HU007 | Feed | Must | G | Sprint 2 |
| HU009 | Interações | Must | P | Sprint 2 |
| HU010 | Interações | Must | P | Sprint 2 |
| HU013 | Perfil | Must | M | Sprint 2 |
| HU014 | Perfil | Must | M | Sprint 2 |
| HU005 | Publicações | Should | P | Sprint 3 |
| HU006 | Publicações | Should | P | Sprint 3 |
| HU008 | Feed | Should | M | Sprint 3 |
| HU011 | Interações | Should | M | Sprint 3 |
| HU012 | Interações | Should | P | Sprint 3 |
| HU015 | Perfil | Should | P | Sprint 3 |
| HU016 | Perfil | Should | M | Sprint 4 |
| HU017 | Busca | Should | M | Sprint 4 |
| HU018 | Busca | Could | G | Sprint 4 |

---

## Issues detalhados

---

### HU001: Cadastro com e-mail institucional

**Épico:** Autenticação | **MoSCoW:** Must | **Size:** G | **Sprint:** Sprint 1

> Como estudante ou professor do IFPB, quero criar uma conta usando meu e-mail institucional, para ter acesso ao IFgram com identidade da comunidade.

**Tasks:**
- [ ] Criar migration da tabela `users` (id, username, email, password_hash, bio, avatar_url, created_at, confirmed)
- [ ] Criar endpoint `POST /auth/register` no backend
- [ ] Validar que o e-mail tem domínio `@ifpb.edu.br`
- [ ] Aplicar hash de senha com bcrypt antes de salvar
- [ ] Disparar e-mail de confirmação após o cadastro
- [ ] Criar endpoint `GET /auth/confirm?token=...` para ativar a conta
- [ ] Criar página de cadastro no React com formulário (nome, e-mail, username, senha)
- [ ] Exibir mensagem de sucesso orientando o usuário a confirmar o e-mail
- [ ] Tratar e exibir erros: e-mail já cadastrado, domínio inválido, senha fraca

---

### HU002: Login

**Épico:** Autenticação | **MoSCoW:** Must | **Size:** P | **Sprint:** Sprint 1

> Como usuário cadastrado, quero fazer login com e-mail e senha, para acessar minha conta no IFgram.

**Tasks:**
- [ ] Criar endpoint `POST /auth/login` que valida credenciais e retorna JWT
- [ ] Configurar expiração do token (ex: 7 dias)
- [ ] Criar página de login no React com formulário (e-mail, senha)
- [ ] Armazenar o JWT no localStorage após login bem-sucedido
- [ ] Redirecionar para o feed após login
- [ ] Exibir mensagem de erro genérica em caso de credenciais inválidas (sem dizer qual campo errou)

---

### HU003: Logout

**Épico:** Autenticação | **MoSCoW:** Must | **Size:** P | **Sprint:** Sprint 1

> Como usuário logado, quero fazer logout, para sair da minha conta com segurança.

**Tasks:**
- [ ] Criar botão de logout acessível no menu/header em todas as páginas
- [ ] Remover o JWT do localStorage ao clicar em logout
- [ ] Redirecionar para a página de login após logout
- [ ] Garantir que rotas protegidas redirecionam para login se não houver token válido

---

### HU004: Criar publicação

**Épico:** Publicações | **MoSCoW:** Must | **Size:** G | **Sprint:** Sprint 1

> Como usuário logado, quero publicar uma foto com legenda, para compartilhar um momento ou projeto acadêmico.

**Tasks:**
- [ ] Criar migration da tabela `posts` (id, user_id, image_url, caption, created_at)
- [ ] Configurar upload de imagem no backend (armazenamento local ou S3)
- [ ] Criar endpoint `POST /posts` que recebe imagem e legenda
- [ ] Validar tipo de arquivo (JPG/PNG) e tamanho máximo (5 MB) no backend
- [ ] Criar componente de nova publicação no React (seleção de imagem + campo de legenda)
- [ ] Integrar com o endpoint de upload
- [ ] Exibir preview da imagem antes de publicar
- [ ] Exibir mensagem de sucesso após publicação e redirecionar para o perfil

---

### HU007: Ver feed personalizado

**Épico:** Feed | **MoSCoW:** Must | **Size:** G | **Sprint:** Sprint 2

> Como usuário logado, quero ver as publicações de quem sigo em ordem cronológica reversa, para acompanhar o que está acontecendo na comunidade.

**Tasks:**
- [ ] Criar endpoint `GET /feed?page=0&size=20` que retorna posts de quem o usuário segue
- [ ] Ordenar resultado por `created_at` decrescente
- [ ] Adicionar índice em `follows(follower_id)` e `posts(user_id, created_at)` para performance
- [ ] Criar página de feed no React
- [ ] Criar componente de card de publicação (foto, username, legenda, contador de curtidas)
- [ ] Exibir mensagem de boas-vindas se o usuário não segue ninguém ainda

---

### HU009: Curtir publicação

**Épico:** Interações | **MoSCoW:** Must | **Size:** P | **Sprint:** Sprint 2

> Como usuário logado, quero curtir uma publicação, para demonstrar que apreciei o conteúdo.

**Tasks:**
- [ ] Criar migration da tabela `likes` (user_id, post_id, created_at)
- [ ] Criar endpoint `POST /posts/{id}/likes`
- [ ] Criar componente de ícone de curtida no React
- [ ] Atualizar contador de curtidas na tela sem recarregar a página
- [ ] Destacar o ícone quando o usuário já curtiu

---

### HU010: Descurtir publicação

**Épico:** Interações | **MoSCoW:** Must | **Size:** P | **Sprint:** Sprint 2

> Como usuário logado, quero desfazer uma curtida, para remover minha reação de uma publicação.

**Tasks:**
- [ ] Criar endpoint `DELETE /posts/{id}/likes`
- [ ] Alternar estado do ícone de curtida no clique (curtir/descurtir)
- [ ] Decrementar contador de curtidas na tela sem recarregar

---

### HU013: Visualizar perfil de usuário

**Épico:** Perfil | **MoSCoW:** Must | **Size:** M | **Sprint:** Sprint 2

> Como usuário logado, quero visualizar o perfil de outro usuário, para ver suas publicações e informações.

**Tasks:**
- [ ] Criar endpoint `GET /users/{username}` retornando dados do perfil e posts do usuário
- [ ] Criar página de perfil no React
- [ ] Exibir foto, username, bio, contadores (publicações, seguidores, seguindo)
- [ ] Exibir posts em grade de 3 colunas, do mais recente ao mais antigo
- [ ] Mostrar botão "Seguir" ou "Seguindo" dependendo do estado

---

### HU014: Seguir usuário

**Épico:** Perfil | **MoSCoW:** Must | **Size:** M | **Sprint:** Sprint 2

> Como usuário logado, quero seguir outro usuário, para receber suas publicações no meu feed.

**Tasks:**
- [ ] Criar migration da tabela `follows` (follower_id, following_id)
- [ ] Criar endpoint `POST /users/{username}/follow`
- [ ] Atualizar botão para "Seguindo" após a ação
- [ ] Atualizar contadores de seguidores/seguindo no perfil sem recarregar

---

### HU005: Editar legenda de publicação

**Épico:** Publicações | **MoSCoW:** Should | **Size:** P | **Sprint:** Sprint 3

> Como autor de uma publicação, quero editar a legenda, para corrigir erros ou complementar o texto.

**Tasks:**
- [ ] Criar endpoint `PUT /posts/{id}` que aceita nova legenda
- [ ] Validar que apenas o autor pode editar (comparar user_id do token com o do post)
- [ ] Exibir opção de editar apenas nos próprios posts
- [ ] Criar campo de edição inline na legenda
- [ ] Salvar e exibir a legenda atualizada sem recarregar a página

---

### HU006: Excluir publicação

**Épico:** Publicações | **MoSCoW:** Should | **Size:** P | **Sprint:** Sprint 3

> Como autor de uma publicação, quero excluir uma publicação, para removê-la do meu perfil e do feed dos meus seguidores.

**Tasks:**
- [ ] Criar endpoint `DELETE /posts/{id}`
- [ ] Validar que apenas o autor pode excluir
- [ ] Remover o arquivo de imagem do storage ao excluir o post
- [ ] Exibir opção de excluir apenas nos próprios posts
- [ ] Exibir modal de confirmação antes de excluir
- [ ] Remover o card da tela após exclusão confirmada

---

### HU008: Scroll infinito no feed

**Épico:** Feed | **MoSCoW:** Should | **Size:** M | **Sprint:** Sprint 3

> Como usuário logado, quero carregar mais publicações conforme rolo o feed, para não precisar esperar carregar tudo de uma vez.

**Tasks:**
- [ ] Garantir que o endpoint `GET /feed` suporta paginação por offset/cursor
- [ ] Implementar detecção de fim de página com Intersection Observer no React
- [ ] Carregar próximo bloco de 20 posts ao chegar no fim
- [ ] Exibir spinner de carregamento durante a busca
- [ ] Exibir mensagem "Você chegou ao fim do feed" quando não houver mais posts

---

### HU011: Comentar em publicação

**Épico:** Interações | **MoSCoW:** Should | **Size:** M | **Sprint:** Sprint 3

> Como usuário logado, quero comentar em uma publicação, para interagir com o autor e outros usuários.

**Tasks:**
- [ ] Criar migration da tabela `comments` (id, user_id, post_id, content, created_at)
- [ ] Criar endpoint `POST /posts/{id}/comments`
- [ ] Criar endpoint `GET /posts/{id}/comments` para listar comentários
- [ ] Criar seção de comentários na página da publicação
- [ ] Criar campo de texto para novo comentário com botão de envio
- [ ] Exibir comentário na lista imediatamente após o envio
- [ ] Bloquear envio de comentário vazio

---

### HU012: Excluir comentário

**Épico:** Interações | **MoSCoW:** Should | **Size:** P | **Sprint:** Sprint 3

> Como autor de um comentário, quero excluir meu comentário, para removê-lo de uma publicação.

**Tasks:**
- [ ] Criar endpoint `DELETE /comments/{id}`
- [ ] Validar que apenas o autor do comentário pode excluí-lo
- [ ] Exibir ícone de excluir apenas nos próprios comentários
- [ ] Remover o comentário da lista imediatamente após exclusão

---

### HU015: Deixar de seguir usuário

**Épico:** Perfil | **MoSCoW:** Should | **Size:** P | **Sprint:** Sprint 3

> Como usuário logado, quero deixar de seguir um usuário, para que suas publicações não apareçam mais no meu feed.

**Tasks:**
- [ ] Criar endpoint `DELETE /users/{username}/follow`
- [ ] Exibir modal de confirmação ao clicar em "Seguindo"
- [ ] Atualizar botão para "Seguir" após confirmar
- [ ] Atualizar contadores de seguidores/seguindo no perfil

---

### HU016: Editar perfil

**Épico:** Perfil | **MoSCoW:** Should | **Size:** M | **Sprint:** Sprint 4

> Como usuário logado, quero editar minha foto de perfil e bio, para personalizar como apareço para outros usuários.

**Tasks:**
- [ ] Criar endpoint `PUT /users/me` que aceita nova foto e bio
- [ ] Validar tamanho máximo da foto (2 MB) e tipo de arquivo no backend
- [ ] Criar página de edição de perfil no React
- [ ] Exibir preview da nova foto antes de salvar
- [ ] Limitar campo de bio a 150 caracteres com contador visível
- [ ] Exibir perfil atualizado após salvar

---

### HU017: Buscar usuário por nome

**Épico:** Busca | **MoSCoW:** Should | **Size:** M | **Sprint:** Sprint 4

> Como usuário logado, quero buscar por nome de usuário, para encontrar pessoas da comunidade do IFPB.

**Tasks:**
- [ ] Criar endpoint `GET /search/users?q=...` com busca parcial por username
- [ ] Criar barra de busca acessível no header em todas as páginas
- [ ] Exibir resultados em tempo real conforme o usuário digita (debounce de 300ms)
- [ ] Cada resultado exibe foto e username clicável
- [ ] Clicar no resultado navega para o perfil do usuário

---

### HU018: Buscar por hashtag

**Épico:** Busca | **MoSCoW:** Could | **Size:** G | **Sprint:** Sprint 4

> Como usuário logado, quero buscar por hashtag, para encontrar publicações sobre um tema específico.

**Tasks:**
- [ ] Implementar parser de hashtags na legenda ao criar/editar post (extrair e salvar tags)
- [ ] Criar tabela de relacionamento `post_hashtags` ou extrair hashtags via query
- [ ] Criar endpoint `GET /search/hashtags?tag=...` retornando posts com a hashtag
- [ ] Tornar hashtags clicáveis na legenda e nos comentários
- [ ] Criar página de resultados de busca por hashtag
- [ ] Exibir mensagem quando nenhuma publicação for encontrada para a hashtag
