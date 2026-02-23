# 🔧 Odd-Handyman API

**Connecting skilled handymen with local tasks — quickly, transparently, and securely.**

Odd-Handyman API is a **RESTful backend service** powering a micro-marketplace.
It bridges the gap between underemployed skilled workers and customers by providing a
structured environment for task broadcasting, task bidding, communication, and verified
completion.

---

## 🏗 Engineering Decisions & Architecture

To demonstrate growth in backend engineering, this project prioritizes **maintainability, security, and data integrity** over a simple CRUD implementation.

### 1. Stateless Security with JWT

- **Decision:** Implemented Spring Security with stateless JWT (JSON Web Tokens).
- **Why:** Allows the API to scale horizontally across multiple containers without session stickiness, enabling high availability and a modern decoupled frontend-backend architecture.

### 2. State Machine for Task Lifecycles

- **Decision:** Enforced a strict state transition: `PENDING ➔ ASSIGNED ➔ COMPLETED`.
- **Why:** Prevents logical inconsistencies (e.g., bidding on a completed task or deleting an assigned task). Encapsulated in the Service Layer for data integrity.

### 3. Global Exception Handling

- **Decision:** Implemented a `@ControllerAdvice` layer.
- **Why:** Ensures consistent error responses (e.g., 404 for missing resources, 403 for unauthorized actions) without leaking stack traces.

---

## 🛠 Technology Stack

| Layer         | Technology              | Purpose                                          |
|---------------|-------------------------|--------------------------------------------------|
| Backend       | Spring Boot 3.x         | Core framework and dependency injection          |
| Database      | PostgreSQL 14           | Relational storage for task-user relationships   |
| Security      | Spring Security + JWT   | Authentication & Role-Based Access Control (RBAC)|
| Testing       | JUnit 5 / AssertJ       | High-coverage unit & integration tests           |
| Documentation | Swagger / OpenAPI       | Auto-generated interactive API docs              |
| DevOps        | Docker & Docker Compose | Local environment orchestration                  |

---

## 🎯 Key Features

- 🔐 **Auth:** Secure registration/login with BCrypt password hashing
- 📋 **Tasking:** CRUD for tasks with budget, location, and status management
- 💰 **Bidding:** Handymen submit private bids; customers can accept/reject
- 💬 **Chat:** Contextual messaging scoped to a specific task
- ⭐ **Reviews:** Post-completion feedback to build platform trust

---

## 🚀 Getting Started

### Clone Repository

```bash
git clone https://github.com/MrJusticeShai/odd-handyman-api.git
cd odd-handyman-api
```

### 🐳 Quick Start (Docker)

```bash
docker-compose up --build
```

- **API Base URL:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html

### Manual Maven Build

```bash
# Install dependencies and build
mvn clean install

# Run unit tests
mvn test

# Start Spring Boot app
mvn spring-boot:run
```

---

## 📖 Using Swagger UI & Authentication

### Step 1: Get Your Token

Navigate to `/api/auth/login` with your credentials and copy the JWT token from the response body.

### Step 2: Authorize Swagger UI

1. Click **Authorize** (padlock icon, top-right)
2. Enter: `Bearer <YOUR_TOKEN_HERE>`
3. Click **Authorize**, then **Close**

### Step 3: Test Endpoints

All protected requests (e.g., `POST /api/tasks`) now include your JWT in headers automatically.

---

## 📦 API Endpoints

| Endpoint      | Method                  | Description                        |
|---------------|-------------------------|------------------------------------|
| `/api/auth`   | `POST`                  | User registration & login          |
| `/api/profile`| `GET` / `PUT`           | Retrieve/update user profile       |
| `/api/tasks`  | `GET/POST/PUT/DELETE`   | Task CRUD & lifecycle management   |
| `/api/bids`   | `GET` / `POST`          | Submit/manage handyman bids        |
| `/api/chats`  | `GET` / `POST`          | Task-based messaging               |
| `/api/reviews`| `GET` / `POST`          | Private post-task reviews          |

### Example Task Creation (`POST /api/tasks`)

```json
{
  "title": "Fix leaky faucet",
  "description": "Kitchen faucet is dripping and requires replacement",
  "budget": 50.00,
  "location": "Midrand, Johannesburg"
}
```

---

## 🧪 Testing

- **Unit Tests:** Service layer logic
- **Integration Tests:** Controller & endpoint workflows

```bash
# Run all tests
mvn test
```

---

## 📈 Roadmap

- [ ] Flyway/Liquibase database migrations
- [ ] Redis caching for frequently accessed tasks
- [ ] Cloud storage integration for task photos (AWS S3 / Minio)
- [ ] GitHub Actions CI/CD for testing and linting
- [ ] Observability: Prometheus + Grafana
- [ ] Rate limiting & audit logging

---

## 🪶 Project Philosophy

- **Minimalist by design**
- Single task type, one lifecycle, direct customer–handyman relationship
- Focused on practical impact: fair work, real income, local economic support