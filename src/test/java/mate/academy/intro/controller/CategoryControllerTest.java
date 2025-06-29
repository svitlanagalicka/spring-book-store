package mate.academy.intro.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.intro.dto.CategoryDto;
import mate.academy.intro.dto.CreateCategoryRequestDto;
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
class CategoryControllerTest {

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
    @DisplayName("Category creation is successful")
    @WithMockUser(roles = "ADMIN")
    void createCategory_returnCreated_success() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();
        CategoryDto expected = TestUtil.createCategoryDto(1L);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post("/categories")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
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
    @Sql(scripts = "classpath:database/categories/add-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAll_returnListOfCategories() throws Exception {
        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Does not return a list of categories without registration")
    void getAll_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Successfully finds categories by ID")
    @WithMockUser(roles = "USER")
    @Sql(scripts = "classpath:database/categories/delete-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/add-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getCategoryById_returnCategoryId_success() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Science");
        MvcResult result = mockMvc.perform(get("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
    }

    @Test
    @DisplayName("Does not find categories with an ID that does not exist")
    @WithMockUser(roles = "USER")
    void getCategoryById_returnNotFound_categoryNotExist() throws Exception {
        Long id = 999L;
        mockMvc.perform(get("/categories/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Return 200 OK when category is updated successfully by ADMIN")
    @WithMockUser(roles = "ADMIN")
    @Sql(scripts = "classpath:database/categories/delete-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/add-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateCategory_returnUpdateCategory_success() throws Exception {
        Long id = 1L;
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();
        requestDto.setName("Animal");

        MvcResult result = mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        assertNotNull(actual);
        assertEquals(requestDto.getName(), actual.getName());
    }

    @Test
    @DisplayName("Return NOT_FOUND when category does not exist")
    @WithMockUser(roles = "ADMIN")
    void updateCategory_returnNotFound_categoryNotExist() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/categories/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Category successfully deleted by administrator")
    @WithMockUser(roles = "ADMIN")
    @Sql(scripts = "classpath:database/categories/delete-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/add-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/delete-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteCategory_returnNoContent_success() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Return NOT_FOUND when category does not exist")
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_returnNotFound_categoryNotExist() throws Exception {
        mockMvc.perform(delete("/categories/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Returns a list of books by category ID when the category exists")
    @WithMockUser(roles = "USER")
    void getBooksByCategoryId_returnBooks_success() throws Exception {
        mockMvc.perform(get("/categories/1/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
