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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
public class RegisterTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InputValidationService inputValidationService;
    @MockBean
    private UserService userService;
    @MockBean
    private RecipeService recipeService;
    private MockHttpSession session;
    String validUsername = "umbaja";
    String invalidUsername = "um12";
    String validPassword = "alksjd";
    String invalidPassword = "als";
    String existentUsername = "timon";

    @BeforeEach
    void init() {
        when(userService.saveUser(argThat(user -> user.getUsername().equals(existentUsername))))
                .thenThrow(new IllegalArgumentException());
        when(inputValidationService.validateUsername(existentUsername)).thenReturn(true);
        when(inputValidationService.validatePassword(invalidPassword)).thenReturn(false);
        when(inputValidationService.validatePassword(validPassword)).thenReturn(true);
        when(inputValidationService.validateUsername(invalidUsername)).thenReturn(false);
        when(inputValidationService.validateUsername(validUsername)).thenReturn(true);
        session = new MockHttpSession();
    }

    @Test
    void testPasswordsNotMatching() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", validUsername)
                        .param("password", validPassword)
                        .param("password2", "something"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attributeExists("username"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/register"));
    }

    @Test
    void testValidRegistrationData() throws Exception {
        mockMvc.perform(post("/register").session(session)
                        .param("username", validUsername)
                        .param("password", validPassword)
                        .param("password2", validPassword))
                .andExpect(view().name("redirect:/recipes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeCount(0));
        User savedUser = (User) session.getAttribute("me");
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getPassword()).isEqualTo(validPassword);
        assertThat(savedUser.getUsername()).isEqualTo(validUsername);
    }


    @Test
    void testUsernameAlreadyExists() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", existentUsername)
                        .param("password", validPassword)
                        .param("password2", validPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/register"))
                .andExpect(flash().attribute("errorMessage", "Username already exists"));
    }
}
