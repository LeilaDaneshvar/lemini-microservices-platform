# Epic 9: Cloud Platform — Planned

**Objective:** Deploy the Kubernetes-based LEMINI platform to AWS using EKS and supporting managed AWS infrastructure.

---

## Scope

### In Scope
- Amazon EKS cluster deployment.
- Amazon ECR for container images.
- AWS networking required by the platform.
- Kubernetes workload deployment to EKS.
- Managed relational database infrastructure.
- Cloud configuration and secret management.
- External application access.
- Initial cloud logging/monitoring integration required for operability.
- Repeatable deployment documentation.

### Out of Scope
- Multi-region deployment.
- Active-active disaster recovery.
- Advanced autoscaling optimization.
- Full production hardening.
- Event-driven architecture.
- GenAI deployment.

---

## Technical Notes
- Epic 8 Kubernetes manifests/configuration should be reused or adapted for EKS rather than replaced with an unrelated deployment model.
- Container images should be stored in ECR with traceable tags.
- Database schemas remain independently owned by their services even if hosted on a shared managed database server.
- Cloud credentials must never be committed to the repository.
- Infrastructure provisioning should be repeatable; infrastructure-as-code may be adopted in this Epic if selected for the project.
- Public exposure should be minimized to only required entry points.

---

## Definition of Done

### Container Registry
- [ ] Application images can be published to Amazon ECR.
- [ ] Image naming and tagging conventions are documented.
- [ ] EKS workloads can pull required images securely.

### EKS
- [ ] An EKS cluster is provisioned and accessible.
- [ ] Required Kubernetes workloads deploy successfully.
- [ ] Kubernetes Services and ingress/load balancing work in AWS.
- [ ] Core services reach required AWS-hosted dependencies.

### Networking
- [ ] Required VPC/subnet/security-group configuration is established.
- [ ] External access reaches the API Gateway through the intended AWS entry point.
- [ ] Internal services are not unnecessarily exposed publicly.

### Persistence
- [ ] Managed relational database infrastructure is provisioned.
- [ ] User, Account, and Transaction persistence ownership remains logically separated.
- [ ] Database credentials are provided securely.
- [ ] Connectivity from workloads to the database is verified.

### Configuration & Secrets
- [ ] Runtime secrets use an approved AWS/Kubernetes secret mechanism.
- [ ] Environment-specific configuration is not baked into images.
- [ ] Cloud credentials are not stored in source control.

### Verification
- [ ] The platform starts successfully in AWS.
- [ ] User/security flows work through the deployed Gateway.
- [ ] Account and transfer flows work end to end.
- [ ] Distributed service communication works within EKS.
- [ ] Basic operational logs are available for troubleshooting.

### Documentation
- [ ] AWS architecture and prerequisites are documented.
- [ ] Deployment steps are documented.
- [ ] Required secrets/configuration are documented without exposing values.
- [ ] Teardown/cost-control procedures are documented.

---

## Stories
- [ ] **9.1:** Define AWS Deployment Architecture & Resource Plan
- [ ] **9.2:** Configure Amazon ECR & Image Publishing
- [ ] **9.3:** Provision Amazon EKS
- [ ] **9.4:** Configure AWS Networking & External Access
- [ ] **9.5:** Provision Managed Relational Database Infrastructure
- [ ] **9.6:** Configure Cloud Secrets & Runtime Configuration
- [ ] **9.7:** Deploy LEMINI Kubernetes Workloads to EKS
- [ ] **9.8:** Verify End-to-End Cloud Platform Operation
- [ ] **Audit:** Epic 9 Definition of Done Verification
