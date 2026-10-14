# Manual de Integração do VLibras em Projetos Java (Spring Boot)

Este manual fornece um passo a passo completo e detalhado para integrar a ferramenta de acessibilidade em Libras (**VLibras**) em aplicações Java desenvolvidas com **Spring Boot**.

---

## 1. Visão Geral da Arquitetura

O **VLibras** (desenvolvido pelo Governo Federal do Brasil em parceria com a UFPB) é uma ferramenta client-side (executada no navegador do usuário). Ele lê os elementos de texto do DOM HTML e gera a tradução em Libras por meio de um avatar 3D.

Em uma arquitetura Spring Boot, a integração depende do tipo de front-end utilizado:
- **Spring Boot + Server-Side Rendering (Thymeleaf, JSP, HTML estático):** A inclusão dos scripts do VLibras é feita diretamente nas páginas/templates HTML renderizados pelo Spring.
- **Spring Boot + Front-end Desacoplado (React, Angular, Vue):** O VLibras é adicionado no projeto Front-end, e o Spring Boot atua apenas como API REST (podendo requerer ajustes de CORS e Content Security Policy - CSP).

---

## 2. Passo a Passo da Integração no Front-End

### Cenário A: Spring Boot com Thymeleaf / HTML Estático

Para adicionar o widget do VLibras em sua aplicação Spring Boot usando Thymeleaf ou HTML tradicional:

1. **Localize o layout principal ou cabeçalho/rodapé:**
   Abra seu arquivo base HTML (ex: `src/main/resources/templates/layout.html` ou `index.html`).

2. **Adicione a estrutura HTML do VLibras:**
   Cole o seguinte código logo após a abertura da tag `<body>` ou no rodapé do documento:

```html
<!-- Início do componente VLibras -->
<div vw class="enabled">
  <div vw-access-button class="active"></div>
  <div vw-plugin-wrapper>
    <div class="vw-plugin-top-wrapper"></div>
  </div>
</div>

<script src="https://vlibras.gov.br/app/vlibras-plugin.js"></script>
<script>
  new window.VLibras.Widget('https://vlibras.gov.br/app');
</script>
<!-- Fim do componente VLibras -->
```

### Cenário B: Spring Boot como API REST + React/Angular

Se você usa o Spring Boot apenas para o backend:

* **React:** Adicione o trecho de código HTML/JS no arquivo `public/index.html` da aplicação React, ou utilize uma biblioteca/componente react-vlibras.
* **Angular:** Adicione o trecho no arquivo `src/index.html` da aplicação Angular.

---

## 3. Configurações Necessárias no Spring Boot

Para garantir que o VLibras carregue corretamente sem ser bloqueado por políticas de segurança do Spring Boot, aplique as configurações descritas a seguir.

### 3.1. Spring Security - Content Security Policy (CSP)

Se o seu projeto utiliza **Spring Security**, as políticas de segurança padrão podem bloquear os scripts externos ou imagens vindo de `https://vlibras.gov.br`.

Adicione as permissões para a origem `https://vlibras.gov.br` na classe de configuração de segurança:

```java
package com.exemplo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configuração do Content Security Policy (CSP)
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                        "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' https://vlibras.gov.br; " +
                        "style-src 'self' 'unsafe-inline' https://vlibras.gov.br; " +
                        "img-src 'self' data: https://vlibras.gov.br; " +
                        "frame-src 'self' https://vlibras.gov.br; " +
                        "connect-src 'self' https://vlibras.gov.br;"
                    )
                )
            );

        return http.build();
    }
}
```

### 3.2. Configuração de CORS (Quando Aplicável)

Se o front-end consome APIs REST do Spring Boot localizadas em domínios ou portas diferentes, certifique-se de habilitar o CORS na sua aplicação Spring Boot:

```java
package com.exemplo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
```

---

## 4. Testando a Integração

1. Inicie a sua aplicação Spring Boot:
   ```bash
   ./mvnw spring-boot:run
   ```
2. Acesse a aplicação através do seu navegador (ex: `http://localhost:8080`).
3. Verifique se o **ícone azul de acessibilidade (mão amarela/azul)** aparece na lateral da tela.
4. Clique no ícone para abrir a caixa do widget e selecione qualquer texto da página para verificar se o avatar interpreta e traduz o conteúdo para Libras.

---

## 5. Resolução de Problemas Comuns (Troubleshooting)

| Problema | Causa Provável | Solução |
| :--- | :--- | :--- |
| **Widget não aparece na página** | Erro ao carregar script `vlibras-plugin.js` | Verifique se o servidor possui acesso à internet ou se o script está sendo bloqueado por extensores/adblockers. |
| **Console exibe erro `Refused to load script...`** | Bloqueio de CSP do Spring Security | Atualize as diretrizes do `contentSecurityPolicy` no Spring Security incluindo `https://vlibras.gov.br`. |
| **O avatar não carrega as animações** | Falha de conexão com os servidores da UFPB/Governo | Certifique-se de que conexões de saída na porta `80` e `443` para `vlibras.gov.br` não estejam bloqueadas pelo firewall. |

---

## 6. Recursos Adicionais

- **Documentação Oficial VLibras:** [https://vlibras.gov.br/](https://vlibras.gov.br/)
- **Repositório do VLibras:** [https://github.com/vlibras](https://github.com/vlibras)