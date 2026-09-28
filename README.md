# LEMINI Microservices Platform

**Built with Java 17, Spring Boot 3, and Spring Cloud.**

## Project Overview

LEMINI is a microservices platform focused on secure service communication, independent service ownership, distributed tracing, and progressive deployment from local development to containerized and cloud environments.

The platform currently centers on user management and security, with Account and Transaction services planned as the next core business capabilities.

Future milestones extend the platform with Docker, Kubernetes, AWS deployment, event-driven communication, reliability improvements, and GenAI integration.

---

## Architecture

<p align="center">
  <img src="docs/images/lemini-core-architecture.png"
       alt="LEMINI Core Application Architecture"
       width="900">
</p>

The platform architecture includes:

- Spring Cloud Gateway as the external API entry point.
- Eureka for service registration and discovery.
- Spring Cloud Config for centralized configuration.
- User Service for identity and profile management.
- Account Service for account ownership and balance management.
- Transaction Service for transfers and transaction history.
- Independent persistence ownership for each business service.
- Micrometer and Zipkin for distributed tracing.

See [`docs/architecture/system-design.md`](docs/architecture/system-design.md) for the detailed architecture description.

---

## Current Technology Stack

| Area | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Cloud Framework | Spring Cloud 2023.x |
| Security | Spring Security 6, JWT, BCrypt |
| API Gateway | Spring Cloud Gateway |
| Service Discovery | Netflix Eureka |
| Configuration | Spring Cloud Config |
| Persistence | Spring Data JPA, Hibernate, AWS SDK for Java 2.x DynamoDB Enhanced Client |
| Database | MySQL, H2 |
| Database Migration | Flyway |
| NoSQL Database | DynamoDB Local / Amazon DynamoDB |
| Validation | Jakarta Bean Validation / Hibernate Validator |
| API Documentation | Springdoc OpenAPI / Swagger |
| Testing | JUnit 5, Mockito, MockMvc |
| Observability | Micrometer, Zipkin |
| Build | Maven Multi-module |

---

## Application Profiles

| Profile | Database | Purpose |
|---|---|---|
| `demo` | H2 | Zero-setup local demo |
| `test` | H2 | Automated tests |
| `dev` | MySQL | Local development |
| `stage` | MySQL | Staging environment |
| `prod` | MySQL | Production environment |

The `demo` profile allows the User Service to run without installing MySQL or configuring production secrets.

Stage and production credentials, JWT secrets, and administrator bootstrap credentials are supplied through environment variables and are not stored in the repository.

---

## Planned Platform Capabilities

The platform is developed incrementally through defined milestones.

Planned capabilities include:

- Docker and Docker Compose for local containerized execution.
- CI/CD automation and quality checks.
- Local Kubernetes orchestration.
- AWS deployment using EKS and supporting AWS services.
- Centralized logging, metrics, tracing, and resilience mechanisms.
- Event-driven communication.
- Production hardening.
- GenAI integration through the deferred GenAI Orchestrator Epic.

See [`docs/roadmap/milestones.md`](docs/roadmap/milestones.md) for the complete milestone plan.

---

## Repository Structure

```text
lemini-microservices-platform/
├── lemini-config-server/
├── lemini-discovery-service/
├── lemini-gateway/
├── lemini-user-service/
├── docs/
│   ├── architecture/
│   ├── epics/
│   ├── guides/
│   ├── images/
│   └── roadmap/
└── pom.xml