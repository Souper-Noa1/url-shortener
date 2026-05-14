## Quick Start

**Prerequisites:** Java 17+, Maven, PostgreSQL running locally

```bash
# 1. Create the database
psql -U postgres -c "CREATE DATABASE url_shortener;"

# 2. Clone and run
git clone <your-repo>
cd url_shortener
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The app starts on `http://localhost:8080`. Flyway runs migrations automatically.

---

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/v1/auth/register` | No | Register a new user |
| POST | `/api/v1/auth/login` | No | Login, get JWT |
| POST | `/api/v1/urls` | Yes | Create short URL |
| GET | `/api/v1/urls/{code}` | No | Get URL details |
| GET | `/api/v1/urls/{code}/stats` | No | Get click stats |
| DELETE | `/api/v1/urls/{code}` | Yes (owner) | Disable a URL |
| GET | `/r/{code}` | No | Redirect to original URL |

**Authentication:** `Authorization: Bearer <token>`


Docker update soon
