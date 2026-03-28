# AI Chat Integration API

> I built a Spring Boot integration layer that acts as a middleware between a client application and an external AI model. The system authenticates users via JWT, forwards their requests to the Groq AI API using REST with Bearer token authorization, persists every conversation in MySQL for auditability, and returns structured JSON responses. The architecture is: User → REST API → JWT Auth → External AI Service → MySQL → JSON Response. This is the same pattern used in enterprise AI integrations where you never expose the AI directly to the client — everything goes through a secured integration layer. 

---

## What This Project Does

```
Client  →  REST API  →  JWT Auth  →  Groq AI (Llama)  →  MySQL  →  JSON Response
```

This is not a basic CRUD app. It is a **system integration layer** — a secured backend service that:

- Authenticates users with stateless JWT tokens
- Accepts messages via REST API
- Forwards them to an external AI model (Groq / Llama3)
- Saves every conversation to MySQL for auditability
- Returns structured JSON responses

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend Framework | Spring Boot 2.7.18 |
| Language | Java 8 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| AI Service | Groq API (Llama 3.1) |
| Database | MySQL |
| ORM | Spring Data JPA + Hibernate |
| Build Tool | Maven |
| API Testing | Postman |

---

## Project Structure

```
ai-chat-integration/
│
├── src/main/java/com/aichat/app/
│   │
│   ├── AiChatApplication.java              ← Entry point
│   │
│   ├── config/
│   │   └── SecurityConfig.java             ← JWT security rules
│   │
│   ├── controller/
│   │   ├── AuthController.java             ← /api/auth/register  /api/auth/login
│   │   ├── ChatController.java             ← /api/chat  /api/chat/history
│   │   └── HealthController.java           ← /api/health
│   │
│   ├── model/
│   │   ├── User.java                       ← users table
│   │   └── ChatHistory.java                ← chat_history table
│   │
│   ├── repository/
│   │   ├── UserRepository.java
│   │   └── ChatHistoryRepository.java
│   │
│   ├── security/
│   │   ├── JwtUtil.java                    ← token generation & validation
│   │   ├── JwtFilter.java                  ← intercepts every request
│   │   └── CustomUserDetailsService.java   ← loads user from MySQL
│   │
│   └── service/
│       ├── AuthService.java                ← register & login logic
│       ├── ChatService.java                ← saves conversations to MySQL
│       └── GroqAIService.java              ← calls Groq AI API ← core integration
│
├── src/main/resources/
│   └── application.properties
│
└── pom.xml
```

---

## Prerequisites

Before running this project make sure you have:

