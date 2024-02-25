package de.hsflensburg.certainlyuncertainse2hausarbeit.controllers;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Recipe;
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
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InputValidationService inputValidationService;
    @MockBean
    private UserService userService;
    @MockBean
    private RecipeService recipeService;
    private MockHttpSession session;

    User user = new User("lukas", "passw");
    @BeforeEach
    void init() {
        when(userService.findByUsername("lukas")).thenReturn(user);
        session = new MockHttpSession();
    }

    @Test
    void testPreviewRecipes() throws Exception {
        // mock recipes to return when findRandom is called
        List<Recipe> recipes = Arrays.asList(new Recipe(), new Recipe());
        when(recipeService.findRandom(5)).thenReturn(recipes);

        mockMvc.perform(get("/login").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));

        assertThat(recipes).isEqualTo(session.getAttribute("previewRecipes"));
    }

    @Test
    void testLogout() throws Exception {
        session.setAttribute("me", new User()); //set login status
        mockMvc.perform(get("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));

        assertThat(session.getAttribute("me")).isNull();
    }

    @Test
    void testValidLoginData() throws Exception {
        mockMvc.perform(post("/login").session(session)
                        .param("username", "lukas")
                        .param("password", "passw"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipes"));
        assertThat(session.getAttribute("me")).isEqualTo(user);
    }

    @Test
    void testWrongPassword() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "lukas")
                        .param("password", "pa"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testUserNotExistent() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "ajet")
                        .param("password", "passw"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
