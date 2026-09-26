# Epic 6: Containerized Platform

**Objective:** Containerize the LEMINI services and run the complete platform locally through a reproducible Docker Compose environment.

---

## Scope

### In Scope
- Docker images for all application services.
- Docker Compose orchestration for the local platform.
- Container networking and service dependencies.
- Externalized runtime configuration.
- Containerized database and infrastructure dependencies.
- Health checks and startup dependency handling.
- Local containerized smoke verification.
- Documentation for building and running the platform with containers.

### Out of Scope
- Kubernetes orchestration.
- AWS deployment.
- Production-grade container registry and image promotion.
- Advanced autoscaling and production deployment strategies.

---

## Technical Notes
- Each Spring Boot service should have an independent Docker image.
- Images should use a consistent Java runtime and build strategy.
- Runtime secrets must not be embedded in Docker images.
- Docker Compose should provide a single local entry point for starting the platform.
- Service-to-service communication must use container-resolvable service names or the existing discovery mechanism.
- Persistent data required during local development should use named volumes where appropriate.
- The containerized environment should preserve the existing service boundaries and independent persistence ownership.

---

## Definition of Done

### Service Images
- [ ] Config Server has a working Docker image.
- [ ] Discovery Service has a working Docker image.
- [ ] API Gateway has a working Docker image.
- [ ] User Service has a working Docker image.
- [ ] Account Service has a working Docker image.
- [ ] Transaction Service has a working Docker image.
- [ ] All images build successfully from the repository.

### Docker Compose Environment
- [ ] Docker Compose starts the required application and infrastructure containers.
- [ ] Services communicate successfully through the container network.
- [ ] Required databases start and initialize correctly.
- [ ] Configuration and discovery dependencies are available before dependent services become ready.
- [ ] Required ports are exposed only where local access is needed.
- [ ] Persistent development data uses appropriate named volumes.

### Configuration & Security
- [ ] Environment-specific values are supplied at runtime.
- [ ] Secrets are not baked into images or committed in Compose files.
- [ ] Containerized services use the same security behavior as local non-containerized execution.

### Verification
- [ ] The complete platform starts from a clean local Docker environment.
- [ ] Gateway routing works against containerized services.
- [ ] Service registration and discovery work correctly.
- [ ] Core Epic 5 account and transfer flows work in the Docker Compose environment.
- [ ] Smoke tests pass against the containerized platform.

### Documentation
- [ ] Local container prerequisites are documented.
- [ ] Build and startup commands are documented.
- [ ] Shutdown, cleanup, and reset procedures are documented.
- [ ] Common container troubleshooting steps are documented.

---

## Stories
- [ ] **6.1:** Define Docker Image Strategy & Shared Conventions
- [ ] **6.2:** Containerize Platform Infrastructure Services
- [ ] **6.3:** Containerize Business Microservices
- [ ] **6.4:** Configure Containerized Databases & Persistent Volumes
- [ ] **6.5:** Implement Docker Compose Platform Environment
- [ ] **6.6:** Add Health Checks, Startup Dependencies & Runtime Configuration
- [ ] **6.7:** Verify Core End-to-End Flows in Docker Compose
- [ ] **Audit:** Epic 6 Definition of Done Verification
