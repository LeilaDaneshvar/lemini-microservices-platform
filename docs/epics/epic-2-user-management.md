# Epic 2: User Management Service

**Objective:** Implement a secure Identity Management System that allows users to register, authenticate (login), and manage their profiles. This service will act as the "Source of Truth" for user identity across the LEMINI platform.


**Target Milestone:** LEMINI Core Platform v0.1

---

## Scope

### In Scope
- **User Registration:** REST API to create new users with request validation.
- **User Profile Management:** Retrieve, update, delete, and list user profiles through REST endpoints.
- **Basic Authentication Foundation:** Login authentication with JWT issuance and bearer-token protection for secured User APIs.
- **Data Persistence:** Store user and related profile data in an H2 relational database.
- **Password Security:** Hash passwords using BCrypt before persistence.
- **Validation & Privacy:** Validate inbound requests and prevent sensitive fields such as passwords from being exposed in API responses.
- **Documentation:** Auto-generated API documentation using OpenAPI.


### Moved Out
- **Advanced JWT & Session Management:** Refresh-token lifecycle, token revocation, advanced expiration/tamper handling, and session management.
- **Role-Based Access Control:** Role-Based Access Control (RBAC).
- **Resource Ownership:** User ownership and self-resource authorization.
- **User Account Security:** Account locking and advanced login-security controls.
- **Multi-Factor Authentication:** 2FA and other advanced authentication mechanisms.

---

## Technical Notes
- **Encryption:** BCrypt algorithm with a workload factor strength of `10`.
- **Validation:** Framework handling powered by `Hibernate Validator`.
- **Testing Fabric:** Unit and isolation testing implemented via `JUnit 5` and `Mockito`.
- **Documentation:** Structured via `springdoc-openapi` (Available locally at `/swagger-ui.html`).

---

## Definition of Done

### Feature Completion
- [ ] **Story Verification:** All defined child user stories and their specific Acceptance Criteria (AC) are fully met.
- [ ] **Defect Verification:** No critical defects identified during audit verification in the local development environment.
- [ ] **CRUD Functionality:** Full CRUD operations for user entities are entirely functional via exposed REST endpoints.

### Code Quality & Privacy
- [x] **Build Quality:** Codebase compiles cleanly with zero errors or breaking deployment warnings.
- [x] **Configuration Management:** externalized configuration using profiles/environment variables.
- [x] **Sensitive Data Protection:** Sensitive data (hashed passwords) is excluded from API responses using DTOs.

### Testing Verification
- [x] **Unit Testing:** Core service and repository business logic is covered by automated tests for primary success paths and relevant edge/error cases.
- [x] **Integration Testing:** Exposed REST endpoints are validated using `MockMvc`, including successful requests and defined failure scenarios.
- [x] **Manual Verification:** Epic 2 endpoints are smoke-tested using the maintained local `api-tests.http` request files.
- [x] **Automated Test Suite:** The complete Epic 2 automated test suite passes successfully.

### Security Controls
- [x] **Input Validation:** Inbound payloads are strictly validated using Jakarta Bean Validation constraints, and passwords are hashed before database persistence.
- [x] **Endpoint Protection:** Endpoint path filtering is actively configured to differentiate between public and protected resources.

### Documentation & Database Setup
- [x] **API Documentation:** Live API structures are dynamically exposed using OpenAPI with clean, explicit data schemas mapping out request/response types.
- [x] **Database Initialization:** Relational database schema models initialize cleanly across development runtime environments.
---

## Stories
- [x] **2.1**: User Registration API (Implementation of POST for register with BCrypt hashing).
- [x] **2.2**: User Login API (Authentication with email/password and JWT issuance).
- [x] **2.3**: Get User Profile API (Implementation of GET for user data).
- [x] **2.4**: Update User Profile API (Implementation of PUT for user data).
- [x] **2.5**: Delete User API (Authenticated DELETE operation).
- [x] **2.6**: List User Profiles API (Authenticated paginated user retrieval).
- [ ] **Audit:** Epic 2 Definition of Done Verification