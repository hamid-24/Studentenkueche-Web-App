package de.hsflensburg.certainlyuncertainse2hausarbeit.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Recipe;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.RecipeRequest;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.InputValidationService;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.RecipeService;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
public class ApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InputValidationService inputValidationService;
    @MockBean
    private UserService userService;
    @MockBean
    private RecipeService recipeService;

    @Test
    void testLoginSuccess() throws Exception {
        User validUser = User.builder().id(1).username("someone").password("passw").build();
        when(userService.findByUsername("someone")).thenReturn(validUser);
        String userJson = "{\"username\":\"someone\",\"password\":\"passw\"}";
        mockMvc.perform(post("/apilogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("auth"));
    }

    @Test
    void testWrongPassword() throws Exception {
        User validUser = User.builder().id(1).username("someone").password("passw").build();
        when(userService.findByUsername("someone")).thenReturn(validUser);
        String userJson = "{\"username\":\"someone\",\"password\":\"pasdf\"}";
        mockMvc.perform(post("/apilogin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isUnauthorized())
                .andExpect(cookie().doesNotExist("auth"));
    }

    @Test
    void testLoginFail() throws Exception {
        String userJson = "{\"username\":\"anurt\",\"password\":\"kortuz\"}";
        mockMvc.perform(post("/apilogin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isUnauthorized())
                .andExpect(cookie().doesNotExist("auth"));
    }

    @Test
    void testGetRecipeNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetRecipeLoggedIn() throws Exception {
        List<Recipe> returnedRecipe = new ArrayList<Recipe>();
        Cookie cookie = new Cookie("auth", "1");
        User mockUser = new User();
        mockUser.setId(1);

        when(userService.findById(1)).thenReturn(mockUser);
        when(recipeService.findByUser(any(User.class))).thenReturn(returnedRecipe);

        mockMvc.perform(get("/api/recipes/1").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(returnedRecipe)));
    }

    @Test
    void testUpdateSuccess() throws Exception {
        Cookie validCookie = new Cookie("auth", "1");
        User user = User.builder().id(1).build();
        Recipe recipe = Recipe.builder().id(1).build();
        recipe.setUser(user);

        when(userService.findById(1)).thenReturn(user);
        when(recipeService.findById(1)).thenReturn(recipe);

        mockMvc.perform(put("/api/recipes/1").cookie(validCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new RecipeRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateFail() throws Exception {
        Cookie invalidCookie = new Cookie("auth", "2");
        User user = User.builder().id(1).build();
        Recipe recipe = Recipe.builder().id(1).build();
        recipe.setUser(user);

        when(userService.findById(1)).thenReturn(user);
        when(recipeService.findById(1)).thenReturn(recipe);

        mockMvc.perform(put("/api/recipes/1").cookie(invalidCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new RecipeRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateRecipeNotExistent() throws Exception {
        Cookie validCookie = new Cookie("auth", "1");
        User user = User.builder().id(1).build();
        Recipe recipe = Recipe.builder().id(1).build();
        recipe.setUser(user);

        when(userService.findById(1)).thenReturn(user);
        when(recipeService.findById(1)).thenReturn(recipe);

        mockMvc.perform(put("/api/recipes/2").cookie(validCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new RecipeRequest())))
                .andExpect(status().isNotFound());
    }
}
