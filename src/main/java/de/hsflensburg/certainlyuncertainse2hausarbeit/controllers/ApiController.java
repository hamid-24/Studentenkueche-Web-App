package de.hsflensburg.certainlyuncertainse2hausarbeit.controllers;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.RecipeRequest;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Recipe;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.RecipeService;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
public class ApiController {

    @Autowired
    private UserService userService;
    @Autowired
    RecipeService recipeService;

    // upon a successful login, send a cookie to the client.
    // the cookie contains the user id as an unsafe authorization method
    @PostMapping(value = "/apilogin")
    public ResponseEntity<User> login (@RequestBody User user, HttpServletResponse response) {
        User u = userService.findByUsername(user.getUsername());
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!Objects.equals(u.getPassword(), user.getPassword())) {
            System.out.println(u.getPassword() + " " + user.getPassword());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        response.addCookie(new Cookie("auth", u.getId().toString()));
        return ResponseEntity.status(HttpStatus.OK).body(u);
    }

    // check if the request contains the cookie "auth", equalling login status,
    // before returning a recipe as a JSON object
    @GetMapping(value = "/api/recipes/{id}")
    public ResponseEntity<List<Recipe>> getRecipe(@PathVariable Integer id, @CookieValue(required = false) String auth){
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Integer i = Integer.parseInt(auth);
        User user = userService.findById(i);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(recipeService.findByUser(user));
    }

    @DeleteMapping(value = "/api/recipes/{id}")
    public ResponseEntity<Recipe> deleteRecipe(@PathVariable Integer id,
    @CookieValue(required = false) String auth){
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // parse the value of the "auth" cookie to an Integer and use it to retrieve a user
        Integer i = Integer.parseInt(auth);
        User user = userService.findById(i);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Recipe recipe = recipeService.findById(id);
        if (recipe == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // check if the retrieved user is the owner of the recipe, before deleting it
        if (!Objects.equals(recipe.getUser().getId(), user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        recipeService.deleteRecipe(recipe.getId());
        return ResponseEntity.status(HttpStatus.OK).body(recipe);
    }

    @PutMapping(value = "/api/recipes/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Integer id,
    @CookieValue(required = false) String auth, @RequestBody RecipeRequest request) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Integer i = Integer.parseInt(auth);
        User user = userService.findById(i);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Recipe recipe = recipeService.findById(id);
        if (recipe == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!Objects.equals(recipe.getUser().getId(), user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        recipeService.updateRecipe(recipe.getId(), request);
        recipe = recipeService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(recipe);
    }
}
