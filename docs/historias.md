# Histórias de Usuário: Portal Incluir

Histórias em ordem numérica, com épico e sprint conforme o Projects consultado em 15/09/2026. Os critérios de aceitação definem a conclusão de cada história; o status de execução consta no backlog.

## HU001: Cadastrar estudante

**Épico:** Gestão de Estudantes | **Sprint:** Sprint 1

**Como** coordenador,  
**Quero** cadastrar um novo estudante com seus dados completos,  
**Para** manter suas informações registradas e disponíveis para acompanhamento.

**Critérios de aceitação:**  
* O formulário deve permitir informar os dados cadastrais do estudante.  
* O sistema deve validar os campos obrigatórios.  
* O sistema deve impedir o cadastro de informações inválidas.  
* Após o cadastro, o estudante deve ficar disponível para consulta.  
* Os dados devem ser armazenados de forma segura, conforme as diretrizes da LGPD.  
* A interface deve ser intuitiva e de fácil utilização.

---

## HU002: Atualizar dados do estudante

**Épico:** Gestão de Estudantes | **Sprint:** Sprint 1

**Como** coordenador,  
**Quero** atualizar os dados de um estudante cadastrado,  
**Para** manter suas informações sempre atualizadas.

**Critérios de aceitação:**  
* O sistema deve permitir localizar um estudante cadastrado.  
* O sistema deve permitir editar seus dados.  
* Os campos obrigatórios devem continuar sendo validados durante a atualização.  
* Após salvar, as informações atualizadas devem ser exibidas nas consultas.  
* A alteração deve respeitar as permissões do perfil do usuário.

---

## HU003: Gerenciar status do estudante

**Épico:** Gestão de Estudantes | **Sprint:** Sprint 1

**Como** coordenador,
**Quero** alterar o status de um estudante entre ativo e inativo,
**Para** controlar seu acompanhamento no sistema sem perder seus dados e histórico.

**Critérios de aceitação:**

* O sistema deve permitir localizar um estudante cadastrado.
* O sistema deve permitir visualizar se o estudante está ativo ou inativo.
* O sistema deve permitir inativar um estudante ativo.
* O sistema deve solicitar confirmação antes da inativação.
* Um estudante inativado não deve aparecer como estudante ativo nas consultas.
* O sistema deve permitir consultar estudantes inativados para usuários autorizados.
* O sistema deve permitir reativar um estudante anteriormente inativado.
* O sistema deve solicitar confirmação antes da reativação.
* Após a reativação, o estudante deve voltar a ser considerado ativo.
* Os dados cadastrais do estudante devem ser preservados durante a alteração de status.
* Os registros de acompanhamento devem ser preservados.
* O PEI e o Plano de Ação associados ao estudante devem ser preservados.
* O histórico do estudante não deve ser excluído com a alteração de status.
* Após a reativação, os dados, documentos e histórico anteriormente registrados devem permanecer disponíveis.

---

## HU004: Registrar acompanhamento pedagógico

**Épico:** Gestão Pedagógica | **Sprint:** Sprint 2

**Como** professor ou coordenador,  
**Quero** registrar entrevistas, acompanhamentos e procedimentos realizados com o estudante,  
**Para** manter seu histórico pedagógico atualizado.

**Critérios de aceitação:**  
* O sistema deve permitir selecionar um estudante cadastrado.  
* Deve ser possível registrar entrevistas realizadas.  
* Deve ser possível registrar acompanhamentos pedagógicos.  
* Deve ser possível registrar procedimentos realizados.  
* Cada registro deve ficar associado ao estudante correspondente.  
* As informações devem ser armazenadas de forma segura.

---

## HU005: Cadastrar documento PEI

**Épico:** Documentação | **Sprint:** Sprint 2

**Como** coordenador,
**Quero** cadastrar um Plano Educacional Individualizado (PEI) para um estudante,
**Para** registrar suas necessidades educacionais, objetivos e estratégias de acompanhamento de forma individualizada.

**Critérios de aceitação:**

