# 🛒 Flipkart Clone — Full-Stack E-Commerce App

A complete production-grade e-commerce application built with **Spring Boot 3** + **Java 17**, featuring JWT authentication, full REST API, and a Flipkart-like responsive frontend.

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Run the Application
```bash
cd ecommerce
mvn spring-boot:run
```

The app starts at **http://localhost:8080**

### Demo Credentials (auto-seeded)
| Role     | Email                    | Password    |
|----------|--------------------------|-------------|
| Customer | john@example.com         | user123     |
| Seller   | seller@flipkart.com      | seller123   |
| Admin    | admin@flipkart.com       | admin123    |

### Key URLs
| URL                                     | Description              |
|-----------------------------------------|--------------------------|
| http://localhost:8080                   | Frontend UI (Flipkart-like) |
| http://localhost:8080/swagger-ui.html   | Interactive API docs      |
| http://localhost:8080/h2-console        | H2 in-memory database    |
| http://localhost:8080/api-docs          | OpenAPI JSON spec        |

---

## 🏗️ Project Architecture

```
src/main/java/com/flipkart/
├── EcommerceApplication.java       # Main entry point
│
├── model/                          # JPA Entities
│   ├── User.java                   # User with roles (CUSTOMER/SELLER/ADMIN)
│   ├── Product.java                # Product with specs, images, ratings
│   ├── Category.java               # Hierarchical categories
│   ├── Cart.java / CartItem.java   # Shopping cart
│   ├── Order.java / OrderItem.java # Orders with status tracking
│   ├── Review.java                 # Product ratings & reviews
│   ├── Wishlist.java               # User wishlist
│   ├── Address.java                # Shipping addresses
│   └── Coupon.java                 # Discount coupons
│
├── repository/                     # Spring Data JPA repositories
│   ├── UserRepository.java
│   ├── ProductRepository.java      # Custom search + filter queries
│   ├── OrderRepository.java        # Revenue + stats queries
│   └── ...
│
├── dto/                            # Request/Response DTOs (30+ classes)
│   ├── RegisterRequest.java
│   ├── ProductResponse.java
│   ├── PageResponse.java           # Generic paginated response
│   └── ApiResponse.java            # Unified API response wrapper
│
├── service/                        # Business logic layer
│   ├── AuthService.java            # JWT auth, register, login
│   ├── ProductService.java         # Search, filter, CRUD
│   ├── OrderService.java           # Place, track, cancel orders
│   ├── CartService.java            # Cart management
│   ├── ReviewService.java          # Reviews + rating recalculation
│   └── ...
│
├── controller/                     # REST Controllers
│   ├── AuthController.java         # POST /api/auth/**
│   ├── ProductController.java      # GET/POST/PUT/DELETE /api/products/**
│   ├── CartController.java         # /api/cart/**
│   ├── OrderController.java        # /api/orders/**
│   ├── AdminController.java        # /api/admin/** (ADMIN only)
│   └── MiscControllers.java        # Reviews, Addresses, Wishlist, Coupons
│
├── security/
│   ├── JwtUtils.java               # JWT generation & validation
│   ├── JwtAuthFilter.java          # Request filter
│   └── UserDetailsServiceImpl.java
│
├── config/
│   ├── SecurityConfig.java         # Spring Security configuration
│   ├── DataInitializer.java        # Seeds 20+ products, categories, coupons
│   ├── OpenApiConfig.java          # Swagger/OpenAPI setup
│   └── WebMvcConfig.java           # Static resource serving
│
└── exception/
    ├── GlobalExceptionHandler.java  # @RestControllerAdvice
    ├── ResourceNotFoundException.java
    ├── BadRequestException.java
    └── UnauthorizedException.java

src/main/resources/static/
└── index.html                       # Complete Flipkart-like SPA frontend
```

---

## 📡 API Reference

### Authentication
```
POST /api/auth/register        Register new user
POST /api/auth/login           Login → returns JWT token
POST /api/auth/refresh         Refresh access token
```

