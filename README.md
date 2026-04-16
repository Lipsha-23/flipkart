# 🛒 Flipkart Clone — Full-Stack E-Commerce Application

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white)
![H2](https://img.shields.io/badge/H2-In--Memory_DB-003FBF?style=for-the-badge&logo=h2&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)

A production-grade **Flipkart-like e-commerce platform** built with Spring Boot 3, featuring JWT authentication, a complete REST API, role-based access control, and a fully responsive vanilla JS frontend — no external UI framework required.

[🚀 Quick Start](#-quick-start) · [📡 API Reference](#-api-reference) · [🏗️ Architecture](#-architecture) · [🔒 Security](#-security) · [🗄️ Database](#-database-setup) · [🧪 Tests](#-running-tests)

</div>

---

## ✨ Features

| Module | What's Included |
|---|---|
| **Auth** | JWT login/register, refresh tokens, BCrypt passwords, role-based access |
| **Products** | CRUD, full-text search, category/brand/price filtering, pagination, sorting |
| **Categories** | Hierarchical tree (parent → subcategories), slugs, soft delete |
| **Cart** | Add, update quantity, remove items, real-time stock validation |
| **Orders** | Place from cart, 8-step status tracking, cancel, stock auto-management |
| **Reviews** | Star ratings, comments, auto-recalculates product average rating |
| **Wishlist** | Toggle add/remove, per-user saved products |
| **Addresses** | Multiple shipping addresses, default flag, type (Home / Work / Other) |
| **Coupons** | Percentage & flat discounts, min order, usage limits, expiry dates |
| **Payments** | Simulated payment gateway (UPI, Cards, Net Banking, COD) |
| **Seller** | Seller dashboard, manage own product listings |
| **Admin** | Full dashboard: users, orders, products, revenue stats, coupon management |
| **Frontend** | Flipkart-like SPA — hero carousel, category grid, product cards, cart sidebar, full checkout flow |
| **Tests** | 25 integration tests with Spring MockMvc |

---

## 🚀 Quick Start

### Prerequisites

- **Java 17+** — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/)

### Run in 3 Steps

```bash
# 1. Clone the repository
git clone https://github.com/YOUR_USERNAME/flipkart-clone.git
cd flipkart-clone

# 2. Build
mvn clean install -DskipTests

# 3. Run
mvn spring-boot:run
```

> The app starts on **http://localhost:8080** using H2 in-memory database — no external DB setup needed for development.

---

### 🌐 Key URLs After Startup

| URL | Description |
|-----|-------------|
| `http://localhost:8080` | Flipkart-like Frontend UI |
| `http://localhost:8080/swagger-ui.html` | Interactive API Documentation |
| `http://localhost:8080/h2-console` | H2 Database Console (dev only) |
| `http://localhost:8080/api-docs` | OpenAPI 3.0 JSON Spec |

---

### 🔑 Demo Accounts (auto-seeded on startup)

| Role | Email | Password |
|------|-------|----------|
| 👤 Customer | `john@example.com` | `user123` |
| 🏪 Seller | `seller@flipkart.com` | `seller123` |
| 🔑 Admin | `admin@flipkart.com` | `admin123` |

### 🎟️ Demo Coupon Codes

| Code | Discount | Min Order |
|------|----------|-----------|
| `WELCOME10` | 10% off (max ₹200) | ₹500 |
| `FLAT200` | ₹200 flat off | ₹999 |
| `SAVE15` | 15% off (max ₹2,000) | ₹5,000 |

---

## 🏗️ Architecture

### Project Structure

```
src/
├── main/
│   ├── java/com/flipkart/
│   │   ├── EcommerceApplication.java          # Entry point (@SpringBootApplication)
│   │   │
│   │   ├── model/                             # JPA Entities
│   │   │   ├── User.java                      # Roles: CUSTOMER | SELLER | ADMIN
│   │   │   ├── Product.java                   # Images list, specs map, avg rating
│   │   │   ├── Category.java                  # Self-referencing hierarchy
│   │   │   ├── Cart.java / CartItem.java
│   │   │   ├── Order.java / OrderItem.java    # 8 order statuses
│   │   │   ├── Review.java
│   │   │   ├── Wishlist.java
│   │   │   ├── Address.java
│   │   │   └── Coupon.java
│   │   │
│   │   ├── repository/                        # Spring Data JPA Repositories
│   │   │   ├── ProductRepository.java         # JPQL: search, filter, brands
│   │   │   ├── OrderRepository.java           # Revenue aggregation
│   │   │   └── ...                            # 10 repositories total
│   │   │
│   │   ├── dto/                               # Request & Response DTOs
│   │   │   ├── ApiResponse.java               # Uniform envelope: {success, message, data}
│   │   │   ├── PageResponse.java              # Generic pagination wrapper
│   │   │   └── ...                            # 27 DTO classes total
│   │   │
│   │   ├── service/                           # Business Logic Layer
│   │   │   ├── AuthService.java               # JWT auth, registration, refresh
│   │   │   ├── ProductService.java            # Search, filter, CRUD
│   │   │   ├── OrderService.java              # Cart → Order, stock deduction
│   │   │   ├── CartService.java               # Cart management
│   │   │   ├── ReviewService.java             # Reviews + rating recalculation
│   │   │   └── ...                            # 10 services total
│   │   │
│   │   ├── controller/                        # REST Controllers
│   │   │   ├── AuthController.java            # /api/auth/**
│   │   │   ├── ProductController.java         # /api/products/**
│   │   │   ├── CartController.java            # /api/cart/**
│   │   │   ├── OrderController.java           # /api/orders/**
│   │   │   ├── ReviewController.java          # /api/reviews/**
│   │   │   ├── AddressController.java         # /api/addresses/**
│   │   │   ├── WishlistController.java        # /api/wishlist/**
│   │   │   ├── CouponController.java          # /api/coupons/**
│   │   │   ├── PaymentController.java         # /api/payments/** (simulated)
│   │   │   ├── SellerController.java          # /api/seller/** (SELLER/ADMIN)
│   │   │   ├── AdminController.java           # /api/admin/** (ADMIN only)
│   │   │   └── UserController.java            # /api/users/**
│   │   │
│   │   ├── security/
│   │   │   ├── JwtUtils.java                  # Token generation & validation
│   │   │   ├── JwtAuthFilter.java             # OncePerRequestFilter
│   │   │   └── UserDetailsServiceImpl.java    # Loads user by email
│   │   │
│   │   ├── config/
│   │   │   ├── SecurityConfig.java            # Filter chain, CORS, @PreAuthorize
│   │   │   ├── DataInitializer.java           # Seeds 20+ products, 14 categories
│   │   │   ├── OpenApiConfig.java             # Swagger + bearer auth scheme
│   │   │   └── WebMvcConfig.java              # Static SPA resource serving
│   │   │
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java    # @RestControllerAdvice
│   │       ├── ResourceNotFoundException.java # 404
│   │       ├── BadRequestException.java       # 400
│   │       └── UnauthorizedException.java     # 401
│   │
│   └── resources/
│       ├── application.properties             # Dev (H2 in-memory)
│       ├── application-prod.properties        # Prod (MySQL + env vars)
│       └── static/index.html                  # Full Flipkart-like SPA
│
└── test/
    ├── java/com/flipkart/
    │   └── EcommerceApplicationTests.java     # 25 integration tests
    └── resources/application-test.properties
```

### Entity Relationships

```
User ──────────────┬──── Cart ──── CartItem ──── Product ──── Category
                   ├──── Order ─── OrderItem          │
                   ├──── Review ───────────────────────┘
                   ├──── Wishlist ──────────────────────┘
                   └──── Address

Order ─────────────────── Coupon (applied at checkout)
Category ──────────────── Category (parent self-reference)
```

### Uniform API Response

All endpoints return a consistent JSON envelope:

```json
{
  "success": true,
  "message": "Product fetched successfully",
  "data": { ... }
}
```

Paginated endpoints include:

```json
{
  "success": true,
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "first": true,
    "last": false
  }
}
```

---

## 📡 API Reference

### Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/auth/register` | Register new user | Public |
| `POST` | `/api/auth/login` | Login → JWT token | Public |
| `POST` | `/api/auth/refresh` | Refresh access token | Public |

**Register / Login example:**
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@test.com","password":"pass123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"user123"}'
```

---

### Products

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/products` | All products (paginated) | Public |
| `GET` | `/api/products/{id}` | Product by ID | Public |
| `GET` | `/api/products/search?q=` | Full-text search | Public |
| `GET` | `/api/products/featured` | Featured products | Public |
| `GET` | `/api/products/best-sellers` | Top 10 by sales | Public |
| `GET` | `/api/products/new-arrivals` | Latest 8 products | Public |
| `GET` | `/api/products/category/{id}` | By category | Public |
| `GET` | `/api/products/filter` | Filter by price/brand/sort | Public |
| `GET` | `/api/products/category/{id}/brands` | Available brands | Public |
| `POST` | `/api/products` | Create product | Seller / Admin |
| `PUT` | `/api/products/{id}` | Update product | Seller / Admin |
| `DELETE` | `/api/products/{id}` | Soft delete | Seller / Admin |

