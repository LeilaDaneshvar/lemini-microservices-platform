# Epic 3: Authentication & Security

**Objective:** Transition from basic authentication to a robust, fine-grained authorization system. This Epic ensures that users are not only authenticated ("who they say they are") but also properly authorized ("only doing what they are allowed to do").

---

## Scope

### In Scope
- **Authentication:** Secure login flow and JWT generation.
- **Session Management:** Storing refresh tokens in the database to allow long-lived client sessions securely.
- **Authorization:** Spring Security Filter Chain configuration and Role-Based Access Control (RBAC) supporting `ROLE_USER` and `ROLE_ADMIN`.
- **Security Documentation:** Configuring OpenAPI/Swagger to support JWT Bearer authentication tokens.
- **Multi-Factor Authentication (2FA):** Time-based One-Time Password (TOTP) integration using Google Authenticator.
- **Account Safety Management:** Implementation of "Lock/Enable" logic after failed login attempts.

### Out of Scope

- **Email Service Integration:** Integration with SES/SMTP (Moved to Epic 5).

---

## Technical Notes
- **Security Framework:** Spring Security 6.x.
- **JWT Library:** `io.jsonwebtoken:jjwt-api` (and runtime implementations).
- **Cryptographic Keys:** Signature generation using `Keys.hmacShaKeyFor` utilizing the HS256 algorithm.
- **Token Storage:** Access tokens remain stateless. Refresh tokens are persisted to the database layer for visibility and lifecycle revocation.
- **2FA Library Engine:** Implementation via `com.warrenstrange:googleauth` or `org.jboss.aerogear:aerogear-otp-java`.

---

## Definition of Done

### 1. Security Handshake
- [ ] The `/login` endpoint successfully validates active user credentials and provides valid, signed JWTs.
- [ ] Expired or structurally tampered JWTs are explicitly intercepted and rejected with a `401 Unauthorized` response status.

### 2. Access Control & RBAC
- [ ] Enforced boundary restrictions where endpoints requiring `ADMIN` authority reject `USER` requests with a `403 Forbidden` error status.
- [ ] Public-facing endpoints (e.g., registration) bypass security evaluation and remain accessible anonymously.

### 3. Session Stability & Lifecycle
- [ ] Valid `/refresh` processing successfully provisions new short-lived Access Tokens without demanding physical user re-authentication.
- [ ] Explicit logout processes successfully invalidate and drop active Refresh Tokens from the database.

### 4. Testing & Validation
- [ ] Complete Unit test matrix coverage for core JWT utility components (Signing, Parsing, and Validation properties).
- [ ] Comprehensive Integration tests for the Spring Security Filter Chain using `MockMvc` to guarantee authorized/unauthorized request routing path accuracy.

### 5. Documentation
- [ ] OpenAPI configuration is complete; the Swagger UI features a global interactive **"Authorize"** button.
- [ ] Endpoints can be interactively queried directly inside the Swagger interface by injecting valid tokens into the secure context.   