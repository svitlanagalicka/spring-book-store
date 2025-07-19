package mate.academy.intro.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import mate.academy.intro.dto.CartItemDto;
import mate.academy.intro.dto.CartItemRequestDto;
import mate.academy.intro.dto.ShoppingCartDto;
import mate.academy.intro.dto.UpdateCartItemDto;
import mate.academy.intro.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@email");

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testUser, null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Return ShoppingCartDto when cart exist")
    @Sql(scripts = "classpath:database/cartItems/add-cartItems.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCarts/delete-shoppingCarts.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getShoppingCart_returnShoppingCartDto_success() throws Exception {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(1L);
        cartItemDto.setBookId(1L);
        cartItemDto.setBookTitle("Effective Java");
        cartItemDto.setQuantity(2);

        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        expected.setUserId(1L);
        expected.setCartItems(Set.of(cartItemDto));

        MvcResult result = mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Return NotFound when cart does not exist")
    void getShoppingCart_returnNotFound_shoppingCartNotExist() throws Exception {
        mockMvc.perform(get("/cart")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Successfully adds the item to the cart")
    @Sql(scripts = "classpath:database/shoppingCarts/add-shoppingCarts.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCarts/delete-shoppingCarts.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addItemToCart_addToEmptyCart_success() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto();
        requestDto.setBookId(1L);
        requestDto.setQuantity(5);

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setBookId(1L);
        cartItemDto.setBookTitle("Effective Java");
        cartItemDto.setQuantity(5);

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(1L);
        shoppingCartDto.setUserId(1L);
        shoppingCartDto.setCartItems(Set.of(cartItemDto));

        MvcResult result = mockMvc.perform(post("/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        assertEquals(shoppingCartDto, actual);
    }

    @Test
    @DisplayName("Return BadRequest for missing book id")
    void addItemToCart_badRequest_missingBookId() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto();
        requestDto.setQuantity(2);
        mockMvc.perform(post("/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Successfully updates the number of items in the cart")
    @Sql(scripts = "classpath:database/cartItems/add-cartItems.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCarts/delete-shoppingCarts.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateItemQuantity_success() throws Exception {
        UpdateCartItemDto updateDto = new UpdateCartItemDto();
        updateDto.setQuantity(3);

        CartItemDto expectedCartItem = new CartItemDto();
        expectedCartItem.setId(1L);
        expectedCartItem.setBookId(1L);
        expectedCartItem.setBookTitle("Effective Java");
        expectedCartItem.setQuantity(3);

        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        expected.setUserId(1L);
        expected.setCartItems(Set.of(expectedCartItem));

        MvcResult result = mockMvc.perform(put("/cart/cart-items/{cartItemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Return NotFound when id for item does not exist")
    @Sql(scripts = "classpath:database/shoppingCarts/add-shoppingCarts.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCarts/delete-shoppingCarts.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateItemQuantity_notFoundId() throws Exception {
        UpdateCartItemDto updateDto = new UpdateCartItemDto();
        updateDto.setQuantity(3);

        mockMvc.perform(put("/cart/cart-items/{cartItemId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Successfully removes the item from the cart")
    @Sql(scripts = "classpath:database/cartItems/add-cartItems.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCarts/delete-shoppingCarts.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteItem_success() throws Exception {
        mockMvc.perform(delete("/cart/cart-items/{cartItemId}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Cannot remove product with non-existent id from cart")
    @Sql(scripts = "classpath:database/shoppingCarts/add-shoppingCarts.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCarts/delete-shoppingCarts.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteItem_whenIdNotExist() throws Exception {
        mockMvc.perform(delete("/cart/cart-items/{cartItemId}", 999L))
                .andExpect(status().isNotFound());
    }
}
