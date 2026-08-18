# Entrevistas: Portal Incluir

As entrevistas foram realizadas ao longo das etapas de levantamento, análise e refinamento de requisitos do projeto. Todas as reuniões ocorreram com a coordenadora da CLAI, com o objetivo de compreender as necessidades do setor, identificar dificuldades do processo atual, validar requisitos funcionais e não funcionais, definir prioridades para o MVP e 
orientar as decisões relacionadas à arquitetura, prototipação, interface e segurança do sistema.
---

## Entrevista 1: Coordenadora Luzia (CLAI)

**Entrevistado:** Luzia (coordenadora da CLAI)  
**Entrevistador:** Equipe do projeto  
**Data:** 15/04/2026  
**Duração aproximada:** 45 minutos

### Objetivo da reunião

Levantar as necessidades iniciais para o desenvolvimento de um sistema de acompanhamento de estudantes com deficiência, buscando otimizar o fluxo de informações entre coordenação, professores e equipes de apoio.

### Roteiro e respostas

**1. Por que você precisa deste sistema?**

> Para otimizar e agilizar a organização de materiais e documentos utilizados na divulgação de informações sobre estudantes com deficiência para professores e profissionais da educação.

**2. Como o software pode mudar a realidade do trabalho atual?**

> Vai facilitar a produção de materiais de apoio para os professores, melhorar a sistematização das informações dos estudantes, permitir agrupamentos por tipo de deficiência e possibilitar consultas rápidas sem precisar acessar Google Drive ou SUAP.

**3. Quem vai utilizar o sistema e quem será impactado?**

> O sistema será utilizado pelo coordenador, pelo substituto do coordenador e pelas equipes de acompanhamento dos estudantes, incluindo COPED e CAEST. Os professores serão impactados diretamente por receberem as informações pelo sistema. Os estudantes não terão acesso, mas deverá existir um canal de comunicação com os responsáveis.

**4. Qual é o principal problema que precisa ser resolvido?**

> A falta de integração das informações dos estudantes. Hoje os dados ficam espalhados em diferentes locais, dificultando consultas, geração de relatórios e compartilhamento com os professores.

**5. Como esse processo funciona atualmente?**

> Os documentos são produzidos no Word, convertidos em PDF, assinados e enviados pelo SUAP. Todo o processo é manual, repetitivo e pouco padronizado. Um documento de anamnese digital, com opções de marcação, facilitaria bastante o trabalho.

**6. Existe alguma referência de sistema que gostaria de seguir?**

> Não há uma referência específica.

**7. O sistema deve gerar relatórios exportáveis?**

> Sim. Os relatórios devem poder ser exportados em PDF e Excel.

**8. Como o sistema deverá ser acessado?**

> Deve ser responsivo para celulares, mas com prioridade para o uso em computadores.

**9. Será necessário um perfil administrativo?**

> Sim. A Gestão de TI do IFPB deverá possuir um perfil administrativo com permissões ampliadas.

**10. Por quanto tempo os dados devem ser mantidos?**

> Os dados deverão ser armazenados por cinco anos após a conclusão do vínculo do estudante.

**11. Por que esse sistema é relevante?**

> Porque facilita o fluxo de trabalho entre coordenação e professores, centraliza informações importantes e agiliza processos administrativos e pedagógicos.

**12. Qual é o público-alvo da plataforma?**

> Professores e coordenadores de curso.

### Requisitos adicionais levantados

Durante a conversa também foram identificadas necessidades complementares para o sistema:

- Acessibilidade para todos os usuários.
- Cadastro individual de estudantes com histórico de entrevistas e procedimentos.
- Visualização centralizada de todos os documentos em um único local.
- Modelos padronizados de documentos e formulários.
- Registro de informações como nome, data de nascimento, curso, tipo de deficiência, escolaridade e dificuldades apresentadas.
- Segurança para informações sensíveis e conformidade com a LGPD.
- Canal de comunicação entre coordenação, professores e responsáveis pelos estudantes.
- Relatórios por tipo de deficiência, adaptações realizadas e necessidades específicas.

### Observações da equipe

