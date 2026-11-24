# Integração de usuários

O estudante permanece um registro acadêmico em `/api/estudantes`. Não possui conta, senha ou acesso ao sistema.

Cada `Usuario` possui exatamente um vínculo: coordenador, professor, administrador ou membro da equipe multidisciplinar. O cadastro cria a conta e o perfil na mesma transação. E-mail da conta é único entre os perfis. E-mail e matrícula já existentes no perfil geram HTTP 409; registros antigos não recebem contas automaticamente.

## Executar

1. Inicie o PostgreSQL com as configurações de `src/main/resources/application.properties` ou defina `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD`.
2. Execute o backend com `mvn spring-boot:run` (Java 21 ou superior). O Hibernate atualiza o schema, criando `usuarios` e `administradores`.
3. No frontend `C:\Users\pedro\portal-incluir-web`, execute `npm install` e `npm run dev`. A API padrão é `http://localhost:8080/api`; configure `VITE_API_URL` em `.env.local` para alterá-la. Reinicie o Vite após mudar a variável.

## Organização das camadas

Frontend: telas → `useAuth` → `AuthContext`/`AuthProvider` → `authService` → cliente Axios (`services/http.ts`) → `config/endpoints.ts`. A URL base e o timeout ficam em `config/api.ts`; contratos ficam em `types/`. O dashboard usa `useDashboardCoordenador` → `dashboardService` e obtém o perfil pelo contexto. A identificação local é acessada apenas pelo provider através de um adaptador de armazenamento.

Backend: `AuthController` → `UsuarioService` → repositories; o dashboard segue a estrutura existente, `CoordenadorController` → `CoordenadorService` → repositories. Requisições e respostas ficam em `dto/request` e `dto/response`. CORS está isolado em `config/CorsConfig.java`; a liberação temporária dos endpoints e o encoder de senha ficam em `config/SecurityConfig.java`.

A documentação do frontend, incluindo o mapa de endpoints e as variáveis de ambiente, está em `C:\Users\pedro\portal-incluir-web\docs\integracao.md`.

| Operação | Método | Endpoint | Serviço frontend |
| --- | --- | --- | --- |
| Cadastro | POST | `/api/auth/register` | `authService.register` |
| Login | POST | `/api/auth/login` | `authService.login` |
| Dashboard | GET | `/api/coordenadores/{id}/dashboard` | `dashboardService.buscarCoordenador` |

## Contratos

`POST /api/auth/register` retorna HTTP 201 com `{id, nome, email, perfil, perfilId}`. Exemplo:

```json
{
  "nome": "Maria Silva",
  "email": "maria@ifpb.edu.br",
  "senha": "senha123",
  "perfil": "COORDENADOR",
  "matricula": "12345",
  "cargo": "Coordenadora",
  "setor": "CLAI"
}
```

Campos comuns: nome, email, senha (6 a 72 caracteres), perfil e matrícula; telefone e dataNascimento são opcionais. Professor exige disciplina e setor; coordenador e administrador exigem cargo e setor; equipe exige areaAtuacao, cargo e setor. Os valores do enum são `COORDENADOR`, `PROFESSOR`, `ADMINISTRADOR` e `EQUIPE_MULTIDISCIPLINAR`.

`POST /api/auth/login` recebe `{ "email": "maria@ifpb.edu.br", "senha": "senha123" }` e retorna o mesmo formato. Senha inválida retorna 401 e perfil inativo retorna 403. Senhas são armazenadas com BCrypt e nunca retornadas pela API.

`GET /api/coordenadores/{id}/dashboard` retorna estudantes e professores ativos da instituição, PEIs ativos/arquivados vinculados ao coordenador e os últimos cinco estudantes cadastrados (ordem decrescente de ID). Não representa uma cronologia de acompanhamento. Notificações e anamnese não possuem indicadores disponíveis nesta integração.

O frontend guarda apenas a identificação do usuário em sessionStorage e direciona o coordenador ao dashboard. Os outros perfis recebem uma página inicial de identificação enquanto suas áreas não existem. Sair remove a identificação local.

## Escopo desta etapa

As APIs estão públicas, sem JWT, sessão no servidor ou autorização por perfil. A navegação do frontend serve apenas à experiência de uso. O cadastro de administrador também está aberto. CORS usa `CORS_ALLOWED_ORIGINS`, com localhost e 127.0.0.1 permitidos por padrão.

Os CRUDs de perfis existentes continuam independentes da conta: alterar o e-mail do perfil não altera o e-mail de login; excluir um perfil com conta vinculada é impedido pela chave estrangeira. Para desabilitar o login, inative o perfil. A vinculação de contas a perfis antigos e a sincronização desses CRUDs devem ser tratadas em uma próxima etapa.
