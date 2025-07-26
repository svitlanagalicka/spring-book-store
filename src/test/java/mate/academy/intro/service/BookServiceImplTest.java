package mate.academy.intro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import mate.academy.intro.util.TestUtil;
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
        Category category = TestUtil.createCategory();
        Book book = TestUtil.createBook();

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle(book.getTitle());
        savedBook.setAuthor(book.getAuthor());
        savedBook.setIsbn(book.getIsbn());
        savedBook.setPrice(book.getPrice());
        savedBook.setDescription(book.getDescription());
        savedBook.setCoverImage(book.getCoverImage());
        savedBook.setCategories(Set.of(category));

        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();
        BookDto expectedDto = TestUtil.createBookDto(1L);

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(expectedDto);
        when(bookRepository.findByIdWithCategory(savedBook.getId()))
                .thenReturn(Optional.of(savedBook));

        BookDto result = bookService.save(requestDto);

        assertEquals(expectedDto, result);
        verify(bookMapper).toModel(requestDto);
        verify(categoryRepository).findById(1L);
        verify(bookRepository).save(book);
        verify(bookMapper).bookToBookDto(savedBook);
    }

    @Test
    @DisplayName("Gives an error if incorrect data is entered")
    void save_throwEntityNotFoundException_whenCategoryNotFound() {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();
        requestDto.setCategoriesId(List.of(1L, 999L));
        Book book = TestUtil.createBook();

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.save(requestDto));

        assertTrue(exception.getMessage().contains("Can not find category with id: 999"));
        verify(bookMapper).toModel(requestDto);
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).findById(999L);
    }

    @Test
    @DisplayName("Returns the book by identifier if it exists")
    void getBookById_returnBookDto_whenBookExist() {
        Book book = TestUtil.createBook();

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        Long id = 1L;

        when(bookRepository.findByIdWithCategory(id)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(bookDto);

        BookDto result = bookService.getBookById(id);
        assertEquals(bookDto, result);
        verify(bookRepository).findByIdWithCategory(id);
        verify(bookMapper).bookToBookDto(book);
    }

    @Test
    @DisplayName("Throws an error if the book with the specified identifier does not exist")
    void getBookById_throwEntityNotFoundException_whenBookNotFound() {
        Long id = 999L;
        when(bookRepository.findByIdWithCategory(id)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.getBookById(id));
        assertTrue(exception.getMessage().contains("Book not found with id 999"));
        verify(bookRepository).findByIdWithCategory(id);
    }

    @Test
    @DisplayName("Deletes the book by the specified ID")
    void deleteById_deleteBook_whenBookExist() {
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(true);
        bookService.deleteById(id);
        verify(bookRepository).existsById(id);
        verify(bookRepository).deleteById(id);
    }

    @Test
    @DisplayName("Returns an error when deleting a book with an ID that does not exist")
    void deleteById_throwEntityNotFoundException_whenBookNotExist() {
        Long id = 999L;
        when(bookRepository.existsById(id)).thenReturn(false);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.deleteById(id));
        assertTrue(exception.getMessage().contains("Can not find book with id: " + id));
        verify(bookRepository).existsById(id);
        verify(bookRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Update book successfully when book exists")
    void updateBook_returnUpdatedBookDto_whenBookExist() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Head First Java - Updated");
        requestDto.setAuthor("Kathy Sierra");
        requestDto.setIsbn("9780596009205");
        requestDto.setPrice(BigDecimal.valueOf(650.00));
        requestDto.setDescription("Updated description");
        requestDto.setCoverImage("https://example.com/headfirst-updated.jpg");
        requestDto.setCategoriesId(List.of(1L));
        Long id = 1L;

        Book book = new Book();
        book.setId(id);
        book.setTitle("Head First Java");
        book.setAuthor("Kathy Sierra");
        book.setIsbn("9780596009205");
        book.setPrice(BigDecimal.valueOf(599.99));
        book.setDescription("Introductory Java book");
        book.setCoverImage("https://example.com/headfirst.jpg");

        Book updatedBook = new Book();
        updatedBook.setId(id);
        updatedBook.setTitle(requestDto.getTitle());
        updatedBook.setAuthor(requestDto.getAuthor());
        updatedBook.setIsbn(requestDto.getIsbn());
        updatedBook.setPrice(requestDto.getPrice());
        updatedBook.setDescription(requestDto.getDescription());
        updatedBook.setCoverImage(requestDto.getCoverImage());

        BookDto updatedBookDto = new BookDto();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));
        when(bookMapper.updateBook(book, requestDto)).thenReturn(updatedBook);
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        when(bookRepository.findByIdWithCategory(id)).thenReturn(Optional.of(updatedBook));
        when(bookMapper.bookToBookDto(updatedBook)).thenReturn(updatedBookDto);

        BookDto result = bookService.updateBook(id, requestDto);
        assertEquals(updatedBookDto, result);
        verify(bookRepository).findById(id);
        verify(bookMapper).updateBook(book, requestDto);
        verify(bookRepository).save(updatedBook);
        verify(bookMapper).bookToBookDto(updatedBook);
        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Returns an error when updating a non-existent book")
    void updateBook_throwEntityNotFoundException_whenBookNotFound() {
        Long id = 999L;
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBook(id, requestDto));
        assertTrue(exception.getMessage().contains("Cant find book by id " + id));
        verify(bookRepository).findById(id);
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
        verify(bookSpecificationBuilder).build(parametersDto);
        verify(bookRepository).findAll(specification);
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
        verify(bookRepository).findAllByCategoriesId(categoryId);
    }
}
