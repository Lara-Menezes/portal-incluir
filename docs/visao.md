# Visão do Produto — Portal Incluir

## Problema

Atualmente, as informações relacionadas ao acompanhamento de estudantes com deficiência ou necessidades educacionais específicas estão distribuídas entre documentos Word, arquivos PDF, Google Drive, SUAP e diferentes meios de comunicação.

Essa descentralização dificulta a localização e o compartilhamento das informações, provoca retrabalho, reduz a padronização dos documentos e torna mais lento o acompanhamento pedagógico e assistencial.

Também existe dificuldade para reunir, em um único local, dados cadastrais, entrevistas, diagnósticos, atendimentos, adaptações pedagógicas, planos de ação, Planos Educacionais Individualizados (PEI) e documentos relacionados aos estudantes.

## Solução proposta

O **Portal Incluir** é uma plataforma digital de inclusão e acompanhamento educacional destinada à centralização, organização e consulta das informações dos estudantes atendidos.

A plataforma permitirá que usuários autorizados registrem e consultem informações, acompanhem o histórico dos estudantes, produzam documentos estruturados e acessem dados relevantes de acordo com as permissões de cada perfil.

A solução busca substituir processos manuais e descentralizados por um ambiente único, organizado, seguro, acessível e adequado ao trabalho dos profissionais envolvidos no acompanhamento educacional.

## Objetivo geral

Desenvolver uma plataforma digital para centralizar e organizar as informações relacionadas ao acompanhamento de estudantes com deficiência ou necessidades educacionais específicas, facilitando o trabalho dos coordenadores, professores e equipes de apoio.

## Objetivos específicos

- centralizar informações atualmente distribuídas em diferentes ferramentas;
- reduzir a produção manual e repetitiva de documentos;
- padronizar o registro de entrevistas, atendimentos e acompanhamentos;
- facilitar a consulta ao histórico do estudante;
- apoiar a elaboração de planos de ação e PEIs;
- melhorar a comunicação entre os profissionais envolvidos;
- permitir a geração e exportação de documentos e relatórios;
- garantir que cada usuário acesse somente as informações autorizadas;
- manter rastreabilidade das operações realizadas no sistema;
- oferecer uma interface responsiva e acessível.

## Público-alvo

### Usuários diretos

- coordenador responsável pelo acompanhamento dos estudantes;
- substituto do coordenador;
- professores;
- equipe de apoio ou equipe multidisciplinar;
- profissionais da COPED e da CAEST;
- gestão de Tecnologia da Informação.

### Beneficiários indiretos

- estudantes com deficiência ou necessidades educacionais específicas;
- familiares e responsáveis;
- comunidade acadêmica;
- gestão institucional.

## Principais necessidades dos usuários

### Coordenador

- cadastrar e manter os dados dos estudantes;
- registrar entrevistas e acompanhamentos;
- acompanhar planos de ação;
- elaborar e consultar PEIs;
- organizar documentos;
- gerar relatórios;
- localizar rapidamente as informações necessárias.

### Professor

- consultar as informações pedagógicas autorizadas;
- registrar adaptações pedagógicas;
- acompanhar objetivos e estratégias definidos para o estudante.

### Equipe de apoio

- registrar atendimentos e observações;
- inserir informações multidisciplinares;
- colaborar com a elaboração dos planos de ação.

### Gestão de TI

- administrar usuários;
- definir e manter os perfis de acesso;
- acompanhar aspectos técnicos e de segurança;
- garantir a disponibilidade e a manutenção do sistema.

## Impacto esperado

- reduzir o tempo necessário para localizar informações;
- diminuir o retrabalho na produção de documentos;
- melhorar a organização dos registros;
- facilitar o acompanhamento pedagógico e multidisciplinar;
- padronizar documentos e procedimentos;
- melhorar a comunicação entre os profissionais envolvidos;
- apoiar a tomada de decisões;
- aumentar a segurança e a rastreabilidade das informações;
- contribuir para um acompanhamento educacional mais eficiente e inclusivo.

## Solução em alto nível

O Portal Incluir será uma aplicação web responsiva na qual usuários autorizados poderão:

- entrar com credenciais institucionais;
- acessar funções conforme seu perfil;
- cadastrar e atualizar estudantes;
- consultar o perfil de um estudante;
- buscar estudantes por nome, turma, curso ou situação;
- filtrar estudantes segundo critérios autorizados;
- registrar entrevistas;
- registrar atendimentos e acompanhamentos;
- registrar adaptações pedagógicas;
- elaborar planos de ação;
- elaborar e consultar PEIs;
- organizar documentos associados ao estudante;
- visualizar o histórico em uma linha do tempo;
- gerar e exportar documentos e relatórios;
- consultar indicadores e notificações em um dashboard;
- acompanhar registros de auditoria, conforme o perfil.

