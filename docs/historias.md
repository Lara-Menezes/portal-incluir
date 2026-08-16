# Histórias de Usuário: IFgram

As histórias estão organizadas por épico. Cada história tem critérios de aceitação que definem quando ela pode ser considerada concluída.

---

## Autenticação

### HU001: Cadastro com e-mail institucional

**Como** estudante ou professor do IFPB,  
**quero** criar uma conta usando meu e-mail institucional,  
**para** ter acesso ao IFgram com identidade da comunidade.

**Critérios de aceitação:**
- O sistema aceita apenas e-mails com domínio `@ifpb.edu.br`
- O usuário informa nome, e-mail, nome de usuário e senha
- A senha deve ter no mínimo 8 caracteres
- O sistema envia um e-mail de confirmação antes de ativar a conta
- Nome de usuário deve ser único no sistema

---

### HU002: Login

**Como** usuário cadastrado,  
**quero** fazer login com e-mail e senha,  
**para** acessar minha conta no IFgram.

**Critérios de aceitação:**
- O sistema valida e-mail e senha
- Em caso de erro, exibe mensagem genérica (sem informar qual campo está errado)
- Após login bem-sucedido, o usuário é redirecionado para o feed
- A sessão é mantida até o logout explícito

---

### HU003: Logout

**Como** usuário logado,  
**quero** fazer logout,  
**para** sair da minha conta com segurança.

**Critérios de aceitação:**
- O botão de logout está acessível em qualquer página após login
- Após logout, o token JWT é invalidado
- O usuário é redirecionado para a tela de login

---

## Publicações

### HU004: Criar publicação

**Como** usuário logado,  
**quero** publicar uma foto com legenda,  
**para** compartilhar um momento ou projeto acadêmico.

**Critérios de aceitação:**
- O usuário seleciona uma imagem do dispositivo (JPG ou PNG, máx. 5 MB)
- A legenda é opcional, mas limitada a 2.200 caracteres
- A publicação é exibida imediatamente no perfil do usuário após o envio
- O sistema exibe uma mensagem de sucesso após a publicação

---

### HU005: Editar legenda de publicação

**Como** autor de uma publicação,  
**quero** editar a legenda,  
**para** corrigir erros ou complementar o texto.

**Critérios de aceitação:**
- Apenas o autor pode editar a própria publicação
- A foto não pode ser alterada, apenas a legenda
- O sistema salva a alteração e exibe a legenda atualizada

---

### HU006: Excluir publicação

**Como** autor de uma publicação,  
**quero** excluir uma publicação,  
**para** removê-la do meu perfil e do feed dos meus seguidores.

**Critérios de aceitação:**
- Apenas o autor pode excluir a própria publicação
- O sistema pede confirmação antes de excluir
- Após exclusão, a publicação desaparece do perfil e do feed

---

## Feed

### HU007: Ver feed personalizado

**Como** usuário logado,  
**quero** ver as publicações de quem sigo em ordem cronológica reversa,  
**para** acompanhar o que está acontecendo na comunidade.

**Critérios de aceitação:**
- O feed exibe apenas publicações de usuários que o usuário segue
- As publicações aparecem da mais recente para a mais antiga
- O feed é paginado (20 publicações por página)
- Se o usuário não segue ninguém, exibe uma mensagem de boas-vindas com sugestão de buscar usuários

---

### HU008: Carregar mais publicações no feed

**Como** usuário logado,  
**quero** carregar mais publicações conforme rolo o feed,  
**para** não precisar esperar carregar tudo de uma vez.

**Critérios de aceitação:**
- As publicações são carregadas em blocos de 20
- O carregamento é acionado automaticamente ao chegar no fim da página (scroll infinito)
- Exibe indicador de carregamento enquanto busca novos itens
- Quando não há mais publicações, exibe mensagem "Você chegou ao fim do feed"

---

## Interações

### HU009: Curtir publicação

**Como** usuário logado,  
**quero** curtir uma publicação,  
**para** demonstrar que apreciei o conteúdo.

