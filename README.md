## Тестовое задание для Effective Mobile

### Bank Card Management System.

<details>
    <summary>
        <b>Запуск</b>
    </summary>

```ignorelang
# Клонировать репо
git clone https://github.com/sergeev-alexander/bank-rest.git

# Запустить
docker-compose up -d
```

#### Админ добавлен через миграцию
- Email: `admin@bank.com`
- Пароль: `admin123`

Адрес: http://localhost:8080
</details>

<details>
    <summary>
        <b>Тестирование</b>
    </summary>

#### Интеграционное тестирование

```ignorelang
# Запуск (должен работать docker engine)
mvn test
```
#### End-to-end тестирование API



1. Импортируйте коллекцию `Bank-Cards-API.postman_collection.json` в Postman
2. Удалите volumes `docker volume rm bank-rest_postgres_data`
3. Запустите приложение `docker-compose up -d`
4. Запустите коллекцию `Run collection`

</details>

<details>
    <summary>
        <b>Документация</b>
    </summary>

Swagger: http://localhost:8080/swagger-ui.html
</details>

### Key Features:

- User Management: Registration, authentication, and profile management with admin/user roles
- Card Operations: Create, view, and manage bank cards with encrypted card numbers
- Transaction Processing: Handle deposits, withdrawals, and transfers between cards
- Card Blocking: Request and manage card blocking/unblocking with approval workflow
- Security: JWT-based authentication, password encryption, and card number encryption
- API Documentation: Swagger/OpenAPI integration for comprehensive API documentation

### Target Users:

- Bank Customers: Manage their cards, view transactions, and perform transfers
- Bank Administrators: Oversee user accounts, approve card blocks, and manage system operations

### Project Structure

- Java 17 with 
- Spring Boot 3.2.0
- PostgreSQL
- Liquibase
- JWT authentication with Spring Security
- Docker containerization
- Testcontainers

### Core Architecture:

- Controllers: REST endpoints for auth, cards, transactions, transfers, users, and card blocks
- Services: Business logic implementation with interface-based design
- Repositories: JPA repositories with custom specifications for filtering
- Entities: JPA entities with validation annotations and lifecycle callbacks
- DTOs: Data transfer objects for API requests/responses
- Security: JWT-based authentication with custom filters and services
- Utils: Helper classes for encryption, validation, and specifications
- Exception Handling: Global exception handler with custom exception types

### Technology Details

- Spring Boot Starters: Web, Security, Data JPA, Validation
- Database: PostgreSQL driver, Liquibase for migrations
- Security: JWT (jjwt 0.11.5), Spring Security
- Documentation: SpringDoc OpenAPI 2.3.0
- Testing: Spring Boot Test, Testcontainers, Spring Security Test
- Utilities: Lombok, Apache Commons Lang3
- Repository pattern with JPA Specification for dynamic queries
- Global exception handling with @ControllerAdvice
- DTO pattern for API request/response objects
- Entity lifecycle management with @PrePersist and @PreUpdate
- Custom validation annotations (e.g., @ValidTransfer)

### Security

- JWT token-based authentication with custom filters
- Card number encryption using custom converter
- Role-based access control (ADMIN/USER roles)
- Password encoding and validation

### Testing

- Integration tests with Testcontainers for PostgreSQL
- Base test class with database cleanup after each test
- Test profiles with separate configuration
- Sequence reset for consistent test data
