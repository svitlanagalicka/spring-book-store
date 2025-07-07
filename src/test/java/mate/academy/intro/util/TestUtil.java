package mate.academy.intro.util;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.CategoryDto;
import mate.academy.intro.dto.CreateBookRequestDto;
import mate.academy.intro.dto.CreateCategoryRequestDto;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.Category;

public class TestUtil {
    public static CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setAuthor("Joshua Bloch");
        requestDto.setTitle("Effective Java");
        requestDto.setPrice(BigDecimal.valueOf(799));
        requestDto.setDescription("Best Java practices");
        requestDto.setIsbn("9780134685991");
        requestDto.setCoverImage("https://example.com/effective-java.jpg");
        requestDto.setCategoriesId(List.of(1L));
        return requestDto;
    }

    public static BookDto createBookDto(Long id) {
        CreateBookRequestDto requestDto = createBookRequestDto();
        BookDto expected = new BookDto();
        expected.setId(id);
        expected.setAuthor(requestDto.getAuthor());
        expected.setTitle(requestDto.getTitle());
        expected.setPrice(requestDto.getPrice());
        expected.setDescription(requestDto.getDescription());
        expected.setIsbn(requestDto.getIsbn());
        expected.setCoverImage(requestDto.getCoverImage());
        expected.setCategoryIds(requestDto.getCategoriesId());
        return expected;
    }

    public static CreateCategoryRequestDto createCategoryRequestDto() {
        CreateCategoryRequestDto createCategoryRequestDto = new CreateCategoryRequestDto();
        createCategoryRequestDto.setName("Animal");
        createCategoryRequestDto.setDescription("All about animals");
        return createCategoryRequestDto;
    }

    public static CategoryDto createCategoryDto(Long id) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(id);
        categoryDto.setName("Animal");
        categoryDto.setDescription("All about animals");
        return categoryDto;
    }

    public static Book createBook() {
        Book book = new Book();
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setIsbn("9780134685991");
        book.setPrice(BigDecimal.valueOf(799));
        book.setDescription("Best Java practices");
        book.setCoverImage("https://example.com/effective-java.jpg");
        return book;
    }

    public static Category createCategory() {
        Category category = new Category();
        category.setName("Programming");
        category.setDescription("All about programming");
        return category;
    }
}


