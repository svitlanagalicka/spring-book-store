package mate.academy.intro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Saves the book if the entered data is correct")
    void save_returnSavedBookDto_whenInputIsValid() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setCategoriesId(List.of(1L));
        Category category = new Category();
        category.setId(1L);

        Book book = new Book();
        Book savedBook = new Book();
        BookDto expectedDto = new BookDto();

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(expectedDto);

        BookDto result = bookService.save(requestDto);

        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("Gives an error if incorrect data is entered")
    void save_throwEntityNotFoundException_whenCategoryNotFound() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setCategoriesId(List.of(1L, 999L));
        Book book = new Book();

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.save(requestDto));

        assertTrue(exception.getMessage().contains("Can not find category with id: 999"));
    }

    @Test
    @DisplayName("Returns the book by identifier if it exists")
    void getBookById_returnBookDto_whenBookExist() {
        Long id = 1L;
        Book book = new Book();
        BookDto bookDto = new BookDto();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(bookDto);

        BookDto result = bookService.getBookById(id);
        assertEquals(bookDto, result);
    }

    @Test
    @DisplayName("Throws an error if the book with the specified identifier does not exist")
    void getBookById_throwEntityNotFoundException_whenBookNotFound() {
        Long id = 999L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.getBookById(id));
        assertTrue(exception.getMessage().contains("Book not found with id 999"));
    }

    @Test
    @DisplayName("Deletes the book by the specified ID")
    void deleteById_deleteBook_whenBookExist() {
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(true);
        bookService.deleteById(id);
    }

    @Test
    @DisplayName("Returns an error when deleting a book with an ID that does not exist")
    void deleteById_throwEntityNotFoundException_whenBookNotExist() {
        Long id = 999L;
        when(bookRepository.existsById(id)).thenReturn(false);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.deleteById(id));
        assertTrue(exception.getMessage().contains("Can not find book with id: " + id));
    }

    @Test
    @DisplayName("Update book successfully when book exists")
    void updateBook_returnUpdatedBookDto_whenBookExist() {
        Long id = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        Book book = new Book();
        Book updatedBook = new Book();
        BookDto updatedBookDto = new BookDto();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.updateBook(book, requestDto)).thenReturn(updatedBook);
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        when(bookMapper.bookToBookDto(updatedBook)).thenReturn(updatedBookDto);

        BookDto result = bookService.updateBook(id, requestDto);
        assertEquals(updatedBookDto, result);
    }

    @Test
    @DisplayName("Returns an error when updating a non-existent book")
    void updateBook_throwEntityNotFoundException_whenBookNotFound() {
        Long id = 999L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBook(id, requestDto));
        assertTrue(exception.getMessage().contains("Cant find book by id " + id));
    }

    @Test
    @DisplayName("Should return list of BookDto when searching with empty parameters")
    void search_returnListOfBookDto() {
        BookSearchParametersDto parametersDto = new BookSearchParametersDto(null, null, null);
        Specification<Book> specification = mock(Specification.class);
        List<Book> books = List.of(new Book(), new Book(), new Book());
        List<BookDto> bookDtos = List.of(new BookDto(), new BookDto(), new BookDto());

        when(bookSpecificationBuilder.build(parametersDto)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(books);
        when(bookMapper.bookToBookDto(any(Book.class)))
                .thenReturn(bookDtos.get(0), bookDtos.get(1), bookDtos.get(2));

        List<BookDto> result = bookService.search(parametersDto);
        assertEquals(bookDtos, result);
    }

    @Test
    @DisplayName("Return list of books for a given category")
    void findBooksByCategoryId_returnListOfBookDto() {
        Long categoryId = 1L;
        List<Book> books = List.of(new Book(), new Book(), new Book());
        List<BookDto> bookDtos = List.of(new BookDto(), new BookDto(), new BookDto());

        when(bookRepository.findAllByCategoriesId(categoryId)).thenReturn(books);
        when(bookMapper.bookToBookDto(any(Book.class)))
                .thenReturn(bookDtos.get(0), bookDtos.get(1), bookDtos.get(2));

        List<BookDto> result = bookService.findBooksByCategoryId(categoryId);
        assertEquals(bookDtos.size(), result.size());
    }
}
