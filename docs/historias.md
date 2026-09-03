# Histórias de Usuário: Portal Incluir

As histórias estão organizadas por épico. Cada história tem critérios de aceitação que definem quando ela pode ser considerada concluída.

---

# Gestão de Estudantes

## HU001: Cadastrar estudante

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

# Gestão Pedagógica

## HU004: Registrar acompanhamento pedagógico

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

# Documentação

## HU005: Cadastrar documento PEI

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

# Relatórios

## HU007: Emitir relatório de estudantes

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

# Inclusão e Acessibilidade

## HU008: Agrupar estudantes por deficiência

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

# Consulta de Dados

## HU009: Buscar estudante

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

# Comunicação Institucional

## HU011: Comunicar-se com professores

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

# Segurança e Acesso

## HU013: Controlar acesso por perfil

**Como** administrador,  
**Quero** definir permissões de acesso conforme o perfil do usuário,  
**Para** garantir que cada usuário tenha acesso somente às funcionalidades autorizadas.

**Critérios de aceitação:**  
* O sistema deve possuir diferentes perfis de usuário.  
* Cada perfil deve possuir permissões específicas.  
* O sistema deve impedir que um usuário acesse funcionalidades não autorizadas.  
* O controle de acesso deve ser aplicado às informações dos estudantes.  
* Dados protegidos devem estar disponíveis somente para usuários autorizados.

---

# Gestão de usuários

## HU014: Cadastrar coordenador

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

# Armazenamento e Acessibilidade

## HU017: Preservar histórico dos estudantes

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

**Como** usuário com necessidade de acessibilidade,  
**Quero** utilizar recursos de acessibilidade digital,  
**Para** conseguir utilizar o sistema de forma adequada.

**Critérios de aceitação:**  
* A interface deve disponibilizar recursos de acessibilidade digital.  
* Os recursos devem estar disponíveis nas principais funcionalidades do sistema.  
* Os elementos da interface devem ser apresentados de forma clara e compreensível.  
* A acessibilidade deve ser considerada nas funcionalidades de consulta e agrupamento de estudantes.