- Confirmou que o maior problema atual é a descentralização das informações entre Word, Google Drive e SUAP.
- Identificou como requisito prioritário a centralização dos dados dos estudantes em um único ambiente.
- Levantou a necessidade de perfis de acesso diferenciados para coordenadores, equipes de acompanhamento e Gestão de TI.
- Reforçou que os estudantes não terão acesso ao sistema, exigindo controle rigoroso de permissões.
- Destacou a importância da acessibilidade e da conformidade com a LGPD devido ao tratamento de informações sensíveis.
- Indicou como funcionalidades prioritárias a geração automática de documentos, formulários padronizados e relatórios exportáveis.

---

## Entrevista 2: Coordenadora Luzia (CLAI)

**Entrevistado:** Luzia (coordenadora da CLAI)  
**Entrevistador:** Equipe do projeto  
**Data:** 08/05/2026  
**Duração aproximada:** 40 minutos

### Objetivo da reunião

Aprofundar o entendimento sobre as funcionalidades do sistema, definir os perfis de usuários, estabelecer regras de visualização de informações e detalhar os mecanismos de comunicação e gestão documental.

### Roteiro e respostas

**1. O que exatamente cada perfil poderá visualizar?**

> O coordenador terá acesso a todas as informações do estudante, incluindo dados pedagógicos, avaliação psicossocial, adaptações realizadas pelos professores, informações sensíveis, contato dos responsáveis e perfil completo. Os professores visualizarão apenas as informações necessárias para a adaptação pedagógica. A equipe de suporte terá acesso apenas às informações relacionadas ao acompanhamento diário e ao suporte prestado ao estudante.

**2. Quem poderá visualizar dados sensíveis?**

> Apenas os coordenadores e a equipe multidisciplinar.

**3. O que cada perfil poderá fazer dentro do sistema?**

> O coordenador poderá inserir e gerenciar informações gerais. Os professores poderão registrar atividades e adaptações pedagógicas. A equipe de suporte poderá consultar informações e registrar atendimentos ou necessidades de acompanhamento.

**4. Como funcionará a comunicação entre os usuários?**

> O sistema deverá possuir um chat privado e um espaço em formato de comunidade, permitindo comentários entre os participantes.

**5. Onde ficará o chat?**

> O chat ficará integrado ao próprio sistema.

**6. Como serão enviadas as notificações?**

> As notificações serão enviadas por e-mail.

**7. Quais documentos precisarão ser anexados?**

> O sistema deverá armazenar documentos como legislação, PEI, PEA, plano de ação e modelos de relatório.

**8. Será necessária assinatura eletrônica?**

> Não haverá necessidade de assinatura eletrônica.

**9. Quais relatórios o sistema deverá gerar?**

> Relatórios sobre laudos, diagnósticos, especificidades dos estudantes, necessidades de adaptação, tipos de adaptações realizadas e agrupamentos por tipo de deficiência.

**10. Existe alguma outra funcionalidade importante?**

> O sistema deverá permitir consultar rapidamente o perfil de cada estudante, reunindo informações de diagnóstico, suporte profissional e necessidades de adaptação pedagógica.

### Observações da equipe

- Confirmou a necessidade de controle rigoroso de permissões por perfil de usuário.
- Definiu que apenas coordenadores e equipe multidisciplinar poderão acessar dados sensíveis.
- Validou a necessidade de um chat interno integrado ao sistema, incluindo comunicação privada e comunitária.
- Reforçou a importância de um repositório centralizado para documentos institucionais.
- Confirmou que notificações por e-mail são suficientes para a comunicação do sistema.
- Consolidou como prioridade a consulta individual do estudante com todas as informações relevantes organizadas em um único ambiente.

---

## Entrevista 3: Coordenadora Luzia (CLAI)

**Entrevistado:** Luzia (coordenadora da CLAI)  
**Entrevistador:** Equipe do projeto  
**Data:** 12/05/2026  
**Duração aproximada:** 20 minutos

### Objetivo da reunião

