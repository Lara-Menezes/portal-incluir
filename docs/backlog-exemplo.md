# Backlog do Sistema: Portal Incluir

Este documento organiza o backlog do Portal Incluir. Cada seção representa um Issue no GitHub Projects, com campos customizados (Épico, MoSCoW, Size, Sprint) e checklist de tasks técnicas que devem ser implementadas.

---

## Visão geral do backlog

| ID    | Épico                     | MoSCoW | Size | Sprint   |
|-------|---------------------------|--------|------|----------|
| HU001 | Gestão de Estudantes      | Must   | L    | Sprint 1 |
| HU002 | Gestão de Estudantes      | Must   | M    | Sprint 1 |
| HU003 | Gestão de Estudantes      | Must   | M    | Sprint 1 |
| HU004 | Gestão Pedagógica         | Must   | L    | Sprint 2 |
| HU005 | Documentação              | Must   | L    | Sprint 1 |
| HU006 | Documentação              | Must   | L    | Sprint 2 |
| HU007 | Relatórios                | Must   | L    | Sprint 3 |
| HU008 | Inclusão                  | Should | M    | Sprint 3 |
| HU009 | Consulta de Dados         | Must   | L    | Sprint 4 |
| HU010 | Consulta de Dados         | Must   | XL   | Sprint 4 |
| HU011 | Comunicação Institucional | Should | S    | Sprint 5 |
| HU012 | Comunicação Institucional | Should | M    | Sprint 5 |
| HU013 | Segurança e Acesso        | Must   | L    | Sprint 6 |
| HU014 | Gestão de Usuários        | Must   | L    | Sprint 1 |
| HU015 | Gestão de Usuários        | Must   | M    | Sprint 1 |
| HU016 | Gestão de Usuários        | Must   | M    | Sprint 1 |
| HU017 | Armazenamento             | Should | M    | Sprint 6 |
| HU018 | Acessibilidade            | Must   | L    | Sprint 7 |
| HU019 | Acessibilidade            | Must   | L    | Sprint 7 |

---

## Issues detalhados

---

### HU001: Cadastrar estudante
**Épico:** Gestão de Estudantes | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 1  

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
**Épico:** Gestão de Estudantes | **MoSCoW:** Must | **Size:** M | **Sprint:** Sprint 1  

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
**Épico:** Gestão de Estudantes | **MoSCoW:** Must | **Size:** M | **Sprint:** Sprint 1

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
**Épico:** Gestão Pedagógica | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 2  

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
**Épico:** Documentação | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 1

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
**Épico:** Documentação | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 2

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
**Épico:** Relatórios | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 3  

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
**Épico:** Inclusão | **MoSCoW:** Should | **Size:** M | **Sprint:** Sprint 3  

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
**Épico:** Consulta de Dados | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 4  

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
**Épico:** Consulta de Dados | **MoSCoW:** Must | **Size:** XL | **Sprint:** Sprint 4  

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
**Épico:** Comunicação Institucional | **MoSCoW:** Should | **Size:** S | **Sprint:** Sprint 5  

> Como coordenador, quero ter acesso aos dados de contato dos professores, para realizar comunicações institucionais por meios externos ao sistema.

**Tasks:**
- [ ] Cadastrar os dados de contato dos professores  
- [ ] Criar consulta dos contatos dos professores  
- [ ] Exibir os dados de contato para usuários autorizados  
- [ ] Definir os meios externos de comunicação utilizados pela instituição  
- [ ] Garantir proteção dos dados de contato  
- [ ] Avaliar futuramente a implementação de chat interno  

---

### HU012: Comunicar-se com pais e responsáveis
**Épico:** Comunicação Institucional | **MoSCoW:** Should | **Size:** M | **Sprint:** Sprint 5  

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

### HU013: Controlar acesso por perfil
**Épico:** Segurança e Acesso | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 6  

> Como administrador, quero definir permissões de acesso conforme o perfil do usuário, para garantir que cada usuário tenha acesso somente às funcionalidades autorizadas.

**Tasks:**
- [ ] Criar os perfis de usuário  
- [ ] Definir as permissões de cada perfil  
- [ ] Implementar controle de acesso às funcionalidades  
- [ ] Implementar controle de acesso aos dados dos estudantes  
- [ ] Impedir acesso a funcionalidades não autorizadas  
- [ ] Implementar proteção das informações restritas  
- [ ] Testar as permissões de cada perfil  

Claro, Lívia! Vamos finalizar os **issues detalhados** restantes no mesmo padrão:

---

### HU014: Cadastrar coordenador
**Épico:** Segurança e Acesso | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 1

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
**Épico:** Segurança e Acesso | **MoSCoW:** Must | **Size:** M | **Sprint:** Sprint 1

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
**Épico:** Segurança e Acesso | **MoSCoW:** Must | **Size:** M | **Sprint:** Sprint 6

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
**Épico:** Armazenamento | **MoSCoW:** Should | **Size:** M | **Sprint:** Sprint 6  

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
**Épico:** Multidispositivos | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 7  

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
**Épico:** Acessibilidade | **MoSCoW:** Must | **Size:** L | **Sprint:** Sprint 7  

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
