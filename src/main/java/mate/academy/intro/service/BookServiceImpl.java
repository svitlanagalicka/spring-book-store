package mate.academy.intro.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.BookSearchParametersDto;
import mate.academy.intro.dto.CreateBookRequestDto;
import mate.academy.intro.exception.EntityNotFoundException;
import mate.academy.intro.mapper.BookMapper;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.Category;
import mate.academy.intro.repository.BookRepository;
import mate.academy.intro.repository.BookSpecificationBuilder;
import mate.academy.intro.repository.CategoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        Set<Category> categories = requestDto.getCategoriesId().stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException("Can not find category with id: "
                                        + id)))
                .collect(Collectors.toSet());
        book.setCategories(categories);
        Book saved = bookRepository.save(book);
        Book savedBook = bookRepository.findByIdWithCategory(saved.getId())
                .orElseThrow(()
                        -> new EntityNotFoundException("Can not find book after saving"));
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + id));
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void deleteById(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Can not find book with id: " + id);
        }
    }

    @Override
    @Transactional
    public BookDto updateBook(Long id, CreateBookRequestDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(()
                        -> new EntityNotFoundException("Cant find book by id " + id));
        book = bookMapper.updateBook(book, bookDto);
        Set<Category> categories = bookDto.getCategoriesId().stream()
                .map(categoryId -> categoryRepository.findById(categoryId)
                        .orElseThrow(()
                                -> new EntityNotFoundException("Can not find category by id"
                                + categoryId)))
                .collect(Collectors.toSet());
        book.setCategories(categories);
        bookRepository.save(book);
        Book updatedBook = bookRepository.findByIdWithCategory(id).orElseThrow(()
                -> new EntityNotFoundException("Can not find book after saving by id" + id));
        return bookMapper.bookToBookDto(updatedBook);
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto parametersDto) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(parametersDto);
        return bookRepository.findAll(bookSpecification)
                .stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }

    @Override
    public List<BookDto> findBooksByCategoryId(Long id) {
        return bookRepository.findAllByCategoriesId(id).stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }
}
