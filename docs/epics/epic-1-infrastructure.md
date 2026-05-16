# Epic 1: Microservices Infrastructure

**Objective:**  Build the architectural foundation for the LEMINI Microservices Platform. This Epic provides the essential infrastructure (Gateway, Config, Registry) required to support a scalable microservices ecosystem.

---

## Scope
- **Monorepo Setup:** Parent POM and Git structure.
- **Config Server:** Centralized configuration management.
- **Discovery Service:** Eureka Server for service registration.
- **API Gateway:** Single entry point for all client requests.
- **User Service Skeleton:** Empty service to verify connectivity.

### Moved Out:

- **Containerization:** Docker/Kubernetes (Moved to **Epic 6** to focus on local dev stability first).

---

## Technical Notes:

- **Stack:** Java 17, Spring Boot 3.x, Spring Cloud 2023.x.
- **Structure:** Git Monorepo using Maven Multi-module (Parent POM for dependency management).
- **Port Mapping:** Config (`8888`), Eureka (`8761`), Gateway (`8080`), User-Service (`8081`).
- **Tracing:** Micrometer with Zipkin exporter.

---

## Definition of Done
- [x] All Stories in this Epic are completed.
- [x] System starts up without errors in a local environment.
- [x] Services are registered and "UP" in the Eureka Dashboard.
- [x] API Gateway successfully routes traffic to the User Service.
- [x] Traces are successfully captured in Zipkin.