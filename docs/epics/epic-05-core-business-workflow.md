# Epic 5: Account & Transaction Services — Planned

**Objective:** Implement independent Account and Transaction microservices that provide account management and transfer processing with authorization, independent persistence, service-to-service communication, gateway routing, service discovery, and end-to-end traceability.

**Target Milestone:** LEMINI Core Platform v0.1

---

## Scope

### In Scope

- **Account Service:** Implement `lemini-account-service` as an independent Spring Boot module responsible for account state, ownership references, and balances.
- **Account REST API:** Implement separate endpoints for account creation, account retrieval, and user account listing.
- **Transaction Service:** Implement `lemini-transaction-service` as an independent Spring Boot module responsible for transfer requests, transaction state, and transaction history.
- **Transfer REST API:** Implement separate endpoints for transfer creation, transfer retrieval, and account transaction history.
- **Transfer Processing:** Implement the synchronous transfer workflow between Transaction Service and Account Service.
- **Service-to-Service Communication:** Transaction Service must call Account Service through a defined service API. Direct cross-service database access is prohibited.
- **Security:** Apply Epic 3 authentication, authorization, roles, authorities, and ownership rules to Account and Transfer operations.
- **Independent Persistence:** Account Service and Transaction Service must own separate schemas/databases and persist their own domain state.
- **Gateway & Discovery:** Register both services with Eureka and expose their public APIs through the API Gateway.
- **Failure Handling:** Return controlled and consistent API errors for validation, authorization, business-rule, and downstream-service failures.
- **Idempotency:** Prevent duplicate processing of the same transfer request from applying account balance changes more than once.
- **Distributed Tracing:** Preserve trace context across Gateway → Transaction Service → Account Service and expose the complete request flow in Zipkin.
- **API Documentation:** Provide OpenAPI documentation for the Account and Transfer APIs.

### Out of Scope

- **Event-Driven Transaction Processing:** Kafka/RabbitMQ-based transfer processing is deferred to a later Epic.
- **Saga / Outbox Patterns:** The initial transfer implementation uses synchronous orchestration. Eventual-consistency patterns are deferred.
- **Notifications:** Email, SMS, and push notifications are not part of this Epic.
- **GenAI Integration:** AI orchestration, RAG, semantic search, and AI-based analysis remain part of Epic 4.

---

## Technical Requirements

- **Language:** Java 17.
- **Framework:** Spring Boot 3.x and Spring Cloud 2023.x.
- **Build:** Maven multi-module monorepo.
- **Modules:** Add `lemini-account-service` and `lemini-transaction-service` to the parent `pom.xml`.
- **Discovery:** Both services must register as Eureka clients.
- **Centralized Configuration:** Both services must use the existing LEMINI configuration approach.
- **Gateway:** Spring Cloud Gateway must route `/accounts/**` and `/transfers/**` to the correct services.
- **Inter-Service Communication:** Use a Spring-supported HTTP client with Eureka service discovery/load balancing for the synchronous workflow.
- **Persistence:** Use Spring Data JPA with MySQL. H2 may be used for isolated tests where appropriate.
- **Database Ownership:** Use separate logical schemas/databases, such as `lemini_accounts` and `lemini_transactions`.
- **Schema Management:** Database schema changes must be reproducible through versioned migrations such as Flyway or Liquibase.
- **Transaction Boundary:** Account debit and credit changes must commit atomically inside Account Service.
- **Security:** Reuse Epic 3 JWT, RBAC, authority, ownership, and security-context rules.
- **Validation:** Use Jakarta Bean Validation for request validation and domain-level validation for business rules.
- **Testing:** Use JUnit 5, Mockito, MockMvc, persistence tests, security tests, and end-to-end integration tests.
- **Observability:** Use Micrometer tracing and Zipkin propagation across all participating services.
- **Documentation:** Maintain OpenAPI/Swagger definitions and update architecture and local-setup documentation.

---

## Definition of Done

### 1. Account Service

