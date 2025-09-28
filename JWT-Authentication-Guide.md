# Guia Completo: Sistema de Autenticação JWT com Spring Boot

Este guia ensina como implementar um sistema de autenticação JWT completo no Spring Boot, do zero ao deploy.

## 📋 Índice

1. [Pré-requisitos](#pré-requisitos)
2. [Configuração Inicial](#configuração-inicial)
3. [Estrutura do Projeto](#estrutura-do-projeto)
4. [Dependências Maven](#dependências-maven)
5. [Modelo de Usuário](#modelo-de-usuário)
6. [DTOs de Autenticação](#dtos-de-autenticação)
7. [Repository](#repository)
8. [Serviço JWT](#serviço-jwt)
9. [Filtro de Autenticação](#filtro-de-autenticação)
10. [Configuração de Segurança](#configuração-de-segurança)
11. [Serviços de Autenticação](#serviços-de-autenticação)
12. [Controllers](#controllers)
13. [Configurações](#configurações)
14. [Configuração de Testes](#configuração-de-testes)
15. [Testando a API](#testando-a-api)
16. [Boas Práticas](#boas-práticas)

---

## 🔧 Pré-requisitos

- Java 17+
- Spring Boot 3.x
- Maven
- PostgreSQL (produção)
- H2 (testes)
- IDE (IntelliJ IDEA, VS Code, etc.)

---

## 🚀 Configuração Inicial

### 1. Criar projeto Spring Boot

Use o [Spring Initializr](https://start.spring.io/) com as seguintes dependências:
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Boot DevTools
- PostgreSQL Driver
- Lombok
- Validation

---

## 📁 Estrutura do Projeto

```
src/main/java/com/seuapp/
├── config/
│   ├── SecurityConfig.java
│   └── DotEnvEnvironmentPostProcessor.java
├── controllers/
│   └── AuthController.java
├── domain/
│   ├── auth/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── JwtResponse.java
│   └── users/
│       ├── UserModel.java
│       ├── UserDtoRequest.java
│       └── UserDtoResponse.java
├── repositories/
│   └── users/
│       └── IUserRepository.java
├── security/
│   └── JwtAuthenticationFilter.java
├── services/
│   ├── AuthService.java
│   ├── JwtService.java
│   └── CustomUserDetailsService.java
└── Application.java
```

---

## 📦 Dependências Maven

### pom.xml

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- JWT Dependencies -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Utilities -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Test Dependencies -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 👤 Modelo de Usuário

### UserModel.java

```java
package com.seuapp.domain.users;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private String role = "USER";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

**Explicação:**
- `@Entity`: Marca a classe como entidade JPA
- `@GeneratedValue(AUTO)`: Gera UUID automaticamente
- `@Column(unique = true)`: Garante unicidade de email/username
- `@CreationTimestamp/@UpdateTimestamp`: Timestamps automáticos

---

## 📝 DTOs de Autenticação

### LoginRequest.java

```java
package com.seuapp.domain.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull @NotEmpty
        @Email(message = "Email deve ter formato válido")
        String email,

        @NotNull @NotEmpty
        String password
) {
}
```

### RegisterRequest.java

```java
package com.seuapp.domain.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotNull @NotEmpty
        @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
        String username,

        @NotNull @NotEmpty
        @Email(message = "Email deve ter formato válido")
        String email,

        @NotNull @NotEmpty
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
                 message = "Senha deve conter pelo menos 1 letra maiúscula, 1 minúscula e 1 número")
        String password,

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone deve ter formato válido")
        String phoneNumber
) {
}
```

### JwtResponse.java

```java
package com.seuapp.domain.auth;

import java.util.UUID;

public record JwtResponse(
        String token,
        String type,
        UUID id,
        String username,
        String email,
        String role
) {
}
```

**Por que usar Records?**
- Mais conciso que classes tradicionais
- Imutáveis por padrão
- Ideais para DTOs
- Geração automática de equals(), hashCode(), toString()

---

## 🗄️ Repository

### IUserRepository.java

```java
package com.seuapp.repositories.users;

import com.seuapp.domain.users.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<UserModel, UUID> {
    
    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
```

**Métodos importantes:**
- `findByEmail()`: Para login por email
- `existsByEmail()/existsByUsername()`: Validação de unicidade
- `JpaRepository`: CRUD automático

---

## 🔐 Serviço JWT

### JwtService.java

```java
package com.seuapp.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UUID userId, String username, String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaims(token).getSubject());
    }

    public String extractUsername(String token) {
        return extractClaims(token).get("username", String.class);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return !isTokenExpired(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
```

**Funcionalidades:**
- **Gerar token**: Com claims personalizados (user ID, role, etc.)
- **Extrair dados**: Do token (userId, username, role)
- **Validar token**: Verificar se é válido e não expirado
- **Chave segura**: HMAC SHA-256

---

## 🛡️ Filtro de Autenticação

### JwtAuthenticationFilter.java

```java
package com.seuapp.security;

import com.seuapp.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (jwtService.isTokenValid(token)) {
                UUID userId = jwtService.extractUserId(token);
                String username = jwtService.extractUsername(token);
                String role = jwtService.extractRole(token);
                
                List<SimpleGrantedAuthority> authorities = 
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));
                
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

**Como funciona:**
1. Intercepta todas as requisições
2. Extrai o token do header `Authorization`
3. Valida o token JWT
4. Define a autenticação no contexto do Spring Security

---

## ⚙️ Configuração de Segurança

### SecurityConfig.java

```java
package com.seuapp.config;

import com.seuapp.security.JwtAuthenticationFilter;
import com.seuapp.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

**Configurações importantes:**
- **STATELESS**: Não armazena sessões (usa JWT)
- **CSRF disabled**: Não necessário com JWT
- **Endpoints públicos**: `/api/auth/**` para login/register
- **Filtro JWT**: Executado antes do filtro padrão

---

## 🔧 Serviços de Autenticação

### CustomUserDetailsService.java

```java
package com.seuapp.services;

import com.seuapp.domain.users.UserModel;
import com.seuapp.repositories.users.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );

        return new User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
```

### AuthService.java

```java
package com.seuapp.services;

import com.seuapp.domain.auth.JwtResponse;
import com.seuapp.domain.auth.LoginRequest;
import com.seuapp.domain.auth.RegisterRequest;
import com.seuapp.domain.users.UserModel;
import com.seuapp.repositories.users.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public JwtResponse login(LoginRequest request) {
        // Autenticar usuário
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // Buscar usuário
        UserModel user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Gerar token
        String token = jwtService.generateToken(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );

        return new JwtResponse(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    public JwtResponse register(RegisterRequest request) {
        // Verificar se usuário já existe
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email já está em uso");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username já está em uso");
        }

        // Criar novo usuário
        UserModel user = new UserModel();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhoneNumber(request.phoneNumber());
        user.setRole("USER"); // Role padrão

        UserModel savedUser = userRepository.save(user);

        // Gerar token
        String token = jwtService.generateToken(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );

        return new JwtResponse(
                token,
                "Bearer",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }
}
```

---

## 🎮 Controllers

### AuthController.java

```java
package com.seuapp.controllers;

import com.seuapp.domain.auth.JwtResponse;
import com.seuapp.domain.auth.LoginRequest;
import com.seuapp.domain.auth.RegisterRequest;
import com.seuapp.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            JwtResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            JwtResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
```

---

## ⚙️ Configurações

### application.properties (desenvolvimento)

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/${POSTGRES_DB}
spring.datasource.username=${POSTGRES_USER}
spring.datasource.password=${POSTGRES_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
jwt.secret=minha-chave-secreta-muito-segura-para-jwt-tokens-deve-ter-pelo-menos-256-bits-de-comprimento
jwt.expiration=86400000

# Logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### .env

```env
POSTGRES_DB=meuapp
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin123
```

### .gitignore

```
.env
target/
*.log
```

---

## 🧪 Configuração de Testes

### src/test/resources/application.properties

```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

# JWT Configuration
jwt.secret=test-secret-key-for-testing-purposes-only-must-be-at-least-256-bits-long-for-hmac-sha
jwt.expiration=86400000

# Logging
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN
logging.level.com.zaxxer.hikari=WARN
```

**Por que H2 para testes?**
- Banco em memória (rápido)
- Não depende de infraestrutura externa
- Limpo a cada execução
- Ideal para testes automatizados

---

## 🚀 Testando a API

### 1. Registro de Usuário

```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "joao123",
  "email": "joao@email.com",
  "password": "MinhaSenh@123",
  "phoneNumber": "+5511999999999"
}
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "joao123",
  "email": "joao@email.com",
  "role": "USER"
}
```

### 2. Login

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "joao@email.com",
  "password": "MinhaSenh@123"
}
```

### 3. Endpoint Protegido

```bash
GET http://localhost:8080/api/users/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## ✅ Boas Práticas

### 🔒 Segurança

1. **Chave Secreta Forte**
   - Mínimo 256 bits
   - Usar variáveis de ambiente
   - Nunca commitar no repositório

2. **Senha Forte**
   - Usar BCrypt para hash
   - Validações no frontend e backend
   - Política de complexidade

3. **Token JWT**
   - Tempo de expiração adequado
   - Claims mínimos necessários
   - Validação em todas as requisições

### 🏗️ Arquitetura

1. **Separação de Responsabilidades**
   - Controller: Entrada da API
   - Service: Lógica de negócio
   - Repository: Acesso a dados
   - Security: Configurações de segurança

2. **DTOs**
   - Input: Validações e estrutura específica
   - Output: Dados que devem ser expostos
   - Nunca expor entidades diretamente

3. **Configurações**
   - Ambientes separados (dev, test, prod)
   - Variáveis de ambiente para dados sensíveis
   - Profiles do Spring Boot

### 🧪 Testes

1. **Testes Unitários**
   - Services: Lógica de negócio
   - JWT: Geração e validação de tokens
   - Validações: DTOs e constraints

2. **Testes de Integração**
   - Controllers: Endpoints completos
   - Security: Autenticação e autorização
   - Database: Operações CRUD

### 📝 Documentação

1. **README.md**
   - Como executar o projeto
   - Endpoints disponíveis
   - Variáveis de ambiente

2. **API Documentation**
   - Swagger/OpenAPI
   - Exemplos de requisições
   - Códigos de resposta

---

## 🔄 Fluxo Completo de Autenticação

```mermaid
sequenceDiagram
    participant Client
    participant Controller
    participant AuthService
    participant JwtService
    participant Database

    Client->>Controller: POST /api/auth/register
    Controller->>AuthService: register(request)
    AuthService->>Database: check user exists
    AuthService->>Database: save new user
    AuthService->>JwtService: generateToken()
    JwtService-->>AuthService: JWT token
    AuthService-->>Controller: JwtResponse
    Controller-->>Client: 200 + token

    Client->>Controller: GET /api/protected (with token)
    Controller->>JwtAuthenticationFilter: validate token
    JwtAuthenticationFilter->>JwtService: isTokenValid()
    JwtService-->>JwtAuthenticationFilter: valid
    JwtAuthenticationFilter->>Controller: authenticated request
    Controller-->>Client: protected data
```

---

## 🎯 Próximos Passos

### Funcionalidades Avançadas

1. **Refresh Tokens**
   - Renovação automática de tokens
   - Logout seguro
   - Revogação de tokens

2. **Roles e Permissões**
   - Sistema hierárquico de roles
   - Permissões granulares
   - Autorização baseada em recursos

3. **OAuth2 Integration**
   - Login com Google/GitHub
   - Social authentication
   - Federation de identidade

4. **Rate Limiting**
   - Proteção contra ataques
   - Bucket4j ou Redis
   - Throttling por usuário

### Deployment

1. **Docker**
   - Containerização da aplicação
   - Docker Compose para stack completa
   - Multi-stage builds

2. **Cloud Deploy**
   - AWS ECS/EKS
   - Azure Container Instances
   - Google Cloud Run

---

## 📚 Referências

- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/) - Debugger de JWT
- [JJWT Library](https://github.com/jwtk/jjwt)
- [Spring Boot Reference](https://spring.io/projects/spring-boot)

---

**🎉 Parabéns!** Você agora tem um sistema de autenticação JWT completo e seguro!

Este guia pode ser adaptado para qualquer projeto Spring Boot que necessite de autenticação robusta e escalável.
