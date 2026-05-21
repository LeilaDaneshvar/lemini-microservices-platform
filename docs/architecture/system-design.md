# System Design: LEMINI Platform

## Architecture Overview
LEMINI employs a hybrid microservices architecture. It combines **synchronous REST APIs** for immediate client responses with an **Event-Driven Architecture (EDA)** for heavy, background AI data processing. The codebase is maintained as a **Git Monorepo** using **Maven Multi-module** management to ensure unified dependency control across all services.

## High-Level Data Flows

### 1. Core Platform Flow (Synchronous Read/Write)
This flow handles standard, real-time user requests.
1. **Client Request:** External clients hit the **Spring Cloud Gateway**.
2. **Routing:** The Gateway queries the **Eureka Discovery Service** to locate the target service.
3. **Identity & Security:** The request passes through the **Security Filter Chain** for strict JWT Validation.
4. **Execution:** The target service (e.g., User Management or AI Orchestrator) processes the request and instantly returns an HTTP response to the client.

### 2. AI Data Ingestion Flow (Asynchronous ETL)
This decoupled flow ensures that heavy AI processing never slows down core platform APIs.
1. **Event Trigger:** A core service performs an action (e.g., User Service saves a new profile to MySQL) and instantly publishes an event message to **RabbitMQ**.
2. **Message Brokering:** RabbitMQ securely queues the message.
3. **Background Consumption:** The GenAI Orchestrator listens to the queue, picking up messages on a background thread.
4. **Vectorization & Storage:** **Spring AI** chunks the data, generates vector embeddings, and stores them in **ChromaDB** for future Retrieval-Augmented Generation (RAG).

## Infrastructure & Observability
* **Configuration:** All services dynamically fetch environment properties from the **Spring Config Server** on startup.
* **Tracing:** Distributed tracing is captured via **Micrometer & Zipkin**, tracking request spans across both HTTP boundaries and RabbitMQ message queues.

## Core Tech Stack
* **Language/Framework:** Java 17, Spring Boot 3.x
* **Ecosystem:** Spring Cloud 2023.x
* **AI Engine:** Spring AI, Ollama (Local Inference), ChromaDB (Vector Store)
* **Message Broker:** RabbitMQ
* **Security:** JWT, BCrypt, Spring Security
* **Databases:** MySQL (Primary), H2 (Local Dev/Test)
* **Observability:** Micrometer/Zipkin