# ADR 001 — Escolha da Stack Tecnológica

- **Status:** Aceito
- **Data:** 18/08/2026
- **Projeto:** Portal Incluir

---

## Contexto

O Portal Incluir será uma aplicação web destinada à centralização e organização das informações relacionadas ao acompanhamento de estudantes com deficiência ou necessidades educacionais específicas.

A definição da stack tecnológica precisa considerar:

- o conhecimento atual da equipe;
- as tecnologias estudadas durante o curso;
- o prazo disponível para desenvolvimento em Projeto II;
- a necessidade de uma aplicação web responsiva;
- a manipulação de dados pessoais e potencialmente sensíveis;
- o controle de acesso para diferentes perfis;
- a integração futura com serviços externos;
- a disponibilidade de ferramentas gratuitas e open source;
- a possibilidade de implantação com baixo custo.

O estudo de viabilidade identificou que a equipe possui maior familiaridade com Java, Spring Boot, React e PostgreSQL. Também identificou menor experiência com integrações externas, como SUAP e VLibras.

---

## Decisão

A stack principal do Portal Incluir será composta por:

| Camada | Tecnologia | Finalidade |
|---|---|---|
| Frontend | React | Desenvolvimento da interface web responsiva |
| Backend | Java | Implementação das regras de negócio |
| Framework backend | Spring Boot | Desenvolvimento da API e organização dos serviços |
| Segurança | Spring Security | Autenticação, autorização e controle de acesso |
| Banco de dados | PostgreSQL | Persistência relacional dos dados |
| Prototipação | Figma | Criação e validação das interfaces |
| Versionamento | Git e GitHub | Controle de versão, documentação e colaboração |

As versões específicas das tecnologias serão definidas antes do início da implementação, considerando versões estáveis e com suporte ativo.

---

## Arquitetura associada

A aplicação seguirá inicialmente uma arquitetura cliente-servidor em camadas:

```text
Navegador do usuário
        ↓
Frontend em React
        ↓
API em Java e Spring Boot
        ↓
Regras de negócio e controle de acesso
        ↓
Camada de persistência
        ↓
PostgreSQL
```

As integrações externas deverão ser isoladas em uma camada de serviços, evitando que indisponibilidades ou mudanças em sistemas terceiros afetem diretamente o restante da aplicação.

---

## Justificativa

### React

O React foi escolhido para a construção da interface por:

- permitir o desenvolvimento de interfaces baseadas em componentes;
- atender à necessidade de uma aplicação web responsiva;
- fazer parte do conhecimento técnico da equipe;
- possuir documentação, comunidade e bibliotecas amplamente disponíveis;
- facilitar a integração com uma API backend.

### Java e Spring Boot

Java e Spring Boot foram escolhidos para o backend por:

- fazerem parte do conhecimento técnico da equipe;
- oferecerem uma estrutura adequada para aplicações em camadas;
- possuírem ferramentas consolidadas para APIs web;
- facilitarem a integração com bancos de dados relacionais;
- oferecerem suporte a autenticação e autorização com Spring Security;
- possuírem ampla documentação e uso no mercado.

### PostgreSQL

O PostgreSQL foi escolhido por:

- ser um banco de dados relacional;
- ser adequado a dados estruturados e relacionados;
- fazer parte do conhecimento técnico da equipe;
- ser gratuito e open source;
- possuir integração consolidada com Java e Spring Boot;
- estar disponível em diferentes plataformas de hospedagem.

A decisão sobre o banco de dados é detalhada no [ADR 002](002-banco-de-dados.md).

---

## Integrações em avaliação

As seguintes tecnologias não fazem parte da stack principal confirmada e ainda dependem de avaliação técnica:

| Integração | Objetivo | Situação |
|---|---|---|
| SUAP | Aproveitar informações institucionais existentes | Em avaliação |
| VLibras | Oferecer recurso complementar de acessibilidade | Em avaliação |
| Serviço SMTP | Enviar notificações por e-mail | Em avaliação |
| Plataforma de hospedagem | Implantar frontend, backend e banco | Em avaliação |

Para cada integração, deverá ser verificada a disponibilidade, documentação, autorização institucional, segurança e comportamento em caso de indisponibilidade.

---

## Alternativas de hospedagem consideradas

O estudo de viabilidade mencionou as seguintes possibilidades:

- AWS;
- Railway;
- Render;
- Supabase;
- infraestrutura institucional do IFPB.

Nenhuma plataforma foi escolhida neste ADR. A decisão de hospedagem deverá ser registrada posteriormente em um ADR específico.

---

## Consequências positivas

- utilização de tecnologias conhecidas pela equipe;
- redução da curva de aprendizado da stack principal;
- tecnologias gratuitas e open source;
- boa integração entre backend e banco de dados;
- disponibilidade de bibliotecas consolidadas de segurança;
- separação clara entre frontend e backend;
- possibilidade de desenvolvimento e teste independente das camadas;
- stack com documentação e comunidade amplamente disponíveis.

---

## Consequências negativas e riscos

- a equipe precisará manter duas aplicações, frontend e backend;
- será necessário configurar a comunicação entre React e Spring Boot;
- autenticação e autorização exigirão atenção especial;
- o tratamento de dados sensíveis aumentará a complexidade de segurança;
- a equipe possui pouca experiência com APIs externas;
- integrações com SUAP e VLibras podem sofrer alterações ou indisponibilidade;
- a plataforma de hospedagem ainda precisa ser escolhida;
- o prazo exige que a implementação seja limitada ao MVP.

---

## Medidas de mitigação

- utilizar Spring Security para autenticação e autorização;
- adotar controle de acesso por perfil;
- manter as integrações em uma camada de serviços;
- realizar provas de conceito antes de incluir integrações no MVP;
- manter o backlog priorizado;
- revisar periodicamente o código relacionado à segurança;
- utilizar dados fictícios durante desenvolvimento e testes;
- registrar novas decisões técnicas em ADRs próprios.

---

## Decisões não incluídas neste ADR

Este ADR não define:

- versões exatas das tecnologias;
- método definitivo de autenticação;
- formato dos tokens ou sessões;
- utilização de Docker;
- plataforma de hospedagem;
- integração definitiva com o SUAP;
- integração definitiva com o VLibras;
- serviço de armazenamento de documentos;
- serviço de envio de e-mails.

Essas decisões deverão ser avaliadas e registradas separadamente.

---

## Critério de revisão

Esta decisão deverá ser revisada se:

- alguma tecnologia inviabilizar uma funcionalidade essencial;
- houver incompatibilidade relevante com a infraestrutura institucional;
- a equipe não conseguir atender aos requisitos de segurança;
- uma integração obrigatória exigir tecnologia incompatível;
- o prazo do projeto for comprometido pela stack escolhida.

Pequenos ajustes de configuração ou versão não exigem a substituição deste ADR.