* O sistema deve permitir selecionar um estudante previamente cadastrado.
* O sistema deve permitir cadastrar um novo PEI associado ao estudante selecionado.
* O formulário do PEI deve permitir registrar as informações necessárias para sua elaboração.
* O sistema deve validar os campos obrigatórios antes de permitir o salvamento.
* O sistema deve associar o PEI exclusivamente ao estudante selecionado.
* O sistema deve permitir consultar o PEI posteriormente a partir dos dados do estudante.
* O sistema deve permitir editar o PEI para atualização das informações, respeitando as permissões do usuário.
* O sistema deve registrar a data de cadastro e/ou atualização do documento.
* Somente usuários autorizados devem poder cadastrar, visualizar ou alterar o PEI.
* O documento deve ser armazenado de forma segura, conforme as diretrizes da LGPD.

---

## HU006: Cadastrar documento de Plano de Ação

**Épico:** Documentação | **Sprint:** Sprint 2

**Como** coordenador,
**Quero** cadastrar um Plano de Ação para um estudante,
**Para** registrar as ações, estratégias e responsáveis necessários para seu acompanhamento e desenvolvimento.

**Critérios de aceitação:**

* O sistema deve permitir selecionar um estudante previamente cadastrado.
* O sistema deve permitir cadastrar um Plano de Ação associado ao estudante selecionado.
* O formulário deve permitir registrar as ações e informações necessárias para o planejamento.
* O sistema deve permitir informar, quando aplicável, responsáveis pelas ações.
* O sistema deve permitir registrar prazos ou períodos para realização das ações.
* O sistema deve validar os campos obrigatórios antes de permitir o salvamento.
* O sistema deve associar o Plano de Ação exclusivamente ao estudante selecionado.
* O sistema deve permitir consultar o Plano de Ação posteriormente a partir dos dados do estudante.
* O sistema deve permitir atualizar as informações do Plano de Ação, respeitando as permissões do usuário.
* O sistema deve registrar a data de cadastro e/ou atualização do documento.
* Somente usuários autorizados devem poder cadastrar, visualizar ou alterar o Plano de Ação.
* O documento deve ser armazenado de forma segura, conforme as diretrizes da LGPD.

---

## HU007: Emitir relatório de estudantes

**Épico:** Relatórios | **Sprint:** Não definida

**Como** coordenador,  
**Quero** gerar relatórios com informações dos estudantes,  
**Para** acompanhar e analisar os dados da instituição.

**Critérios de aceitação:**  
* O sistema deve permitir selecionar os dados que serão apresentados no relatório.  
* O relatório deve apresentar informações atualizadas.  
* O sistema deve permitir gerar o relatório em formato PDF.  
* O sistema deve permitir gerar o relatório em formato Excel.  
* O arquivo gerado deve conter somente informações às quais o usuário possui permissão de acesso.

---

## HU008: Agrupar estudantes por deficiência

**Épico:** Inclusão e Acessibilidade | **Sprint:** Sprint 1

**Como** coordenador,  
**Quero** agrupar estudantes de acordo com o tipo de deficiência,  
**Para** facilitar o acompanhamento e o planejamento de ações de inclusão.

**Critérios de aceitação:**  
* O sistema deve permitir identificar o tipo de deficiência do estudante.  
* O sistema deve permitir visualizar estudantes agrupados por tipo de deficiência.  
* Um estudante deve aparecer no grupo correspondente às suas informações cadastradas.  
* A consulta deve ser acessível aos usuários autorizados.  
* A funcionalidade deve possuir recursos de acessibilidade digital.

---

## HU009: Buscar estudante

**Épico:** Consulta de Dados | **Sprint:** Sprint 1

**Como** usuário autorizado,  
**Quero** buscar estudantes cadastrados,  
**Para** localizar rapidamente suas informações.

**Critérios de aceitação:**  
* O sistema deve permitir realizar buscas por informações do estudante.  
* Os resultados devem apresentar os estudantes correspondentes aos critérios informados.  
* A busca deve retornar os resultados em até 3 segundos.  
* O sistema deve permitir acessar os dados do estudante a partir do resultado da busca.  
* A interface de busca deve ser intuitiva e acessível.  
* A funcionalidade deve funcionar em computadores e dispositivos móveis.