Aprofundar o entendimento dos requisitos funcionais e não funcionais, identificar riscos críticos para o sistema e definir prioridades para o desenvolvimento da primeira versão da plataforma.

### Roteiro e respostas

**1. Qual seria o pior problema se o sistema não acontecesse do jeito esperado?**

> A maior preocupação é a perda das informações armazenadas, como dados dos estudantes, necessidades específicas, atividades realizadas e documentos produzidos. Como o sistema será uma base central de informações, qualquer falha que cause perda desses dados teria um grande impacto.

**2. O que mais preocupa em relação ao sistema?**

> O acesso por pessoas não autorizadas. Como o sistema armazenará informações sensíveis, será necessário priorizar autenticação segura, controle de acesso, criptografia e conformidade com a LGPD.

**3. Qual tarefa atualmente consome mais tempo?**

> A elaboração do Plano de Ação e do PEI (Plano Educacional Individualizado), pois exige reunir informações da família, da equipe multidisciplinar e dos professores.

**4. Qual processo deveria ser automatizado primeiro?**

> A elaboração do Plano de Ação, porque essa atividade é realizada exclusivamente pela CLAI, tornando sua automação mais simples e viável.

**5. Existe alguma funcionalidade que precisa ser muito rápida?**

> O cadastro dos estudantes e o registro das informações iniciais precisam ser rápidos. Além disso, seria importante disponibilizar materiais internos para consulta da coordenação desde a primeira versão.

**6. O sistema deve integrar diretamente com o SUAP?**

> Seria interessante, mas não é considerado viável devido às limitações institucionais e à dependência da DGTI. O sistema deve funcionar de forma independente, permitindo cadastro manual dos estudantes e utilização de informações obtidas por outros meios, como formulários de pré-matrícula.

**7. O sistema deve possuir integração com recursos de acessibilidade?**

> Sim. A integração com a API do VLibras é desejável, assim como futuras soluções voltadas para acessibilidade de pessoas com deficiência visual.

**8. O que deve acontecer quando faltar alguma informação obrigatória no cadastro?**

> O sistema deve exibir notificações de atenção, indicando claramente quais informações precisam ser revisadas ou preenchidas posteriormente.

**9. Existe uma ordem obrigatória das etapas do processo?**

> Sim. Primeiro ocorre o cadastro dos dados pessoais e dos diagnósticos do estudante, depois a elaboração do Plano de Ação, seguida da construção do PEI e, por fim, dos relatórios de acompanhamento.

**10. Como deve ser a identidade visual da plataforma?**

> A interface deve manter referências visuais do IFPB, utilizando predominantemente branco com destaques em verde e vermelho, além de elementos relacionados à inclusão e às diretrizes visuais da CLAI.

**11. Como será realizado o login dos usuários?**

> O acesso poderá ocorrer utilizando matrícula funcional e senha ou e-mail institucional e senha. A integração direta de autenticação com o SUAP não será necessária.

**12. Quem será responsável pelos cadastros?**

> O coordenador realizará seu próprio cadastro e será responsável pelo cadastro e gerenciamento das informações dos estudantes. Professores e demais usuários farão seus próprios cadastros para acessar o sistema.

**13. Os pais terão acesso direto ao sistema?**

> Não inicialmente. Eles serão beneficiários indiretos das informações e materiais produzidos pela plataforma, sem acesso direto ao sistema.

### Observações da equipe

- Confirmou que segurança da informação e prevenção de perda de dados são prioridades máximas do projeto.
- Validou que a primeira versão deve priorizar cadastro de estudantes, registro de informações e automação do Plano de Ação.
- Reforçou que o sistema deve funcionar independentemente do SUAP, evitando barreiras institucionais.
- Identificou como requisito importante a integração com recursos de acessibilidade, especialmente VLibras.
- Definiu um fluxo obrigatório entre cadastro, Plano de Ação, PEI e relatórios.
- Consolidou o uso da identidade visual institucional do IFPB como referência para a interface da plataforma.
- Confirmou que notificações para dados pendentes e autenticação por e-mail institucional ou matrícula são requisitos importantes para usabilidade e segurança.

---

