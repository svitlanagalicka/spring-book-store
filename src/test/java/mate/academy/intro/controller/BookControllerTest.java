package mate.academy.intro.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.CreateBookRequestDto;
import mate.academy.intro.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Successfully returns the list of books")
    @WithMockUser(roles = "USER")
    @Sql(scripts = "classpath:database/books/add-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_returnListOfBooks() throws Exception {
        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> actual = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<BookDto>>() {});

        List<BookDto> expected = List.of(new BookDto(1L, "Effective Java",
                "Joshua Bloch", "9780134685991",
                new BigDecimal("799.00"), "Best Java practices",
                "https://example.com/effective-java.jpg", List.of(1L, 2L)));
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Does not return a list of books without registration")
    void findAll_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Successfully finds books by ID")
    @WithMockUser(roles = "USER")
    @Sql(scripts = "classpath:database/books/add-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getBookById_returnBook_success() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle("Effective Java");
        expected.setAuthor("Joshua Bloch");
        expected.setIsbn("9780134685991");
        expected.setDescription("Best Java practices");
        expected.setPrice(new BigDecimal("799.00"));
        expected.setCategoryIds(List.of(1L, 2L));
        expected.setCoverImage("https://example.com/effective-java.jpg");

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);

        assertEquals(expected,actual);
    }

    @Test
    @DisplayName("Does not find books with an ID that does not exist")
    @WithMockUser(roles = "USER")
    void getBookById_returnNotFound_bookNotExist() throws Exception {
        Long id = 999L;
        mockMvc.perform(get("/books/" + id))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("When the book was successfully saved")
    @Sql(scripts = "classpath:database/books/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/add-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void save_validRequestDto_returnSuccess() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();

        BookDto expected = TestUtil.createBookDto(1L);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/books")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertEquals(expected, actual);
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("Creating a book with incorrect data")
    void save_invalidRequestDto_returnBadRequest() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setPrice(BigDecimal.valueOf(20));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Return 200 OK when book is updated successfully by ADMIN")
    @Sql(scripts = "classpath:database/books/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/add-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBook_returnUpdateBook_success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Updated Title");
        requestDto.setAuthor("Updated Author");
        requestDto.setPrice(BigDecimal.valueOf(999));
        requestDto.setDescription("Updated Description");
        requestDto.setIsbn("9876543210987");
        requestDto.setCoverImage("https://example.com/updated.jpg");
        requestDto.setCategoriesId(List.of(1L, 2L));
        Long id = 1L;

        BookDto expected = new BookDto();
        expected.setId(id);
        expected.setTitle("Updated Title");
        expected.setAuthor("Updated Author");
        expected.setPrice(BigDecimal.valueOf(999));
        expected.setDescription("Updated Description");
        expected.setIsbn("9876543210987");
        expected.setCoverImage("https://example.com/updated.jpg");
        expected.setCategoryIds(List.of(1L, 2L));

        MvcResult result = mockMvc.perform(put("/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Return NOT_FOUND when book does not exist")
    void updateBook_returnNotFound_bookNotExist() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Book successfully deleted by administrator")
    @Sql(scripts = "classpath:database/books/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/add-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_returnNoContent_success() throws Exception {
        mockMvc.perform(delete("/books/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Return NOT_FOUND when book does not exist")
    void delete_returnNotFound_bookNotExist() throws Exception {
        mockMvc.perform(delete("/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Book search returns a list of books based on the specified parameters")
    @Sql(scripts = "classpath:database/books/add-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void searchBooks_returnSearchBooks_success() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/search")
                .param("title", "Effective Java")
                .param("author", "Joshua Bloch")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<BookDto>>() {});
        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        BookDto firstBook = actual.get(0);
        assertEquals("Effective Java", firstBook.getTitle());
        assertEquals("Joshua Bloch", firstBook.getAuthor());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Book search returns an empty list if no books are found")
    void searchBooks_returnEmptyList_whenBooksNotFound() throws Exception {
        mockMvc.perform(get("/books/search")
                        .param("title", "Unknown")
                        .param("author", "Nobody"))
                .andExpect(status().isOk());
    }
}
