package de.hsflensburg.certainlyuncertainse2hausarbeit.controllers;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.InputValidationService;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.RecipeService;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private UserService userService;
    @Autowired
    private InputValidationService validator;

    @GetMapping
    public String get(HttpSession session) {
        session.setAttribute("previewRecipes", recipeService.findRandom(5));
        return "register";
    }

    // validate username and password input. After registration, instantly log in the user
    @PostMapping
    public String register(String username, String password, String password2, RedirectAttributes ra, HttpSession session) {
        if (!password.equals(password2)) {
            ra.addFlashAttribute("errorMessage", "Passwords do not match");
            ra.addFlashAttribute("username", username);
            return "redirect:/register";
        }
        if (!validator.validateUsername(username)) {
            ra.addFlashAttribute("errorMessage", "Username must start with 3 letters");
            return "redirect:/register";
        }
        if (!validator.validatePassword(password)) {
            ra.addFlashAttribute("errorMessage", "Password must be at least 5 characters");
            ra.addFlashAttribute("username", username);
            return "redirect:/register";
        }
        try {
            User user = new User(username, password);
            userService.saveUser(user);
            session.setAttribute("me", user);
            return "redirect:/recipes";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Username already exists");
            return "redirect:/register";
        }
    }
}