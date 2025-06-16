package mate.academy.intro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.CategoryDto;
import mate.academy.intro.dto.CreateCategoryRequestDto;
import mate.academy.intro.exception.EntityNotFoundException;
import mate.academy.intro.mapper.BookMapper;
import mate.academy.intro.mapper.CategoryMapper;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.Category;
import mate.academy.intro.repository.BookRepository;
import mate.academy.intro.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private BookServiceImpl bookService;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Returns category by id if it exists")
    void getById_returnCategoryDto_whenCategoryExist() {
        Long id = 1L;
        Category category = new Category();
        CategoryDto categoryDto = new CategoryDto();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryDto(category)).thenReturn(categoryDto);
        CategoryDto result = categoryService.getById(id);
        assertEquals(categoryDto, result);
    }

    @Test
    @DisplayName("Returns an error if there is no category with the given id")
    void getById_throwEntityNotFoundException_whenCategoryNotFound() {
        Long id = 999L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(id));
        assertTrue(exception.getMessage().contains("Category not found with id 999"));
    }

    @Test
    @DisplayName("Saves the category if the correct input data is specified")
    void save_returnCategoryDto_whenInputIsValid() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        Category category = new Category();
        Category saved = new Category();
        CategoryDto expected = new CategoryDto();

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(saved);
        when(categoryMapper.toCategoryDto(saved)).thenReturn(expected);

        CategoryDto actualDto = categoryService.save(requestDto);
        assertEquals(expected, actualDto);
    }

    @Test
    @DisplayName("Should throw RuntimeException when categoryMapper fails during save")
    void save_throwException_whenMapperFails() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        when(categoryMapper.toEntity(requestDto))
                .thenThrow(new RuntimeException("Failed to map category request DTO to entity"));
        assertThrows(RuntimeException.class, () -> categoryService.save(requestDto));
    }

    @Test
    @DisplayName("Updates the category if it exists")
    void update_returnUpdatedCategoryDto_whenCategoryExist() {
        Long id = 1L;
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        Category existingCategory = new Category();
        CategoryDto expectedCategoryDto = new CategoryDto();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
        when(categoryMapper.toCategoryDto(existingCategory)).thenReturn(expectedCategoryDto);
        CategoryDto result = categoryService.update(id, requestDto);
        assertEquals(expectedCategoryDto, result);
    }

    @Test
    @DisplayName("Throw EntityNotFoundException when category with given ID does not exist")
    void update_throwEntityNotFoundException_whenCategoryNotFound() {
        Long id = 999L;
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(id, requestDto));
        assertTrue(exception.getMessage().contains("Cant find category by id " + id));
    }

    @Test
    @DisplayName("Deletes category by id if it exists")
    void deleteById_deleteCategory_whenCategoryExist() {
        Long id = 1L;
        when(categoryRepository.existsById(id)).thenReturn(true);
        categoryService.deleteById(id);
    }

    @Test
    @DisplayName("Returns an error when deleting a category with a non-existent id")
    void deleteById_throwEntityNotFoundException_whenCategoryNotExist() {
        Long id = 999L;
        when(categoryRepository.existsById(id)).thenReturn(false);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(id));
        assertTrue(exception.getMessage().contains("Can not find category with id: " + id));
    }

    @Test
    @DisplayName("Should return list of BookDto when books exist for given category ID")
    void getBookCategoryById_returnMappedBooks_whenBooksExist() {
        Long categoryId = 1L;
        List<Book> books = List.of(new Book(), new Book());
        List<BookDto> bookDtos = List.of(new BookDto(), new BookDto());
        when(bookRepository.findAllByCategoriesId(categoryId)).thenReturn(books);
        when(bookMapper.bookToBookDto(any(Book.class)))
                .thenReturn(bookDtos.get(0), bookDtos.get(1));
        List<BookDto> result = bookService.findBooksByCategoryId(categoryId);
        assertEquals(bookDtos, result);
    }

    @Test
    @DisplayName("Should return empty list when no books found for given category ID")
    void getBookCategoryById_returnEmptyList_whenBooksNotFound() {
        Long categoryId = 999L;
        when(bookRepository.findAllByCategoriesId(categoryId)).thenReturn(List.of());
        List<BookDto> result = bookService.findBooksByCategoryId(categoryId);
        assertTrue(result.isEmpty());
    }
}
