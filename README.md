# Job Portal Microservices

A production-ready, cloud-native Job Portal backend built with Spring Boot 3 and Spring Cloud. The system follows a microservices architecture where each service owns its domain, communicates asynchronously via Kafka, and is fronted by a single API Gateway.

---

## Architecture Overview

```
Client
  │
  ▼
API Gateway (Spring Cloud Gateway + JWT Auth)
  │
  ├──► User Service       (Auth, Registration, User lookup)
  ├──► Job Service        (Job CRUD, Redis Cache, Kafka Consumer)
  └──► Application Service (Apply to Jobs, Resume Upload, Kafka Producer)

All services register with Eureka Discovery Server
All services ship logs to ELK Stack (Elasticsearch + Logstash + Kibana)
All services export traces to Zipkin
```

---

## Services

| Service | Port | Description |
|---|---|---|
| `discovery-server` | 8761 | Eureka Service Registry |
| `api-gateway` | 8080 | Single entry point, JWT validation, routing |
| `user-service` | 8081 | User registration, login, JWT issuance |
| `jobservice` | 8082 | Job post CRUD, search, Redis caching |
| `application-service` | 8083 | Job applications, resume upload, Kafka events |

---

## Tech Stack

### Core Framework

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language (Virtual Threads enabled) |
| Spring Boot | 3.4.9 | Application framework |
| Spring Cloud | 2024.0.2 | Microservices toolkit |
| Maven | - | Build tool (multi-module) |

### Service Communication

| Technology | Purpose |
|---|---|
| Spring Cloud Gateway | API Gateway, routing, filter chain |
| Netflix Eureka | Service discovery and registration |
| OpenFeign | Declarative HTTP client between services |
| Apache Kafka | Async event streaming (job application events) |
| Resilience4j | Circuit breaker for inter-service calls |

### Data & Caching

| Technology | Purpose |
|---|---|
| MySQL 8.0 | Primary relational database (3 separate schemas) |
| Spring Data JPA / Hibernate | ORM layer |
| Flyway | Database schema migrations |
| Redis 6 | Response caching for public job listings |

### Security

| Technology | Purpose |
|---|---|
| Spring Security | Authentication and authorization |
| JJWT 0.12.5 | JWT generation and validation |
| Role-based Access | SEEKER / RECRUITER / ADMIN roles |

### Observability

| Technology | Port | Purpose |
|---|---|---|
| Zipkin | 9411 | Distributed tracing |
| Micrometer Brave | - | Trace instrumentation |
| Elasticsearch | 9200 | Log storage |
| Logstash | 5044 | Log ingestion pipeline |
| Kibana | 5601 | Log visualization |
| Spring Actuator | - | Health checks and metrics |

### Developer Experience

| Technology | Purpose |
|---|---|
| Lombok | Boilerplate reduction |
| SpringDoc OpenAPI 2.8.5 | Swagger UI per service |
| spring-dotenv | `.env` file support |
| Testcontainers | Integration testing with real MySQL/Kafka |
| Docker Compose | Full local environment orchestration |

---

## Databases

| Database | Used By | Schema |
|---|---|---|
| `user_db` | user-service | `users` table |
| `jobpost_db` | jobservice | `job_post`, `processed_events` tables |
| `jobApplication_db` | application-service | `job_applications`, `outbox_events` tables |

---

## API Endpoints

All requests go through the API Gateway at `http://localhost:8080`. The gateway strips the `/api` prefix before forwarding.

### Authentication (Public — no token required)

| Method | Gateway Path | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user (SEEKER or RECRUITER) |
| `POST` | `/api/auth/login` | Login and receive a JWT token |

### Users (Protected — JWT required)

| Method | Gateway Path | Description | Role |
|---|---|---|---|
| `GET` | `/api/users/{recruiter-id}/recruiter-id` | Verify a recruiter by ID | Any |
| `GET` | `/api/users/by_email/{email}` | Get seeker ID by email (used by Application Service) | Internal |
| `GET` | `/api/users/by-email/{email}` | Get full user object by email (used by Job Service) | Internal |

### Jobs — Protected (JWT required)

| Method | Gateway Path | Description | Role |
|---|---|---|---|
| `POST` | `/api/jobs/post` | Create a new job posting | RECRUITER |
| `PUT` | `/api/jobs/updatejob/{jobId}` | Update an existing job posting | RECRUITER |
| `DELETE` | `/api/jobs/delete-job/{jobId}` | Delete a job posting | RECRUITER |
| `GET` | `/api/jobs/my-jobs` | Get all jobs posted by the authenticated recruiter | RECRUITER |
| `GET` | `/api/jobs/{jobId}/applications` | View all applications for a specific job | RECRUITER |

### Jobs — Public (no token required)

| Method | Gateway Path | Description |
|---|---|---|
| `GET` | `/api/public` | Get all job posts (paginated, Redis cached) |
| `GET` | `/api/public/search?keyword=` | Search jobs by title, description, or location (cached) |
| `GET` | `/api/public/{job-id}/job-id` | Get job ID by ID (used internally) |
| `PUT` | `/api/public/internal/job/{jobId}/increment-count` | Increment application count (internal) |

### Job Applications — Protected (JWT required)

| Method | Gateway Path | Description | Role |
|---|---|---|---|
| `POST` | `/api/job-applications/apply-to-job/{job-id}` | Apply to a job with resume PDF upload | SEEKER |
| `GET` | `/api/job-applications/my-applications` | Get all applications submitted by the seeker | SEEKER |
| `GET` | `/api/job-applications/debug/thread-info` | Debug virtual thread info | Any |