**Filter parameters:** `categoryId` · `minPrice` · `maxPrice` · `brand` · `sortBy` (`newest` / `popular` / `price_asc` / `price_desc` / `rating`) · `page` · `size`

---

### Cart

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/cart` | View cart |
| `POST` | `/api/cart/items` | Add item `{ productId, quantity }` |
| `PUT` | `/api/cart/items/{id}` | Update quantity `{ quantity }` — 0 removes |
| `DELETE` | `/api/cart/items/{id}` | Remove item |

---

### Orders

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/orders` | Place order from cart |
| `GET` | `/api/orders` | My orders (paginated) |
| `GET` | `/api/orders/{id}` | Order by ID |
| `GET` | `/api/orders/number/{num}` | Order by order number |
| `POST` | `/api/orders/{id}/cancel` | Cancel order |

**Order statuses:** `PENDING` → `CONFIRMED` → `PROCESSING` → `SHIPPED` → `OUT_FOR_DELIVERY` → `DELIVERED` / `CANCELLED` / `RETURNED`

**Place order body:**
```json
{
  "addressId": 1,
  "paymentMethod": "UPI",
  "couponCode": "WELCOME10",
  "paymentTransactionId": "TXN-ABC123"
}
```

**Payment methods:** `UPI` · `CREDIT_CARD` · `DEBIT_CARD` · `NET_BANKING` · `WALLET` · `CASH_ON_DELIVERY` · `EMI`