- Java 8 installed
- Maven installed
- MySQL running locally
- A free Groq API key from [console.groq.com](https://console.groq.com)

---

## Setup & Run

### Step 1 — Get a free Groq API key

1. Go to [console.groq.com](https://console.groq.com)
2. Sign up (free, no credit card required)
3. Click **API Keys** → **Create API Key**
4. Copy the key — it starts with `gsk_...`

---

### Step 2 — Create MySQL database

```sql
CREATE DATABASE ai_chat_db;
```

Tables are auto-created when the app starts. No manual schema needed.

---

### Step 3 — Configure application.properties

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.password=YOUR_MYSQL_ROOT_PASSWORD
groq.api.key=YOUR_GROQ_API_KEY_HERE
groq.model=llama-3.1-8b-instant
```

---

### Step 4 — Run

```bash
mvn spring-boot:run
```

You should see:

```
=========================================
  AI Chat Integration API is running!
  URL: http://localhost:8080
=========================================
```

---

## API Reference

### Health Check
No authentication required.

```
GET /api/health
```

```json
{
  "status": "UP",
  "message": "AI Chat Integration API is running!",
  "version": "1.0.0"
}
```

---

### Register

```
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "username": "ravi",
  "password": "password123"
}
```

Response:
```json
{
  "status": "success",
  "message": "User registered successfully! You can now login.",
  "username": "ravi"
}
```

---

### Login

```
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "username": "ravi",
  "password": "password123"
}
```

Response:
```json
{
  "status": "success",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "username": "ravi"
}
```

Copy the `token` value for the next requests.

---

### Chat with AI

Requires Bearer token in Authorization header.

```
POST /api/chat
Authorization: Bearer <your_token>
Content-Type: application/json
```

```json
{
  "message": "What is system integration?"
}
```

Response:
```json
{
  "status": "success",
  "id": 1,
  "username": "ravi",
  "yourMessage": "What is system integration?",
  "aiResponse": "System integration is the process of connecting different systems...",
  "timestamp": "2026-03-29T00:12:46.500384"
}
```

---

### Get Chat History

Returns all conversations for the logged-in user, ordered by most recent first.

```
GET /api/chat/history
Authorization: Bearer <your_token>
```

---

## How to Test in Postman

| Step | Method | URL | Auth |
|---|---|---|---|
| 1 | GET | `localhost:8080/api/health` | None |
| 2 | POST | `localhost:8080/api/auth/register` | None |
| 3 | POST | `localhost:8080/api/auth/login` | None |
| 4 | POST | `localhost:8080/api/chat` | Bearer Token |
| 5 | GET | `localhost:8080/api/chat/history` | Bearer Token |

For steps 4 and 5 in Postman:
- Click the **Authorization** tab
- Select **Bearer Token** from the dropdown
- Paste the token from the login response

---

## How the Integration Works

```
┌─────────┐     POST /api/chat      ┌──────────────┐
│  Client │ ──────────────────────► │ Spring Boot  │
│ Postman │ ◄────────────────────── │ REST API     │
└─────────┘     JSON Response       └──────┬───────┘
                                           │
                              ┌────────────┼────────────┐
                              │            │            │
                         JWT Validate  Call Groq    Save to
                         (JwtFilter)   AI API       MySQL
                              │            │            │
                              ▼            ▼            ▼
                         Authenticate  AI Response  ChatHistory
                         User          (Llama 3.1)  Table
```

Every API call goes through the `JwtFilter` which validates the Bearer token before the request reaches any controller. Unauthenticated requests are rejected with 401. Only the `/api/auth/**` and `/api/health` endpoints are public.

---

## Database Schema

Tables are auto-created by Hibernate on first run.

**users**
| Column | Type | Description |
|---|---|---|
| id | BIGINT (PK) | Auto-increment |
| username | VARCHAR | Unique username |
| password | VARCHAR | BCrypt hashed |
| role | VARCHAR | Default: USER |

**chat_history**
| Column | Type | Description |
|---|---|---|
| id | BIGINT (PK) | Auto-increment |
| username | VARCHAR | Who sent the message |
| user_message | TEXT | The user's question |
| ai_response | TEXT | The AI's answer |
| created_at | DATETIME | Timestamp |

---

## Common Errors & Fixes

| Error | Cause | Fix |
|---|---|---|
| `Access denied` on startup | Wrong MySQL password | Update `spring.datasource.password` |
| `401 Unauthorized` on /api/chat | Missing or expired token | Login again and use the new token |
| `model_decommissioned` in AI response | Groq model removed | Change `groq.model` to `llama-3.1-8b-instant` |
| Port 8080 already in use | Another app on same port | Add `server.port=8081` in properties |
| Tables not created | JPA config issue | Ensure `spring.jpa.hibernate.ddl-auto=update` |

---

## Skills Demonstrated

This project demonstrates the core skills required for a **System Integrator** role:

- REST API design and development
- External service integration (AI API over HTTP)
- JWT-based stateless authentication
- Role-based access control foundation
- Database design and ORM mapping
- Request/response JSON handling
- Error handling and status codes
- Separation of concerns (Controller → Service → Repository)
- Environment-based configuration

---

## Author

Built as a System Integration portfolio project demonstrating enterprise-grade API integration patterns using Spring Boot, JWT Security, and external AI service connectivity.
