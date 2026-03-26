## Link Analytics Platform

A **high-performance URL shortening and analytics system** built with Spring Boot, engineered for **low-latency redirects**, **efficient tracking**, and **scalable data handling**.

---

## 🚀 Features

### 🔗 URL Shortening

* Generates **secure 6-character alphanumeric keys**
* Compact, collision-resistant identifiers
* Optimized for fast resolution

### 📊 Analytics Tracking

* **Asynchronous redirect counting**
* Eliminates latency overhead during link resolution
* Designed for high-throughput systems

### 🧾 Visual Identifiers

* Generate:

  * **QR Codes**
  * **Barcodes**
* Enables seamless offline-to-online interaction

### ⚡ High Performance

* Uses **time-ordered epoch UUIDs**
* Benefits:

  * Better index locality
  * Faster inserts
  * Efficient queries

### 🧠 Caching

* Implemented using **ConcurrentMapCacheManager**
* Reduces database load for frequently accessed links

### 📈 Monitoring

* Integrated:

  * **Spring Boot Actuator**
  * **Prometheus metrics**
  * **Hibernate statistics**

---

## 🛠 Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 4.0.3
* **Database:** PostgreSQL
* **Persistence:** Spring Data JPA / Hibernate
* **Caching:** Spring Cache
* **Utilities:** Lombok, ZXing, UUID-Creator
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

---

## 📊 Analytics & Monitoring

### Prometheus Metrics

Available at:

```
/actuator/prometheus
```

Includes:

* Request latency percentiles:

  * P50
  * P95
  * P99

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

* **Built-in Observability**

  * Metrics and stats available by default

---

## 📄 License

Licensed under the **MIT License**.

---
