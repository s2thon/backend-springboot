# Backend Spring Boot Architecture

This project is a backend REST API for an e-commerce platform, built with **Spring Boot**. It provides endpoints for user authentication, product management, order processing, reviews, wishlists, payments (Stripe integration), notifications, and more.

---

## Features

- **User Authentication & Authorization**
  - JWT-based authentication  
  - Role-based access control  
  - Registration & login endpoints  

- **Product Management**
  - CRUD operations for products and categories  
  - Seller-specific product management  

- **Order & Payment**
  - Order and order item management  
  - Stripe payment integration  

- **Wishlist**
  - Add/remove products to/from user wishlists  

- **Reviews**
  - Product review creation and listing  

- **Notifications**
  - User notifications with read/unread status  

---

## Tech Stack

- **Java 17+**  
- **Spring Boot**  
- **Spring Security**  
- **Spring Data JPA (Hibernate)**  
- **Lombok**  
- **Stripe Java SDK**  
- **JWT (JSON Web Token)**  
- **H2/MySQL/PostgreSQL** (configurable)  
- **Maven**  

---

## Project Structure

```
src/main/java/com/example/backend/
├── config/         # Security and CORS configuration
├── controller/     # REST API controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── repository/     # Spring Data JPA repositories
├── security/       # JWT utilities and filters
├── service/        # Service interfaces
├── service/impl/   # Service implementations
└── BackendApplication.java
```

---

## Key Endpoints

| Resource      | Endpoint Example                          | Description                  |
|---------------|--------------------------------------------|------------------------------|
| Auth          | `/api/auth/register`, `/api/auth/login`    | User registration & login    |
| Products      | `/api/products`, `/api/products/{id}`      | Product CRUD                 |
| Categories    | `/api/categories`                          | Category CRUD                |
| Orders        | `/api/orders`, `/api/order-items`          | Order management             |
| Reviews       | `/api/products/{productId}/reviews`        | Product reviews              |
| Wishlist      | `/api/lists`                               | User wishlist management     |
| Payments      | `/api/payments/checkout`                   | Stripe payment integration   |
| Notifications | `/api/notifications`                       | User notifications           |
| Users         | `/api/users`                               | User management              |
| Roles         | `/api/roles`                               | Role management              |

---

## Security

- **JWT Authentication**: All protected endpoints require a valid JWT in the `Authorization` header.  
- **CORS**: Configured for frontend at [`http://localhost:4200`](http://localhost:4200).

---

## Running the Application

1. **Clone the repository**
   ```sh
   git clone https://github.com/your-org/your-repo.git
   cd backend-springboot
   ```

2. **Configure Database & Stripe**
   - Edit `src/main/resources/application.properties` for your DB and Stripe keys.

3. **Build & Run**
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

4. **API Docs**
   - (Optional) Integrate Swagger/OpenAPI for interactive docs.

---

## Testing

- Unit and integration tests are located under:  
  `src/test/java/com/example/backend/`

---

## Contributing

1. Fork the repo  
2. Create your feature branch  
   ```sh
   git checkout -b feature/YourFeature
   ```
3. Commit your changes  
4. Push to the branch  
5. Open a Pull Request  

---

## License

This project is licensed under the MIT License.

---

## Contact

For questions or support, please open an issue or contact the maintainer.