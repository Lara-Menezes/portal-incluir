# Conteúdo acadêmico adaptado — contrato da API

Contrato utilizado pela biblioteca do professor e pela consulta somente leitura da coordenação.

## Autorização

Todas as operações exigem sessão HttpSession ativa e a permissão existente
`academico.gerar`. O perfil PROFESSOR consulta e gerencia somente seus próprios
materiais; consultar ou alterar material de outro professor retorna 404, inclusive
no download. Nenhum identificador de autor enviado pelo cliente autoriza acesso.

O perfil COORDENADOR pode utilizar somente os GET existentes para listar, consultar
detalhes e baixar anexos. A listagem inclui materiais de professores com estudante
no escopo autorizado e materiais reutilizáveis sem estudante. A retenção continua
sendo aplicada à consulta e ao download; favoritos pessoais são retornados como
false nessa visão. Criar, editar, excluir, alterar anexos ou favoritar continuam
exclusivos do professor autor. ADMINISTRADOR, equipe e outros professores não
ganham acesso adicional. Nenhuma permissão administrativa de usuários é exigida.

O estudante é opcional: sem ele, o material integra a biblioteca reutilizável do
professor. Quando informado, exige vínculo docente ativo para cadastrar e gerenciar.
O encerramento do vínculo preserva a consulta do próprio material, respeitando a
retenção do estudante. Histórico concluído é somente leitura. O estudante original
e a autoria não podem ser transferidos na edição. Atribuição de etapa ou solicitação
de PEA não concede autorização para associar materiais.

## Endpoints

Base: `/api/conteudos-academicos`.

| Método e caminho | Operação | Resposta |
| --- | --- | --- |
| GET `/` | Listar materiais próprios (professor) ou autorizados (coordenação) | 200, lista |
| GET `/{id}` | Consultar um material | 200 |
| PUT `/{id}/favorito?versao={versao}&favorito={true ou false}` | Definir favorito do próprio material | 200 |
| POST `/` com JSON | Cadastrar material textual ou com link | 201 + Location |
| POST `/` multipart | Cadastrar material e anexo atomicamente | 201 + Location |
| PUT `/{id}` com JSON | Editar metadados, incluindo a versão consultada | 200 |
| DELETE `/{id}?versao={versao}` | Excluir material e anexo | 204 |
| PUT `/{id}/arquivo?versao={versao}` multipart | Adicionar ou substituir anexo | 200 |
| GET `/{id}/arquivo` | Download privado do anexo | 200, binário |
| DELETE `/{id}/arquivo?versao={versao}` | Remover somente o anexo | 200 |

POST e PUT de metadados usam:

```json
{
  "titulo": "Atividade de leitura adaptada",
  "tipo": "ATIVIDADE",
  "componenteCurricular": "Língua portuguesa",
  "descricao": "Atividade com instruções em etapas.",
  "orientacoesAdaptacao": "Leitura orientada e fonte ampliada.",
  "estudanteId": null,
  "link": null,
  "versao": 0
}
```

`versao` é obrigatória para atualizar; é ignorada no cadastro. Sempre utilizar a
versão da resposta mais recente, inclusive depois de enviar ou remover arquivo.
Atualizações concorrentes são protegidas por bloqueio transacional e `@Version`;
uma versão antiga retorna 409. Não repetir automaticamente uma alteração após 409.

No POST multipart, a parte `dados` contém esse JSON com Content-Type
`application/json`; a parte opcional `arquivo` contém o binário. Erro em qualquer
parte desfaz o cadastro inteiro. Também é possível criar com JSON e enviar arquivo
separadamente; nesse caso são duas transações e uma falha no upload não desfaz o
cadastro anterior.

## Campos e validações

`favorito` é retornado como booleano, inicialmente false. A preferência fica salva
na conta do autor e é auditada; não altera conteúdo ou data de atualização. Exige a
mesma permissão e autoria, mas não exige vínculo atual para organizar um material
histórico ainda disponível. Não libera edição do documento. A versão consultada é
obrigatória e a resposta deve substituir a versão local. A coluna é criada com
default false para preservar materiais já existentes.

- Obrigatórios: título (150 caracteres), tipo, componente curricular (150) e
  descrição (5.000). Textos são aparados com `trim()`.
- Tipos: `PROVA`, `ATIVIDADE`, `MATERIAL_APOIO`, `OUTRO`.
- Orientações opcionais: 5.000 caracteres. Link opcional: 2.000 caracteres,
  somente HTTP/HTTPS com host e sem credenciais. O servidor não acessa esse link.
- Anexo opcional: até 10 MiB, PDF, DOCX, TXT UTF-8, PNG ou JPEG. A extensão e a
  estrutura/assinatura inicial são verificadas; o MIME informado pelo cliente não
  define o tipo. DOCX possui limite de entradas e de descompressão e rejeita macros.
- Um material pode conter texto sem anexo/link. Link e anexo não coexistem:
  remova explicitamente a origem anterior antes de adicionar a outra.

A resposta inclui `id`, `versao`, `professorId`, `professorNome`, `estudanteId`,
`estudanteNome`, `estudanteMatricula`, os campos de conteúdo e `arquivoNome`,
`arquivoTipo`, `arquivoTamanho`, `dataCriacao` e `dataAtualizacao`. Campos opcionais
ausentes retornam null. O binário nunca é incluído na listagem ou consulta JSON.

Erros possuem `message`: 400 para dados inválidos, 401 sem sessão, 403 sem perfil,
permissão ou vínculo, 404 para material ausente/alheio ou anexo inexistente,
409 para versão antiga, transferência de estudante ou histórico concluído,
410 para retenção vencida, 413 para tamanho excedido. O servidor HTTP também pode
rejeitar pedidos maiores que os limites multipart já configurados no projeto.

## Persistência, auditoria e implantação

Novas tabelas: `conteudos_academicos` e `arquivos_conteudo_academico`, com FK
obrigatória para professor, FK opcional para estudante e anexo único por material.
O binário é armazenado em tabela separada; Hibernate mapeia byte[] para bytea no
PostgreSQL, sem usar Large Object/OID. A consulta de metadados não busca o binário.

As duas entidades seguem a auditoria Envers obrigatória do projeto, com revisões
em `auditoria`. O binário não é duplicado nas revisões; substituição ou exclusão de
anexo não permite recuperar versões anteriores do arquivo. Metadados e alterações
permanecem auditáveis até o descarte por retenção. As FKs com ON DELETE CASCADE
removem material e anexo quando o estudante é descartado; o serviço de retenção
também remove as novas revisões, inclusive de materiais anteriormente excluídos.

Não há Flyway/Liquibase neste repositório. Os mapeamentos seguem a configuração
existente `ddl-auto=update`: na próxima inicialização autorizada, Hibernate criará
as tabelas necessárias. Esta implementação e seus testes não iniciam nem modificam
o PostgreSQL operacional. A implantação e a verificação em PostgreSQL permanecem
pendentes. A validação de formato não substitui análise antivírus de anexos.

## Verificação

`mvn -Dtest=ConteudoAcademicoTest test` usa H2 em memória com propriedades isoladas
no teste, sem executar os scripts PostgreSQL e sem acessar o banco operacional.
A suíte cobre sessão/perfil/permissão, autoria, vínculo, retenção, validações,
upload/download, substituição e exclusão, rollback do cadastro multipart,
versão antiga e duas edições simultâneas.
