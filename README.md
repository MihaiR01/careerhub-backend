# CareerHub

CareerHub is a Java/Spring Boot REST API for managing job postings and job applications.

The project was built as a backend-focused personal project to practice my Java skills, including Spring Boot, Spring Security, JPA, PostgreSQL, database migrations, automated and manual testing, and containerization.

## Features

* User registration and authentication
* JWT-based authentication
* Role-based authorization with `USER`(ur casual job looking person) and `ADMIN` roles
* User profile access control
* Company management
* Job posting management
* Job search with filtering, pagination, and sorting
* Job applications
* Duplicate application prevention
* Application status workflow
* DTOs and entity mappers
* Request validation
* Centralized exception handling
* PostgreSQL database
* Flyway database migrations
* Docker Compose environment
* PostgreSQL-based integration tests with Testcontainers
* Repository, service, controller, and security tests

## Tech Stack

* Java 21
* Spring Boot 4
* Spring Web
* Spring Security
* JWT
* Spring Data JPA / Hibernate
* PostgreSQL
* Flyway
* Maven
* Docker / Docker Compose
* Testcontainers
* JUnit 5
* Mockito
* Lombok
* Postman

## Architecture

CareerHub is implemented as a modular monolith.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

The main domain entities are:

```text
User
  │
  └── JobApplication ─── Job ─── Company
```

Authentication is handled by Spring Security using JWTs.

```text
POST /login
    ↓
AuthenticationManager
    ↓
JWT
    ↓
Authorization: Bearer <token>
    ↓
Spring Security
    ↓
ROLE_USER / ROLE_ADMIN
```

## Main Entities

### User

Stores user account information and role.

Roles:

* `USER`
* `ADMIN`

### Company

Represents a company that can publish job postings.

### Job

Represents a job posting and contains:

* title
* technologies
* location
* employment type
* company

Supported employment types:

* `FULL_TIME`
* `PART_TIME`
* `INTERNSHIP`
* `CONTRACT`

### JobApplication

Connects a user to a job.

Supported application statuses:

* `APPLIED`
* `UNDER_REVIEW`
* `ACCEPTED`
* `REJECTED`

A user can apply to a specific job only once.

## Authentication

Authentication is handled through JWT.

### Login

```http
POST /login
Content-Type: application/json
```

```json
{
  "email": "user@example.com",
  "password": "password"
}
```

Response:

```json
{
  "token": "<JWT>"
}
```

The token is then sent with protected requests:

```http
Authorization: Bearer <JWT>
```

JWTs contain the authenticated user's email as the subject and their Spring Security roles.

## Authorization

### Public endpoints

```text
POST /login
POST /users
GET  /jobs
GET  /jobs/{id}
```

### USER endpoints

```text
POST /applications
GET  /applications/**
GET  /users/{id}       own profile
PUT  /users/{id}       own profile
```

A regular user cannot access or modify another user's profile.

### ADMIN endpoints

```text
GET    /users
POST   /companies
POST   /jobs
PUT    /applications/{id}/status
DELETE /users/{id}
```

## API Overview

### Users

```http
POST   /users
GET    /users
GET    /users/{id}
PUT    /users/{id}
DELETE /users/{id}
```

### Jobs

```http
GET  /jobs
GET  /jobs/{id}
POST /jobs
```

`GET /jobs` supports filtering, pagination, and sorting.

### Companies

```http
POST /companies
```

### Applications

```http
POST /applications
GET  /applications
GET  /applications/{id}
GET  /applications/user/{userId}
GET  /applications/job/{jobId}
PUT  /applications/{id}/status
```

When creating an application, the user ID is **not** provided by the client. The backend determines the authenticated user from the JWT.

Example:

```json
{
  "jobId": 1
}
```

## Database

CareerHub uses PostgreSQL.

The schema is managed with Flyway.

Migrations are located in:

```text
src/main/resources/db/migration
```

Current initial migration:

```text
V1__initial_schema.sql
```

Hibernate is configured with:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

This means Hibernate validates the schema instead of modifying it. Database structure changes should be introduced through new Flyway migrations.

## Running with Docker

Docker Compose provides both the application and PostgreSQL.

Start the application:

```bash
docker compose up --build
```

The API will be available at:

```text
http://localhost:8080
```

Check the containers:

```bash
docker compose ps
```

Stop the application:

```bash
docker compose down
```

Stop the application and remove the PostgreSQL volume:

```bash
docker compose down -v
```

The PostgreSQL data is stored in the Docker volume:

```text
careerhub-postgres-data
```

## Environment Variables

Docker configuration is provided through `.env`.

A template is available in:

```text
.env.example
```

Example:

```env
POSTGRES_DB=careerhub
POSTGRES_USER=postgres
POSTGRES_PASSWORD=1234

DB_HOST=db
DB_PORT=5432
DB_NAME=careerhub
DB_USERNAME=postgres
DB_PASSWORD=1234

JWT_SECRET=<your-base64-secret>
JWT_EXPIRATION=3600000

JPA_SHOW_SQL=false
```

`.env` is excluded from Git.

## Running Locally

A local PostgreSQL instance can also be used without Docker.

Run the application with Maven:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The default local configuration expects:

```text
Host: localhost
Port: 5432
Database: careerhub
Username: postgres
```

## Testing

The project uses automated tests at several levels:

```text
Repository tests
Service tests
Controller tests
Security tests
Integration tests
```

Repository/integration tests use Testcontainers with PostgreSQL.

This means tests do not use the developer's local `careerhub` database.

Docker must be running before executing the Testcontainers tests.

Run the complete test suite:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

Each test run uses an isolated PostgreSQL container and applies the Flyway migrations automatically.

## Development Workflow

A typical local workflow is:

```text
docker compose up
        ↓
PostgreSQL + Spring Boot
        ↓
POST /login
        ↓
JWT
        ↓
API requests with Bearer token
```

For example:

```text
ADMIN
  ↓
Create company
  ↓
Create job

USER
  ↓
View job
  ↓
Apply for job

ADMIN
  ↓
View applications
  ↓
Update application status
```

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── ro/mihai/careerhub/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       │   ├── request/
│   │       │   └── response/
│   │       ├── entity/
│   │       ├── enums/
│   │       ├── exception/
│   │       ├── mapper/
│   │       ├── repository/
│   │       └── service/
│   │
│   └── resources/
│       ├── application.properties
│       └── db/
│           └── migration/
│               └── V1__initial_schema.sql
│
└── test/
    └── java/
        └── ro/mihai/careerhub/
```

## Example Workflow

Create a company as an administrator:

```http
POST /companies
Authorization: Bearer <ADMIN_TOKEN>
```

Create a job:

```http
POST /jobs
Authorization: Bearer <ADMIN_TOKEN>
```

A user can then apply:

```http
POST /applications
Authorization: Bearer <USER_TOKEN>
Content-Type: application/json
```

```json
{
  "jobId": 1
}
```

The backend determines the user from the JWT rather than trusting a `userId` supplied by the client.

## Future Improvements

Possible future additions include:

* refresh tokens
* password reset
* email verification
* additional job search criteria
* automated API documentation
* CI/CD pipeline
* frontend application

These are intentionally outside the current project scope.