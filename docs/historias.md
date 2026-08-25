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

# Gestão Pedagógica

## HU003: Registrar acompanhamento pedagógico

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

## HU004: Gerar documentos do estudante

**Como** coordenador,  
**Quero** gerar documentos estruturados a partir das informações do estudante,  
**Para** facilitar a organização e padronização da documentação.

**Critérios de aceitação:**  
* O sistema deve permitir selecionar o estudante.  
* O sistema deve utilizar as informações cadastradas para gerar o documento.  
* O documento deve possuir estrutura padronizada.  
* O documento gerado deve apresentar as informações correspondentes ao estudante selecionado.  
* Somente usuários autorizados devem poder acessar os documentos.

---

# Relatórios

## HU005: Emitir relatório de estudantes

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

## HU006: Agrupar estudantes por deficiência

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

## HU007: Buscar estudante

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

## HU008: Visualizar informações centralizadas

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

## HU009: Comunicar-se com professores

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

## HU010: Comunicar-se com pais e responsáveis

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

## HU011: Controlar acesso por perfil

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

# Armazenamento e Acessibilidade

## HU012: Preservar histórico dos estudantes

**Como** instituição de ensino,  
**Quero** manter os dados dos estudantes armazenados após sua conclusão,  
**Para** preservar seu histórico para consultas futuras.

**Critérios de aceitação:**  
* Os dados do estudante devem permanecer armazenados por até 5 anos após sua conclusão.  
* As informações devem continuar disponíveis para usuários autorizados durante esse período.  
* O armazenamento deve preservar a integridade dos dados.  
* Os dados devem ser protegidos conforme a LGPD.

---

## HU013: Utilizar o sistema em diferentes dispositivos

**Como** usuário,  
**Quero** acessar o sistema em computadores e dispositivos móveis,  
**Para** utilizar suas funcionalidades independentemente do dispositivo.

**Critérios de aceitação:**  
* As principais funcionalidades devem funcionar em computadores.  
* As principais funcionalidades devem funcionar em dispositivos móveis.  
* A interface deve se adaptar aos diferentes tamanhos de tela.  
* Os dados devem permanecer consistentes independentemente do dispositivo utilizado.

---

## HU014: Utilizar recursos de acessibilidade

**Como** usuário com necessidade de acessibilidade,  
**Quero** utilizar recursos de acessibilidade digital,  
**Para** conseguir utilizar o sistema de forma adequada.

**Critérios de aceitação:**  
* A interface deve disponibilizar recursos de acessibilidade digital.  
* Os recursos devem estar disponíveis nas principais funcionalidades do sistema.  
* Os elementos da interface devem ser apresentados de forma clara e compreensível.  
* A acessibilidade deve ser considerada nas funcionalidades de consulta e agrupamento de estudantes.
