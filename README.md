This is a web application built with Java and Spring Boot. It allows managing books, categories, shopping cart, 
orders, users, and uses JWT-based authentication.

🚀 Features
- Books: add, view, update, delete books
- Categories: organize books into categories (e.g., "Science Fiction," "Detectives")
- Shopping Cart: users can add books, change quantity, remove items
- Orders: create orders from cart, view and update order statuses
- Authentication / Registration: users can register and log in securely using Spring Security and JWT tokens
- Validation: input data validation (e.g., price or book ISBN)
- Swagger UI: interactive API documentation
- Database: database changes are managed with Liquibase to avoid errors
- Docker: run the app with Docker Compose.

🛠️ Technologies
Java 17
Spring Boot 3.4.1
Spring Data JPA 
Spring Security
Spring Web (MVC)
Lombok 1.18.36
MapStruct 1.6.3
JJWT 0.12.6 (JWT tokens)
Jakarta Validation
MySQL 
Liquibase 4.27.0
Springdoc OpenAPI + Swagger UI 2.8.0
Maven
Docker Compose 2.5+

🏛️ How is it organized?
👤 AuthController (for login and registration)
POST /auth/registration — register new users
POST /auth/login — users log in and get a JWT token

📖 BookController (for books)
GET /books — list all books (ROLE_USER)
GET /books/{id} — show book details by ID (ROLE_USER)
GET /books/search — search books (ROLE_USER)
POST /books — add new book (ROLE_ADMIN only)
PUT /books/{id} — update book details (ROLE_ADMIN only)
DELETE /books/{id} — delete book (ROLE_ADMIN only)

📂 CategoryController (for book categories)
GET /categories — list all categories (ROLE_USER)
GET /categories/{id} — show category details by ID (ROLE_USER)
GET /categories/{id}/books — show books in a category (ROLE_USER)
POST /categories — create a new category (ROLE_ADMIN only)
PUT /categories/{id} — update category (ROLE_ADMIN only)
DELETE /categories/{id} — delete category (ROLE_ADMIN only)

🛒 OrderController (for orders)
POST /orders — place a new order (ROLE_USER)
GET /orders — list all orders of the current user (ROLE_USER)
GET /orders/{orderId}/items — show items in a specific order (order owner only, ROLE_USER)
GET /orders/{orderId}/items/{itemId} — show details of a specific item in an order (order owner only, ROLE_USER)

🛍️ ShoppingCartController (for shopping cart)
GET /cart — show current user’s cart (ROLE_USER)
POST /cart — add a book to the cart (ROLE_USER)
PUT /cart/{cartItemId} — update quantity of an item in the cart (ROLE_USER)
DELETE /cart/{cartItemId} — remove an item from the cart (ROLE_USER)

📋 Requirements
Java JDK 17+
Maven
Docker Desktop (version 20+)
Docker Compose (version 2.5+)

📝 How to run? (Instructions)
Step 1: Clone the repository 
Open your terminal and run:
shell
git clone https://github.com/YOUR_GITHUB_USERNAME/spring-book-store.git
cd spring-book-store

Step 2: Configure the .env file
Create a .env file in the project folder (where your docker-compose.yml is) 
and add the following, replacing <...> with your own values:
env
MYSQLDB_USER=<your_mysql_username>
MYSQLDB_ROOT_PASSWORD=<your_mysql_root_password>
MYSQLDB_DATABASE=online_book_store
MYSQL_LOCAL_PORT=3308
MYSQL_DOCKER_PORT=3306

JWT_EXPIRATION=3600000
JWT_SECRET=<your_jwt_secret_key>

SPRING_LOCAL_PORT=8080
SPRING_DOCKER_PORT=8080
DEBUG_PORT=5005
Important: Do not add this .env file to Git to keep your secrets safe!

Step 3: Run with Docker
docker compose up --build

Step 4: Access the app
If you run the app locally (not inside Docker), open:
http://localhost:8081/api
API documentation (Swagger UI) is available at:
http://localhost:8081/api/swagger-ui/index.html

🚧 Troubleshooting
🔐 Access and security
Issue: Difficult to manage access roles and endpoint protection
Solution: Use @PreAuthorize on controllers and properly configure 
JWT authentication in Spring Security

📊 Database structure (schema)
- The diagram below illustrates the main relationships between tables/entities in the Online Book Store application:
- Users ↔ Roles: Many-to-many — each user can have multiple roles, and each role can be assigned to multiple users. 
- Users ↔ Shopping Carts: One-to-one — each user has their own shopping cart.
- Shopping Cart ↔ Cart Items: One-to-many — a cart can contain multiple cart items.
- Cart Item ↔ Book: Many-to-one — each cart item refers to a specific book.
- Book ↔ Category: Many-to-many — a book can belong to multiple categories, and a category can include many books.
- Users ↔ Orders: One-to-many — a user can place multiple orders.
- Orders ↔ Order Items: One-to-many — each order consists of several items.

📬 Postman Collection
To test the API endpoints easily, use the included Postman collection 
(postman/spring-book-store.postman_collection.json).
How to use:
1.Open Postman.
2.Click Import and select the .json file from the repository.
3.For secured endpoints, authenticate first by calling POST /auth/login with your credentials.
4.Copy the JWT token from the login response.
5.For subsequent requests, go to the Authorization tab, select Bearer Token, and paste your JWT token.
6.Now you can send authenticated requests with proper permissions.