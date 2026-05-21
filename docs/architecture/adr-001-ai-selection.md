# ADR 001: Selection of Local GenAI Orchestration Stack

**Status:** Accepted

## Context
LEMINI requires an intelligent data-processing engine (capable of Summarization, Semantic Search, and RAG) that avoids third-party cloud API costs and maintains absolute local data sovereignty. Furthermore, the process of ingesting and vectorizing data must not degrade the performance or response times of the core platform APIs.

## Decision
We have selected a fully local, event-driven AI ecosystem consisting of the following components:
* **Framework - Spring AI:** Chosen over LangChain4j due to its seamless integration with our existing Spring Boot architecture, auto-configuration, and native support for Micrometer observability.
* **Model Inference - Ollama:** For hosting local Large Language Models (e.g., Llama 3.2) to ensure zero data leakage.
* **Vector Store - ChromaDB:** For storing and querying document embeddings.
* **Message Broker - RabbitMQ:** Chosen over Kafka (for a lighter footprint) to decouple data ingestion. The core platform publishes events (e.g., "User Created"), allowing the AI service to process embeddings asynchronously on a background thread.

## Consequences
* **Pro:** Full local control with zero per-token billing and strict data privacy.
* **Pro:** Spring AI allows the engineering team to use familiar Dependency Injection and `@Configuration` patterns.
* **Pro:** RabbitMQ guarantees high-performance platform APIs; vector math and ETL chunking will never block primary user requests.
* **Con:** Requires significant local compute resources (GPU/RAM) to run Ollama inference efficiently.
* **Con:** RabbitMQ and ChromaDB add additional infrastructure containers, slightly increasing the complexity of the local Docker deployment footprint.