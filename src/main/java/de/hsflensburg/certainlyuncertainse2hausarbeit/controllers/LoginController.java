package de.hsflensburg.certainlyuncertainse2hausarbeit.controllers;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.RecipeService;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private RecipeService recipeService;
    @GetMapping
    public String get(){
        return "redirect:/login";
    }

    // remove the user attribute from the session to indicate logged out status
    @GetMapping
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("me");
        return "redirect:/login";
    }

    @GetMapping(value = "/login")
    public String get(HttpSession session) {
        session.setAttribute("previewRecipes", recipeService.findRandom(5));
        return "login";
    }

    // retrieve the user from the database and set it as a session attribute to indicate logged in status
    @PostMapping(value = "/login")
    public String post(String username, String password, RedirectAttributes ra, HttpSession session){
        User user = userService.findByUsername(username);
        if (user == null) {
            ra.addFlashAttribute("errorMessage", "Username is wrong");
            return "redirect:/login";
        }
        if (!user.getPassword().equals(password)) {
            ra.addFlashAttribute("errorMessage", "Password is wrong");
            return "redirect:/login";
        }
        session.setAttribute("me", user);
        return "redirect:/recipes";
    }

    @GetMapping(value = "/game", produces = "application/wasm")
    public String game() {
        return "game";
    }
}
