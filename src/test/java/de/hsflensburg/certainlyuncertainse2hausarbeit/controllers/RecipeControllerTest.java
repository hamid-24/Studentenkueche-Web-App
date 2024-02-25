package de.hsflensburg.certainlyuncertainse2hausarbeit.controllers;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.InputValidationService;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.RecipeService;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
@WebMvcTest
@ExtendWith(MockitoExtension.class)
public class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InputValidationService inputValidationService;
    @MockBean
    private UserService userService;
    @MockBean
    private RecipeService recipeService;
    private MockHttpSession session;

    @BeforeEach
    public void init() {
        session = new MockHttpSession();
    }

    @Test
    void testFilterCategory() throws Exception {

        mockMvc.perform(get("/recipes")
                        .queryParam("category", "snack"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("activeFilter", "category"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipes"));
    }

    @Test
    void testFilterFast() throws Exception {
        mockMvc.perform(get("/recipes")
                        .queryParam("filter", "fast"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("activeFilter", "fast"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipes"));
    }

    @Test
    void testFilterCheap() throws Exception {
        mockMvc.perform(get("/recipes")
                        .queryParam("filter", "cheap"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("activeFilter", "cheap"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipes"));
    }

    @Test
    void testGetFavorites() throws Exception {
        session.setAttribute("me", new User());
        when(recipeService.findAllFavorites(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/recipes/favorites").session(session))
                .andExpect(view().name("favorites"));
    }

    @Test
    void testGetMyRecipes() throws Exception {
        session.setAttribute("me", new User());
        when(recipeService.findByUser(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/recipes/myrecipes").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userRecipes"))
                .andExpect(view().name("myRecipes"));
    }

    @Test
    void testGetPopularRecipes() throws Exception {
            mockMvc.perform(get("/recipes")
                            .queryParam("filter", "popular"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("activeFilter", "popular"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("recipes"));
        }
}
