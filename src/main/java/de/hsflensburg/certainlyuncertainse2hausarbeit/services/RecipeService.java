package de.hsflensburg.certainlyuncertainse2hausarbeit.services;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.RecipeRequest;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Rating;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Recipe;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import de.hsflensburg.certainlyuncertainse2hausarbeit.tables.RatingTable;
import de.hsflensburg.certainlyuncertainse2hausarbeit.tables.RecipeTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class RecipeService {

    @Autowired
    private UserService userService;
    @Autowired
    private RecipeTable recipes;
    @Autowired
    private RatingTable ratingTable;

    public Recipe findById(Integer id) {return recipes.findById(id).orElse(null);}
    public List<Recipe> findAll() {
        return recipes.findAll();
    }
    public List<Recipe> findFast() {return recipes.findByTimeLessThan(30);}
    public List<Recipe> findCheap() {return recipes.findByPriceLessThan(5);}
    public List<Recipe> findByCategory(String category) {return recipes.findByCategoriesContaining(category);}
    public List<Recipe> findAllFavorites(Integer userId) {return recipes.findByFavorites_id(userId);}
    public List<Recipe> findByUser(User user) {
        return recipes.findByUser(user);
    }

    public void saveRecipe(Recipe recipe) {
        recipes.save(recipe);
    }

    public void createFromRequest(RecipeRequest request) {
        Path path = saveRecipeImage(request.getImage());
        saveRecipe(
                Recipe.builder()
                        .name(request.getName())
                        .time(request.getTime())
                        .difficulty(request.getDifficulty())
                        .calories(request.getCalories())
                        .imagePath(path.toString())
                        .price(request.getPrice())
                        .preparation(request.getPreparation())
                        .ingredients(request.getIngredients())
                        .user(userService.findById(request.getUserId()))
                        .categories(request.getCategories()).build()
        );
    }

    public Path saveRecipeImage(MultipartFile image) {
        String dir = "upload/recipeImages";
        Path path = Path.of(dir, UUID.randomUUID().toString());
        try {
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return path;
        } catch (IOException e) {
            return null;
        }
    }

    public List<Recipe> findRandom(int n) {
        return recipes.findRandomEntries(n);
    }

    @Transactional
    public void deleteRecipe(Integer id) {
        recipes.deleteById(id);
    }

    // overwrites the details of a recipe based on the information provided in the updatedRecipe request
    @Transactional
    public void updateRecipe(Integer id, RecipeRequest updatedRecipe) {
        Recipe existingRecipe = recipes.findById(id).orElse(null);

        if (existingRecipe != null) {
            // replaces the existing image with the new one and deletes the old image if a new image is selected
            if (updatedRecipe.getImage() != null && !updatedRecipe.getImage().isEmpty()) {
                Path path = saveRecipeImage(updatedRecipe.getImage());
                deleteImage(existingRecipe.getImagePath());
                existingRecipe.setImagePath(path.toString());
            }

            existingRecipe.setName(updatedRecipe.getName() == null ? existingRecipe.getName() : updatedRecipe.getName());
            existingRecipe.setTime(updatedRecipe.getTime() == 0? existingRecipe.getTime() : updatedRecipe.getTime());
            existingRecipe.setDifficulty(updatedRecipe.getDifficulty() == null? existingRecipe.getDifficulty() : updatedRecipe.getDifficulty());
            existingRecipe.setCalories(updatedRecipe.getCalories() == 0? existingRecipe.getCalories() : updatedRecipe.getCalories());
            existingRecipe.setPrice(updatedRecipe.getPrice() == 0? existingRecipe.getPrice() : updatedRecipe.getPrice());
            existingRecipe.setIngredients(updatedRecipe.getIngredients() == null ? existingRecipe.getIngredients() : updatedRecipe.getIngredients());
            existingRecipe.setCategories(updatedRecipe.getCategories() == null ? existingRecipe.getCategories() : updatedRecipe.getCategories());
            existingRecipe.setPreparation(updatedRecipe.getPreparation() == null ? existingRecipe.getPreparation() : updatedRecipe.getPreparation());
            recipes.save(existingRecipe);
        }
    }

    // deletes an image file if it exists, otherwise logs an error message
    void deleteImage(String imagePath) {
        try {
            Files.deleteIfExists(Paths.get(imagePath));
        } catch (IOException e) {
            System.err.println("Error deleting image file: " + e.getMessage());
        }
    }

    // increments the view count of a recipe if the recipe exists and is not viewed by the owner
    public void incrementViews(Integer recipeId, User recipeOwner) {
        Recipe recipe = recipes.findById(recipeId).orElse(null);
        if (recipe != null && !recipe.getUser().equals(recipeOwner)) {
            recipe.setViews(recipe.getViews() + 1);
            recipes.save(recipe);
        }
    }

    // rates a recipe for a user and deletes their old rating if it exists
    public void rateRecipe(User user, Recipe recipe, int userRating) {
        Optional<Rating> existingRating = ratingTable.findByUserAndRecipe(user, recipe);

        existingRating.ifPresent(rating -> {
            ratingTable.delete(rating);
            recipe.getRatings().remove(rating);
        });

        Rating newRating = new Rating();
        newRating.setUser(user);
        newRating.setRecipe(recipe);
        newRating.setUserRating(userRating);

        ratingTable.save(newRating);
        recipe.getRatings().add(newRating);
    }

    // calculates the average rating of a recipe based on all its user ratings
    public double calculateAverageRating(Recipe recipe) {
        List<Rating> ratings = ratingTable.findByRecipe(recipe);

        // returns 0.0 if the recipe has no ratings yet
        if (ratings.isEmpty()) {
            return 0.0;
        }

        // adds all ratings for the recipe together
        int sum = ratings.stream().mapToInt(Rating::getUserRating).sum();
        double averageRating = (double) sum / ratings.size();

        // returns a result with a single digit after the decimal
        return Math.round(averageRating * 10.0) / 10.0;
    }

    // returns a list of all average ratings of all existing recipes
    public List<Double> getAverageRatings(List<Recipe> recipes){
        List<Double> averageRatings = new ArrayList<>();
        for (Recipe recipe : recipes) {
            double averageRating = calculateAverageRating(recipe);
            averageRatings.add(averageRating);
        }
        return averageRatings;
    }

    // returns the user rating of a user for a recipe
    public Double getUserRatingForRecipe(User user, Recipe recipe) {
        Optional<Rating> userRating = ratingTable.findByUserAndRecipe(user, recipe);

        // returns the current rating or 0.0 in case no rating has been given yet
        return userRating.map(rating -> (double) rating.getUserRating()).orElse(0.0);
    }

    public List<Recipe> findPopularRecipes() {
        List<Recipe> popularRecipes = new ArrayList<>();
        List<Recipe> allRecipes = findAll();

        for (Recipe recipe : allRecipes) {
            List<Rating> ratings = ratingTable.findByRecipe(recipe);
            if (ratings.size() > 2) {
                double averageRating = calculateAverageRating(recipe);
                if (averageRating > 4.0) {
                    popularRecipes.add(recipe);
                }
            }
        }
        return popularRecipes;
    }
}