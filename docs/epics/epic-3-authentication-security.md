# EPIC 3: Authentication, RBAC & Advanced Security 

**Objective**: Establish a secure and production-ready authentication and authorization foundation for LEMINI. This Epic introduces environment-aware database initialization, persistent security data, JWT-based authentication, session management, Role-Based Access Control (RBAC), account protection, and two-factor authentication.

**Target Milestone:** LEMINI Core Platform v0.1

---

##  Scope

### **In Scope**

* **Environment & Database Initialization**: Establish environment-specific profiles for `demo`, `test`, `stage`, and `prod`; use H2 for demo/test environments and MySQL for stage/production; introduce Flyway for database schema versioning and required reference/security data initialization.

* **Security Data Initialization**: Initialize required security reference data such as roles, authorities, and role-authority mappings through versioned database migrations.

* **Administrative Bootstrap**: Provide controlled initialization of demo and administrative users according to the active environment, without storing production credentials in source control.

* **Authentication**: Refine the login flow and JWT generation.

* **Session Management**: Store refresh tokens in the database to support secure, long-lived client sessions and token revocation.

* **Authorization**: Configure the Spring Security Filter Chain and Role-Based Access Control (RBAC), supporting `ROLE_USER` and `ROLE_ADMIN`.

* **Security Documentation**: Configure OpenAPI/Swagger to support JWT Bearer authentication.

* **2FA (Two-Factor Authentication)**: Integrate Time-based One-Time Password (TOTP) authentication using Google Authenticator-compatible applications.

* **Account Management**: Implement account lock/enable behavior after repeated failed login attempts.

### **Moved Out**

* **Email Service**: SES/SMTP integration is deferred to a future Epic.

---
## Technical Notes

* **Security Framework**: Spring Security 6.x.
* **JWT Library**: `io.jsonwebtoken` (`jjwt`).
* **Cryptographic Keys**: JWT signatures use HMAC keys generated through `Keys.hmacShaKeyFor`, with HS256 unless the implementation is intentionally changed later.
* **Token Storage**: Access tokens remain stateless. Refresh tokens are persisted in the database to support lifecycle management and revocation.
* **Environment Profiles**: The application supports dedicated `demo`, `test`, `stage`, and `prod` profiles.
* **Database Strategy**:
  * H2 is used for demo and automated test environments.
  * MySQL is used for stage and production environments.
* **Database Schema Management**: Flyway owns schema creation and database versioning. Applied migrations are immutable; future schema changes are introduced through new migration versions.
* **Reference/Security Data**: Required coding data such as roles, authorities, and role-authority mappings is initialized through Flyway migrations.
* **Hibernate Schema Management**: Hibernate is used for ORM and schema validation rather than production schema creation.
* **Demo Initialization**: Demo-specific sample data and the demo administrator account are initialized only when the `demo` profile is active.
* **Administrative Bootstrap**: Stage and production administrator creation uses an explicit bootstrap mechanism with credentials supplied through environment configuration rather than source-controlled values.
* **Authorization Model**: Role-Based Access Control (RBAC) supports `ROLE_USER` and `ROLE_ADMIN`, with finer-grained authorities where required.
* **Spring Security Context**: Authentication is established during request processing and stored in the request-specific `SecurityContext`.
* **Security Filter Chain**: JWT authentication filters execute before protected controller access and establish authenticated principals and authorities for authorization decisions.
* **HTTP Security Semantics**:
  * Missing, invalid, expired, or tampered authentication credentials return `401 Unauthorized`.
  * Authenticated users without sufficient authority receive `403 Forbidden`.
* **2FA Library Engine**: TOTP integration may use `com.warrenstrange:googleauth` or another maintained Google Authenticator-compatible implementation.

---

## Definition of Done (DoD)

### 1. Environment & Database Foundation

- [ ] The application provides working `demo`, `test`, `stage`, and `prod` profiles.
- [ ] Demo and automated tests run using H2 without requiring a locally installed MySQL instance.
- [ ] Stage and production profiles are configured to use MySQL.
- [ ] Production-sensitive configuration, including database credentials and JWT secrets, is supplied externally and is not committed to source control.
- [ ] Flyway is integrated into application startup and executes database migrations before application runtime initialization.
- [ ] Database schema creation and evolution are managed through versioned Flyway migrations.
- [ ] Hibernate validates entity-to-schema compatibility rather than owning production schema generation.
- [ ] Existing applied Flyway migrations are not modified; future changes are introduced as new migration versions.

