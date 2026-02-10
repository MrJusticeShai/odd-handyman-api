# ODD-HANDYMAN

**Connecting skilled handymen with people who need reliable work done — quickly, transparently, and locally.**

Odd-Handy-Man is a lightweight service marketplace designed to bridge the gap between **unemployed or underemployed skilled workers** and **customers who need everyday tasks completed**.

The platform focuses on **simplicity, trust, and clear task ownership**, avoiding unnecessary complexity while solving a real economic problem.

---

## 🧑‍🔧 Product Overview

Odd-Handy-Man enables customers to post small, practical tasks and allows nearby handymen to compete for that work through transparent bidding and direct communication.

Unlike large, bloated marketplaces, Odd-Handy-Man is intentionally minimal:

* One task type
* One clear task lifecycle
* One direct customer–handyman relationship

The result is faster job completion, fewer disputes, and real income opportunities for skilled workers.

---

## 🧠 What Problem Does This App Solve?

### The Problem

Many skilled handymen struggle to find consistent work, while customers often:

* Don’t know where to find trustworthy help
* Face unclear pricing
* Deal with unreliable communication

At the same time, existing platforms are often:

* Overly complex
* Subscription-heavy
* Optimized for scale rather than fairness

### The Solution

Odd-Handy-Man provides:

* A **clear task posting flow**
* **Private bidding**
* **Direct communication** between customer and handyman
* A **defined task lifecycle** that reduces disputes

---

## 🎯 Goals & Impact

Odd-Handy-Man is built with practical impact in mind:

* **Reduce unemployment** by giving skilled workers access to paid tasks
* **Lower barriers to entry** — no subscriptions or upfront fees
* **Encourage fair negotiation** through open bidding
* **Build trust** via private, job-linked reviews

The long-term vision is to support local economies by making short-term work more accessible and reliable.

---

## 🔁 High-Level Workflow

1. A customer creates a task with a budget
2. Handymen place bids on the task
3. The customer negotiates or accepts a bid
4. The task is assigned and worked on
5. The task is completed and reviewed

This single flow is the heart of the platform.

---

## 📌 Task Lifecycle

Tasks move through a small, well-defined lifecycle:

* **PENDING**
  Task is created and open for bids and negotiation

* **ASSIGNED**
  A handyman has been selected and the task is in progress

* **COMPLETED**
  Work is finished, chat is locked, and the task can be reviewed

Negotiation happens **only while a task is PENDING**.

---

## 🏗️ Tech Stack

### Backend

* Java 17
* Spring Boot
* Spring Web (REST)
* Spring Security (JWT)
* Spring Data JPA / Hibernate
* PostgreSQL
* Maven
* Docker & Docker Compose
* OpenAPI / Swagger
* Cloudinary (media support)

### Frontend (separate repository)

* React (web)
* REST API integration

---

## 🌐 API Structure

```
/api/auth        -> registration & login
/api/profile     -> user profiles & verification
/api/tasks       -> task/job lifecycle
/api/bids        -> handyman bids
/api/chats       -> task-based chat
/api/reviews     -> private reviews
```

Controllers are located under their respective modules, following a **feature-based package structure**.

---

## 🗄️ Database

* PostgreSQL
* Accessed via Spring Data JPA repositories

### Current State

* Schema managed via JPA entities
* `ddl-auto` configurable via `application.yml`

### Production Recommendations

* Introduce **Flyway** for schema migrations
* Add indexes on:

    * foreign keys (task_id, user_id)
    * frequently queried fields
* Enable optimistic locking where needed

---

## 🚀 Running Locally

### Using Docker Compose (Recommended)

```bash
docker-compose up --build
```

This starts:

* Spring Boot API
* PostgreSQL database

### Without Docker

```bash
mvn clean install
mvn spring-boot:run
```

API runs at:

```
http://localhost:8080
```

Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

---

## 🧪 Testing

Recommended:

* Unit tests for services
* Integration tests for controllers
* Testcontainers for PostgreSQL

---

## 🔒 Security Considerations

* Passwords hashed with BCrypt
* JWT token expiration
* Role‑based authorization
* Job ownership validation on every request

---

## 📈 Production‑Grade TODOs

This MVP is functional but **not yet production‑ready**.

Planned improvements:

* [ ] Swagger / OpenAPI documentation
* [ ] Flyway database migrations
* [ ] Global exception handling
* [ ] Request/response validation
* [ ] Audit logging
* [ ] Rate limiting
* [ ] Observability (logs, metrics)
* [ ] Dockerization
* [ ] CI pipeline

---

## 🪶 Product Philosophy

Odd-Handy-Man follows a *less-is-more* approach:

* Fewer features
* Clearer rules
* Faster outcomes

By avoiding payments and subscriptions in the early stages, the platform reduces friction and disputes while focusing on what matters most: **getting work done and paid fairly**.

---


## 📄 License

TBD
