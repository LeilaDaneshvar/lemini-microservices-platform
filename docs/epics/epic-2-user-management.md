# Epic 2: User Management Service

**Objective:** Implement a secure and robust Identity Management System that allows users to register, authenticate (login), and manage their profiles. This service will act as the "Source of Truth" for user identity across the LEMINI platform.

---

## Scope

### In Scope
- **User Registration:** REST API to create new users with validation.
- **Authentication:** Secure Login using JWT (JSON Web Tokens).
- **Authorization:** Role-Based Access Control (Admin vs. User).
- **Data Persistence:** Storing user data in a relational database (H2 for Dev, MySQL for Prod).
- **Security:** Password encryption via BCrypt and API endpoints protection.
- **Documentation:** Auto-generated API documentation using Swagger/OpenAPI.

### Moved Out
- **JWT & Sessions:** Authentication logic details deferred to **Epic 3: Security & Identity**.
- **Refresh Tokens:** Long-lived session management deferred to **Epic 3: Security & Identity**.
- **Email Notifications:** Sending "Welcome" emails deferred to **Epic 5**.
- **Forgot Password:** Password recovery flow deferred to **Epic 5**.

---

## Technical Notes
- **Encryption:** BCrypt algorithm with a workload factor strength of `10`.
- **Validation:** Framework handling powered by `Hibernate Validator`.
- **Testing Fabric:** Unit and isolation testing implemented via `JUnit 5` and `Mockito`.
- **Documentation:** Structured via `springdoc-openapi` (Available locally at `/swagger-ui.html`).

---

## Definition of Done

### Feature Completion
- [x] All defined child user stories and their specific Acceptance Criteria (AC) are fully met.
- [x] Entire component features function flawlessly in the local development environment without critical bugs.
- [x] Full CRUD operations for user entities are entirely functional via exposed REST endpoints.

### Code Quality & Privacy
- [x] Codebase compiles cleanly with zero errors or breaking deployment warnings.
- [x] No hardcoded secrets (use application.properties).
- [x] Sensitive data (hashed passwords) is excluded from API responses using DTOs.

### Testing Verification
- [x] **Unit Testing:** Core Service and Repository layer classes hit a branch coverage threshold of `> 80%`.
- [x] **Integration Testing:** Edge controller endpoints are thoroughly validated using `MockMvc` setups.
- [x] **Manual Verification:** Endpoints physically smoke-tested and verified via local `api-tests.http` script files.

### Security Controls
- [x] Inbound payloads strictly checked using bean validation constraints (`JSR-380`), and passwords are hashed prior to database commitment.
- [x] Endpoint path filtering is actively configured to differentiate between Public and Protected resources.

### Documentation & Database Setup
- [x] Live API structures are dynamically exposed using Swagger/OpenAPI with clean, explicit data schemas mapping out request/response types.
- [x] Relational database schema models initialize cleanly across development runtime environments.