# Story Map: Portal Incluir

## Acesso ao mapa

O Story Map do **Portal Incluir** organiza as funcionalidades do sistema a partir das principais atividades realizadas pelos usuários, permitindo visualizar o fluxo de uso, as histórias de usuário e a priorização das funcionalidades para o MVP.

---

## Estrutura do mapa

O mapa está organizado em 3 linhas:

| Linha                          | O que representa                                                          |
| ------------------------------ | ------------------------------------------------------------------------- |
| **Atividades** (linha do topo) | O que o usuário realiza em alto nível dentro do sistema                   |
| **Tarefas** (linha do meio)    | As ações específicas realizadas dentro de cada atividade                  |
| **Histórias** (linhas abaixo)  | As histórias de usuário relacionadas às tarefas, ordenadas por prioridade |

---

## Atividades mapeadas

1. **Acessar o sistema**: Entrar no sistema e acessar funcionalidades de acordo com o perfil e as permissões do usuário.
2. **Gerenciar estudantes**: Cadastrar, atualizar, buscar e consultar informações dos estudantes.
3. **Realizar acompanhamento**: Registrar e consultar informações relacionadas ao acompanhamento pedagógico.
4. **Gerenciar documentação**: Gerar documentos e acessar as informações e documentos associados aos estudantes.
5. **Analisar informações**: Emitir relatórios e organizar estudantes de acordo com suas características.
6. **Realizar comunicação institucional**: Consultar dados de contato de professores, pais e responsáveis para comunicação externa.
7. **Gerenciar usuários**: Cadastrar, consultar, atualizar, inativar e reativar coordenadores, professores e profissionais da equipe multidisciplinar.
8. **Garantir acesso e preservação**: Manter o histórico dos estudantes e garantir que o sistema possa ser utilizado em diferentes dispositivos e com recursos de acessibilidade.

---

## Mapa de atividades, tarefas e histórias

| Atividade                              | Tarefa                             | História                                                    |
| -------------------------------------- | ---------------------------------- |-------------------------------------------------------------|
| **Acessar o sistema**                  | Autenticar com JWT e autorizar por perfil               | **HU013** – Controlar acesso por perfil - Autenticar e autorizar usuários com JWT                     |
| **Gerenciar estudantes**               | Cadastrar estudante                | **HU001** – Cadastrar estudante                             |
|                                        | Atualizar estudante                | **HU002** – Atualizar dados do estudante                    |
|                                        | Gerenciar status do estudante      | **HU003** – Gerenciar status do estudante                   |
|                                        | Buscar estudante                   | **HU009** – Buscar estudante                                |
|                                        | Consultar estudante                | **HU010** – Visualizar informações centralizadas            |
| **Realizar acompanhamento**            | Registrar acompanhamento           | **HU004** – Registrar acompanhamento pedagógico             |
| **Gerenciar documentação**             | Cadastrar PEI                      | **HU005** – Cadastrar documento PEI                         |
|                                        | Cadastrar Plano de Ação            | **HU006** – Cadastrar documento de Plano de Ação            |
|                                        | Consultar histórico e documentos   | **HU010** – Visualizar informações centralizadas            |
| **Analisar informações**               | Emitir relatórios                  | **HU007** – Emitir relatório de estudantes                  |
|                                        | Agrupar estudantes                 | **HU008** – Agrupar estudantes por deficiência              |
| **Realizar comunicação institucional** | Consultar contato de professores   | **HU011** – Comunicar-se com professores                    |
|                                        | Consultar contato de responsáveis  | **HU012** – Comunicar-se com pais e responsáveis            |
| **Gerenciar usuários**                 | Cadastrar coordenador              | **HU014** – Cadastrar coordenador                           |
|                                        | Atualizar coordenador              | **HU015** – Atualizar dados do coordenador                  |
|                                        | Gerenciar status do coordenador    | **HU016** – Gerenciar status do coordenador                 |
| | Gerenciar professores (cadastro, consulta, edição e status) | **HU020** – Gerenciar professores (CRUD) |
| | Gerenciar profissionais da equipe (cadastro, consulta, edição e status) | **HU021** – Gerenciar equipe multidisciplinar (CRUD) |
| **Garantir acesso e preservação**      | Preservar histórico                | **HU017** – Preservar histórico dos estudantes              |
|                                        | Acessar em diferentes dispositivos | **HU018** – Utilizar o sistema em diferentes dispositivos   |
|                                        | Utilizar acessibilidade            | **HU019** – Utilizar recursos de acessibilidade             |
---

# Definição do MVP

O MVP do **Portal Incluir** cobre o fluxo essencial para que a equipe responsável pela inclusão consiga **registrar um estudante, localizar suas informações, acompanhar sua situação pedagógica e manter sua documentação centralizada e protegida**.

## Histórias incluídas no MVP

| História  | Descrição                                     |
|-----------|-----------------------------------------------|
| **HU001** | Cadastrar estudante                           |
| **HU002** | Atualizar dados do estudante                  |
| **HU003** | Gerenciar status do estudante                 |
| **HU004** | Registrar acompanhamento pedagógico           |
| **HU005** | Cadastrar documento PEI                       |
| **HU006** | Cadastrar documento Plano de Ação             |
| **HU009** | Buscar estudante                              |
| **HU010** | Visualizar informações centralizadas          |
| **HU013** | Controlar acesso por perfil - Autenticar e autorizar usuários com JWT                   |
| **HU014** | Cadastrar coordenador                         |
| **HU015** | Atualizar dados do coordenador                |
| **HU016** | Gerenciar status do coordenador               |
| **HU018** | Utilizar o sistema em diferentes dispositivos |
| **HU019** | Utilizar recursos de acessibilidade           |
| **HU020** | Gerenciar professores (CRUD) — Sprint 2 |
| **HU021** | Gerenciar equipe multidisciplinar (CRUD) — Sprint 2 |