### Job Applications — Public / Internal

| Method | Gateway Path | Description |
|---|---|---|
| `GET` | `/api/all/job-application/{jobId}` | Get application count for a job |
| `GET` | `/api/all/applications-list/{jobId}` | Get full application list for a job |
| `DELETE` | `/api/all/delete-applications/{jobId}` | Delete all applications for a job |

---

## Design Patterns

| Pattern | Where Used | Description |
|---|---|---|
| **API Gateway Pattern** | `api-gateway` | Single entry point for all client requests |
| **Service Discovery** | All services + Eureka | Services register and discover each other dynamically |
| **JWT Authentication** | `api-gateway` + `user-service` | Stateless auth; gateway validates token and injects `X-User-Email` / `X-User-Role` headers |
| **Transactional Outbox Pattern** | `application-service` | Job application saved + outbox event written in one transaction; a poller publishes to Kafka |
| **Idempotent Consumer Pattern** | `jobservice` | Kafka consumer checks `processed_events` table before processing to prevent duplicate increments |
| **Circuit Breaker Pattern** | `application-service`, `jobservice` | Resilience4j wraps Feign calls to User Service and Job Service with fallback logic |
| **Redis Caching** | `jobservice` | Public job listings and search results cached in Redis with 60-minute TTL |
| **Role-Based Access Control** | All protected routes | SEEKER and RECRUITER roles enforced at gateway and service level |
| **Database per Service** | All services | Each service owns its own MySQL schema (`user_db`, `jobpost_db`, `jobApplication_db`) |
| **Async Processing** | `application-service` | Resume upload runs asynchronously using Java 21 Virtual Threads |
| **Pagination** | `jobservice` | Public job listing supports Spring Data `Pageable` with custom `PageWrapper` DTO |
| **Global Exception Handling** | `jobservice`, `application-service` | `@RestControllerAdvice` for consistent error responses |
| **Distributed Tracing** | All services | Micrometer Brave + Zipkin traces requests across service boundaries |
| **Centralized Logging** | All services | Logstash encoder ships structured JSON logs to ELK stack |

---

## Key Features

| Feature | Details |
|---|---|
| Multi-module Maven build | Single parent POM manages all versions centrally |
| Docker Compose orchestration | One command spins up all services + infrastructure |
| Flyway migrations | Schema versioning per service database |
| Virtual Threads (Java 21) | Async executor uses `Thread.ofVirtual()` for lightweight concurrency |
| Resume file upload | PDF resume stored on disk, path persisted to DB |
| Duplicate application guard | `existsBySeekerIdAndJobPostId` check before saving |
| Swagger UI | Available per service at `/swagger-ui.html` |
| `.env` configuration | All secrets loaded from `.env` via `spring-dotenv` |
| Testcontainers | Integration tests spin up real MySQL and Kafka containers |

---

## Running Locally

### Prerequisites

| Requirement | Version |
|---|---|
| Docker & Docker Compose | Latest |
| Java | 21+ |
| Maven | 3.9+ |

### Steps

**1. Clone and configure environment**
```bash
git clone <repo-url>
cd <project-root>
# Create .env file with required variables (see below)
```

**2. Start all infrastructure and services**
```bash
docker-compose up --build
```

**3. Access services**

| Service | URL |
|---|---|
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| Zipkin UI | http://localhost:9411 |
| Kibana | http://localhost:5601 |
| Swagger (User Service) | http://localhost:8081/swagger-ui.html |
| Swagger (Job Service) | http://localhost:8082/swagger-ui.html |
| Swagger (Application Service) | http://localhost:8083/swagger-ui.html |

---

## Environment Variables

| Variable | Description |
|---|---|
| `DB_PASSWORD` | MySQL root password |
| `DB_USERNAME` | MySQL username |
| `JWT_SECRET_KEY` | Secret key for JWT signing |
| `USER_DB_URL` | JDBC URL for user_db |
| `JOB_DB_URL` | JDBC URL for jobpost_db |
| `APPLICATION_DB_URL` | JDBC URL for jobApplication_db |
| `EUREKA_SERVER_URL` | Eureka server URL |
| `REDIS_HOST` | Redis hostname |
| `REDIS_PORT` | Redis port |
| `REDIS_PASSWORD` | Redis password |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address |
| `KAFKA_TOPIC_JOB_APPLICATION` | Kafka topic name for job application events |
| `ZIPKIN_BASE_URL` | Zipkin server URL |
| `DISCOVERY_SERVER_PORT` | Port for Eureka server |
| `USER_SERVICE_PORT` | Port for user-service |
| `JOB_SERVICE_PORT` | Port for jobservice |
| `APPLICATION_SERVICE_PORT` | Port for application-service |
| `API_GATEWAY_PORT` | Port for api-gateway |

---

## Project Structure

```
.
├── pom.xml                    # Parent POM (version management)
├── docker-compose.yml         # Full stack orchestration
├── .env                       # Environment variables (not committed)
├── mysql-init/init.sql        # Database schema initialization
├── logstash/pipeline/         # Logstash pipeline config
├── discovery-server/          # Eureka Server
├── api-gateway/               # Spring Cloud Gateway + JWT filter
├── user-service/              # Auth, registration, user lookup
├── jobservice/                # Job CRUD, Redis cache, Kafka consumer
└── ApplicationService/        # Job applications, resume upload, Kafka producer
```
