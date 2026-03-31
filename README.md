JobPortal Microservices Ecosystem

A robust, scalable, and highly available Job Portal application built on a modern Microservices Architecture. This project demonstrates industry-standard patterns including Service Discovery, API Gateway, Distributed Tracing, Event-Driven Communication, and a resilient Outbox pattern for reliable message delivery.

🛠 Tech Stack & Tools

Backend & Core

Technology

Usage

Logo

Java 17

Primary Language



Spring Boot 3

Framework



Spring Cloud

Microservices Toolkit



PostgreSQL

Relational Database



Apache Kafka

Event Streaming



Redis

Distributed Caching



Observability & Infrastructure

Technology

Usage

Logo

ELK Stack

Centralized Logging



Eureka

Service Discovery



Docker

Containerization



Flyway

DB Migrations



🏗 System Architecture & Features

1. Microservices Breakdown

Discovery Server (Eureka): Central hub for service registration and discovery.

API Gateway: The entry point for all clients. Handles routing, security filtering, and JWT verification.

User Service: Manages user registration, authentication (JWT), and profile management.

Job Service: Handles job postings, job details, and integrates with Redis for high-performance job listings.

Application Service: Manages the lifecycle of a job application, including resume uploads.

2. Resilient Event-Driven Design (The Outbox Pattern)

To ensure Atomicity between database updates and Kafka message publishing, we implemented the Transactional Outbox Pattern in the ApplicationService.

How it works: Instead of sending a message directly to Kafka, the service saves the event in an events table within the same transaction as the business logic.

Poller: A dedicated background scheduler (OutboxEventPoller) fetches "PENDING" events and publishes them to Kafka, ensuring no data loss even if the message broker is temporarily down.

3. Distributed Caching

Job Service utilizes Redis to cache frequently accessed job posts, significantly reducing database load and improving response times for public job seekers.

4. Centralized Logging (ELK)

Logs from all microservices are streamed to Logstash, processed, and stored in Elasticsearch, making it easy to debug cross-service issues via Kibana.

📡 API Endpoints

User Service

Method

Endpoint

Description

Auth

POST

/api/users/register

Register a new user

Public

POST

/api/users/login

Authenticate and get JWT

Public

GET

/api/users/profile

Get logged-in user details

JWT

Job Service

Method

Endpoint

Description

Auth

POST

/api/jobs/create

Create a new job post

Admin/Employer

GET

/api/jobs/all

List all jobs (Paginated/Cached)

Public

GET

/api/jobs/{id}

Get specific job details

Public

Application Service

Method

Endpoint

Description

Auth

POST

/api/applications/apply

Apply for a job (Multipart Resume)

JWT

GET

/api/applications/my

View my applications

JWT

🚀 Getting Started

Prerequisites

Docker & Docker Compose

Java 17 (for local development)

Maven

Running with Docker

Simply clone the repo and run the following command to spin up the entire ecosystem (Databases, Kafka, ELK, and Microservices):

docker-compose up --build


Service URLs (Default)

Eureka Dashboard: http://localhost:8761

API Gateway: http://localhost:8080

Kibana (Logs): http://localhost:5601

🧪 Testing

The project includes comprehensive testing suites:

Unit Tests: Mockito & JUnit 5.

Integration Tests: Using Testcontainers to spin up real PostgreSQL and Kafka instances during the build process.

mvn clean test


🌟 Why this project is impressive

Fault Tolerance: Use of the Outbox pattern guarantees eventual consistency.

Performance: Redis caching implementation for high-traffic endpoints.

Security: Centralized JWT authentication at the Gateway level.

DevOps Ready: Fully containerized with Docker Compose and managed DB migrations via Flyway.

Observability: Integrated ELK stack for professional-grade logging.