---

## HU010: Visualizar informações centralizadas

**Épico:** Consulta de Dados | **Sprint:** Sprint 1

**Como** usuário autorizado,  
**Quero** visualizar documentos e informações do estudante em um único local,  
**Para** ter acesso centralizado ao seu histórico.

**Critérios de aceitação:**  
* O sistema deve apresentar os dados cadastrais do estudante.  
* O sistema deve apresentar os registros de acompanhamento.  
* O sistema deve disponibilizar os documentos relacionados ao estudante.  
* As informações devem estar organizadas de forma clara.  
* O sistema deve restringir o acesso conforme o perfil do usuário.  
* Os dados devem ser protegidos conforme a LGPD.

---

## HU011: Comunicar-se com professores

**Épico:** Comunicação Institucional | **Sprint:** Sprint 1

**Como** coordenador,  
**Quero** ter acesso aos dados de contato dos professores,  
**Para** realizar comunicações institucionais por meios externos ao sistema.

**Critérios de aceitação:**  
* O sistema deve permitir consultar os dados de contato dos professores.  
* O coordenador deve conseguir identificar o professor relacionado à comunicação.  
* A comunicação deve ocorrer por meios externos ao sistema.  
* O sistema não deve exigir que a comunicação seja realizada dentro da plataforma.  
* O sistema poderá futuramente disponibilizar um chat interno entre usuários autorizados, caso essa funcionalidade seja priorizada.  
* Caso o chat seja implementado, somente usuários autorizados poderão utilizá-lo.

---

## HU012: Comunicar-se com pais e responsáveis

**Épico:** Comunicação Institucional | **Sprint:** Sprint 1

**Como** professor ou coordenador,  
**Quero** ter acesso aos dados de contato dos pais ou responsáveis pelo estudante,  
**Para** realizar comunicações necessárias por meios externos ao sistema.

**Critérios de aceitação:**  
* O sistema deve permitir consultar os dados de contato do responsável associado ao estudante.  
* O usuário autorizado deve conseguir identificar o responsável relacionado ao estudante.  
* A comunicação deve ocorrer por meios externos ao sistema.  
* O responsável não deve possuir usuário ou acesso ao sistema.  
* O sistema não deve enviar, receber ou armazenar as mensagens trocadas com o responsável.  
* Os dados de contato devem estar disponíveis somente para usuários autorizados.  
* O sistema poderá futuramente disponibilizar um chat caso essa funcionalidade seja priorizada.

---

## HU013: Controlar acesso por perfil - Autenticar e autorizar usuários com JWT

**Épico:** Segurança e Acesso | **Sprint:** Sprint 1

**Como** usuário do Portal Incluir,  
**Quero** me autenticar com minhas credenciais,  
**Para** acessar com segurança somente as funcionalidades autorizadas para o meu perfil.

**Critérios de aceitação:**

* O sistema deve autenticar usuários por meio de credenciais válidas.
* Após a autenticação, o sistema deve gerar um token JWT que identifique o usuário e seu perfil.
* As rotas protegidas devem exigir um token JWT válido e recusar tokens inválidos ou expirados.
* Credenciais inválidas não devem revelar se o login ou a senha estão incorretos.
* As senhas devem ser armazenadas utilizando hash BCrypt.
* O sistema deve possuir os perfis ADMINISTRADOR, COORDENADOR, PROFESSOR e EQUIPE_MULTIDISCIPLINAR.
* Cada usuário deve possuir um perfil com permissões específicas.
* O controle de acesso deve ser aplicado às rotas da aplicação, aos endpoints da API e aos dados dos estudantes.
* Usuários autenticados não devem acessar funcionalidades incompatíveis com seu perfil.
* Requisições não autenticadas devem retornar HTTP 401 Unauthorized.
* Requisições sem a permissão necessária devem retornar HTTP 403 Forbidden.
* O endpoint de autenticação deve ser público; os demais endpoints devem ser protegidos conforme as permissões definidas.
* A autenticação deve ser stateless, sem armazenamento de sessão no servidor.
* Dados sensíveis, senhas e tokens completos não devem ser registrados nos logs.
* As permissões devem ser validadas no backend, independentemente das restrições do frontend.

