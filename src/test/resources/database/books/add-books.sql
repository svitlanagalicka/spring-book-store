INSERT INTO books (id, title, author, isbn, price, description, cover_image) VALUES
(1, 'Effective Java', 'Joshua Bloch', '9780134685991', 799, 'Best Java practices', 'https://example.com/effective-java.jpg');
INSERT INTO categories (id, name, description)
VALUES
(1, 'Programming', 'Books about programming'),
(2, 'Java', 'Java-specific books');
INSERT INTO books_categories (book_id, category_id)
VALUES
(1, 1),
(1, 2);