### 2. Security Reference Data & Bootstrap

- [ ] Required roles, authorities, and role-authority mappings are initialized through Flyway migrations.
- [ ] The required security reference data is consistent across demo, test, stage, and production environments.
- [ ] Demo startup automatically provisions a usable demo administrator account when the `demo` profile is active.
- [ ] Demo credentials are isolated from production configuration.
- [ ] Stage and production administrator creation is supported through an explicit bootstrap mechanism.
- [ ] Production administrator credentials are supplied through secure runtime configuration and are not stored in the repository.
- [ ] Administrative bootstrap is idempotent and does not create duplicate administrator accounts.

### 3. Security Handshake

- [ ] The `/login` endpoint validates active user credentials and returns valid signed JWTs.
- [ ] JWT signature validation rejects structurally invalid or tampered tokens.
- [ ] Expired JWTs are rejected.
- [ ] Missing, invalid, expired, or tampered authentication tokens result in `401 Unauthorized`.
- [ ] Successful authentication creates an authenticated principal available through Spring Security's `SecurityContext`.

### 4. Access Control & RBAC

- [ ] The Spring Security Filter Chain clearly separates public and protected endpoints.
- [ ] Public endpoints such as registration and login remain accessible without authentication.
- [ ] Protected endpoints require valid authentication.
- [ ] Endpoints requiring `ADMIN` privileges reject authenticated `USER` accounts with `403 Forbidden`.
- [ ] Role and authority information used by Spring Security is resolved from the persisted security model.
- [ ] Authorization rules are covered by automated integration tests.

### 5. Session Stability & Token Lifecycle

- [ ] Valid refresh tokens can be exchanged for new short-lived access tokens without requiring users to re-enter credentials.
- [ ] Refresh tokens are persisted and associated with the appropriate user/session.
- [ ] Invalid, expired, revoked, or otherwise unusable refresh tokens are rejected.
- [ ] Logout revokes or removes the associated active refresh token.
- [ ] Revoked refresh tokens cannot be reused.

### 6. Account Protection

- [ ] Repeated failed login attempts are tracked according to the defined security policy.
- [ ] Accounts can be automatically locked when the configured failure threshold is reached.
- [ ] Disabled or locked accounts cannot successfully authenticate.
- [ ] Account state is enforced consistently during authentication.

### 7. Two-Factor Authentication

- [ ] TOTP-based two-factor authentication can be enabled for supported accounts.
- [ ] A compatible authenticator application can generate valid verification codes.
- [ ] Invalid or expired TOTP codes are rejected.
- [ ] Two-factor authentication integrates correctly with the primary authentication flow.

### 8. Testing & Validation

- [ ] Unit tests cover JWT signing, parsing, expiration, validation, and failure scenarios.
- [ ] Integration tests cover the Spring Security Filter Chain using `MockMvc`.
- [ ] Integration tests verify anonymous access, authenticated access, insufficient authority, and successful authorized access.
- [ ] Database migration tests or application-context tests verify that Flyway migrations execute successfully.
- [ ] Automated tests verify that required roles and authorities exist after migration.
- [ ] Demo startup is verified without external MySQL or manually supplied production secrets.
- [ ] The full Maven verification lifecycle completes successfully through `mvn clean verify`.

### 9. Documentation

- [ ] The README documents how to run the application in demo, test, stage, and production modes.
- [ ] The README clearly identifies which external configuration values are required for stage and production.
- [ ] Database migration conventions and Flyway usage are documented.
- [ ] The system design documentation reflects the database initialization and application startup lifecycle.
- [ ] OpenAPI configuration supports JWT Bearer authentication.
- [ ] Swagger UI provides an interactive **Authorize** mechanism for authenticated endpoint testing.
- [ ] Protected endpoints can be exercised through Swagger using a valid JWT.


---

## Stories
- [ ] **3.0**: Story 3.0: Migrate to MySQL and Establish Database Initialization
- [ ] **3.1**: RBAC & Ownership Validation (ROLE_USER/ADMIN + Self-resource protection)
- [ ] **3.2**: Refresh Token Mechanism (Rotation & DB Storage)
- [ ] **3.3**: Login Security & Account Locking (Failed attempts logic)
- [ ] **3.4**: Password Reset Logic (Internal Token generation only)
- [ ] **3.5**: 2FA (TOTP) Integration (Google Authenticator flow)
- [ ] **3.6**: Swagger/OpenAPI Security Configuration
- [ ] **Audit:** Epic 3 Definition of Done Verification