- [ ] `lemini-account-service` is included in the Maven parent project and starts successfully.
- [ ] Account Service registers with Eureka.
- [ ] Account Service loads its centralized configuration successfully.
- [ ] Account Service connects to and owns its dedicated account schema/database.
- [ ] Versioned schema migration initializes the account schema successfully.
- [ ] API Gateway routes Account API requests correctly.
- [ ] Authenticated users can create accounts.
- [ ] Authorized users can retrieve account details.
- [ ] Authorized users can list accounts according to ownership and role rules.
- [ ] Account status and balances are managed only by Account Service.
- [ ] Invalid account requests return the expected validation, authorization, or business errors.
- [ ] Account API unit, integration, persistence, and security tests pass.
- [ ] Account API OpenAPI/Swagger documentation is complete.
- [ ] Account Service Definition of Done audit passes.

### 2. Transaction Service 

- [ ] `lemini-transaction-service` is included in the Maven parent project and starts successfully.
- [ ] Transaction Service registers with Eureka.
- [ ] Transaction Service loads its centralized configuration successfully.
- [ ] Transaction Service connects to and owns its dedicated transaction schema/database.
- [ ] Versioned schema migration initializes the transaction schema successfully.
- [ ] API Gateway routes Transfer API requests correctly.
- [ ] An authenticated and authorized user can submit a valid transfer.
- [ ] Transaction records are persisted in the Transaction Service-owned schema/database.
- [ ] Transfers expose a stable transaction identifier and lifecycle state.
- [ ] Transfer details can be retrieved by transaction ID.
- [ ] Transaction history can be retrieved for an authorized account.
- [ ] Invalid amounts, invalid accounts, insufficient balances, and unauthorized source-account access are rejected correctly.
- [ ] Duplicate processing cannot apply an account balance change more than once for the same transfer request.
- [ ] Transaction API unit, integration, persistence, and security tests pass.
- [ ] Transfer API OpenAPI/Swagger documentation is complete.
- [ ] Transaction Service Definition of Done audit passes.

### 3. Cross-Service 

- [ ] Transaction Service communicates with Account Service through the defined service API.
- [ ] No service directly reads or writes another service's schema/tables.
- [ ] Account debit and credit changes are atomic inside Account Service.
- [ ] Transaction state is updated correctly for successful and failed account operations.
- [ ] Downstream Account Service failures are translated into controlled Transaction API responses.
- [ ] Authentication/authorization context required for the transfer flow is preserved and enforced.
- [ ] Duplicate transfer submissions are handled idempotently.
- [ ] The primary Gateway → Transaction Service → Account Service workflow passes end-to-end tests.
- [ ] Trace IDs propagate across Gateway, Transaction Service, and Account Service.
- [ ] Transfer processing can be followed end-to-end in Zipkin.
- [ ] Epic 5 End-to-End Integration audit passes.

### 4. Platform & Documentation

- [ ] The full Maven build succeeds with both new modules included.
- [ ] All automated tests pass.
- [ ] Architecture documentation includes Account Service, Transaction Service, their ownership boundaries, and communication flow.
- [ ] Local setup documentation includes the new services and their databases.
- [ ] Public API documentation matches the implemented Account and Transfer APIs.

---

## Stories
### Account Service
- [ ] **5.1:** Account Service Foundation & Persistence
- [ ] **5.2:** Create Account API
- [ ] **5.3:** Get Account API
- [ ] **5.4:** List User Accounts API
- [ ] **Audit:** Account Service Definition of Done Verification

### Transaction Service
- [ ] **5.5:** Transaction Service Foundation & DynamoDB Persistence
- [ ] **5.6:** Create Transfer API
- [ ] **5.7:** Get Transfer API
- [ ] **5.8:** List Account Transactions API
- [ ] **Audit:** Transaction Service Definition of Done Verification

### Cross-Service Integration
- [ ] **5.9:** Cross-Service Transfer Workflow
- [ ] **5.10:** Cross-Service Security, Error Handling, Idempotency & Distributed Tracing
- [ ] **Audit:** Epic 5 End-to-End Integration Verification