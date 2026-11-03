# Auditoria no pgAdmin

Ao iniciar a aplicacao, os cadastros continuam no schema `public`. O historico fica em
`auditoria`: `estudantes_aud`, `planos_acao_aud`, `adaptacoes_pedagogicas_aud`,
`professores_aud`, `coordenadores_aud`, `membros_equipe_multidisciplinar_aud` e `revinfo`.
No pgAdmin, atualize **Schemas** e expanda **auditoria > Tables**.

## Instalacao e migracao

A inicializacao SQL cria o schema e move as tabelas de auditoria existentes de `public`
antes da atualizacao do Hibernate. A movimentacao preserva registros, chaves e indices;
nao exige apagar dados nem rodar o Postman novamente. Se houver uma tabela com o mesmo
nome nos dois schemas, a inicializacao para, em vez de escolher um historico e ignorar o outro.
O usuario do banco precisa poder criar o schema e alterar as tabelas existentes.

Depois da criacao/atualizacao das tabelas pelo Hibernate, a configuracao instala as
funcoes e os triggers PostgreSQL que preenchem `detalhes`, uma coluna `jsonb` em cada `_aud`.
Revisoes antigas com `detalhes` vazio sao preenchidas a partir dos snapshots existentes.
Os scripts podem rodar novamente sem duplicar revisoes. Sao especificos de PostgreSQL.
A aplicacao falha na inicializacao se a instalacao dos detalhes falhar.

Arquivos: `src/main/resources/db/auditoria-schema.sql` (antes do Hibernate) e
`src/main/resources/db/auditoria-detalhes.sql` (depois do Hibernate). `^^^` e o separador
usado pelo Spring; para executar esses arquivos manualmente no pgAdmin, substitua cada
linha `^^^` por `;` e respeite a ordem acima.

## Como ler detalhes

`rev` identifica a revisao global, e `revtype` preserva o padrao do Envers:
`0 = ADD`, `1 = MOD`, `2 = DEL`. A coluna `detalhes` explica essa operacao:

- `operacao`: `ADD`, `MOD` ou `DEL`.
- `antes`: snapshot anterior completo, ou `null` em uma criacao.
- `depois`: snapshot resultante completo, ou `null` em uma exclusao.
- `camposAlterados`: em `MOD`, somente os campos diferentes, com seus valores antes/depois.
- `anteriorDisponivel`: indica se o estado anterior e conhecido. Em `ADD`, vale `true`
  porque sabemos que nao havia registro. Em `MOD` sem revisao anterior, vale `false`.

Exemplo do trecho que explica a troca de nome:

```json
{
  "operacao": "MOD",
  "anteriorDisponivel": true,
  "camposAlterados": {
    "nome": {
      "antes": "Estudante Teste Postman",
      "depois": "Estudante Nome Atualizado"
    }
  }
}
```

O JSON real tambem inclui `antes` e `depois` com todos os campos auditados da versao.
Usa nomes de colunas do banco (`nome`, `data_conclusao`, etc.). Inclui IDs dos vinculos,
sem copiar o cadastro inteiro dos responsaveis. Exclui `rev`, `revtype` e `detalhes` dos
snapshots para nao misturar metadados ou gerar recursao. Campos nulos sao preservados.

Para um cadastro preexistente sem historico anterior, a primeira alteracao tem
`antes: null`, `anteriorDisponivel: false` e `camposAlterados: {}`. Isso significa
**estado anterior desconhecido**, nao que nada mudou. O Envers nao recupera alteracoes
anteriores a sua ativacao. `DEL` usa o snapshot excluido preservado por
`store_data_at_delete=true`.

## Consultas

```sql
SELECT a.id, a.rev, a.detalhes->>'operacao' AS operacao,
       to_timestamp(r.revtstmp / 1000.0)
           AT TIME ZONE 'America/Sao_Paulo' AS data_hora,
       a.detalhes->'camposAlterados' AS campos_alterados,
       a.detalhes->'antes' AS antes,
       a.detalhes->'depois' AS depois
FROM auditoria.estudantes_aud a
JOIN auditoria.revinfo r ON r.rev = a.rev
WHERE a.id = 1 -- substitua pelo ID do estudante
ORDER BY a.rev;
```

Para ver o JSON formatado:

```sql
SELECT id, rev, jsonb_pretty(detalhes) AS detalhes
FROM auditoria.estudantes_aud
ORDER BY rev, id;
```

Na API, cadastro gera `ADD`, atualizacao gera `MOD` e conclusao gera outro `MOD`.
Conclusao nao e exclusao. Tentativas recusadas e rollback nao geram revisoes.
A API de historico continua retornando `revisao`, `data`, `tipo` e `dados`;
a coluna `detalhes` e consultada no banco.

## Novas entidades

Marque cada nova entidade de negocio com `@Audited`:

```java
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.envers.Audited;

@Entity
@Audited
public class NovaEntidade {
    @Id
    @GeneratedValue
    private Long id;
}
```

Com `ddl-auto=update`, ao iniciar o projeto o Envers cria a tabela `_aud` no schema
`auditoria`. A instalacao dos detalhes descobre automaticamente essas tabelas pelo
catalogo do PostgreSQL, acrescenta `detalhes` e instala o trigger. Nao ha lista fixa
de entidades ou tabelas para atualizar. A identidade e obtida da chave primaria;
IDs com outro nome, UUID e chaves compostas tambem sao suportados.

A migracao de tabelas antigas tambem descobre automaticamente as tabelas `_aud` em
`public` que possuem `rev` e `revtype`. Mantenha essas convencoes de nome e schema
para novas entidades e seus mapeamentos de auditoria.

A inicializacao verifica todas as entidades JPA de negocio: se alguma nao estiver
auditada pelo Envers, a aplicacao para com uma mensagem indicando a classe e pedindo
`@Audited`. A entidade tecnica de revisao e excluida dessa verificacao. Essa verificacao
nao acrescenta anotacoes nem cria historico retroativo. Em producao, se `ddl-auto`
for substituido por migracoes versionadas, as novas tabelas precisam constar nas migracoes.

## Limites e retencao

Envers registra escritas feitas pelo Hibernate. SQL manual, `TRUNCATE`, exclusoes em
lote e o descarte de retencao nao geram automaticamente revisoes. Os triggers apenas
enriquecem as linhas que o Envers insere nas tabelas `_aud`.
A remocao por retencao usa os nomes qualificados de `auditoria` e remove o JSON junto
com cada revisao do estudante e dos filhos. Auditorias dos profissionais tem ciclo
de vida separado e nao sao apagadas junto com um estudante.

As entidades adicionais passam a ser auditadas a partir desta versao; seus dados antigos
nao geram retrospectivamente revisoes de criacao. Isso tambem nao adiciona identificacao
do autor: `revinfo` continua armazenando somente numero e instante.

Referencias: [criacao automatica das tabelas pelo Envers](https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/chapters/envers/Envers.html)
e [triggers PostgreSQL](https://www.postgresql.org/docs/current/sql-createtrigger.html).