**Matriz inicial de permissões:**

| Funcionalidade | Administrador | Coordenador | Professor | Equipe multidisciplinar |
|---|---|---|---|---|
| Gerenciar usuários e perfis | Sim | Não | Não | Não |
| Cadastrar e editar coordenadores | Sim | Não | Não | Não |
| Ativar e inativar coordenadores | Sim | Não | Não | Não |
| Cadastrar estudantes | Sim | Sim | Não | Não |
| Atualizar estudantes | Sim | Sim | Não | Não |
| Ativar e inativar estudantes | Sim | Sim | Não | Não |
| Consultar estudantes | Sim | Sim | Sim | Sim |
| Registrar acompanhamento pedagógico | Sim | Sim | Sim | Sim |
| Gerenciar PEI e Plano de Ação | Sim | Sim | A definir | A definir |
| Consultar documentos e histórico | Sim | Sim | A definir | A definir |
| Emitir relatórios | Sim | Sim | A definir | A definir |

As permissões marcadas como **A definir** permanecem pendentes de definição pela equipe. O gerenciamento dos CRUDs HU020 e HU021 segue a permissão administrativa de gerenciamento de usuários.

---

## HU014: Cadastrar coordenador

**Épico:** Gestão Pedagógica | **Sprint:** Sprint 1

**Como** administrador,
**Quero** cadastrar um coordenador com seus dados de acesso e informações profissionais,
**Para** permitir que ele utilize o sistema de acordo com suas responsabilidades e permissões.

**Critérios de aceitação:**

* O sistema deve permitir ao administrador cadastrar um novo coordenador.
* O formulário deve permitir informar os dados cadastrais e profissionais do coordenador.
* O sistema deve permitir informar os dados necessários para acesso ao sistema.
* O sistema deve validar os campos obrigatórios.
* O sistema deve impedir o cadastro de informações inválidas.
* O sistema deve impedir o cadastro de um coordenador com identificador de acesso já utilizado.
* O coordenador cadastrado deve receber o perfil e as permissões correspondentes à sua função.
* Após o cadastro, o coordenador deve estar disponível para consulta e gerenciamento pelos usuários autorizados.
* Os dados cadastrais e de acesso devem ser armazenados de forma segura, conforme as diretrizes da LGPD.
* Somente usuários com permissão administrativa devem poder cadastrar ou alterar coordenadores.

---

## HU015: Atualizar dados do coordenador

**Épico:** Gestão Pedagógica | **Sprint:** Sprint 1

**Como** administrador,
**Quero** atualizar os dados de um coordenador cadastrado,
**Para** manter suas informações cadastrais, profissionais e de acesso atualizadas.

**Critérios de aceitação:**

* O sistema deve permitir localizar um coordenador cadastrado.
* O sistema deve permitir editar seus dados.
* Os campos obrigatórios devem ser validados durante a atualização.
* O sistema deve impedir o cadastro de informações inválidas.
* O sistema deve permitir atualizar as informações de acesso, quando autorizado.
* Após salvar, os dados atualizados devem ser apresentados nas consultas.
* A alteração deve respeitar as permissões do administrador.
* As alterações devem ser armazenadas de forma segura.

---

## HU016: Gerenciar status do coordenador

**Épico:** Gestão Pedagógica | **Sprint:** Sprint 1

**Como** administrador,
**Quero** alterar o status de um coordenador entre ativo e inativo,
**Para** controlar seu acesso ao sistema sem perder os dados e o histórico relacionados a ele.

**Critérios de aceitação:**