## Funcionalidades previstas para o MVP

O MVP do Portal Incluir deverá priorizar:

1. autenticação dos usuários;
2. controle de acesso por perfil;
3. dashboard do coordenador;
4. cadastro e gerenciamento de estudantes;
5. busca e filtragem de estudantes;
6. visualização do perfil do estudante;
7. registro de entrevistas e acompanhamentos;
8. organização de documentos;
9. histórico ou linha do tempo do estudante;
10. auditoria das principais operações;
11. interface responsiva para desktop e dispositivos móveis.

A inclusão de uma funcionalidade nesta lista não elimina a necessidade de refinamento, priorização e validação no backlog.

## Requisitos transversais

O produto deverá considerar:

- privacidade e proteção dos dados pessoais;
- conformidade com a LGPD;
- controle de acesso por perfil;
- registro de ações relevantes para auditoria;
- segurança no armazenamento e na transmissão das informações;
- acessibilidade da interface;
- navegação por teclado;
- contraste e legibilidade;
- adaptação para dispositivos móveis;
- integridade e disponibilidade das informações;
- uso de dados fictícios durante desenvolvimento, testes e demonstrações.

## O que este produto não é

- Não é um sistema acadêmico completo.
- Não substitui integralmente o SUAP.
- Não é uma plataforma pública de consulta.
- Não oferece acesso irrestrito às informações dos estudantes.
- Não é uma rede social ou plataforma de comunicação aberta.
- Não é um prontuário médico ou sistema destinado à realização de diagnósticos clínicos.
- Não permite que qualquer professor consulte todas as informações cadastradas.
- Não deve utilizar dados pessoais reais em protótipos ou ambientes de desenvolvimento.
- Não substitui a análise e a atuação dos profissionais responsáveis pelo acompanhamento.

## Premissas

- Os usuários possuirão credenciais previamente cadastradas.
- Cada usuário terá um perfil com permissões específicas.
- O coordenador será o principal responsável pela gestão dos estudantes.
- Professores e equipes de apoio terão acesso limitado conforme suas atribuições.
- Os documentos estarão vinculados ao perfil de cada estudante.
- As operações sensíveis deverão possuir rastreabilidade.
- As decisões sobre coleta, acesso, retenção e descarte dos dados deverão seguir as orientações institucionais.

## Restrições e pontos que precisam de validação

Os seguintes pontos ainda devem ser confirmados com o cliente e com a instituição:

- dados obrigatórios do cadastro do estudante;
- informações que podem ser consultadas por cada perfil;
- procedimento para inclusão, correção e inativação de registros;
- regras para acesso a diagnósticos e laudos;
- modelos de PEI, plano de ação e demais documentos;
- política de retenção e descarte;
- necessidade e forma de integração com o SUAP;
- envio de notificações por e-mail;
- forma de participação dos pais ou responsáveis;
- infraestrutura de hospedagem e armazenamento;
- processo de aprovação dos documentos gerados.

## Telas iniciais

A visão inicial do produto contempla:

- Login;
- Dashboard do coordenador;
- Gerenciar estudantes;
- Perfil do estudante;
- versões responsivas para dispositivos móveis.

O protótipo está disponível no Figma:

[Visualizar protótipo do Portal Incluir](https://www.figma.com/design/vFjSKJomALDbPFoxTb5RvQ/Prototipa%C3%A7%C3%A3o---Portal-Incluir?m=auto&t=b3gqbFZ6KjZkovY8-6)

## Validação da visão

A visão inicial foi construída a partir de entrevistas e reuniões de levantamento realizadas com o cliente.

Posteriormente, os requisitos, casos de uso, escopo e protótipos foram apresentados para avaliação. O cliente confirmou que os materiais representam adequadamente as necessidades identificadas e considerou a etapa aceita para prosseguimento.

Os detalhes do processo estão registrados em:

- [Validação do protótipo](validacao-prototipo.md)
- [Casos de uso](especificacao-funcional/casos-de-uso.md)
- [Matriz de rastreabilidade](especificacao-funcional/matriz-rastreabilidade.md)

## Resumo da visão

> Para coordenadores, professores e equipes de apoio que precisam acompanhar estudantes com deficiência ou necessidades educacionais específicas, o Portal Incluir é uma plataforma digital que centraliza registros, documentos e informações de acompanhamento. Diferentemente do processo atual, distribuído entre Word, PDF, Google Drive, SUAP e comunicações isoladas, o Portal Incluir oferece um ambiente único, organizado, acessível e controlado por perfis de acesso.