## Entrevista 4: Coordenadora Luzia (CLAI)

**Entrevistado:** Luzia (coordenadora da CLAI)  
**Entrevistador:** Equipe do projeto  
**Data:** 16/06/2026  
**Duração aproximada:** 40 minutos

### Objetivo da reunião

Levantar informações para apoiar a elaboração da prototipação, matriz de rastreabilidade, casos de uso resumidos, definições arquiteturais e projeto da interface, além de definir o escopo do Produto Mínimo Viável (MVP).

### Roteiro e respostas

**1. Se a primeira versão tivesse apenas três funcionalidades, quais seriam?**

> Para o coordenador, as prioridades seriam cadastrar avaliações diagnósticas e anamneses, gerar modelos de Plano de Ação e PEI e administrar perfis de acesso. Para os professores, registrar adaptações acadêmicas, consultar informações dos estudantes e gerar conteúdo acadêmico. A equipe de suporte deverá registrar atendimentos, consultar informações e gerar relatórios específicos. Os responsáveis deverão apenas consultar informações.

**2. O que obrigatoriamente precisa funcionar no MVP?**

> Todas essas funcionalidades essenciais precisam estar presentes na primeira versão.

**3. Quais funcionalidades podem ficar para versões futuras?**

> O chat interno, melhorias na base de dados integrada e compatibilidade com servidores institucionais podem ser implementados posteriormente.

**4. Qual é o principal problema que o sistema precisa resolver imediatamente?**

> Centralizar todas as informações do estudante em uma única base de dados, permitindo acompanhar sua evolução ao longo do tempo e identificar informações pendentes.

**5. O que o coordenador deve visualizar ao entrar no sistema?**

> Uma lista dos estudantes acompanhados, acesso rápido ao perfil de cada um, opção para cadastrar informações, modelos de documentos e notificações sobre atualizações feitas por professores ou equipe multidisciplinar.

**6. Quais informações devem aparecer no dashboard?**

> Manual do usuário, legislação, modelos de documentos e links relacionados à LGPD.

**7. Quais telas são indispensáveis para o MVP?**

> A tela de cadastro dos estudantes e a tela de consulta das informações dos estudantes.

**8. Como deve funcionar a busca por estudantes?**

> Deve permitir localizar estudantes pelos filtros de nome, matrícula, curso e ano.

**9. O que deve aparecer na listagem e no perfil do estudante?**

> A listagem deve exibir nome completo, turma, curso, ano e diagnóstico. No perfil, o Plano de Ação deve aparecer primeiro, acompanhado de contato, responsáveis, dificuldades, necessidades de atendimento e adaptações.

**10. Quais documentos devem ser gerados automaticamente?**

> Plano de Ação, modelo do PEI e relatório de acompanhamento individualizado. Já laudos, relatórios médicos e PEA serão apenas anexados.

**11. Como os relatórios devem ser exportados?**

> Os documentos devem ser exportados em PDF e em formato editável semelhante ao Word. A exportação em Excel será utilizada apenas para dados consolidados, como quantidade de estudantes.

**12. Como ficam as permissões dos usuários?**

> Coordenador e substituto possuem acesso total. Professores podem consultar e editar informações acadêmicas. A equipe de apoio registra atendimentos, preferencialmente com possibilidade de salvar em rascunho para aprovação do coordenador. Os responsáveis terão acesso apenas aos relatórios sobre evolução e adaptações.

**13. O chat interno faz parte do MVP?**

> Existe necessidade de um chat interno, mas ele não é prioridade para a primeira versão.

**14. Quais requisitos de acessibilidade são indispensáveis?**

> O sistema deve oferecer suporte a leitores de tela, modo de alto contraste e funcionamento em computador utilizando mouse e teclado.

**15. Como deve ser a identidade visual da plataforma?**

> A interface deve seguir referências visuais do IFPB, utilizando predominância de verde e branco, mantendo uma aparência acolhedora relacionada à inclusão e melhorando elementos como a barra lateral em relação ao portal atual do IFPB.

**16. Existe alguma preocupação adicional ou resultado esperado?**

