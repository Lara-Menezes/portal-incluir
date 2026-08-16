# ADR 001: Escolha de Stack Tecnológica

- **Status:** Aceito
- **Data:** Semana 10 do semestre

---

## Contexto

O grupo precisava definir as tecnologias que serão usadas para construir o IFgram. A escolha precisa levar em conta:

- O conhecimento técnico atual do grupo
- As tecnologias ensinadas no curso de ADS do IFPB
- A viabilidade de entrega dentro do semestre de Projeto II
- Disponibilidade de hospedagem gratuita ou de baixo custo

---

## Decisão

Adotaremos a seguinte stack:

| Camada | Tecnologia | Versão |
|--------|-----------|--------|
| Frontend | React | 18+ |
| Backend | Spring Boot + Java | 3.x / Java 21 |
| Banco de dados | PostgreSQL | 15+ |
| Autenticação | JWT | - |
| Containerização | Docker | - |
| Deploy | Railway | - |

---

## Alternativas consideradas

### Frontend: React vs. Angular vs. Vue

- **React** foi escolhido por ser o framework ensinado na disciplina de Front-end do ADS e por ter maior adoção no mercado local.
- **Angular** foi descartado por ser mais verboso e ter curva de aprendizado mais acentuada para o tempo disponível.
- **Vue** foi considerado mas o grupo tem menos experiência com ele.

### Backend: Spring Boot vs. Node.js (Express)

- **Spring Boot** foi escolhido por ser a tecnologia ensinada nas disciplinas de Back-end do ADS e por ser amplamente usado em empresas da região.
- **Node.js** foi descartado porque o grupo tem mais domínio de Java, e misturar duas linguagens no mesmo projeto aumentaria o risco.

### Deploy: Railway vs. Heroku vs. Render

- **Railway** foi escolhido por oferecer plano gratuito com suporte nativo a Spring Boot e PostgreSQL, sem precisar de configuração de Docker.
- **Heroku** foi descartado por ter encerrado o plano gratuito.
- **Render** foi considerado mas o deploy de Spring Boot tem menos documentação disponível.

---

## Consequências

**Positivas:**
- Stack alinhada com o que o grupo aprendeu no curso: menor risco técnico
- React + Spring Boot é uma combinação comum no mercado: relevante para o portfólio
- Docker garante que o ambiente de desenvolvimento é igual ao de produção, evitando o clássico "funciona na minha máquina"

**Negativas / Riscos:**
- Spring Boot tem tempo de startup mais lento no plano gratuito do Railway (cold start)
- O grupo precisará aprender a configurar CORS entre frontend e backend
- Docker adiciona uma curva de aprendizado inicial para quem nunca usou containers

---

## Revisão

Esta decisão será revisada apenas se houver impedimento técnico grave que inviabilize a entrega. Pequenos ajustes de configuração não justificam troca de stack.
