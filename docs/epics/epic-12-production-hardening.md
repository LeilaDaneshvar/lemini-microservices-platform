# Epic 12: Production Hardening — Planned

**Objective:** Strengthen the LEMINI platform's security, configuration, persistence, deployment, and recovery behavior for production-like operation.

**Target Milestone:** LEMINI Production Hardening v0.8

---

## Scope

### In Scope
- Security configuration review and hardening.
- Least-privilege access review.
- Dependency and container vulnerability checks.
- Runtime configuration and secret hardening.
- Versioned database migration practices.
- Deployment safeguards and rollback procedures.
- Backup and restore procedures for persistent data.
- Resource limits and operational configuration review.
- Failure/recovery verification.
- Production-oriented operational documentation.

### Out of Scope
- Formal regulatory certification.
- Guaranteed multi-region disaster recovery.
- 24/7 operational support processes.
- Organization-wide compliance programs.
- GenAI production hardening before Epic 4 is resumed.

---

## Technical Notes
- Production hardening should build on the deployed AWS/Kubernetes platform rather than introduce a separate architecture.
- Security changes must preserve existing API contracts unless an explicit breaking change is approved.
- Database schema changes should be versioned and repeatable.
- Deployment safeguards should favor rollback/recovery over manual in-place fixes.
- Recovery procedures must be tested, not only documented.

---

## Definition of Done

### Security Hardening
- [ ] Authentication and authorization configuration is reviewed.
- [ ] Service permissions follow least-privilege principles where applicable.
- [ ] Public exposure of infrastructure components is minimized.
- [ ] Runtime secrets are stored and supplied securely.
- [ ] Sensitive values are absent from logs and repository history within the scope of the audit.
- [ ] Dependency/container vulnerability checks are incorporated into the engineering workflow.

### Database Migrations
- [ ] Database schema changes use a versioned migration mechanism.
- [ ] Migrations are repeatable across supported environments.
- [ ] Application startup does not depend on unsafe automatic destructive schema changes.
- [ ] Migration failure behavior and recovery are documented.

### Deployment Safeguards
- [ ] Deployment health checks are defined.
- [ ] A failed deployment can be rolled back.
- [ ] Deployment procedures minimize avoidable service disruption.
- [ ] Image/version traceability is maintained.

### Resource & Runtime Hardening
- [ ] Kubernetes workload resource requests/limits are reviewed.
- [ ] Environment configuration is validated at startup where appropriate.
- [ ] Unnecessary debug/development settings are disabled in production-like environments.
- [ ] Exposed endpoints and actuator access are reviewed.

### Backup & Recovery
- [ ] Database backup approach is documented.
- [ ] Restore procedure is documented.
- [ ] A restore/recovery exercise is completed in a non-production environment.
- [ ] Recovery of application configuration and required infrastructure is documented.

### Verification
- [ ] Security-focused automated checks pass.
- [ ] Migration workflow is verified.
- [ ] Deployment rollback is verified.
- [ ] Backup/restore procedure is verified.
- [ ] Core user, account, and transfer flows still pass after hardening.

### Documentation
- [ ] Production-like deployment checklist is documented.
- [ ] Security configuration expectations are documented.
- [ ] Migration procedures are documented.
- [ ] Backup/restore and rollback procedures are documented.
- [ ] Known operational limitations are documented.

---

## Stories
- [ ] **12.1:** Review & Harden Authentication, Authorization and Service Exposure
- [ ] **12.2:** Add Dependency & Container Vulnerability Checks
- [ ] **12.3:** Harden Runtime Configuration & Secret Management
- [ ] **12.4:** Establish Versioned Database Migration Practices
- [ ] **12.5:** Configure Kubernetes Resource & Runtime Safeguards
- [ ] **12.6:** Implement Deployment Health Gates & Rollback Procedure
- [ ] **12.7:** Define and Verify Database Backup & Restore
- [ ] **12.8:** Execute Production-Like Recovery & Regression Verification
- [ ] **Audit:** Epic 12 Definition of Done Verification
