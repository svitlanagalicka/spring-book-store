package mate.academy.intro.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.exception.DataProcessingException;
import mate.academy.intro.model.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory sessionFactory;

    @Override
    public Book save(Book book) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(book);
            transaction.commit();
            return book;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can not insert book into DB: " + book, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Book> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT u FROM Book u", Book.class).getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can not get all books from DB", e);
        }
    }

    @Override
    public BookDto getBookById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(BookDto.class, id);
        } catch (Exception e) {
            throw new DataProcessingException("Can not get book by ID from DB", e);
        }
    }

    @Override
    public Optional<Object> findById(Long id) {
        return Optional.empty();
    }
}