* O sistema deve permitir localizar um coordenador cadastrado.
* O sistema deve permitir visualizar se o coordenador está ativo ou inativo.
* O sistema deve permitir inativar um coordenador ativo.
* O sistema deve solicitar confirmação antes da inativação.
* Um coordenador inativado não deve conseguir acessar o sistema.
* O sistema deve permitir consultar coordenadores inativados para usuários com permissão administrativa.
* O sistema deve permitir reativar um coordenador anteriormente inativado.
* O sistema deve solicitar confirmação antes da reativação.
* Após a reativação, o coordenador deve poder acessar o sistema novamente.
* As permissões do coordenador devem ser restauradas conforme seu perfil.
* Os dados cadastrais e profissionais do coordenador devem ser preservados durante a alteração de status.
* Os registros associados ao coordenador devem ser preservados.
* A alteração de status não deve excluir documentos ou históricos relacionados ao coordenador.
* O sistema deve atualizar o status do coordenador para ativo ou inativo conforme a ação realizada.
* O coordenador reativado deve voltar a aparecer na consulta de coordenadores ativos.
* Somente usuários com permissão administrativa devem poder alterar o status de coordenadores.

---

## HU017: Preservar histórico dos estudantes

**Épico:** Armazenamento e Acessibilidade | **Sprint:** Sprint 1

**Como** instituição de ensino,  
**Quero** manter os dados dos estudantes armazenados após sua conclusão,  
**Para** preservar seu histórico para consultas futuras.

**Critérios de aceitação:**  
* Os dados do estudante devem permanecer armazenados por até 5 anos após sua conclusão.  
* As informações devem continuar disponíveis para usuários autorizados durante esse período.  
* O armazenamento deve preservar a integridade dos dados.  
* Os dados devem ser protegidos conforme a LGPD.

---

## HU018: Utilizar o sistema em diferentes dispositivos

**Épico:** Armazenamento e Acessibilidade | **Sprint:** Sprint 1

**Como** usuário,  
**Quero** acessar o sistema em computadores e dispositivos móveis,  
**Para** utilizar suas funcionalidades independentemente do dispositivo.

**Critérios de aceitação:**  
* As principais funcionalidades devem funcionar em computadores.  
* As principais funcionalidades devem funcionar em dispositivos móveis.  
* A interface deve se adaptar aos diferentes tamanhos de tela.  
* Os dados devem permanecer consistentes independentemente do dispositivo utilizado.

---

## HU019: Utilizar recursos de acessibilidade

**Épico:** Armazenamento e Acessibilidade | **Sprint:** Sprint 1

**Como** usuário com necessidade de acessibilidade,  
**Quero** utilizar recursos de acessibilidade digital,  
**Para** conseguir utilizar o sistema de forma adequada.

**Critérios de aceitação:**  
* A interface deve disponibilizar recursos de acessibilidade digital.  
* Os recursos devem estar disponíveis nas principais funcionalidades do sistema.  
* Os elementos da interface devem ser apresentados de forma clara e compreensível.  
* A acessibilidade deve ser considerada nas funcionalidades de consulta e agrupamento de estudantes.

---

## HU020: Gerenciar professores (CRUD)

**Épico:** Gestão Pedagógica | **Sprint:** Sprint 2

**Como** administrador,  
**Quero** cadastrar, consultar, atualizar e inativar professores,  
**Para** manter suas informações atualizadas e controlar seu acesso ao sistema, preservando seus dados e histórico.

**Critérios de aceitação:**

