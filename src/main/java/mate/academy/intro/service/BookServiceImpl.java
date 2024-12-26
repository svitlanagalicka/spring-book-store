package mate.academy.intro.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.CreateBookRequestDto;
import mate.academy.intro.exception.EntityNotFoundException;
import mate.academy.intro.mapper.BookMapper;
import mate.academy.intro.model.Book;
import mate.academy.intro.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.bookToBookDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = (Book) bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + id));
        return bookMapper.bookToBookDto(book);
    }
}
