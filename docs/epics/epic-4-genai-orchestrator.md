# Epic 4: GenAI Orchestrator Service

**Objective:** Establish the "Brain" of the LEMINI platform. This service integrates local Large Language Models (LLMs) with platform data to provide intelligent summarization, semantic search, and contextual Q&A (RAG) without relying on external cloud APIs.

---

## Scope

### In Scope
- **Asynchronous Data Ingestion:** RabbitMQ integration to listen for platform events (e.g., "User Created") and process data embeddings in the background.
- **Summarization Pipeline:** Stateless REST API to ingest unstructured text (like system logs) and return concise summaries.
- **Semantic Search:** A search endpoint querying a local vector database to find context based on meaning rather than exact keywords.
- **RAG (Retrieval-Augmented Generation):** A complex question-answering pipeline that grounds the AI's responses in verified database records.
- **Vector Database Integration:** Connecting the service to `ChromaDB` for storing and retrieving document embeddings.---

## Technical Notes
- **AI Framework:** `Spring AI` for native Spring Boot integration.
- **Message Broker:** `RabbitMQ` (via Spring AMQP) for decoupled, background ETL processing.
- **Local Inference:** `Ollama` hosting local models (e.g., Llama 3.2) mapped to port `11434`.
- **Vector Store:** `ChromaDB` running via Docker container.
- **ETL Processing:** Utilizing Spring AI's `DocumentReader` and `TokenTextSplitter` for data chunking before saving to the vector store.

---

## Definition of Done

### Feature Completion
- [x] Service starts successfully and registers with the Eureka Discovery server.
- [x] All three core AI REST endpoints (Summarize, Search, RAG) return `HTTP 200 OK` with valid JSON payloads.

### Event-Driven Ingestion
- [x] Service successfully binds to a RabbitMQ queue (e.g., `lemini.ai.ingest`).
- [x] Messages received from the queue are successfully parsed, embedded, and saved to ChromaDB without blocking the main application thread.

### AI Performance & Accuracy Guidelines
- [x] **Summarization:** The model successfully reduces input text volume by at least `60%` while retaining critical core entities.
- [x] **Semantic Search:** Search response time from ChromaDB executes in `< 500ms` for local index queries.
- [x] **RAG Accuracy:** The AI response explicitly cites the source Document ID from the vector store to ensure data is traceable and not hallucinated.

### Code Quality & Privacy
- [x] Codebase compiles cleanly with no hardcoded credentials (managed via `application.properties`).
- [x] Distributed tracing is active; AI request spans and RabbitMQ message traces are successfully captured in Zipkin.