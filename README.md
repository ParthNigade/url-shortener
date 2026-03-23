# 🔗 SnapLink — Distributed URL Shortener

A full-stack, scalable URL shortening service built with **Spring Boot** and **React.js**, featuring Redis caching, JWT authentication, click analytics, and rate limiting.

🌐 **Live Demo:** [https://url-shortener-tau-virid.vercel.app](https://url-shortener-tau-virid.vercel.app)
📄 **API Docs:** [https://url-shortener-m6c9.onrender.com/swagger-ui.html](https://url-shortener-m6c9.onrender.com/swagger-ui.html)

---

## ✨ Features

- **URL Shortening** — Generate short codes using Base62 encoding with collision-safe generation
- **Custom Aliases** — Choose your own short URL (e.g., `/my-link`)
- **JWT Authentication** — Secure registration & login with BCrypt password hashing
- **Redis Caching** — Cache-aside pattern for sub-millisecond URL resolution
- **Click Analytics** — Track IP address, user-agent, referer, and timestamps asynchronously
- **Rate Limiting** — Custom sliding-window rate limiter (10 req/min per IP)
- **URL Expiration** — Auto-deactivate expired URLs via scheduled cleanup job
- **Swagger Docs** — Interactive API documentation
- **Responsive UI** — Modern dark-themed dashboard with shadcn/ui components

---

## 🏗️ Architecture

```
┌─────────────┐       ┌──────────────────┐       ┌─────────┐
│   React.js  │──────▶│   Spring Boot    │──────▶│  MySQL  │
│   (Vercel)  │  API  │   (Render)       │       └─────────┘
└─────────────┘       │                  │       ┌─────────┐
                      │  JWT Auth Filter │──────▶│  Redis  │
                      │  Rate Limiter    │       │(Upstash)│
                      └──────────────────┘       └─────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 21, Spring Boot 4, Spring Security, Spring Data JPA |
| **Frontend** | React.js, Vite, Tailwind CSS, shadcn/ui |
| **Database** | MySQL (Aiven) |
| **Caching** | Redis (Upstash) |
| **Auth** | JWT (jjwt), BCrypt |
| **Docs** | Swagger / OpenAPI (springdoc) |
| **Deployment** | Render (backend), Vercel (frontend), Docker |

---

## 📡 API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/auth/register` | ❌ | Register a new user |
| `POST` | `/api/auth/login` | ❌ | Login & get JWT token |
| `POST` | `/api/url/shorten` | ✅ | Shorten a URL |
| `GET` | `/api/url/my-urls` | ✅ | List user's URLs |
| `GET` | `/api/url/{code}/stats` | ✅ | Get URL statistics |
| `DELETE` | `/api/url/{code}` | ✅ | Deactivate a URL |
| `GET` | `/{shortCode}` | ❌ | Redirect to original URL |
| `GET` | `/api/v1/analytics/{code}` | ✅ | Detailed click analytics |

---

## 🚀 Getting Started

### Prerequisites
- Java 21
- Node.js 18+
- MySQL
- Redis

### Backend Setup
```bash
cd urlshortener
# Update src/main/resources/application.properties with your DB credentials
./mvnw spring-boot:run
```
Backend runs on `http://localhost:8080`

### Frontend Setup
```bash
cd FRONTEND
npm install
npm run dev
```
Frontend runs on `http://localhost:3000`

---

## 📂 Project Structure

```
url-shortener/
├── urlshortener/                   # Spring Boot Backend
│   ├── src/main/java/.../
│   │   ├── config/                 # SecurityConfig, JwtAuthFilter, SwaggerConfig
│   │   ├── controller/             # AuthController, UrlController, AnalyticsController
│   │   ├── dto/                    # Request/Response DTOs
│   │   ├── entity/                 # User, Url, ClickAnalytics
│   │   ├── exception/              # GlobalExceptionHandler
│   │   ├── repository/             # JPA Repositories
│   │   ├── scheduler/              # UrlCleanupScheduler
│   │   ├── service/                # Business logic + interfaces
│   │   └── util/                   # Base62Util, JwtUtil
│   ├── Dockerfile
│   └── pom.xml
│
├── FRONTEND/                       # React Frontend
│   ├── src/
│   │   ├── components/             # Navbar, ProtectedRoute, UI components
│   │   ├── context/                # AuthContext (JWT management)
│   │   ├── lib/                    # Axios API client, utilities
│   │   └── pages/                  # Home, Login, Register, Dashboard, Analytics
│   └── vite.config.js
```

---

## 🔑 Key Implementation Details

### URL Shortening Algorithm
- Uses **Base62 encoding** (a-z, A-Z, 0-9) to generate 7-character short codes
- Collision-safe: retries up to 10 times if a code already exists

### Caching Strategy
- **Cache-aside pattern**: Check Redis first → DB on miss → populate cache
- Reduces database load by ~80% for frequently accessed URLs

### Async Click Tracking
- Uses Spring `@Async` with a dedicated `ClickTrackingService`
- Request data (IP, user-agent, referer) extracted on main thread before async handoff
- Zero impact on redirect latency

### Rate Limiting
- Custom sliding-window implementation using `ConcurrentHashMap`
- 10 requests per minute per IP on the `/api/url/shorten` endpoint
- Zero external dependencies

---

## ☁️ Deployment

| Service | Provider | Plan |
|---------|----------|------|
| Backend | [Render](https://render.com) | Free |
| Frontend | [Vercel](https://vercel.com) | Free |
| MySQL | [Aiven](https://aiven.io) | Free |
| Redis | [Upstash](https://upstash.com) | Free |

---

## 👤 Author

**Parth Nigade**
- GitHub: [@ParthNigade](https://github.com/ParthNigade)
