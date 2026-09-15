# Backlog do Sistema: Portal Incluir

Backlog organizado em ordem de HU001 a HU021. Campos de planejamento e status alinhados ao [GitHub Projects](https://github.com/users/Lara-Menezes/projects/1), consultado em **15/09/2026**.

As HU020 e HU021 abrangem o CRUD completo, incluindo inativação e reativação. Seus cartões no Projects ainda usam os títulos “Cadastro de professor” e “Cadastro da Equipe Multidisciplinar”; os IDs e os links identificam as mesmas histórias.

## Visão geral do backlog

| ID | História | Épico | MoSCoW | Size | Sprint | Status |
|---|---|---|---|---|---|---|
| [HU001](https://github.com/Lara-Menezes/portal-incluir/issues/3) | Cadastrar estudante | Gestão de Estudantes | Must | L | Sprint 1 | Done |
| [HU002](https://github.com/Lara-Menezes/portal-incluir/issues/5) | Atualizar dados do estudante | Gestão de Estudantes | Must | M | Sprint 1 | Done |
| [HU003](https://github.com/Lara-Menezes/portal-incluir/issues/18) | Gerenciar status do estudante | Gestão de Estudantes | Must | M | Sprint 1 | Done |
| [HU004](https://github.com/Lara-Menezes/portal-incluir/issues/6) | Registrar acompanhamento pedagógico | Gestão Pedagógica | Must | L | Sprint 2 | Backlog |
| [HU005](https://github.com/Lara-Menezes/portal-incluir/issues/19) | Cadastrar documento PEI | Documentação | Must | L | Sprint 2 | Ready |
| [HU006](https://github.com/Lara-Menezes/portal-incluir/issues/20) | Cadastrar documento de Plano de Ação | Documentação | Must | L | Sprint 2 | Done |
| [HU007](https://github.com/Lara-Menezes/portal-incluir/issues/8) | Emitir relatório de estudantes | Relatórios | Must | L | Não definida | Backlog |
| [HU008](https://github.com/Lara-Menezes/portal-incluir/issues/9) | Agrupar estudantes por deficiência | Inclusão e Acessibilidade | Should | M | Sprint 1 | Backlog |
| [HU009](https://github.com/Lara-Menezes/portal-incluir/issues/10) | Buscar estudante | Consulta de Dados | Must | L | Sprint 1 | Done |
| [HU010](https://github.com/Lara-Menezes/portal-incluir/issues/11) | Visualizar informações centralizadas | Consulta de Dados | Must | XL | Sprint 1 | Backlog |
| [HU011](https://github.com/Lara-Menezes/portal-incluir/issues/12) | Comunicar-se com professores | Comunicação Institucional | Must | S | Sprint 1 | Backlog |
| [HU012](https://github.com/Lara-Menezes/portal-incluir/issues/13) | Comunicar-se com pais e responsáveis | Comunicação Institucional | Must | M | Sprint 1 | Backlog |
| [HU013](https://github.com/Lara-Menezes/portal-incluir/issues/14) | Controlar acesso por perfil - Autenticar e autorizar usuários com JWT | Segurança e Acesso | Must | L | Sprint 1 | Backlog |
| [HU014](https://github.com/Lara-Menezes/portal-incluir/issues/21) | Cadastrar coordenador | Gestão Pedagógica | Must | L | Sprint 1 | Done |
| [HU015](https://github.com/Lara-Menezes/portal-incluir/issues/22) | Atualizar dados do coordenador | Gestão Pedagógica | Must | M | Sprint 1 | Done |
| [HU016](https://github.com/Lara-Menezes/portal-incluir/issues/23) | Gerenciar status do coordenador | Gestão Pedagógica | Must | M | Sprint 1 | Done |
| [HU017](https://github.com/Lara-Menezes/portal-incluir/issues/15) | Preservar histórico dos estudantes | Armazenamento e Acessibilidade | Must | M | Sprint 1 | Backlog |
| [HU018](https://github.com/Lara-Menezes/portal-incluir/issues/16) | Utilizar o sistema em diferentes dispositivos | Armazenamento e Acessibilidade | Should | L | Sprint 1 | Backlog |
| [HU019](https://github.com/Lara-Menezes/portal-incluir/issues/17) | Utilizar recursos de acessibilidade | Armazenamento e Acessibilidade | Must | L | Sprint 1 | Backlog |
| [HU020](https://github.com/Lara-Menezes/portal-incluir/issues/28) | Gerenciar professores (CRUD) | Gestão Pedagógica | Must | L | Sprint 2 | Ready |
| [HU021](https://github.com/Lara-Menezes/portal-incluir/issues/29) | Gerenciar equipe multidisciplinar (CRUD) | Gestão Pedagógica | Must | L | Sprint 2 | Ready |

## Organização por sprint

| Planejamento | Histórias em ordem |
|---|---|
| Sprint 1 — 21/08 a 03/09/2026 | HU001, HU002, HU003, HU008, HU009, HU010, HU011, HU012, HU013, HU014, HU015, HU016, HU017, HU018, HU019 |
| Sprint 2 — 04/09 a 17/09/2026 | HU004, HU005, HU006, HU020, HU021 |
| Sem sprint definida no Projects | HU007 |

**Status:** 10 histórias em Backlog, 3 em Ready (HU005, HU020 e HU021) e 8 em Done (HU001, HU002, HU003, HU006, HU009, HU014, HU015 e HU016). In progress, In review e Blocked estão vazios.

As histórias ainda em Backlog e vinculadas à Sprint 1 continuam pendentes; a atribuição histórica não representa conclusão. Os checklists técnicos não foram marcados automaticamente a partir do status da issue.

## Integrações

- HU013 fornece autenticação e autorização para os cadastros HU020 e HU021. Sua integração é necessária para concluir os CRUDs com controle de acesso.
- HU011 reutiliza os contatos da HU020. Consultar contatos não concede ao coordenador permissão de gerenciar usuários.
- A exclusão nas HU020 e HU021 é lógica: inativar preserva os registros e permite reativação.
- HU018 e HU019 também se aplicam às novas telas.
- Possibilidades de chat citadas nas HU011 e HU012 permanecem futuras.

## Issues detalhadas

### HU001: Cadastrar estudante

**Épico:** Gestão de Estudantes | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 1 | **Status:** Done

> Como coordenador, quero cadastrar um novo estudante com seus dados completos, para manter suas informações registradas e disponíveis para acompanhamento.

**Tasks:**

- [ ] Criar formulário de cadastro do estudante  
- [ ] Definir campos obrigatórios e opcionais  
- [ ] Implementar validação dos campos obrigatórios  
- [ ] Implementar validação de dados inválidos  
- [ ] Implementar armazenamento dos dados do estudante  
- [ ] Disponibilizar o estudante para consulta após o cadastro  
- [ ] Implementar proteção dos dados conforme LGPD  
- [ ] Garantir que a interface seja intuitiva

---

### HU002: Atualizar dados do estudante

**Épico:** Gestão de Estudantes | **MoSCoW:** Must | **Size:** M | **Sprint:** Sprint 1 | **Status:** Done

> Como coordenador, quero atualizar os dados de um estudante cadastrado, para manter suas informações sempre atualizadas.

**Tasks:**

- [ ] Criar funcionalidade para localizar um estudante  
- [ ] Criar tela de edição dos dados  
- [ ] Permitir alterar os dados cadastrados  
- [ ] Implementar validação dos campos obrigatórios  
- [ ] Implementar validação dos dados atualizados  
- [ ] Implementar salvamento das alterações  
- [ ] Atualizar informações exibidas nas consultas  
- [ ] Aplicar permissões de acesso à edição

---

### HU003: Gerenciar status do estudante

**Épico:** Gestão de Estudantes | **MoSCoW:** Must | **Size:** M | **Sprint:** Sprint 1 | **Status:** Done

> Como coordenador, quero ativar ou inativar um estudante cadastrado, para controlar seu status de acompanhamento no sistema sem perder seu histórico.

**Tasks:**

- [ ] Exibir o status atual do estudante
- [ ] Permitir inativar um estudante ativo
- [ ] Solicitar confirmação antes da inativação
- [ ] Atualizar o status do estudante para inativo
- [ ] Permitir reativar um estudante inativo
- [ ] Solicitar confirmação antes da reativação
- [ ] Atualizar o status do estudante para ativo
- [ ] Impedir a exclusão dos dados durante a alteração de status
- [ ] Preservar os dados cadastrais do estudante
- [ ] Preservar os registros de acompanhamento pedagógico
- [ ] Preservar o PEI e o Plano de Ação associados ao estudante
- [ ] Permitir consultar estudantes ativos e inativos
- [ ] Identificar visualmente o status do estudante
- [ ] Aplicar permissões para alteração do status
- [ ] Registrar a alteração de status do estudante

---

### HU004: Registrar acompanhamento pedagógico

**Épico:** Gestão Pedagógica | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 2 | **Status:** Backlog

> Como professor ou coordenador, quero registrar entrevistas, acompanhamentos e procedimentos realizados com o estudante, para manter seu histórico pedagógico atualizado.

**Tasks:**

- [ ] Criar área de acompanhamento pedagógico  
- [ ] Permitir selecionar um estudante  
- [ ] Criar formulário para registrar entrevistas  
- [ ] Criar formulário para registrar acompanhamentos  
- [ ] Criar formulário para registrar procedimentos  
- [ ] Associar cada registro ao estudante correspondente  
- [ ] Exibir o histórico de acompanhamentos  
- [ ] Garantir a proteção dos registros

---

### HU005: Cadastrar documento PEI

**Épico:** Documentação | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 2 | **Status:** Ready

> Como coordenador, quero cadastrar um Plano Educacional Individualizado (PEI) para um estudante, para registrar suas necessidades educacionais, objetivos e estratégias de acompanhamento de forma individualizada.

**Tasks:**

- [ ] Criar formulário para cadastro do PEI
- [ ] Definir os campos e informações necessárias para o PEI
- [ ] Permitir registrar as necessidades educacionais do estudante
- [ ] Permitir registrar os objetivos educacionais
- [ ] Permitir registrar as estratégias de acompanhamento
- [ ] Definir os campos obrigatórios do PEI
- [ ] Implementar validação dos campos obrigatórios
- [ ] Associar o PEI ao estudante selecionado
- [ ] Implementar armazenamento do PEI
- [ ] Permitir consultar o PEI a partir dos dados do estudante
- [ ] Permitir editar o PEI para atualização das informações
- [ ] Aplicar controle de acesso ao PEI
- [ ] Garantir proteção dos dados conforme LGPD

---

### HU006: Cadastrar documento de Plano de Ação

**Épico:** Documentação | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 2 | **Status:** Done

> Como coordenador, quero cadastrar um Plano de Ação para um estudante, para registrar as ações, estratégias e responsáveis necessários para seu acompanhamento e desenvolvimento.

**Tasks:**

- [ ] Criar formulário para cadastro do Plano de Ação
- [ ] Definir os campos e informações necessárias para o Plano de Ação
- [ ] Permitir registrar as ações planejadas
- [ ] Permitir registrar as estratégias a serem utilizadas
- [ ] Permitir informar os responsáveis pelas ações
- [ ] Permitir registrar prazos ou períodos para realização das ações
- [ ] Definir os campos obrigatórios do Plano de Ação
- [ ] Implementar validação dos campos obrigatórios
- [ ] Associar o Plano de Ação ao estudante selecionado
- [ ] Implementar armazenamento do Plano de Ação
- [ ] Permitir consultar o Plano de Ação a partir dos dados do estudante
- [ ] Permitir atualizar as informações do Plano de Ação
- [ ] Aplicar controle de acesso ao Plano de Ação
- [ ] Garantir proteção dos dados conforme LGPD

---

### HU007: Emitir relatório de estudantes

**Épico:** Relatórios | **MoSCoW:** Must | **Size:** L | **Sprint:** Não definida | **Status:** Backlog

> Como coordenador, quero gerar relatórios com informações dos estudantes, para acompanhar e analisar os dados da instituição.

**Tasks:**

- [ ] Criar área de geração de relatórios  
- [ ] Criar filtros para seleção dos dados  
- [ ] Implementar busca das informações atualizadas  
- [ ] Criar estrutura do relatório  
- [ ] Implementar geração do relatório em PDF  
- [ ] Implementar geração do relatório em Excel  
- [ ] Aplicar controle de acesso aos dados do relatório

---

### HU008: Agrupar estudantes por deficiência

**Épico:** Inclusão e Acessibilidade | **MoSCoW:** Should | **Size:** M | **Sprint:** Sprint 1 | **Status:** Backlog

> Como coordenador, quero agrupar estudantes de acordo com o tipo de deficiência, para facilitar o acompanhamento e o planejamento de ações de inclusão.

**Tasks:**

- [ ] Identificar o tipo de deficiência cadastrado para cada estudante  
- [ ] Implementar agrupamento dos estudantes por tipo de deficiência  
- [ ] Criar visualização dos grupos  
- [ ] Permitir consultar os estudantes de cada grupo  
- [ ] Aplicar controle de acesso à consulta  
- [ ] Implementar recursos de acessibilidade na funcionalidade

---

### HU009: Buscar estudante

**Épico:** Consulta de Dados | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 1 | **Status:** Done

> Como usuário autorizado, quero buscar estudantes cadastrados, para localizar rapidamente suas informações.

**Tasks:**

- [ ] Criar campo de busca  
- [ ] Definir os critérios de pesquisa  
- [ ] Implementar mecanismo de busca  
- [ ] Exibir os estudantes correspondentes aos critérios  
- [ ] Permitir acessar os dados do estudante a partir dos resultados  
- [ ] Otimizar a busca para atender ao tempo máximo de 3 segundos  
- [ ] Adaptar a busca para computadores e dispositivos móveis  
- [ ] Garantir acessibilidade da interface

---

### HU010: Visualizar informações centralizadas

**Épico:** Consulta de Dados | **MoSCoW:** Must | **Size:** XL | **Sprint:** Sprint 1 | **Status:** Backlog

> Como usuário autorizado, quero visualizar documentos e informações do estudante em um único local, para ter acesso centralizado ao seu histórico.

**Tasks:**

- [ ] Criar página de informações centralizadas do estudante  
- [ ] Exibir os dados cadastrais  
- [ ] Exibir os registros de acompanhamento  
- [ ] Exibir os documentos relacionados  
- [ ] Organizar as informações de forma clara  
- [ ] Implementar controle de acesso conforme o perfil  
- [ ] Garantir proteção dos dados conforme LGPD

---

### HU011: Comunicar-se com professores

**Épico:** Comunicação Institucional | **MoSCoW:** Must | **Size:** S | **Sprint:** Sprint 1 | **Status:** Backlog

> Como coordenador, quero ter acesso aos dados de contato dos professores, para realizar comunicações institucionais por meios externos ao sistema.

**Tasks:**

- [ ] Reutilizar os dados de contato cadastrados na HU020, sem criar cadastro duplicado  
- [ ] Criar consulta dos contatos dos professores  
- [ ] Exibir os dados de contato para usuários autorizados  
- [ ] Definir os meios externos de comunicação utilizados pela instituição  
- [ ] Garantir proteção dos dados de contato  
- [ ] Avaliar futuramente a implementação de chat interno

---

### HU012: Comunicar-se com pais e responsáveis

**Épico:** Comunicação Institucional | **MoSCoW:** Must | **Size:** M | **Sprint:** Sprint 1 | **Status:** Backlog

> Como professor ou coordenador, quero ter acesso aos dados de contato dos pais ou responsáveis pelo estudante, para realizar comunicações necessárias por meios externos ao sistema.

**Tasks:**

- [ ] Adicionar os dados de contato do responsável ao cadastro do estudante  
- [ ] Criar consulta dos dados de contato do responsável  
- [ ] Exibir os contatos somente para usuários autorizados  
- [ ] Permitir identificar o responsável associado a cada estudante  
- [ ] Garantir proteção dos dados pessoais do responsável  
- [ ] Definir os meios externos de comunicação utilizados pela instituição  
- [ ] Avaliar futuramente a necessidade de comunicação interna

---

### HU013: Controlar acesso por perfil - Autenticar e autorizar usuários com JWT

**Épico:** Segurança e Acesso | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 1 | **Status:** Backlog

**Como** usuário do Portal Incluir,  
**Quero** me autenticar com minhas credenciais,  
**Para** acessar com segurança somente as funcionalidades autorizadas para o meu perfil.

**Tasks:**

- [ ] Adicionar e configurar Spring Security e a biblioteca de JWT.
- [ ] Criar a entidade de usuário e os quatro perfis de acesso.
- [ ] Relacionar cada usuário ao seu perfil.
- [ ] Criar DTOs de login e resposta da autenticação.
- [ ] Implementar carregamento do usuário pelo identificador de acesso.
- [ ] Implementar autenticação por login e senha e armazenamento com BCrypt.
- [ ] Implementar serviço de geração e validação de JWT e definir sua expiração.
- [ ] Criar filtro para processar o token no cabeçalho Authorization.
- [ ] Configurar autenticação stateless.
- [ ] Criar o endpoint público POST /api/auth/login.
- [ ] Configurar respostas HTTP 401 e 403.
- [ ] Definir a matriz definitiva de permissões dos quatro perfis.
- [ ] Proteger endpoints de coordenadores, estudantes, acompanhamentos, PEI, Plano de Ação, documentos, histórico e relatórios.
- [ ] Proteger endpoints de professores e equipe multidisciplinar conforme HU020 e HU021.
- [ ] Aplicar as permissões às rotas correspondentes no frontend.
- [ ] Impedir exposição de senhas e tokens completos em logs.
- [ ] Configurar o primeiro usuário administrador de maneira segura.
- [ ] Documentar a autenticação e o envio de Authorization: Bearer <token>.
- [ ] Testar credenciais válidas e inválidas e tokens ausentes, inválidos ou expirados.
- [ ] Testar endpoints com cada perfil, incluindo respostas 401 e 403 e acesso somente aos dados permitidos.
- [ ] Integrar o bloqueio de usuários inativos às regras de status dos cadastros.

---

### HU014: Cadastrar coordenador

**Épico:** Gestão Pedagógica | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 1 | **Status:** Done

> Como administrador, quero cadastrar um coordenador com seus dados de acesso e informações profissionais, para permitir que ele utilize o sistema de acordo com suas responsabilidades e permissões.

**Tasks:**

- [ ] Criar funcionalidade para cadastro de coordenadores
- [ ] Criar formulário de cadastro do coordenador
- [ ] Definir os dados cadastrais e profissionais do coordenador
- [ ] Definir os dados necessários para acesso ao sistema
- [ ] Definir os campos obrigatórios e opcionais
- [ ] Implementar validação dos campos obrigatórios
- [ ] Implementar validação de dados inválidos
- [ ] Verificar se o identificador de acesso já está sendo utilizado
- [ ] Impedir o cadastro de identificador de acesso duplicado
- [ ] Associar o perfil de coordenador ao usuário cadastrado
- [ ] Definir as permissões correspondentes ao perfil de coordenador
- [ ] Armazenar os dados do coordenador de forma segura
- [ ] Disponibilizar o coordenador para consulta e gerenciamento
- [ ] Definir o coordenador como ativo após o cadastro
- [ ] Aplicar controle de acesso à funcionalidade
- [ ] Garantir proteção dos dados conforme LGPD

---

### HU015: Atualizar dados do coordenador

**Épico:** Gestão Pedagógica | **MoSCoW:** Must | **Size:** M | **Sprint:** Sprint 1 | **Status:** Done

> Como administrador, quero atualizar os dados de um coordenador cadastrado, para manter suas informações cadastrais, profissionais e de acesso atualizadas.

**Tasks:**

- [ ] Criar funcionalidade para localizar um coordenador
- [ ] Criar tela de visualização dos dados do coordenador
- [ ] Criar tela de edição dos dados
- [ ] Permitir alterar os dados cadastrais do coordenador
- [ ] Permitir atualizar os dados profissionais do coordenador
- [ ] Permitir atualizar os dados de acesso quando autorizado
- [ ] Implementar validação dos campos obrigatórios
- [ ] Implementar validação dos dados atualizados
- [ ] Verificar possíveis conflitos no identificador de acesso
- [ ] Implementar salvamento das alterações
- [ ] Registrar a data da atualização
- [ ] Atualizar as informações exibidas nas consultas
- [ ] Aplicar permissões de acesso à edição
- [ ] Garantir a proteção dos dados atualizados

---

### HU016: Gerenciar status do coordenador

**Épico:** Gestão Pedagógica | **MoSCoW:** Must | **Size:** M | **Sprint:** Sprint 1 | **Status:** Done

> Como administrador, quero ativar ou inativar um coordenador cadastrado, para controlar seu acesso ao sistema sem perder seu histórico.

**Tasks:**

- [ ] Exibir o status atual do coordenador
- [ ] Permitir inativar um coordenador ativo
- [ ] Solicitar confirmação antes da inativação
- [ ] Atualizar o status do coordenador para inativo
- [ ] Impedir o acesso do coordenador inativo ao sistema
- [ ] Permitir reativar um coordenador inativo
- [ ] Solicitar confirmação antes da reativação
- [ ] Atualizar o status do coordenador para ativo
- [ ] Restaurar as permissões correspondentes ao perfil do coordenador
- [ ] Preservar os dados cadastrais e profissionais do coordenador
- [ ] Preservar o histórico de registros associados ao coordenador
- [ ] Permitir consultar coordenadores ativos e inativos
- [ ] Identificar visualmente o status do coordenador
- [ ] Aplicar permissões para alteração do status
- [ ] Registrar a alteração de status do coordenador

---

### HU017: Preservar histórico dos estudantes

**Épico:** Armazenamento e Acessibilidade | **MoSCoW:** Must | **Size:** M | **Sprint:** Sprint 1 | **Status:** Backlog

> Como instituição de ensino, quero manter os dados dos estudantes armazenados após sua conclusão, para preservar seu histórico para consultas futuras.

**Tasks:**

- [ ] Definir política de armazenamento do histórico  
- [ ] Implementar armazenamento dos dados após a conclusão do estudante  
- [ ] Garantir a integridade dos dados armazenados  
- [ ] Implementar controle de acesso ao histórico  
- [ ] Definir o período de retenção de até 5 anos  
- [ ] Implementar mecanismos de proteção dos dados  
- [ ] Verificar conformidade com a LGPD

---

### HU018: Utilizar o sistema em diferentes dispositivos

**Épico:** Armazenamento e Acessibilidade | **MoSCoW:** Should | **Size:** L | **Sprint:** Sprint 1 | **Status:** Backlog

> Como usuário, quero acessar o sistema em computadores e dispositivos móveis, para utilizar suas funcionalidades independentemente do dispositivo.

**Tasks:**

- [ ] Desenvolver interface responsiva  
- [ ] Adaptar as telas para diferentes tamanhos de tela  
- [ ] Testar as principais funcionalidades em computadores  
- [ ] Testar as principais funcionalidades em dispositivos móveis  
- [ ] Garantir consistência dos dados entre dispositivos  
- [ ] Corrigir problemas de interface encontrados nos diferentes dispositivos

---

### HU019: Utilizar recursos de acessibilidade

**Épico:** Armazenamento e Acessibilidade | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 1 | **Status:** Backlog

> Como usuário com necessidade de acessibilidade, quero utilizar recursos de acessibilidade digital, para conseguir utilizar o sistema de forma adequada.

**Tasks:**

- [ ] Definir os recursos de acessibilidade necessários  
- [ ] Implementar recursos de acessibilidade nas principais funcionalidades  
- [ ] Garantir navegação clara e compreensível  
- [ ] Aplicar recursos de acessibilidade aos elementos da interface  
- [ ] Verificar acessibilidade na funcionalidade de consulta  
- [ ] Verificar acessibilidade na funcionalidade de agrupamento de estudantes  
- [ ] Realizar testes de acessibilidade  
- [ ] Corrigir problemas identificados nos testes

---

### HU020: Gerenciar professores (CRUD)

**Épico:** Gestão Pedagógica | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 2 | **Status:** Ready

> Como administrador, quero cadastrar, consultar, atualizar e inativar professores, para manter suas informações atualizadas e controlar seu acesso ao sistema, preservando seus dados e histórico.

**Tasks:**

- [ ] Criar tela de cadastro de professores.
- [ ] Definir os campos cadastrais, profissionais e de acesso, indicando os obrigatórios e opcionais.
- [ ] Implementar validação dos dados informados.
- [ ] Implementar validação para impedir identificador de acesso duplicado.
- [ ] Integrar o cadastro à estrutura de usuários e permissões da HU013.
- [ ] Implementar salvamento dos dados com perfil PROFESSOR e status inicial ativo.
- [ ] Criar listagem de professores.
- [ ] Implementar busca por nome ou identificador de acesso.
- [ ] Implementar filtro por status ativo ou inativo.
- [ ] Criar visualização dos dados e do status do professor, sem exibir senhas.
- [ ] Exibir mensagem quando nenhum resultado for encontrado.
- [ ] Criar tela de edição dos dados do professor.
- [ ] Implementar validações e salvamento das alterações.
- [ ] Implementar atualização autorizada das informações de acesso.
- [ ] Garantir a preservação do perfil, do status e dos registros associados durante a edição.
- [ ] Implementar exclusão lógica por inativação.
- [ ] Implementar confirmação antes da inativação.
- [ ] Bloquear o acesso ao sistema para usuários inativados.
- [ ] Implementar reativação com confirmação.
- [ ] Restabelecer o acesso conforme o perfil após a reativação.
- [ ] Garantir a preservação dos dados, registros, documentos e históricos durante as alterações de status.
- [ ] Aplicar as permissões administrativas às telas e aos endpoints de gerenciamento.
- [ ] Implementar mensagens de sucesso e erro nas operações.
- [ ] Adaptar as telas para computadores e dispositivos móveis e garantir acessibilidade.
- [ ] Testar cadastro, consulta, atualização, inativação e reativação.
- [ ] Verificar as validações, as permissões de acesso e a preservação dos registros.
- [ ] Disponibilizar os dados de contato para a consulta autorizada da HU011, sem conceder ao coordenador permissão de gerenciamento.

---

### HU021: Gerenciar equipe multidisciplinar (CRUD)

**Épico:** Gestão Pedagógica | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 2 | **Status:** Ready

> Como administrador, quero cadastrar, consultar, atualizar e inativar profissionais da equipe multidisciplinar, para manter suas informações atualizadas e controlar seu acesso ao sistema, preservando seus dados e histórico.

**Tasks:**

- [ ] Criar tela de cadastro de profissionais da equipe multidisciplinar.
- [ ] Definir os campos cadastrais, profissionais e de acesso, indicando os obrigatórios e opcionais.
- [ ] Implementar validação dos dados informados.
- [ ] Implementar validação para impedir identificador de acesso duplicado.
- [ ] Integrar o cadastro à estrutura de usuários e permissões da HU013.
- [ ] Implementar salvamento dos dados com perfil EQUIPE_MULTIDISCIPLINAR e status inicial ativo.
- [ ] Criar listagem de profissionais da equipe multidisciplinar.
- [ ] Implementar busca por nome ou identificador de acesso.
- [ ] Implementar filtro por status ativo ou inativo.
- [ ] Criar visualização dos dados e do status do profissional, sem exibir senhas.
- [ ] Exibir mensagem quando nenhum resultado for encontrado.
- [ ] Criar tela de edição dos dados do profissional.
- [ ] Implementar validações e salvamento das alterações.
- [ ] Implementar atualização autorizada das informações de acesso.
- [ ] Garantir a preservação do perfil, do status e dos registros associados durante a edição.
- [ ] Implementar exclusão lógica por inativação.
- [ ] Implementar confirmação antes da inativação.
- [ ] Bloquear o acesso ao sistema para usuários inativados.
- [ ] Implementar reativação com confirmação.
- [ ] Restabelecer o acesso conforme o perfil após a reativação.
- [ ] Garantir a preservação dos dados, registros, documentos e históricos durante as alterações de status.
- [ ] Aplicar as permissões administrativas às telas e aos endpoints de gerenciamento.
- [ ] Implementar mensagens de sucesso e erro nas operações.
- [ ] Adaptar as telas para computadores e dispositivos móveis e garantir acessibilidade.
- [ ] Testar cadastro, consulta, atualização, inativação e reativação.
- [ ] Verificar as validações, as permissões de acesso e a preservação dos registros.
- [ ] Implementar campo e filtro de área de atuação ou especialidade.
- [ ] Garantir cadastro e credenciais individuais para cada profissional.

---
