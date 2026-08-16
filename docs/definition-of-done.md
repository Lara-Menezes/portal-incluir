# Definition of Done: IFgram

A Definition of Done (DoD) define os critérios que **toda** história de usuário precisa cumprir para ser considerada concluída. Uma história só é marcada como "Done" quando atende a **todos** os itens desta lista.

---

## Critérios gerais

### Funcionalidade
- [ ] A funcionalidade foi implementada conforme descrita na história de usuário
- [ ] Todos os critérios de aceitação foram atendidos e verificados manualmente
- [ ] Casos de erro foram tratados (ex: campo vazio, dado inválido, falha de rede)

### Qualidade de código
- [ ] O código passou por revisão de pelo menos um outro membro do grupo (code review via Pull Request)
- [ ] Não há código comentado ou console.log desnecessários no PR
- [ ] As variáveis e funções têm nomes descritivos em português ou inglês (sem mistura)

### Testes
- [ ] A funcionalidade foi testada manualmente no navegador (caminho feliz e principais erros)
- [ ] Pelo menos um teste automatizado cobre o caminho feliz da história
- [ ] Histórias dos épicos **Autenticação** e **Publicações** têm obrigatoriamente testes para os cenários de erro mais prováveis (ex: credencial inválida, arquivo fora do formato aceito)
- [ ] Nenhum teste existente foi quebrado pelo PR

### Integração
- [ ] O código está no branch principal após aprovação do Pull Request
- [ ] A funcionalidade funciona no ambiente de homologação (não apenas local)
- [ ] O Pull Request está vinculado à história de usuário no GitHub Projects

### Rastreabilidade
- [ ] O card da história no GitHub Projects foi movido para a coluna "Done"
- [ ] Os critérios de aceitação foram verificados e confirmados no card

---

## O que NÃO faz parte da DoD

- Performance otimizada além do básico (não é critério nesta fase)
- Documentação de código (não é exigida para este projeto)
- Testes automatizados para todos os cenários (apenas os críticos)

---

## Observação

A DoD se aplica a **todas as histórias**, independente do épico ou sprint. Se uma história cumpre os critérios de aceitação mas não passou por code review, ela **não está done**.