---

### Other Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/categories` | All categories | Public |
| `GET` | `/api/categories/tree` | Category tree | Public |
| `GET` | `/api/reviews/product/{id}` | Product reviews | Public |
| `POST` | `/api/reviews` | Submit review | Auth |
| `GET` | `/api/wishlist` | My wishlist | Auth |
| `POST` | `/api/wishlist/toggle/{id}` | Add / remove | Auth |
| `GET` | `/api/addresses` | My addresses | Auth |
| `POST` | `/api/addresses` | Add address | Auth |
| `POST` | `/api/coupons/validate` | Validate coupon | Auth |
| `POST` | `/api/payments/initiate` | Initiate payment | Auth |
| `GET` | `/api/seller/dashboard` | Seller stats | Seller |
| `GET` | `/api/admin/dashboard` | Admin stats | Admin |
| `GET` | `/api/admin/users` | All users | Admin |
| `PATCH` | `/api/admin/orders/{id}/status` | Update order status | Admin |

---

## 🔒 Security

- **JWT Bearer Tokens** — access token (24 h) + refresh token (7 d)
- **BCrypt** password hashing (strength 10)
- **Method-level security** with `@PreAuthorize`
- **CORS** configured (open in dev, restrict in prod)

| Role | Permissions |
|------|-------------|
| Public | Browse products, categories, reviews |
| `CUSTOMER` | Cart, orders, reviews, wishlist, addresses, profile |
| `SELLER` | Everything CUSTOMER + create/update own products, seller dashboard |
| `ADMIN` | Full access to all endpoints |

