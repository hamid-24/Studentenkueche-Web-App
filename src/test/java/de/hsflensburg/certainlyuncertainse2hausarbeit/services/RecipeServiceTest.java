package de.hsflensburg.certainlyuncertainse2hausarbeit.services;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Rating;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.RecipeRequest;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Recipe;
import de.hsflensburg.certainlyuncertainse2hausarbeit.tables.RatingTable;
import de.hsflensburg.certainlyuncertainse2hausarbeit.tables.RecipeTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {
    @Mock
    private RecipeTable recipeTable;
    @Mock
    private RatingTable ratingTable;
    @InjectMocks
    private RecipeService recipeService;

    @Test
    void testSaveRecipeImage() throws IOException {
        // mock image with empty bytes
        MockMultipartFile image =
                new MockMultipartFile("image.png", "image.png", "image", new byte[0]);
        MockMultipartFile nonExistentImage = null;

        Path path = recipeService.saveRecipeImage(image);

        // test valid image and delete
        assertThat(Files.exists(path)).isTrue();
        assertThat(image.getBytes()).isEqualTo(Files.readAllBytes(path));
        if (Files.exists(path)) Files.delete(path);

        // test non existent image
        assertThatException().isThrownBy(() -> {
           recipeService.saveRecipeImage(nonExistentImage);
        });
    }

    @Test
    void testDeleteImage() throws IOException {
        String imagePath = "upload/recipeImages/image.png";

        // create a temporary file for testing purpose
        Path tempFile = Files.createTempFile("test", ".png");
        Files.copy(tempFile, Paths.get(imagePath), StandardCopyOption.REPLACE_EXISTING);

        // check if the file exists
        assertThat(Files.exists(Paths.get(imagePath))).isTrue();

        // delete the image file
        recipeService.deleteImage(imagePath);

        // check if the file is removed
        assertThat(Files.exists(Paths.get(imagePath))).isFalse();
    }

    @Test
    void testUpdateRecipe() {
        // mock data
        int recipeId = 1;
        RecipeRequest updatedRecipe = new RecipeRequest();
        updatedRecipe.setName("Updated Recipe");
        updatedRecipe.setTime(10);
        updatedRecipe.setCalories(10);
        updatedRecipe.setPrice(10.00f);

        Recipe existingRecipe = new Recipe();
        existingRecipe.setId(recipeId);
        existingRecipe.setName("Original Recipe");
        existingRecipe.setTime(5);
        existingRecipe.setCalories(5);
        existingRecipe.setPrice(5.00f);

        // mock findById method
        when(recipeTable.findById(recipeId)).thenReturn(Optional.of(existingRecipe));

        // test updateRecipe method
        recipeService.updateRecipe(recipeId, updatedRecipe);

        assertThat(existingRecipe.getName()).isEqualTo(updatedRecipe.getName());
        assertThat(existingRecipe.getTime()).isEqualTo(updatedRecipe.getTime());
        assertThat(existingRecipe.getCalories()).isEqualTo(updatedRecipe.getCalories());
        assertThat(existingRecipe.getPrice()).isEqualTo(updatedRecipe.getPrice());
    }

    @Test
    void testIncrementViews() {
        // mock data
        Integer recipeId = 1;
        User recipeOwner = new User("Owner", "asdfg");
        recipeOwner.setId(11);
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setUser(recipeOwner);
        recipe.setViews(1);

        User viewer = new User("otherUser", "qwert");
        viewer.setId(22);

        when(recipeTable.findById(eq(recipeId))).thenReturn(Optional.of(recipe));

        // increment views for the recipe
        recipeService.incrementViews(recipeId, viewer);
        verify(recipeTable, times(1)).save(recipe);
        assertThat(recipe.getViews()).isEqualTo(2);
    }


    @Test
    void testRateRecipe() {
        // mock data
        User user = new User();
        Recipe recipe = new Recipe();
        int userRating = 4;

        // mock existing rating
        Rating existingRating = new Rating();
        existingRating.setUser(user);
        existingRating.setRecipe(recipe);
        existingRating.setUserRating(4);

        // test the rateRecipe method
        recipeService.rateRecipe(user, recipe, userRating);

        assertThat(existingRating.getUserRating()).isEqualTo(userRating);
    }

    @Test
    void testCalculateAverageRating() {
        // mock data
        Recipe recipe = new Recipe();
        Rating rating1 = new Rating();
        rating1.setUserRating(4);
        Rating rating2 = new Rating();
        rating2.setUserRating(5);
        Rating rating3 = new Rating();
        rating3.setUserRating(3);

        List<Rating> ratings = Arrays.asList(rating1, rating2, rating3);

        // mock findByRecipe method
        when(ratingTable.findByRecipe(recipe)).thenReturn(ratings);

        // test calculateAverageRating method
        double averageRating = recipeService.calculateAverageRating(recipe);

        assertThat(averageRating).isEqualTo(4.0);
    }

    @Test
    void testCalculateAverageRatingNoRatings() {
        // mock data
        Recipe recipe = new Recipe();

        // mock findByRecipe method with an empty list
        when(ratingTable.findByRecipe(recipe)).thenReturn(List.of());

        // test calculateAverageRating method when there are no ratings
        double averageRating = recipeService.calculateAverageRating(recipe);

        assertThat(averageRating).isEqualTo(0.0);
    }
}
