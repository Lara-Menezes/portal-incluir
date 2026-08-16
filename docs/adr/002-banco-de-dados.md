# ADR 002: Escolha do Banco de Dados

- **Status:** Aceito
- **Data:** Semana 10 do semestre

---

## Contexto

Com a stack backend definida (Spring Boot + Java (ver [ADR 001](001-escolha-de-stack.md))), precisávamos escolher o banco de dados. O IFgram é uma aplicação com:

- Dados estruturados com relacionamentos claros (usuários, publicações, seguidores, curtidas, comentários)
- Volume inicial pequeno (comunidade de um campus)
- Sem necessidade de escala horizontal no curto prazo

---

## Decisão

Usaremos **PostgreSQL 15+** como banco de dados relacional principal.

O acesso ao banco será feito via **Spring Data JPA + Hibernate**, com migrações gerenciadas pelo **Flyway**.

---

## Alternativas consideradas

### PostgreSQL vs. MySQL

- **PostgreSQL** foi escolhido por ter suporte nativo melhor no Railway (banco provisionado automaticamente no mesmo projeto), melhor conformidade com o padrão SQL e suporte a tipos avançados que podem ser úteis no futuro (ex: JSON, array).
- **MySQL** foi descartado por não ter vantagem clara sobre o PostgreSQL no contexto deste projeto e por o grupo já ter mais familiaridade com PostgreSQL nas aulas.

### PostgreSQL vs. MongoDB

- **MongoDB** foi considerado por ser mais simples para armazenar dados de feed (sem joins).
- Descartado porque os relacionamentos do IFgram (seguidores, curtidas, comentários) são naturalmente relacionais: modelá-los em um banco não-relacional aumentaria a complexidade da lógica de negócio.
- Além disso, o grupo não tem experiência com MongoDB e o prazo não permite aprender uma nova tecnologia do zero.

---

## Modelo de dados resumido

```
users
  id, username, email, password_hash, bio, avatar_url, created_at

posts
  id, user_id (FK), image_url, caption, created_at

follows
  follower_id (FK), following_id (FK)

likes
  user_id (FK), post_id (FK), created_at

comments
  id, user_id (FK), post_id (FK), content, created_at
```

---

## Consequências

**Positivas:**
- Modelo relacional cobre bem todos os casos de uso do IFgram sem gambiarras
- PostgreSQL é suportado diretamente pelo Railway sem configuração extra
- JPA + Hibernate reduz o código boilerplate de acesso a dados

**Negativas / Riscos:**
- Queries de feed (publicações de quem o usuário segue, ordenadas por data) podem ficar lentas sem índices adequados
- Mitigação: adicionar índice em `follows(follower_id)` e `posts(user_id, created_at)` desde o início

---

## Revisão

Revisitar se o volume de dados crescer significativamente além do esperado para um campus, ou se surgir necessidade de busca full-text avançada (nesse caso, avaliar Elasticsearch como complemento).