> O sistema deve priorizar segurança, recuperação de senha e proteção contra acessos indevidos. O objetivo é que o Portal Incluir se torne uma ferramenta de uso cotidiano pelos coordenadores, professores e equipes de apoio, com potencial de expansão para outros campi do IFPB.

### Observações da equipe

- Confirmou o escopo do MVP, priorizando cadastro de estudantes, geração automática de documentos e consulta das informações.
- Validou que o dashboard deve reunir recursos institucionais e materiais de apoio em um único local.
- Definiu filtros essenciais para busca rápida dos estudantes e organização semelhante ao SUAP.
- Reforçou que o chat interno é desejável, mas ficará para versões futuras.
- Consolidou requisitos importantes de acessibilidade, como leitores de tela e alto contraste.
- Confirmou que a identidade visual deve seguir o padrão institucional do IFPB, com foco em uma interface acolhedora e voltada para inclusão.
- Indicou que o sucesso do projeto será medido pela adoção cotidiana da plataforma e pela possibilidade de expansão para outros campi.

---

## Entrevista 5: Coordenadora Luzia (CLAI)

**Entrevistado:** Luzia (coordenadora da CLAI)  
**Entrevistador:** Equipe do projeto  
**Data:** 19/06/2026  
**Duração aproximada:** 30 minutos

### Objetivo da reunião

Validar definições técnicas sobre arquitetura do sistema, tecnologia, autenticação, notificações, módulos principais e estrutura de cadastro dos usuários.

### Roteiro e respostas

**1. Onde o sistema será hospedado?**

> A preferência é utilizar servidores próprios do IFPB, podendo ficar hospedado em máquinas do próprio setor.

**2. O sistema será web, mobile ou ambos?**

> O sistema deverá ser acessível pela web e também funcionar em dispositivos móveis.

**3. Existe preferência por alguma tecnologia?**

> A preferência é utilizar Python, mas a equipe pode escolher a tecnologia que considerar mais adequada para o desenvolvimento.

**4. O sistema deverá enviar notificações?**

> Sim. Deve enviar notificações por e-mail, incluindo notificações relacionadas à auditoria, como o cadastro de novos estudantes.

**5. Será necessário funcionamento offline?**

> Não. O sistema dependerá de acesso à internet.

**6. Existem etapas obrigatórias no acompanhamento do estudante?**

> Sim. O fluxo obrigatório inclui cadastro do estudante, acompanhamento do PEI, elaboração do Plano de Ação e geração de relatórios.

**7. Existem procedimentos diferentes para cada tipo de deficiência?**

> Sim. O PEI deverá variar conforme o tipo de deficiência do estudante.

**8. Quais módulos principais o sistema deve possuir?**

> Os módulos essenciais são cadastro, informações dos estudantes, documentação e segurança, incluindo auditoria.

**9. O sistema terá autenticação própria?**

> Sim. O sistema deverá possuir um módulo próprio de autenticação.

**10. Quais partes do sistema possuem maior dependência entre si?**

> A documentação depende diretamente das informações cadastradas dos estudantes, já que os documentos serão gerados a partir desses dados.

**11. Os usuários serão cadastrados em uma tela específica?**

> Sim. O sistema deverá possuir uma tela separada exclusivamente para o cadastro dos usuários.

**12. Haverá integração com sistemas externos?**

> Não. O sistema funcionará de forma independente, sem troca direta de informações com outros sistemas.

### Observações da equipe

- Confirmou a decisão de utilizar hospedagem em infraestrutura própria do IFPB.
- Validou que o sistema deverá funcionar tanto em computadores quanto em dispositivos móveis.
- Reforçou a preferência pelo uso de Python, mantendo flexibilidade tecnológica para a equipe.
- Definiu a necessidade de autenticação própria e notificações por e-mail com foco em auditoria.
- Consolidou os módulos principais do sistema: cadastro, informações, documentação e segurança.
- Confirmou que a geração de documentos depende diretamente dos dados cadastrados dos estudantes.
- Identificou a necessidade de uma tela específica para cadastro de usuários, contribuindo para uma organização mais clara da interface.