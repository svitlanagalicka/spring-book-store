package mate.academy.intro.controller;

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
import java.util.List;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.CategoryDto;
import mate.academy.intro.dto.CreateCategoryRequestDto;
import mate.academy.intro.exception.EntityNotFoundException;
import mate.academy.intro.service.BookService;
import mate.academy.intro.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CategoryService categoryService;

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
    @DisplayName("Category creation is successful")
    @WithMockUser(roles = "ADMIN")
    void createCategory_returnCreated_success() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Animal");
        CategoryDto responseDto = new CategoryDto();
        when(categoryService.save(any(CreateCategoryRequestDto.class))).thenReturn(responseDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(post("/categories")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @DisplayName("Unable to create category")
    @WithMockUser(roles = "ADMIN")
    void createCategory_invalidRequestDto_returnBadRequest() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("Successfully returns the list of categories")
    @WithMockUser(roles = "USER")
    void getAll_returnListOfCategories() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        List<CategoryDto> categories = List.of(categoryDto);
        Page<CategoryDto> page = new PageImpl<>(categories);
        when(categoryService.findAll(any(Pageable.class))).thenReturn((page));
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Does not return a list of categories without registration")
    void findAll_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Successfully finds categories by ID")
    @WithMockUser(roles = "USER")
    void getCategoryById_returnCategoryId_success() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Science");
        when(categoryService.getById(1L)).thenReturn(categoryDto);
        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Does not find categories with an ID that does not exist")
    @WithMockUser(roles = "USER")
    void getCategoryById_returnNotFound_categoryNotExist() throws Exception {
        Long id = 999L;
        when(categoryService.getById(id))
                .thenThrow(new EntityNotFoundException("Category not found"));
        mockMvc.perform(get("/categories/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Return 200 OK when category is updated successfully by ADMIN")
    @WithMockUser(roles = "ADMIN")
    void updateCategory_returnUpdateCategory_success() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        CategoryDto updatedDto = new CategoryDto();

        when(categoryService.update(eq(1L), any(CreateCategoryRequestDto.class)))
                .thenReturn(updatedDto);

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Return NOT_FOUND when category does not exist")
    @WithMockUser(roles = "ADMIN")
    void updateCategory_returnNotFound_categoryNotExist() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        when(categoryService.update(eq(999L), any(CreateCategoryRequestDto.class)))
                .thenThrow(new EntityNotFoundException("Category not found"));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/categories/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Category successfully deleted by administrator")
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_returnNoContent_success() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Return NOT_FOUND when category does not exist")
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_returnNotFound_categoryNotExist() throws Exception {
        doThrow(new EntityNotFoundException("Category not found"))
                .when(categoryService).deleteById(999L);
        mockMvc.perform(delete("/categories/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Returns a list of books by category ID when the category exists")
    @WithMockUser(roles = "USER")
    void getBooksByCategoryId_returnBooks_success() throws Exception {
        BookDto bookDto = new BookDto();
        when(bookService.findBooksByCategoryId(1L)).thenReturn(List.of(bookDto));
        mockMvc.perform(get("/categories/1/books"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Return NOT_FOUND when category does not exist")
    @WithMockUser(roles = "USER")
    void getBooksByCategoryId_returnNotFound_whenCategoryNotExist() throws Exception {
        Long categoryId = 999L;
        when(bookService.findBooksByCategoryId(categoryId))
                .thenThrow(new EntityNotFoundException("Category not found"));
        mockMvc.perform(get("/categories/{id}/books", categoryId))
                .andExpect(status().isNotFound());
    }
}
