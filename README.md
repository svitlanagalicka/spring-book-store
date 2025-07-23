Online Book Store App
This is my Online Book Store App — a web application written in Java using Spring Boot.
It's an application for a book store that helps manage all books, users, shopping carts, and orders.
I created it to demonstrate how to build a Spring Boot app that can do many things — from user registration to order processing.

🚀 Features
Here’s what this app can do:
Books: Add, view, update, and delete books.
Categories: Organize books into categories (e.g., "Science Fiction," "Detectives") for easier searching.
Shopping Cart: Users can add books to their cart, change quantities, or remove items.
Orders: Users can place orders from their cart, and admins can view and update order statuses.
Authentication / Registration: Users can register and log in securely using Spring Security and JWT tokens.
Validation: The app validates input data (e.g., price or book ISBN).
API Documentation: There is a convenient Swagger UI page to explore and try all the API endpoints directly in the browser.
Database: Database changes are managed with Liquibase to avoid errors.
Docker: The entire app can be easily run using Docker Compose.

🛠️ Technologies
I used these tools and technologies:
Java 17
Spring Boot 3.4.1
Spring Data JPA — simplifies database interaction without complex SQL queries
Spring Security — to secure the app so unauthorized users cannot access it
Spring Web (MVC)
Lombok 1.18.36 — to keep code cleaner
MapStruct 1.6.3 — automatically converts objects between different formats (e.g., DB entities to DTOs)
io.jsonwebtoken (jjwt) 0.12.6 — for working with JWT tokens for authentication
Jakarta Validation — to validate incoming data
MySQL — the database for storing books, users, etc.
Liquibase 4.27.0 — to safely update database schema step by step
Springdoc OpenAPI & Swagger UI 2.8.0 — for automatic and user-friendly API documentation
Maven — for project build and dependency management
Docker Compose 2.5+ — to easily run the entire project inside containers

🏛️ How is it organized?
The app has several controllers, each responsible for a specific part:

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

📋 What do you need to run it?
To run this project on your machine, you need:
Java JDK 17 or newer
Maven
Docker Desktop (version 20+)
Docker Compose (version 2.5+)

📝 How to run? (Instructions)
Step 1: Clone the repo
Open your terminal and run:
shell
git clone https://github.com/YOUR_GITHUB_USERNAME/spring-book-store.git
cd spring-book-store

Step 2: Configure the .env file
Create a .env file in the project folder (where your docker-compose.yml is) and add the following, replacing <...> with your own values:

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

Step 3: Run everything with Docker
From the project folder, run:
shell
docker compose up --build
This command will build the Docker images for the app and the database, then start them. Wait until everything is ready.

Step 4: Access the app
If you run the app locally (not inside Docker), open:
http://localhost:8080/api
API documentation (Swagger UI) is available at:
http://localhost:8080/api/swagger-ui/index.html
If running inside Docker, use the same URLs (ports mapped accordingly).

🚧 Challenges I faced and how I solved them
During development, I encountered various difficulties, for example:
Security configuration (who can do what):
Problem: It was tricky to allow regular users to only view books, while admins could add and delete them. Also, handling JWT tokens correctly for authentication was a challenge.
Solution: I used the @PreAuthorize annotation on controller methods (e.g., @PreAuthorize("hasRole('ADMIN')")) to specify access roles. I also carefully configured Spring Security to correctly process JWT tokens and check user roles. It was not easy, but now the app is very secure!

📊 Database structure (schema)
Here are the relationships between entities in my app:

Users and Roles: A user can have multiple roles (e.g., user and admin), and a role can belong to many users.
Shopping Cart: Each user has their own cart with items.
Cart Item and Book: Each cart item references a specific book.
Book and Category: A book can belong to several categories (e.g., "Sci-Fi" and "New Releases"), and each category can have many books.
Order and Order Details: One order can contain many order items.
Order and User: Each order belongs to a specific user.