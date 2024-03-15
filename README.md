# URL Shortener Service

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=springboot&logoColor=white)
![H2](https://img.shields.io/badge/H2-004088?style=flat&logo=h2&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)
![REST API](https://img.shields.io/badge/REST%20API-009688?style=flat)

A production-style URL shortening REST API built with Java and Spring Boot. Paste a long URL, get a short code back, track clicks, and set expiry dates. Includes a clean frontend UI.

---

## Why I Built This

"Design a URL Shortener" is one of the most common system design interview questions at Amazon, Google, and Microsoft. I built this from scratch to understand the architecture hands-on — Base62 encoding, database design, REST API patterns, and redirect handling.

---

## How It Works

```
User sends long URL via POST
          ↓
Service generates unique 6-char Base62 code
62^6 = 56 billion possible combinations
          ↓
Code + URL saved to database
          ↓
User shares short link
          ↓
GET request hits redirect endpoint
          ↓
Click count incremented
          ↓
302 redirect to original URL
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.2 |
| Database | H2 (file-based, persistent) |
| ORM | Spring Data JPA / Hibernate |
| Build | Maven |
| Frontend | HTML, CSS, JavaScript |

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/shorten` | Shorten a URL |
| GET | `/api/{shortCode}` | Redirect to original URL |
| GET | `/api/stats/{shortCode}` | Get click stats |

**Shorten a URL:**
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.google.com", "expiryDays": 30}'
```

**Response:**
```json
{
  "shortCode": "hKDmnn",
  "shortUrl": "http://localhost:8080/api/hKDmnn",
  "originalUrl": "https://www.google.com",
  "expiresAt": "2026-06-17T16:17:39"
}
```

**Get Stats:**
```bash
curl http://localhost:8080/api/stats/hKDmnn
```

**Response:**
```json
{
  "shortCode": "hKDmnn",
  "originalUrl": "https://www.google.com",
  "clickCount": 14,
  "createdAt": "2026-05-18T16:17:39",
  "expiresAt": "2026-06-17T16:17:39"
}
```

---

## Project Structure

```
url-shortener/
├── src/main/java/com/soumya/urlshortener/
│   ├── App.java                    ← Spring Boot entry point
│   ├── model/
│   │   └── Url.java               ← JPA entity / database table
│   ├── repository/
│   │   └── UrlRepository.java     ← Database access layer
│   ├── service/
│   │   └── UrlService.java        ← Business logic + Base62 encoding
│   └── controller/
│       └── UrlController.java     ← REST API endpoints
├── src/main/resources/
│   ├── application.properties     ← App config
│   └── static/
│       └── index.html             ← Frontend UI
└── pom.xml                        ← Maven dependencies
```

---

## Setup and Run

```bash
# Clone the repo
git clone https://github.com/soumyagdev/url-shortener.git
cd url-shortener

# Run with Maven
mvn spring-boot:run
```

App runs at `http://localhost:8080`
H2 console at `http://localhost:8080/h2-console`

---

## Database Schema

```sql
CREATE TABLE urls (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  short_code  VARCHAR(255) UNIQUE NOT NULL,
  original_url VARCHAR(2048) NOT NULL,
  click_count  BIGINT NOT NULL DEFAULT 0,
  created_at   TIMESTAMP NOT NULL,
  expires_at   TIMESTAMP
);
```

---

## Key Concepts

**Base62 Encoding** — uses `a-z A-Z 0-9` (62 chars). With 6 characters: 62^6 = 56 billion unique codes. Virtually unlimited for any real use case.

**Collision Handling** — the service keeps generating codes until it finds one that doesn't exist in the database. Collision chance is extremely low.

**URL Expiry** — each URL can have an optional expiry date. Expired URLs return 404. Checked on every redirect request.

**Click Tracking** — every successful redirect increments the click counter in the database atomically.

---

## Author

**Soumya Reddy Gaddam**
Software Engineer | Java · Spring Boot · Python · AWS

[![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white)](https://github.com/soumyagdev)
[![Portfolio](https://img.shields.io/badge/Portfolio-D4882E?style=flat&logo=safari&logoColor=white)](https://gsoumyar.github.io)
