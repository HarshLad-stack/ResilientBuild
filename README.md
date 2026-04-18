# Resilient

**A lightweight, annotation-driven resilience library for Java and Spring Boot**

Resilient simplifies fault tolerance by providing retry, timeout, and caching capabilities through a single annotation—reducing boilerplate and improving code reliability.

---

## Overview

Modern applications frequently interact with unreliable external systems such as APIs, databases, and microservices. Handling failures typically requires repetitive patterns for retries, timeouts, and caching.

Resilient abstracts these concerns into a declarative model using annotations, allowing developers to focus on business logic while ensuring robust execution behavior.

---

## Key Features

* **Retry with Exponential Backoff**
  Automatically retries failed executions with configurable delay strategy.

* **Timeout Management**
  Prevents long-running operations using asynchronous execution.

* **In-Memory Caching (TTL-based)**
  Reduces redundant calls and improves performance.

* **Spring Boot Integration**
  Seamless integration using AOP and auto-configuration.

* **Minimal Configuration**
  Works out-of-the-box with sensible defaults.

---

## Installation

### Option 1: Local Installation (Development)

Clone and install the library locally:

```bash
git clone https://github.com/your-username/resilient.git
cd resilient
mvn clean install
```

---

### Option 2: Maven Dependency (After Publishing)

```xml
<dependency>
    <groupId>org.dev.velostack</groupId>
    <artifactId>resilient</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## Usage

### Basic Example

```java
@Resilient(
    maxRetries = 3,
    timeout = 2000,
    cacheEnabled = true,
    cacheTtl = 60000
)
public String fetchData() {
    return restTemplate.getForObject("https://api.example.com", String.class);
}
```

---

### Behavior

* On failure → Retries automatically
* On delay → Timeout is enforced
* On success → Result is cached (if enabled)

---

## Configuration

| Property     | Description                       | Default |
| ------------ | --------------------------------- | ------- |
| maxRetries   | Number of retry attempts          | 3       |
| timeout      | Timeout in milliseconds           | 2000    |
| cacheEnabled | Enables response caching          | false   |
| cacheTtl     | Cache time-to-live (milliseconds) | 60000   |

---

## Architecture

Resilient is built using a modular design:

* **Annotation Layer** – Declarative API (`@Resilient`)
* **Interceptor Layer** – AOP-based execution control
* **Retry Engine** – Handles retry logic and backoff
* **Cache Layer** – TTL-based in-memory store
* **Configuration Layer** – Spring Boot auto-configuration

---

## Project Structure

```
resilient/
├── annotation/       # Annotation definitions
├── interceptor/      # AOP interceptors
├── retry/            # Retry logic
├── cache/            # Cache implementation
├── config/           # Auto-configuration
```

---

## Roadmap

* Stale cache fallback
* Logging integration (SLF4J)
* Metrics and monitoring
* Exception filtering
* Non-Spring support (JDK proxy)

---

## Compatibility

* Java 21
* Spring Boot 3.x

---

## Contributing

Contributions are welcome. Please open an issue to discuss proposed changes before submitting a pull request.

---

## License

MIT License

---

## Summary

Resilient provides a clean, declarative approach to handling common failure scenarios in Java applications. By consolidating retry, timeout, and caching logic into a single annotation, it reduces complexity and improves maintainability.
