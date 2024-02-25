package de.hsflensburg.certainlyuncertainse2hausarbeit.controllers;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.RecipeRequest;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Recipe;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.RecipeService;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    UserService userService;
    @Autowired
    RecipeService recipeService;

    // get all recipes and filter them only if there is a Request Parameter in the URL
    @GetMapping
    public String get(Model model,@RequestParam(required = false) String filter, @RequestParam(required = false) String category) {
        List<Recipe> recipes;
        String activeFilter = null;
        switch (filter) {
            case "fast" -> {
                recipes = recipeService.findFast();
                activeFilter = "fast";
            }
            case "cheap" -> {
                recipes = recipeService.findCheap();
                activeFilter = "cheap";
            }
            case "popular" -> {
                recipes = recipeService.findPopularRecipes();
                activeFilter = "popular";
            }
            case null, default -> {
                if (category == null || Objects.equals(category, "all")) {
                    recipes = recipeService.findAll();
                } else {
                    recipes = recipeService.findByCategory(category);
                    activeFilter = "category";
                }
            }
        }
        model.addAttribute("averageRatings", recipeService.getAverageRatings(recipes));
        model.addAttribute("activeFilter", activeFilter);
        model.addAttribute("recipes", recipes);
        model.addAttribute("active", "all");
        return "recipes";
    }

    @GetMapping(value = "/new")
    public String newRecipe(Model model){
        model.addAttribute("active", "new");
        return "newRecipe";
    }

    @PostMapping(value = "/new")
    public String create(RecipeRequest recipeRequest){
        recipeService.createFromRequest(recipeRequest);
        return "redirect:/recipes/new";
    }

    // toggle a Recipe as a favorite for a user.
    // only returns a status code for javascript handling
    @GetMapping(value = "/favorite/{id}")
    public ResponseEntity<Void> toggleFavorite(@PathVariable Integer id, HttpSession session) {
        User user = userService.toggleFavorite((User) session.getAttribute("me"),
                recipeService.findById(id));
        session.setAttribute("me", user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/favorites")
    public String getFavorites(Model model, HttpSession session) {
        User user = (User) session.getAttribute("me");
        List<Recipe> favoriteRecipes = recipeService.findAllFavorites(user.getId());
        model.addAttribute("recipes", favoriteRecipes);
        model.addAttribute("active", "favorites");
        model.addAttribute("averageRatings", recipeService.getAverageRatings(favoriteRecipes));
        return "favorites";
    }

    @GetMapping(value = "/myrecipes")
    public String getMyRecipes(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("me");
        List<Recipe> userRecipes = recipeService.findByUser(loggedInUser);
        model.addAttribute("userRecipes", userRecipes);
        model.addAttribute("averageRatings", recipeService.getAverageRatings(userRecipes));
        model.addAttribute("active", "myrecipes");
        return "myRecipes";
    }

    @GetMapping("/myrecipes/{Id}")
    public String editRecipe(@PathVariable Integer Id, Model model) {
        model.addAttribute("active", "edit");
        Recipe recipe = recipeService.findById(Id);
        model.addAttribute("recipe", recipe);
        return "editRecipe";
    }

    @PutMapping("/myrecipes/{Id}")
    public String updateRecipe(@PathVariable Integer Id, RecipeRequest recipeRequest) {
        recipeService.updateRecipe(Id, recipeRequest);
        return "redirect:/recipes/myrecipes";
    }

    @DeleteMapping(value = "/myrecipes/{Id}")
    public String deleteRecipe(@PathVariable Integer Id) {
        Recipe recipe = recipeService.findById(Id);
        userService.removeFromFavoritesOfAllUsers(recipe);
        recipeService.deleteRecipe(Id);
        return "redirect:/recipes/myrecipes";
    }

    //calls a method named findById, presumably to retrieve the recipe with the specified id.
    //retrieves the currently logged-in user from the session
    //increments the view count for the recipe
    //adds data to the model for rendering in the "recipe-detail" view.
    // if the recipe does not exist, it redirects the user to the recipe list page.
    @GetMapping("/detail/{id}")
    public String showRecipeDetail(@PathVariable Integer id, Model model, HttpSession session) {
        Recipe recipeOptional = recipeService.findById(id);
        User currentUser = (User) session.getAttribute("me");
        if (recipeOptional != null) {
            recipeService.incrementViews(id, currentUser);
            model.addAttribute("recipe", recipeOptional);
            model.addAttribute("userRating", recipeService.getUserRatingForRecipe(currentUser, recipeOptional));
            model.addAttribute("averageRating", recipeService.calculateAverageRating(recipeOptional));
            return "recipe-detail";
        } else {
            return "redirect:/recipes";
        }
    }

    @PostMapping("/rate/{id}")
    public String rateRecipe(@PathVariable Integer id, @RequestParam("rating") int rating, HttpSession session) {
        User user = (User) session.getAttribute("me");
        Recipe recipe = recipeService.findById(id);
        recipeService.rateRecipe(user, recipe, rating);
        return "redirect:/recipes/detail/" + id;
    }
}