* O sistema deve permitir ao administrador cadastrar um novo professor.
* O formulário deve permitir informar os dados cadastrais, profissionais e de acesso do professor.
* O sistema deve validar os campos obrigatórios e impedir o salvamento de informações inválidas.
* O sistema deve impedir o uso de um identificador de acesso já utilizado por outro usuário.
* O professor cadastrado deve receber o perfil e as permissões correspondentes à sua função, conforme a HU013.
* O professor deve ser cadastrado inicialmente com status ativo.
* Após o cadastro, o professor deve estar disponível para consulta e gerenciamento.
* O sistema deve permitir listar professores e realizar buscas por nome ou identificador de acesso.
* O sistema deve permitir filtrar professores por status ativo ou inativo.
* O sistema deve permitir visualizar os dados e o status do professor, sem exibir sua senha.
* O sistema deve informar quando nenhum professor for encontrado.
* O sistema deve permitir editar os dados cadastrais e profissionais do professor.
* O sistema deve permitir atualizar as informações de acesso, quando autorizado.
* Os campos obrigatórios e a duplicidade do identificador de acesso devem ser validados durante a atualização.
* Após salvar, os dados atualizados devem ser apresentados nas consultas.
* A edição deve preservar o perfil, o status e os registros associados ao professor.
* A exclusão deve ocorrer por meio da inativação do professor, sem remover definitivamente seu cadastro.
* O sistema deve solicitar confirmação antes da inativação.
* Um professor inativado não deve conseguir acessar o sistema.
* O sistema deve permitir consultar e reativar professores inativados, solicitando confirmação antes da reativação.
* Após a reativação, o professor deve poder acessar o sistema novamente, conforme as permissões de seu perfil.
* A alteração de status deve preservar os dados, registros, documentos e históricos relacionados ao professor.
* Somente usuários com permissão administrativa devem poder gerenciar os cadastros de professores.
* A consulta dos dados de contato pelo coordenador, prevista na HU011, deve permanecer disponível conforme suas permissões, sem conceder acesso ao gerenciamento do cadastro.
* Os dados cadastrais e de acesso devem ser armazenados de forma segura.
* A interface deve ser intuitiva, acessível e funcionar em computadores e dispositivos móveis.

---

## HU021: Gerenciar equipe multidisciplinar (CRUD)

**Épico:** Gestão Pedagógica | **Sprint:** Sprint 2

**Como** administrador,  
**Quero** cadastrar, consultar, atualizar e inativar profissionais da equipe multidisciplinar,  
**Para** manter suas informações atualizadas e controlar seu acesso ao sistema, preservando seus dados e histórico.

**Critérios de aceitação:**

* O sistema deve permitir ao administrador cadastrar individualmente os profissionais da equipe multidisciplinar.
* O formulário deve permitir informar os dados cadastrais, profissionais e de acesso de cada profissional.
* O sistema deve permitir informar a área de atuação ou especialidade do profissional.
* Cada profissional deve possuir suas próprias credenciais de acesso.
* O sistema deve validar os campos obrigatórios e impedir o salvamento de informações inválidas.
* O sistema deve impedir o uso de um identificador de acesso já utilizado por outro usuário.
* O profissional cadastrado deve receber o perfil EQUIPE_MULTIDISCIPLINAR e as permissões correspondentes à sua função, conforme a HU013.
* O profissional deve ser cadastrado inicialmente com status ativo.
* Após o cadastro, o profissional deve estar disponível para consulta e gerenciamento.
* O sistema deve permitir listar profissionais e realizar buscas por nome ou identificador de acesso.
* O sistema deve permitir filtrar profissionais por área de atuação ou especialidade e por status ativo ou inativo.
* O sistema deve permitir visualizar os dados e o status do profissional, sem exibir sua senha.
* O sistema deve informar quando nenhum profissional for encontrado.
* O sistema deve permitir editar os dados cadastrais e profissionais, incluindo a área de atuação ou especialidade.
* O sistema deve permitir atualizar as informações de acesso, quando autorizado.
* Os campos obrigatórios e a duplicidade do identificador de acesso devem ser validados durante a atualização.
* Após salvar, os dados atualizados devem ser apresentados nas consultas.
* A edição deve preservar o perfil, o status e os registros associados ao profissional.
* A exclusão deve ocorrer por meio da inativação do profissional, sem remover definitivamente seu cadastro.
* O sistema deve solicitar confirmação antes da inativação.
* Um profissional inativado não deve conseguir acessar o sistema.
* O sistema deve permitir consultar e reativar profissionais inativados, solicitando confirmação antes da reativação.
* Após a reativação, o profissional deve poder acessar o sistema novamente, conforme as permissões de seu perfil.
* A alteração de status deve preservar os dados, registros, documentos e históricos relacionados ao profissional.
* Somente usuários com permissão administrativa devem poder gerenciar os cadastros da equipe multidisciplinar.
* Os dados cadastrais e de acesso devem ser armazenados de forma segura.
* A interface deve ser intuitiva, acessível e funcionar em computadores e dispositivos móveis.

---

