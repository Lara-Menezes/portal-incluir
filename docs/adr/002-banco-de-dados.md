# ADR 002 — Escolha do Banco de Dados

- **Status:** Aceito
- **Data:** 18/08/2026
- **Projeto:** Portal Incluir
- **Relacionado:** [ADR 001 — Escolha da Stack Tecnológica](001-escolha-de-stack.md)

---

## Contexto

O Portal Incluir precisa armazenar informações estruturadas e relacionadas sobre estudantes, usuários, entrevistas, atendimentos, documentos, planos de ação, PEIs, adaptações pedagógicas e registros de auditoria.

Essas informações possuem relacionamentos claros. Por exemplo:

- um estudante pode possuir diversos atendimentos;
- um estudante pode possuir diversas entrevistas;
- um estudante pode possuir documentos e registros associados;
- um PEI pertence a um estudante;
- um plano de ação pode envolver diferentes profissionais;
- uma ação de auditoria está associada a um usuário;
- as informações disponíveis dependem do perfil e das permissões do usuário.

Além da estrutura relacional, o banco deverá contribuir para:

- integridade das informações;
- controle de relacionamentos;
- aplicação de restrições;
- rastreabilidade;
- realização de consultas e relatórios;
- proteção de dados pessoais e potencialmente sensíveis.

---

## Decisão

O Portal Incluir utilizará o **PostgreSQL** como banco de dados relacional principal.

O PostgreSQL será utilizado para armazenar os dados persistentes do sistema e será acessado pela aplicação backend desenvolvida em Java e Spring Boot.

A tecnologia específica de mapeamento objeto-relacional e a ferramenta de migração serão definidas durante o planejamento da implementação.

---

## Justificativa

O PostgreSQL foi escolhido porque:

- utiliza o modelo relacional, adequado ao domínio do Portal Incluir;
- oferece integridade referencial por meio de chaves primárias e estrangeiras;
- permite a criação de restrições para proteção da consistência dos dados;
- oferece suporte a transações;
- é gratuito e open source;
- possui integração consolidada com Java e Spring Boot;
- faz parte do conhecimento técnico da equipe;
- está disponível em diferentes serviços de hospedagem;
- permite a realização de consultas e relatórios;
- oferece recursos adequados para crescimento futuro do sistema.

---

## Estrutura conceitual inicial

O banco poderá conter entidades ou tabelas relacionadas a:

```text
usuarios
perfis
permissoes
estudantes
diagnosticos
entrevistas
atendimentos
documentos
peis
planos_de_acao
adaptacoes_pedagogicas
relatorios
notificacoes
registros_de_auditoria
```

Essa lista representa apenas uma visão inicial. Os nomes definitivos, atributos, relacionamentos e cardinalidades serão definidos no modelo de dados e no diagrama de classes.

---

## Relacionamentos esperados

Exemplos de relacionamentos que justificam o uso de um banco relacional:

```text
Usuário → possui → Perfil

Estudante → possui → Entrevistas

Estudante → possui → Atendimentos

Estudante → possui → Documentos

Estudante → possui → PEI

Estudante → possui → Plano de Ação

Estudante → possui → Adaptações Pedagógicas

Usuário → realiza → Operações de Auditoria
```

As permissões de acesso não deverão depender somente da interface. Elas também deverão ser verificadas pelo backend antes de qualquer consulta ou alteração.

---

## Alternativas consideradas

O estudo de viabilidade selecionou o PostgreSQL como parte da stack tecnológica, considerando:

- familiaridade da equipe;
- integração com Java e Spring Boot;
- ausência de custos de licença;
- adequação ao caráter relacional dos dados;
- disponibilidade em plataformas de hospedagem.

Não foi registrada uma comparação formal e detalhada com outros bancos de dados. Caso outra tecnologia seja proposta futuramente, deverá ser analisada em uma revisão deste ADR.

---

## Persistência e migrações

A aplicação deverá utilizar uma camada de persistência para evitar que regras de negócio dependam diretamente de comandos específicos do banco.

Também deverá ser adotado um mecanismo versionado de migrações para:

- criar tabelas;
- adicionar ou alterar colunas;
- criar índices;
- registrar mudanças no esquema;
- permitir a reprodução do banco em diferentes ambientes;
- reduzir alterações manuais.

A escolha da ferramenta de persistência e migração será registrada antes do início da implementação.

---

## Segurança e privacidade

Como o banco armazenará dados pessoais e potencialmente sensíveis, deverão ser considerados:

- princípio do menor privilégio;
- contas separadas por ambiente;
- credenciais fora do código-fonte;
- conexões protegidas;
- controle de acesso realizado pelo backend;
- registro das operações sensíveis;
- política de backup;
- política de retenção e descarte;
- proteção dos dados em repouso, quando aplicável;
- utilização de dados fictícios em desenvolvimento e testes;
- restrição de acesso direto ao banco de produção.

Nenhuma credencial, dado pessoal real, laudo ou documento identificável deverá ser versionado no GitHub.

---

## Auditoria

O banco deverá permitir o registro das principais operações realizadas pelos usuários, incluindo, quando aplicável:

- usuário responsável;
- tipo de operação;
- data e horário;
- registro afetado;
- resultado da operação;
- informações necessárias para investigação de incidentes.

Os registros de auditoria não deverão armazenar senhas, tokens, conteúdo sensível desnecessário ou cópias integrais de documentos.

---

## Consequências positivas

- modelo adequado aos relacionamentos do domínio;
- integridade referencial;
- suporte a transações;
- facilidade para consultas e relatórios;
- integração com Java e Spring Boot;
- tecnologia gratuita e open source;
- disponibilidade em diferentes plataformas de hospedagem;
- conhecimento prévio de parte da equipe.

---

## Consequências negativas e riscos

- o modelo precisará ser planejado cuidadosamente;
- alterações no esquema exigirão migrações;
- consultas complexas poderão exigir índices;
- documentos e arquivos grandes não devem ser armazenados sem uma decisão específica;
- backups e restaurações precisarão ser planejados;
- permissões inadequadas podem expor dados sensíveis;
- o crescimento do volume de documentos pode exigir armazenamento separado.

---

## Medidas de mitigação

- elaborar o modelo de dados antes da implementação;
- utilizar chaves estrangeiras e restrições;
- adotar migrações versionadas;
- criar índices com base nas consultas reais;
- aplicar o princípio do menor privilégio;
- utilizar variáveis de ambiente para credenciais;
- definir estratégia de backup e restauração;
- realizar revisão das consultas que acessam dados sensíveis;
- avaliar armazenamento externo para arquivos, mantendo no banco apenas seus metadados e referências.

---

## Decisões pendentes

Ainda deverão ser definidos:

- versão do PostgreSQL;
- biblioteca de persistência;
- ferramenta de migração;
- convenção de nomes;
- estratégia de armazenamento dos documentos;
- política de backup;
- política de retenção e descarte;
- plataforma de hospedagem;
- configuração dos ambientes de desenvolvimento, homologação e produção.

---

## Critério de revisão

Esta decisão deverá ser revisada se:

- a infraestrutura institucional não oferecer suporte ao PostgreSQL;
- surgir uma exigência técnica incompatível com o modelo relacional;
- os requisitos de escala mudarem significativamente;
- a estratégia de armazenamento de documentos exigir uma solução complementar;
- houver impedimento relevante de segurança, implantação ou manutenção.

A necessidade de armazenamento de arquivos não implica, por si só, substituição do PostgreSQL. Nesse caso, deverá ser avaliada uma solução complementar para os arquivos.
