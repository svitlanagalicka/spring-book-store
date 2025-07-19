DELETE FROM users_roles;
DELETE FROM cart_items;
DELETE FROM shopping_carts;
DELETE FROM books;
DELETE FROM roles;
DELETE FROM users;

INSERT INTO users (id, email, password, first_name, last_name, is_deleted)
VALUES (1, 'test@email', 'password', 'John', 'Doe', 0);

INSERT INTO roles (id, role) VALUES (1, 'USER');
INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);

INSERT INTO shopping_carts (id, user_id, is_deleted)
VALUES (1, 1, 0);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES
(1, 'Effective Java', 'Joshua Bloch', '9780134685991', 799, 'Best Java practices', 'effective-java.jpg', 0);