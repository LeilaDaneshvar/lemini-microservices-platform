# Epic 11: Event-Driven Platform — Planned

**Objective:** Introduce asynchronous messaging for selected workflows that benefit from decoupled event-driven processing while preserving the existing synchronous core business flow.

---

## Scope

### In Scope
- Selection and setup of the project message broker.
- Event naming and contract conventions.
- Event producers and consumers.
- Schema/versioning strategy.
- Retry behavior for transient consumer failures.
- Dead-letter or failed-message handling.
- Idempotent consumer behavior where required.
- Event observability and tracing where supported.
- Integration testing of asynchronous workflows.
- Documentation of event ownership and processing flow.

### Out of Scope
- Replacing all synchronous REST communication with messaging.
- Distributed transactions through a full Saga implementation unless explicitly required by a selected workflow.
- GenAI-specific event processing unless Epic 4 has resumed.
- Unbounded event sourcing of all application state.

---

## Technical Notes
- Messaging should solve a defined business or platform need rather than being added to every interaction.
- Synchronous transfer validation and balance updates from Epic 5 remain synchronous unless a later architectural decision changes them.
- Producers should publish stable, versioned event contracts.
- Consumers must tolerate duplicate delivery where the broker semantics require it.
- Failed messages must be observable and recoverable.
- The broker choice should be documented through an ADR if it materially affects the architecture.

---

## Definition of Done

### Messaging Infrastructure
- [ ] A message broker is selected and documented.
- [ ] Broker configuration is available for local/container/Kubernetes environments as applicable.
- [ ] Producers and consumers can connect using externalized configuration.
- [ ] Credentials are handled securely.

### Event Contracts
- [ ] Event naming conventions are defined.
- [ ] Event payload schemas are documented.
- [ ] Event versioning/backward-compatibility expectations are defined.
- [ ] Events contain stable identifiers and required correlation metadata.

### Producers
- [ ] At least one meaningful workflow publishes an event.
- [ ] Producer failure behavior is defined.
- [ ] Duplicate publication risks are understood and handled appropriately.

### Consumers
- [ ] At least one service consumes the selected event asynchronously.
- [ ] Consumer processing is idempotent where duplicate delivery is possible.
- [ ] Transient failures can be retried safely.
- [ ] Permanently failed messages are routed to a recoverable failure path.

### Observability
- [ ] Event publication and consumption are logged/traced sufficiently for diagnosis.
- [ ] Correlation identifiers connect events to originating requests where applicable.
- [ ] Failed-message state is visible to operators/developers.

### Testing
- [ ] Producer contract behavior is tested.
- [ ] Consumer behavior is tested.
- [ ] Retry and failed-message behavior is tested.
- [ ] End-to-end asynchronous flow is verified.

### Documentation
- [ ] Selected asynchronous workflows are documented.
- [ ] Event ownership is documented.
- [ ] Event contracts are documented.
- [ ] Failure recovery procedures are documented.

---

## Stories
- [ ] **11.1:** Select Message Broker & Document Architectural Decision
- [ ] **11.2:** Configure Messaging Infrastructure Across Local Environments
- [ ] **11.3:** Define Event Contract, Naming & Versioning Conventions
- [ ] **11.4:** Implement First Event Producer
- [ ] **11.5:** Implement First Event Consumer
- [ ] **11.6:** Implement Consumer Idempotency, Retry & Failed-Message Handling
- [ ] **11.7:** Integrate Messaging with Logging, Metrics & Tracing
- [ ] **11.8:** Verify End-to-End Event-Driven Workflow
- [ ] **Audit:** Epic 11 Definition of Done Verification
