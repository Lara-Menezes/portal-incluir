# Recuperação de senha

## Configuração e teste manual

Configure `FRONTEND_URL` com a origem pública do frontend (HTTPS em produção;
HTTP somente em localhost). O link não é construído a partir do cabeçalho Host
da requisição. O valor padrão de desenvolvimento é `http://localhost:5173`.

Mantenha as configurações SMTP existentes: `spring.mail.host`, `spring.mail.port`
e os parâmetros TLS/autenticação do provedor. Na configuração atual, as credenciais
e o remetente vêm de `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` e
`APP_EMAIL_SMTP_FROM`. Não coloque senhas reais no repositório. O remetente deve
ser autorizado pelo provedor SMTP. A aplicação não envia enquanto a configuração
necessária não estiver disponível.

Depois dos testes automatizados, em ambiente de desenvolvimento/homologação:

1. Abra “Esqueci minha senha” no login e informe uma conta ativa de teste.
2. Confira a mensagem genérica e o recebimento do e-mail (inclusive spam).
3. Abra o link, confirme a senha nova e entre novamente.
4. Confira que a senha anterior falha, as sessões anteriores perdem o acesso e
   reutilizar o link não permite outra alteração.
5. Solicite dois links: apenas o mais recente deve funcionar. Confira também
   link expirado, senha divergente e falha de conexão preservando o formulário.

Os testes automatizados usam H2 em memória e transporte de e-mail simulado;
não comprovam entrega pelo provedor, TLS, aparência em clientes de e-mail ou
funcionamento no navegador. Não iniciar esses testes contra o banco operacional.

## Contrato

- `POST /api/auth/recuperar-senha`: `{ "email": "..." }`, resposta 202 genérica
  para conta existente, inexistente, inativa ou tentativa limitada.
- `POST /api/auth/redefinir-senha`: `{ "token": "...", "novaSenha": "..." }`.
  Sucesso 200; link inválido, expirado, revogado ou usado: 400; excesso de
  tentativas por IP: 429 com `Retry-After`.
- Abrir o link não consome o token nem autentica o usuário. O token fica no
  fragmento `#token=...`, removido da barra de endereço após a leitura da tela.
  Atualizar essa página exige reabrir o link original do e-mail.
- Senha: 6 a 72 caracteres e no máximo 72 bytes UTF-8 (BCrypt). Espaços não são
  removidos da senha. Confirmação é validada no frontend.

## Segurança e operação

O token aleatório tem 256 bits de entropia, expira em 30 minutos e só seu SHA-256
é armazenado. Novo pedido revoga os anteriores. Alteração de e-mail, inativação
ou mudança da versão das credenciais também invalida o link. A conta técnica
root fica fora deste fluxo público e exige recuperação administrativa.

A redefinição é transacional e usa bloqueio da conta para impedir dois usos
concorrentes. Incrementar `usuarios.versao_credenciais` invalida as sessões antigas
na próxima requisição autenticada; sessões legadas sem essa informação representam
a versão zero. Nenhuma sessão nova é aberta automaticamente.

Há uma nova tabela `recuperacoes_senha` (e auditoria Envers) e uma coluna de versão
das credenciais com padrão zero. Validar a atualização do schema e o preenchimento
das contas existentes em homologação antes de publicar. Nenhum comando de migração
ou acesso ao banco operacional faz parte dos testes.

Limites por instância, em janelas de 15 minutos: 3 pedidos/e-mail, 20 pedidos/IP
e 60 tentativas de redefinição/IP. O armazenamento dos limites é limitado e guarda
chaves derivadas por hash. Para múltiplas instâncias é necessário um limitador
compartilhado; reiniciar a aplicação reinicia esses contadores. Considera-se o IP
do servlet, não um `X-Forwarded-For` arbitrário; configurar o proxy confiável na
infraestrutura. Não habilitar logs de corpos de requisições contendo senhas/tokens.

O processamento de pedidos usa uma fila em memória limitada, não durável. O envio
ocorre depois do commit; falhas SMTP não alteram a senha nem revelam a existência
da conta. O usuário pode pedir outro link respeitando os limites. Não há garantia
de entrega nem repetição automática em caso de reinício/falha do provedor. Os
registros expirados não têm limpeza automática nesta versão; definir retenção
antes de operação prolongada.
