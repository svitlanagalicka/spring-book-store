package mate.academy.intro.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.BookSearchParametersDto;
import mate.academy.intro.dto.CreateBookRequestDto;
import mate.academy.intro.exception.EntityNotFoundException;
import mate.academy.intro.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private BookService bookService;

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
    void findAll_returnListOfBooks() throws Exception {
        List<BookDto> books = List.of(new BookDto());
        when(bookService.findAll(any(Pageable.class))).thenReturn(books);
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk());
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
    void getBookById_returnBook_success() throws Exception {
        BookDto bookDto = new BookDto();
        when(bookService.getBookById(1L)).thenReturn(bookDto);
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Does not find books with an ID that does not exist")
    @WithMockUser(roles = "USER")
    void getBookById_returnNotFound_bookNotExist() throws Exception {
        Long id = 999L;
        when(bookService.getBookById(id)).thenThrow(new EntityNotFoundException("Book not found"));
        mockMvc.perform(get("/books/" + id))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("When the book was successfully saved")
    void save_validRequestDto_returnSuccess() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setAuthor("Joshua Bloch");
        requestDto.setTitle("Effective Java");
        requestDto.setPrice(BigDecimal.valueOf(799));
        requestDto.setDescription("Best Java practices");
        requestDto.setIsbn("9780134685991");
        requestDto.setCoverImage("https://example.com/effective-java.jpg");
        requestDto.setCategoriesId(List.of(1L, 2L));

        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setAuthor(requestDto.getAuthor());
        expected.setTitle(requestDto.getTitle());
        expected.setPrice(requestDto.getPrice());
        expected.setDescription(requestDto.getDescription());
        expected.setIsbn(requestDto.getIsbn());
        expected.setCoverImage(requestDto.getCoverImage());
        expected.setCategoryIds(requestDto.getCategoriesId());

        when(bookService.save(any(CreateBookRequestDto.class))).thenReturn(expected);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/books")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getPrice(), actual.getPrice());
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
    void updateBook_returnUpdateBook_success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        BookDto updatedDto = new BookDto();

        when(bookService.updateBook(eq(1L), any(CreateBookRequestDto.class)))
                .thenReturn(updatedDto);

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Return NOT_FOUND when book does not exist")
    void updateBook_returnNotFound_bookNotExist() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        when(bookService.updateBook(eq(999L), any(CreateBookRequestDto.class)))
                .thenThrow(new EntityNotFoundException("Book not found"));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Book successfully deleted by administrator")
    void delete_returnNoContent_success() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Return NOT_FOUND when book does not exist")
    void delete_returnNotFound_bookNotExist() throws Exception {
        doThrow(new EntityNotFoundException("Book not found"))
                .when(bookService).deleteById(999L);
        mockMvc.perform(delete("/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Book search returns a list of books based on the specified parameters")
    void searchBooks_returnSearchBooks_success() throws Exception {
        BookDto bookDto = new BookDto();
        List<BookDto> expected = List.of(bookDto);

        when(bookService.search(any(BookSearchParametersDto.class)))
                .thenReturn(expected);
        mockMvc.perform(get("/books/search")
                .param("title", "Effective Java")
                .param("author", "Joshua Bloch"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Book search returns an empty list if no books are found")
    void searchBooks_returnEmptyList_whenBooksNotFound() throws Exception {
        when(bookService.search(any(BookSearchParametersDto.class)))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/books/search")
                        .param("title", "Unknown")
                        .param("author", "Nobody"))
                .andExpect(status().isOk());
    }
}
