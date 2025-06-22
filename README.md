# LiterAlura - Sistema de Gerenciamento Literário

Um sistema completo de gerenciamento de dados literários desenvolvido em Spring Boot, que integra com a API do Gutenberg Project para buscar e gerenciar livros e autores. O projeto demonstra uma arquitetura MVC robusta com múltiplas interfaces (CLI e REST API).

## 🚀 Tecnologias Utilizadas

- **Java 21** - Linguagem principal
- **Spring Boot 3.5.3** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **H2 Database** - Banco de dados em memória
- **OkHttp 4.12.0** - Cliente HTTP para consumo de APIs
- **Jackson** - Serialização/deserialização JSON
- **Maven** - Gerenciamento de dependências

## 🏗️ Arquitetura do Projeto

### Estrutura de Pacotes (MVC Completo)

```
com.jciterceros.literalura
├── controller/                 # Controllers REST API
│   └── LivroController.java
├── service/                    # Camada de serviços e lógica de negócio
│   ├── LivroService.java       # Serviço principal
│   ├── ConsumoAPI.java         # Cliente HTTP
│   ├── ConverteDados.java      # Conversão JSON
│   └── DataLoader.java         # Carregamento inicial de dados
├── repository/                 # Camada de acesso a dados
│   ├── LivroRepository.java
│   └── AutorRepository.java
├── model/                      # Entidades JPA
│   ├── Livro.java
│   └── Autor.java
├── dto/                        # Data Transfer Objects
│   ├── LivroDTO.java
│   ├── AutorDTO.java
│   └── RespostaDTO.java
├── exception/                  # Exceções customizadas
│   └── LivroNaoEncontradoException.java
└── principal/                  # Interface CLI (CommandLineRunner)
    └── Principal.java
```

## 🎯 Funcionalidades Implementadas

### 📚 Interface CLI (Principal.java)
- ✅ **Buscar livros por título** - Integração com API Gutenberg
- ✅ **Listar livros registrados** - Visualização completa com autores
- ✅ **Listar autores registrados** - Com informações biográficas
- ✅ **Buscar autores vivos em ano específico** - Consulta temporal
- ✅ **Buscar autores nascidos em ano específico** - Consulta biográfica
- ✅ **Buscar autores por ano de falecimento** - Consulta histórica
- ✅ **Listar livros por idioma** - Filtro por idioma
- ✅ **Menu interativo** - Interface amigável com validações

### 🌐 Interface REST API (LivroController.java)
- ✅ `GET /api/livros` - Listar todos os livros
- ✅ `GET /api/livros/top5` - Top 5 livros mais baixados
- ✅ `GET /api/livros/idioma/{idioma}` - Buscar por idioma
- ✅ `GET /api/livros/idiomas` - Listar idiomas disponíveis
- ✅ `GET /api/livros/buscar?titulo=...` - Buscar por título

## 🔄 Fluxo de Dados e Integração

### Integração com API Externa
```
CLI/REST → LivroService → ConsumoAPI → Gutenberg API (gutendex.com)
                ↓
            ConverteDados (JSON → DTOs)
                ↓
            Persistência (JPA/H2)
```

### Relacionamentos de Dados
- **Livro ↔ Autor**: Relacionamento Many-to-Many
- **Persistência**: Evita duplicação de autores
- **Consultas**: Queries customizadas para buscas complexas

## 📊 Modelo de Dados

### Entidade Livro
```java
@Entity
public class Livro {
    private Long id;
    private String titulo;
    private String idioma;
    private Integer numeroDownloads;
    @ManyToMany
    private List<Autor> autores;
}
```

### Entidade Autor
```java
@Entity
public class Autor {
    private Long id;
    private String nome;
    private Integer anoNascimento;
    private Integer anoFalecimento;
    @ManyToMany(mappedBy = "autores")
    private List<Livro> livros;
}
```

## 🛠️ Configuração e Execução

### Pré-requisitos
- Java 21 ou superior
- Maven 3.6+

### Execução
```bash
# Clone o repositório
git clone <repository-url>
cd literalura

# Execute a aplicação
mvn spring-boot:run
```

### Acessos Disponíveis

#### 1. Interface CLI
- A aplicação inicia automaticamente com menu interativo
- Navegue pelas opções numeradas (1-7)

#### 2. REST API
- **Base URL**: `http://localhost:8080/api/livros`
- **Endpoints disponíveis**:
  - `GET /api/livros` - Listar todos
  - `GET /api/livros/top5` - Top 5 downloads
  - `GET /api/livros/idioma/{idioma}` - Por idioma
  - `GET /api/livros/idiomas` - Idiomas disponíveis
  - `GET /api/livros/buscar?titulo=...` - Buscar por título

