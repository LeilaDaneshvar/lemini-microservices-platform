# Epic 8: Kubernetes Platform — Planned

**Objective:** Run the containerized LEMINI platform on a local Kubernetes cluster using reproducible manifests and platform configuration.

**Target Milestone:** LEMINI Kubernetes Platform v0.4

---

## Scope

### In Scope
- Local Kubernetes cluster setup.
- Kubernetes Deployments for application services.
- Kubernetes Services for service networking.
- ConfigMaps and Secrets for runtime configuration.
- Liveness, readiness, and startup probes where appropriate.
- Ingress for external access.
- Replica configuration for applicable stateless services.
- Persistent storage for local stateful dependencies where required.
- Local Kubernetes deployment and verification documentation.

### Out of Scope
- AWS EKS.
- Production autoscaling policies.
- Multi-region or multi-cluster deployment.
- Service mesh adoption.
- Advanced GitOps deployment tooling.

---

## Technical Notes
- Docker images created in Epic 6 are the deployment artifacts for Kubernetes.
- Local cluster tooling may be selected during implementation; the Epic should not depend on a specific local Kubernetes product unless required.
- Kubernetes configuration should preserve independent service boundaries and database ownership.
- Secrets must not be committed as plaintext production credentials.
- Existing Eureka and Config Server behavior should be reviewed in Kubernetes rather than assumed to be necessary in the same form forever; changes require an architectural decision.

---

## Definition of Done

### Local Cluster
- [ ] A documented local Kubernetes cluster can be created and accessed.
- [ ] Required namespaces or logical environment boundaries are defined.
- [ ] LEMINI workloads deploy successfully to the local cluster.

### Workloads & Networking
- [ ] Config Server deployment is defined.
- [ ] Discovery Service deployment is defined.
- [ ] API Gateway deployment is defined.
- [ ] User Service deployment is defined.
- [ ] Account Service deployment is defined.
- [ ] Transaction Service deployment is defined.
- [ ] Kubernetes Services provide required internal connectivity.
- [ ] External traffic reaches the platform through the configured ingress path.

### Configuration
- [ ] Non-sensitive configuration is externalized through ConfigMaps or the established configuration mechanism.
- [ ] Sensitive values are supplied through Kubernetes Secrets or an appropriate local equivalent.
- [ ] Environment-specific configuration is separated from container images.

### Health & Lifecycle
- [ ] Applicable services expose health information suitable for probes.
- [ ] Readiness probes prevent traffic from reaching unready services.
- [ ] Liveness/startup behavior is configured where appropriate.
- [ ] Rolling restart/update behavior can be demonstrated locally.

### Persistence
- [ ] Required local persistent storage is configured for stateful dependencies.
- [ ] Business services do not share database ownership.

### Verification
- [ ] Gateway routing works in local Kubernetes.
- [ ] Core account and transfer workflows succeed.
- [ ] Service-to-service communication works correctly.
- [ ] Failure of an application pod can be recovered by Kubernetes for replicated/stateless workloads.

### Documentation
- [ ] Cluster setup is documented.
- [ ] Deployment and teardown commands are documented.
- [ ] Configuration and secret handling are documented.
- [ ] Local troubleshooting procedures are documented.

---

## Stories
- [ ] **8.1:** Establish Local Kubernetes Development Environment
- [ ] **8.2:** Define Kubernetes Workloads for Infrastructure Services
- [ ] **8.3:** Define Kubernetes Workloads for Business Services
- [ ] **8.4:** Configure Kubernetes Services & Internal Networking
- [ ] **8.5:** Configure ConfigMaps, Secrets & Environment Configuration
- [ ] **8.6:** Configure Health Probes & Replica Behavior
- [ ] **8.7:** Configure Ingress & External Access
- [ ] **8.8:** Configure Local Persistent Storage
- [ ] **8.9:** Verify End-to-End Platform Operation on Kubernetes
- [ ] **Audit:** Epic 8 Definition of Done Verification