Essas histórias formam o núcleo funcional do Portal Incluir. Com elas, usuários autorizados conseguem cadastrar estudantes, manter suas informações atualizadas, gerenciar seus status, registrar acompanhamentos, cadastrar a documentação necessária e consultar as informações de forma centralizada e segura.

O cadastro e gerenciamento individual de professores (**HU020**) e profissionais da equipe multidisciplinar (**HU021**) integram o MVP na **Sprint 2**, com acesso próprio para cada usuário e exclusão lógica por inativação. A implementação deve reutilizar os perfis e a autenticação da HU013, preservando os históricos associados.

O controle de acesso (**HU013**) é considerado essencial desde a primeira versão devido à natureza das informações armazenadas pelo sistema.

Da mesma forma, a utilização em diferentes dispositivos (**HU018**) e os recursos de acessibilidade (**HU019**) devem ser considerados desde o início do desenvolvimento, pois fazem parte da proposta de inclusão e da experiência de utilização do Portal Incluir.

---

## Histórias fora do MVP — Incremento seguinte

| História  | Descrição                            |
|-----------| ------------------------------------ |
| **HU007** | Emitir relatório de estudantes       |
| **HU008** | Agrupar estudantes por deficiência   |
| **HU011** | Comunicar-se com professores         |
| **HU012** | Comunicar-se com pais e responsáveis |
| **HU017** | Preservar histórico dos estudantes   |

Essas funcionalidades complementam o sistema, mas não impedem o funcionamento do fluxo principal do Portal Incluir em sua primeira versão.

Os relatórios e agrupamentos ampliam a capacidade de análise dos dados, enquanto as funcionalidades de comunicação facilitam processos institucionais já realizados por meios externos.

A preservação de longo prazo do histórico deve ser considerada na arquitetura e no armazenamento desde o desenvolvimento inicial, embora sua utilização prática aconteça ao longo do ciclo de vida do sistema.

---

# Critério de MVP

O MVP foi definido com base na pergunta:

> *"O que a equipe responsável pela inclusão precisa fazer no mínimo para que o Portal Incluir gere valor no acompanhamento dos estudantes?"*

A resposta foi:

**Cadastrar o estudante, manter seus dados atualizados, registrar seu acompanhamento pedagógico, gerar sua documentação e consultar todas essas informações de forma centralizada e segura.**

Assim, o fluxo principal do MVP pode ser representado como:

**Acessar com JWT → Gerenciar usuários (administrador) → Cadastrar estudante → Consultar estudante → Atualizar informações → Registrar acompanhamento → Gerar documentação → Consultar histórico**

Funcionalidades complementares, como **relatórios, agrupamentos e apoio à comunicação institucional**, ficam destinadas aos incrementos seguintes.

---

# Visão resumida do Story Map

| Atividade                              | MVP                               | Incremento seguinte |
|----------------------------------------|-----------------------------------|---------------------|
| **Acessar o sistema**                  | HU013                             | —                   |
| **Gerenciar estudantes**               | HU001, HU002, HU003, HU009, HU010 | —                   |
| **Realizar acompanhamento**            | HU004                             | —                   |
| **Gerenciar documentação**             | HU005, HU006                      | —                   |
| **Analisar informações**               | —                                 | HU007, HU008        |
| **Realizar comunicação institucional** | —                                 | HU011, HU012        |
| **Gerenciar usuários**                 | HU014, HU015, HU016, HU020, HU021 | —                   |
| **Garantir acesso e preservação**      | HU018, HU019                      | HU017               |

---

## Resultado esperado do MVP

O administrador também deverá conseguir cadastrar, consultar, editar, inativar e reativar professores e profissionais da equipe multidisciplinar. Cada perfil seguirá somente os passos permitidos pela HU013.

Ao final do MVP, o Portal Incluir deverá permitir que um usuário autorizado consiga realizar o seguinte fluxo:

**1. Acessar o sistema com as permissões correspondentes ao seu perfil.**

**2. Cadastrar um estudante e suas informações necessárias para acompanhamento.**

**3. Buscar e acessar o cadastro de um estudante existente.**

**4. Atualizar as informações do estudante quando necessário.**

**5. Registrar entrevistas, acompanhamentos e procedimentos pedagógicos.**

**6. Gerar documentos estruturados utilizando as informações cadastradas.**

**7. Visualizar, em um único local, os dados, acompanhamentos e documentos relacionados ao estudante.**

Todo esse fluxo deve respeitar os requisitos de **segurança, privacidade, acessibilidade e responsividade** definidos para o Portal Incluir.

---

## Recorte por sprint e relação com o Projects

| Recorte | Histórias |
|---|---|
| Sprint 1 | HU001, HU002, HU003, HU008, HU009, HU010, HU011, HU012, HU013, HU014, HU015, HU016, HU017, HU018, HU019 |
| Sprint 2 | HU004, HU005, HU006, HU020, HU021 |
| Sem sprint definida | HU007 |

Referência: Projects consultado em 15/09/2026. A associação a uma sprint não indica conclusão. MVP e incremento seguinte são recortes de produto preservados do mapa original, com a inclusão de HU020 e HU021 no MVP; não equivalem aos status ou às prioridades MoSCoW do quadro.

As atividades do Story Map representam ações dos usuários. Assim, “Gerenciar usuários” reúne histórias que no Projects estão classificadas no épico “Gestão Pedagógica”.