#### 3. H2 Console (Banco de Dados)
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:literalura`
- **Username**: `sa`
- **Password**: (vazio)

## 🌱 Dados Iniciais (Seeds)

O sistema carrega automaticamente dados de autores brasileiros:
- **Autores**: Jorge Amado, Clarice Lispector, Guimarães Rosa, Carlos Drummond, Érico Veríssimo, Monteiro Lobato, José de Alencar, Euclides da Cunha, Lima Barreto
- **Livros**: Obras representativas de cada autor
- **Carregamento**: Executado apenas se o banco estiver vazio

## 🔍 Consultas Disponíveis

### Repositório de Livros
- `findByTituloContainingIgnoreCase()` - Busca por título
- `findTop5ByOrderByNumeroDownloadsDesc()` - Top 5 downloads
- `buscarPorIdioma()` - Filtro por idioma
- `buscarIdiomasDisponiveis()` - Lista idiomas

### Repositório de Autores
- `findByNomeContainingIgnoreCase()` - Busca por nome
- `buscarAutoresVivosEmAno()` - Autores vivos em ano específico
- `buscarAutoresNascidosEmAno()` - Autores nascidos em ano
- `buscarAutoresPorAnoMorte()` - Autores falecidos em ano

## 🎨 Padrões e Boas Práticas

### 1. **Dependency Injection**
```java
@Autowired
private LivroService livroService;
```

### 2. **Repository Pattern**
```java
@Repository
public interface LivroRepository extends JpaRepository<Livro, Long>
```

### 3. **Service Layer**
```java
@Service
@Transactional
public class LivroService {
    // Lógica de negócio centralizada
}
```

### 4. **DTO Pattern**
```java
public record LivroDTO(
    @JsonAlias("title") String titulo,
    @JsonAlias("languages") List<String> idiomas,
    @JsonAlias("download_count") Integer numeroDownloads,
    @JsonAlias("authors") List<AutorDTO> autores
)
```

### 5. **Exception Handling**
```java
public class LivroNaoEncontradoException extends RuntimeException
```

### 6. **CommandLineRunner com Ordem**
```java
@Component
@Order(1) // DataLoader
@Order(2) // Principal (CLI)
```

## ⚙️ Configurações

### application.properties
```properties
# H2 Database
spring.datasource.url=jdbc:h2:mem:literalura
spring.h2.console.enabled=true

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## 🔧 Dependências Principais

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
    </dependency>
    
    <!-- HTTP Client -->
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>okhttp</artifactId>
        <version>4.12.0</version>
    </dependency>
</dependencies>
```

## 📈 Vantagens da Arquitetura

### 1. **Separação de Responsabilidades**
- **View**: CLI e REST API independentes
- **Controller**: Lógica de negócio centralizada
- **Model**: Persistência e acesso a dados

### 2. **Reutilização de Código**
- Mesmo `LivroService` para CLI e REST
- Repositórios compartilhados
- DTOs reutilizáveis

### 3. **Testabilidade**
- Services isolados para testes unitários
- Repositórios mockáveis
- Separação clara de responsabilidades

### 4. **Escalabilidade**
- Fácil adição de novas interfaces
- Lógica de negócio centralizada
- Padrões consistentes

## 🚀 Próximos Passos Sugeridos

1. **Implementar testes unitários** e de integração
2. **Adicionar validações** com Bean Validation
3. **Implementar cache** com Spring Cache
4. **Adicionar logs estruturados** com SLF4J
5. **Criar documentação API** com Swagger/OpenAPI
6. **Implementar autenticação** e autorização
7. **Adicionar métricas** com Micrometer
8. **Configurar CI/CD** com GitHub Actions

## 📝 Exemplo de Uso

### Via CLI
```bash
mvn spring-boot:run

# Menu interativo aparecerá:
# 1 - Buscar livros pelo título
# 2 - Listar livros registrados
# 3 - Listar autores registrados
# 4 - Listar autores vivos em um determinado ano
# 5 - Listar autores nascidos em determinado ano
# 6 - Listar autores por ano de sua morte
# 7 - Listar livros em um determinado idioma
# 0 - Sair
```

### Via REST API
```bash
# Listar todos os livros
curl http://localhost:8080/api/livros

# Buscar por título
curl "http://localhost:8080/api/livros/buscar?titulo=Dom%20Casmurro"

# Top 5 downloads
curl http://localhost:8080/api/livros/top5

# Livros em português
curl http://localhost:8080/api/livros/idioma/pt
```

---

**Conclusão**: Este projeto demonstra uma implementação completa e robusta de arquitetura MVC em Spring Boot, com múltiplas interfaces de usuário, integração com APIs externas, e uma base sólida para expansão futura. 