## GoLinkGone

A high-performance URL shortening and analytics system built with Spring Boot, engineered for low-latency redirects,
efficient tracking, and scalable data handling.

---

## 🚀 Features

### 🔗 URL Shortening

* Generates **secure 6-character alphanumeric keys**
* Compact, collision-resistant identifiers
* Optimized for fast resolution

### 📊 Advanced Analytics and Geo-Tracking

* **Asynchronous redirect counting**
* Asynchronous click event tracking eliminates latency overhead during link resolution.
* Integrates with external Geo-IP services to track visitor continent, country, and city data.
* Uses SHA-256 IP address hashing to maintain visitor privacy while accurately differentiating between new and returning
  visitors.
* Executes complex time-series data bucketing and geographic distribution aggregations via native SQL queries.

### 🧾 Visual Identifiers

* Generate:

    * **QR Codes**
    * **Barcodes**
* Enables seamless offline-to-online interaction

### ⚡ High Performance

* Uses time-ordered epoch UUIDs for primary keys, benefiting index locality and insert speeds.
* In-memory KeyStore using a ConcurrentHashMap pre-loads all short keys at startup, ensuring O(1) collision checks and
  immediate rejection of invalid URLs without querying the database.
* Custom asynchronous ThreadPoolTaskExecutor automatically tuned to match the Hikari connection pool size to prevent
  thread exhaustion during high-throughput analytics processing.

### 🧠 Multi-Layer Caching

* ConcurrentMapCacheManager caches resolved URLs to minimize database reads for frequently accessed links.
* Caffeine cache implementation stores Geo-IP lookup results for 24 hours to prevent rate-limiting and eliminate
  redundant external API calls.

---

## 🛠 Tech Stack

* **Language:** Java 17+
* **Framework:** Spring Boot 4.0.3
* **Database:** PostgreSQL
* **Persistence:** Spring Data JPA / Hibernate
* **Caching:** Spring Cache, Caffeine
* **Utilities:** Lombok, ZXing, UUID-Creator, Google Guava
* **Testing:** k6 (Load Testing)

---

## ⚙️ Getting Started

### Prerequisites

* Java 21
* PostgreSQL (running locally)
* Maven

---

## 🔧 Configuration

Configured via:

```
src/main/resources/application.yml
```

### Key Settings

```
Server Port: 8080
Database: localhost:5432/linksdb
```

### Connection Pool (HikariCP)

```
Max Pool Size: 70
Min Idle: 30
```

### Async Processing

* Thread pool dynamically scales based on Hikari configuration

---

## ▶️ Running the Application

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Application runs at:

```
http://localhost:8080
```

---

## 📡 API Endpoints

### Link Operations

```
POST   /shorten      → Create short link
GET    /{shortKey}   → Redirect + increment analytics
```

### Visual Generation

```
POST   /qr           → Generate QR code
POST   /barcode      → Generate barcode
```

### Monitoring

```
GET    /health       → Health check
GET    /info         → Application info
```

### Hibernate Statistics

* Enabled for query performance debugging
* Useful for identifying bottlenecks

---

## 🧪 Load Testing

k6 script available at:

```
load-tests/baseline-test.js
```

### Test Profile

* Duration: 30 seconds
* Virtual Users: 1000

### Purpose

* Validate redirect latency
* Stress test caching and async processing

---

## 🧠 Design Highlights

* **Optimized Redirect Path**

    * Minimal processing on request path
    * Analytics handled asynchronously

* **Efficient ID Strategy**

    * Time-ordered UUIDs reduce index fragmentation

* **Cache-First Reads**

    * Hot keys served without database access

---

## 📄 License

Licensed under the **MIT License**.

---