**Critérios de aceitação:**
- O usuário pode curtir qualquer publicação, incluindo as próprias
- O contador de curtidas é atualizado imediatamente
- O ícone de curtida fica destacado para indicar que o usuário curtiu

---

### HU010: Descurtir publicação

**Como** usuário logado,  
**quero** desfazer uma curtida,  
**para** remover minha reação de uma publicação.

**Critérios de aceitação:**
- Clicar novamente no ícone de curtida remove a curtida
- O contador é decrementado imediatamente
- O ícone volta ao estado padrão

---

### HU011: Comentar em publicação

**Como** usuário logado,  
**quero** comentar em uma publicação,  
**para** interagir com o autor e outros usuários.

**Critérios de aceitação:**
- O campo de comentário está disponível na página da publicação
- Comentários vazios não são aceitos
- Após envio, o comentário aparece imediatamente na lista
- O comentário exibe o nome de usuário e o texto

---

### HU012: Excluir comentário

**Como** autor de um comentário,  
**quero** excluir meu comentário,  
**para** removê-lo de uma publicação.

**Critérios de aceitação:**
- Apenas o autor do comentário pode excluí-lo
- O sistema remove o comentário sem pedir confirmação
- A lista de comentários é atualizada imediatamente

---

## Perfil

### HU013: Visualizar perfil de usuário

**Como** usuário logado,  
**quero** visualizar o perfil de outro usuário,  
**para** ver suas publicações e informações.

**Critérios de aceitação:**
- O perfil exibe: foto, nome de usuário, bio, quantidade de publicações, seguidores e seguindo
- As publicações aparecem em grade (3 colunas), da mais recente para a mais antiga
- O botão de seguir/deixar de seguir aparece no perfil de outros usuários

---

### HU014: Seguir usuário

**Como** usuário logado,  
**quero** seguir outro usuário,  
**para** receber suas publicações no meu feed.

**Critérios de aceitação:**
- O botão "Seguir" está disponível em perfis que o usuário ainda não segue
- Após seguir, o botão muda para "Seguindo"
- As publicações do seguido aparecem no feed do usuário

---

### HU015: Deixar de seguir usuário

**Como** usuário logado,  
**quero** deixar de seguir um usuário,  
**para** que suas publicações não apareçam mais no meu feed.

**Critérios de aceitação:**
- O botão "Seguindo" está disponível em perfis que o usuário já segue
- Ao clicar, o sistema pede confirmação
- Após confirmar, o botão volta para "Seguir" e as publicações somem do feed

---

### HU016: Editar perfil

**Como** usuário logado,  
**quero** editar minha foto de perfil e bio,  
**para** personalizar como apareço para outros usuários.

**Critérios de aceitação:**
- O usuário pode alterar: foto de perfil (JPG/PNG, máx. 2 MB) e bio (máx. 150 caracteres)
- O nome de usuário não pode ser alterado após o cadastro
- As alterações são salvas e exibidas imediatamente no perfil

---

## Busca

### HU017: Buscar usuário por nome

**Como** usuário logado,  
**quero** buscar por nome de usuário,  
**para** encontrar pessoas da comunidade do IFPB.

**Critérios de aceitação:**
- A barra de busca está acessível em qualquer página
- A busca retorna resultados parciais (ex: "luc" retorna "lucas_mendes")
- Os resultados exibem foto de perfil e nome de usuário
- Clicar num resultado leva ao perfil do usuário

---

### HU018: Buscar por hashtag

**Como** usuário logado,  
**quero** buscar por hashtag,  
**para** encontrar publicações sobre um tema específico.

**Critérios de aceitação:**
- Hashtags na legenda são clicáveis e redirecionam para a busca
- A busca retorna todas as publicações que contêm a hashtag
- Os resultados são ordenados do mais recente para o mais antigo
- Se não houver resultados, exibe mensagem "Nenhuma publicação encontrada para #[hashtag]"