**Authenticating API requests:**
```bash
# Get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"user123"}' | jq -r '.data.token')

# Use token
curl http://localhost:8080/api/cart \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🗄️ Database Setup

### Development — H2 In-Memory (Default)

No setup needed. Access the console:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:flipkartdb`
- Username: `sa` · Password: *(blank)*

---

### Production — MySQL

**1. Create database:**
```sql
CREATE DATABASE flipkartdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'flipkart'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON flipkartdb.* TO 'flipkart'@'localhost';
```

**2. Set environment variables:**
```bash
export DB_URL="jdbc:mysql://localhost:3306/flipkartdb?useSSL=false&serverTimezone=UTC"
export DB_USER=flipkart
export DB_PASSWORD=your_password
export JWT_SECRET=your_very_long_256_bit_secret_key_minimum_32_chars
```

**3. Run with prod profile:**
```bash
java -jar target/ecommerce-1.0.0.jar --spring.profiles.active=prod
```

---

## 🧪 Running Tests

```bash
# Run all 25 integration tests
mvn test

# Run a specific test
mvn test -Dtest=EcommerceApplicationTests#loginUser_success

# Skip tests during build
mvn clean package -DskipTests
```

**Test coverage includes:**
- Auth: register, login, bad credentials
- Products: list, search, filter, get by ID, 404 not found
- Categories: list, tree structure
- Cart: unauthenticated access, add item, invalid product
- Coupons: valid code, invalid code
- Admin: dashboard access control (admin vs. customer)
- User: profile retrieval

---

## 🐳 Docker

**Build and run with Docker Compose (includes MySQL):**

```yaml
# docker-compose.yml
version: '3.8'
services:
  db:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: flipkartdb
      MYSQL_USER: flipkart
      MYSQL_PASSWORD: secret
      MYSQL_ROOT_PASSWORD: rootsecret
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:mysql://db:3306/flipkartdb?useSSL=false&serverTimezone=UTC
      DB_USER: flipkart
      DB_PASSWORD: secret
      JWT_SECRET: change_this_to_a_real_256_bit_secret_key
    depends_on:
      - db

volumes:
  mysql_data:
```

```bash
docker-compose up --build
```

---

## 🛠️ Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.0 |
| Security | Spring Security + JJWT | 6 / 0.12.3 |
| ORM | Spring Data JPA + Hibernate | 6 |
| Database | H2 (dev) / MySQL (prod) | — / 8.0 |
| Validation | Jakarta Bean Validation | 3 |
| Boilerplate | Lombok | latest |
| API Docs | SpringDoc OpenAPI (Swagger UI) | 2.3.0 |
| Build | Apache Maven | 3.8+ |
| Testing | JUnit 5 + Spring MockMvc | 5 |
| Frontend | Vanilla HTML / CSS / JS | — |

---

## 🌱 Seeded Demo Data

`DataInitializer` auto-runs on first startup and creates:

- **3 users** — admin, seller, customer
- **14 categories** — Electronics, Mobiles, Laptops, Headphones, TVs, Men's Fashion, Women's Fashion, Home & Kitchen, Appliances, Books, Sports & Fitness, Beauty, Toys & Baby
- **20+ products** — iPhone 15 Pro Max, Samsung Galaxy S24 Ultra, MacBook Air M3, Dell XPS 15, Sony WH-1000XM5, Apple AirPods Pro, Samsung QLED TV, OnePlus 12, Redmi Note 13, HP Gaming Laptop, Instant Pot, Atomic Habits, The Alchemist, Fitbit Charge 6, LEGO Technic Bugatti, and more
- **3 coupons** — WELCOME10 / FLAT200 / SAVE15

---

## 🔮 Possible Extensions

- [ ] Email notifications via Spring Mail / SendGrid
- [ ] Product image upload to AWS S3 or Cloudinary
- [ ] Redis caching for products and sessions
- [ ] Real payment gateway (Razorpay / Stripe)
- [ ] Elasticsearch for advanced product search
- [ ] React or Angular frontend rewrite
- [ ] Rate limiting with Bucket4j
- [ ] Scheduled order status progression
- [ ] Product recommendations engine

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

---

<div align="center">

Made with ☕ and Spring Boot

⭐ **Star this repo if you found it useful!**

</div>