### Products (Public)
```
GET  /api/products             All products (paginated)
GET  /api/products/{id}        Product detail
GET  /api/products/search?q=   Full-text search
GET  /api/products/featured    Featured products
GET  /api/products/best-sellers Top sellers
GET  /api/products/new-arrivals Latest products
GET  /api/products/category/{id} By category
GET  /api/products/filter      Filter: price, brand, category, sort
```

### Products (Seller/Admin)
```
POST   /api/products           Create product
PUT    /api/products/{id}      Update product
DELETE /api/products/{id}      Deactivate product
```

### Cart (Auth required)
```
GET    /api/cart               View cart
POST   /api/cart/items         Add item
PUT    /api/cart/items/{id}    Update quantity
DELETE /api/cart/items/{id}    Remove item
```

### Orders (Auth required)
```
POST /api/orders               Place order from cart
GET  /api/orders               My orders (paginated)
GET  /api/orders/{id}          Order detail
POST /api/orders/{id}/cancel   Cancel order
```

### Reviews
```
GET  /api/reviews/product/{id} Get product reviews
POST /api/reviews              Submit review (auth)
DELETE /api/reviews/{id}       Delete review (auth)
```

### Addresses (Auth required)
```
GET    /api/addresses          List addresses
POST   /api/addresses          Add address
PUT    /api/addresses/{id}     Update address
DELETE /api/addresses/{id}     Delete address
```

### Wishlist (Auth required)
```
GET  /api/wishlist                  View wishlist
POST /api/wishlist/toggle/{productId} Add/Remove toggle
GET  /api/wishlist/check/{productId}  Is in wishlist?
```

### Coupons
```
POST /api/coupons/validate     Validate coupon + calculate discount
GET  /api/coupons              List available coupons
```

### Admin (Admin role only)
```
GET   /api/admin/dashboard     Stats: users, products, orders, revenue
GET   /api/admin/users         All users
PATCH /api/admin/users/{id}/toggle  Enable/disable user
GET   /api/admin/orders        All orders
PATCH /api/admin/orders/{id}/status Update order status
POST  /api/admin/coupons       Create coupon
```

---

## 💳 Demo Coupon Codes

| Code       | Discount              | Min Order |
|------------|-----------------------|-----------|
| WELCOME10  | 10% off (max ₹200)    | ₹500      |
| FLAT200    | ₹200 flat off         | ₹999      |
| SAVE15     | 15% off (max ₹2000)   | ₹5000     |

---

## 🔒 Security

- **JWT Bearer Token** authentication
- Role-based access: `CUSTOMER`, `SELLER`, `ADMIN`
- BCrypt password hashing
- CORS configured (open for development)
- Method-level security with `@PreAuthorize`

---

## 🗄️ Production: Switch to MySQL

Update `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/flipkartdb
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=false
```

---

## 🎯 Features Implemented

- [x] JWT Authentication (Register, Login, Refresh)
- [x] Role-based authorization (Customer, Seller, Admin)
- [x] Product Management (CRUD, images, specs, SKU)
- [x] Hierarchical Categories with slugs
- [x] Full-text Product Search
- [x] Product Filtering (price, brand, category, sort)
- [x] Shopping Cart (add, update, remove)
- [x] Order Management (place, track, cancel)
- [x] Stock Management (auto-deduct on order, restore on cancel)
- [x] Product Reviews & Ratings (auto-recalculate avg)
- [x] Wishlist toggle
- [x] Shipping Address management
- [x] Coupon System (% and flat discounts)
- [x] Admin Dashboard (stats, user/order/product management)
- [x] Swagger/OpenAPI docs at `/swagger-ui.html`
- [x] H2 in-memory DB (zero-setup dev environment)
- [x] 20+ seeded products across 14 categories
- [x] Flipkart-like responsive frontend SPA
- [x] Global exception handling with clean error responses
- [x] Paginated responses for all list endpoints
