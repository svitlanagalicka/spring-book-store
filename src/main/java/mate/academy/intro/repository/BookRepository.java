package mate.academy.intro.repository;

import java.util.List;
import java.util.Optional;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();

    BookDto getBookById(Long id);

    Optional<Object> findById(Long id);
}
