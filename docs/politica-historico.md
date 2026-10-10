# Histórico e retenção de estudantes

## Escopo da entrega

O Envers registra as alterações de estudantes, planos de ação e adaptações pedagógicas
em tabelas `_aud`, na mesma transação da alteração. Não há implementação nova de JWT,
login, identificação do autor ou autorização por perfil nesta entrega.

Controle de acesso por usuário está **pendente**. Habilitar uma consulta por configuração
não equivale a autorizar usuários. Os endpoints existentes continuam sujeitos à segurança
atual da aplicação; esta entrega não garante que seus consumidores estejam autorizados.
Não declarar a história integralmente aceita enquanto esse critério estiver pendente.

## Política proposta

- Conclusão é diferente de inativação. Somente a conclusão inicia a retenção.
- O prazo termina na data de conclusão acrescida de cinco anos, no fuso America/Sao_Paulo.
  A data limite é exclusiva: a partir desse dia, todas as consultas da aplicação ocultam
  os dados vencidos, mesmo se o descarte ainda estiver aguardando execução.
- Conclusão exige data não futura, prazo ainda vigente e estudante ainda não concluído.
- O cadastro permanece armazenado e pode ser consultado durante o prazo. Estudante
  concluído e seus registros ficam somente para leitura; não podem ser reativados,
  alterados ou excluídos pelos fluxos comuns. Revisões antigas continuam intactas.
- Exclusão manual de estudantes é recusada; usar inativação ou conclusão. Registros
  filhos não podem ser transferidos entre estudantes, preservando a atribuição das revisões.
- O descarte roda por hora, em lotes de 100, com transação por estudante. Remove o
  cadastro, planos, adaptações e todas as suas revisões, inclusive filhos já excluídos.
  SQL em lote evita recriar dados de auditoria durante a exclusão. REVINFO retém apenas
  número e instante de revisão, sem usuário ou conteúdo pessoal.
- A disponibilidade do serviço e o volume de vencimentos podem atrasar a remoção física;
  monitorar falhas e capacidade do lote. A indisponibilidade para consulta é imediata
  no vencimento. A instituição deve definir um SLA de descarte e sua monitoração.

## API

`POST /api/estudantes/{id}/concluir` com `{"dataConclusao":"2026-10-09"}`.
A resposta contém `dataConclusao`, `dataLimiteRetencao` e `concluido`.

Consultas de revisões (página inicial 0, tamanho padrão 20, máximo 100):

- `GET /api/estudantes/{id}/historico?pagina=0&tamanho=20`
- `GET /api/estudantes/{id}/historico/planos`
- `GET /api/estudantes/{id}/historico/adaptacoes`

Cada entrada contém número, instante, tipo ADD/MOD/DEL e valores da revisão.
As revisões de filhos retornam somente os campos próprios, sem resolver nomes atuais
de professores/coordenadores como se fossem históricos. Não expor entidades JPA diretamente.
Respostas de histórico usam `Cache-Control: no-store`.

As consultas de revisões ficam desabilitadas por padrão (403).
`HISTORICO_CONSULTA_HABILITADA=true` permite consultas **somente em ambiente de teste
ou com controle de acesso externo efetivo**. Essa chave não substitui autenticação.
O registro das revisões independe dessa chave.

## Integridade e implantação

Transações, bloqueio do estudante nas escritas e versão otimista dos três tipos de
entidade evitam alterações concorrentes silenciosas e disputa com conclusão/descarte.
O bloqueio não protege alterações SQL feitas fora da aplicação. Restringir credenciais
do banco e impedir edição direta das tabelas de auditoria.

O Envers começa a registrar a partir da implantação: não reconstrói alterações passadas.
A conclusão atualiza o instante dos filhos para registrar seu estado preexistente na
mesma revisão. Para registros antigos ainda ativos, a primeira revisão surge na próxima
alteração. Validar migração em cópia do PostgreSQL antes da implantação: novas tabelas
`auditoria.estudantes_aud`, `auditoria.planos_acao_aud`, `auditoria.adaptacoes_pedagogicas_aud`,
`auditoria.professores_aud`, `auditoria.coordenadores_aud`,
`auditoria.membros_equipe_multidisciplinar_aud`, `auditoria.revinfo`, colunas
de versão e datas de conclusão/retenção. O projeto usa `ddl-auto=update`; isso não
substitui uma migração de produção revisada, backup e ensaio de restauração.

## LGPD: verificação e pendências institucionais

O prazo de cinco anos é um requisito desta história, **não uma regra geral da LGPD**.
A instituição deve validar finalidade, necessidade, base legal e eventual obrigação
de conservação dos documentos acadêmicos. Planos contêm dados de saúde, que exigem
avaliação específica de dados pessoais sensíveis. Envers não criptografa nem autoriza acesso.

Implementado: fim do prazo de consulta, descarte do banco e auditoria, histórico somente
para leitura após conclusão, ausência de SQL nos logs por padrão e cache desabilitado
nas respostas de revisões.

Pendências para produção/conformidade: autorização por perfil (ou gateway autenticado),
registro de acesso/autor quando houver identidade confiável, TLS, criptografia do banco
e backups com gestão de chaves, segregação de permissões, atendimento aos titulares,
registro das operações de tratamento, política de incidentes e validação pelo encarregado.
Não há certificação de conformidade com LGPD nesta entrega.

O descarte implementado cobre somente o banco conectado. Arquivos anexos, exportações,
réplicas e backups exigem política própria. Após restaurar um backup, executar descarte
dos vencidos antes de liberar consultas. Definir prazo e processo para expirar cópias e
anexos; caminhos de documentos não significam que arquivos foram apagados.

Referências: [LGPD, arts. 6, 11, 15, 16 e 46](https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709.htm)
e [Hibernate Envers](https://hibernate.org/orm/envers/).
