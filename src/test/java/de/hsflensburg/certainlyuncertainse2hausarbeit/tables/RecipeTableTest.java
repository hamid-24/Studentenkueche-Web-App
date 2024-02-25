package de.hsflensburg.certainlyuncertainse2hausarbeit.tables;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Recipe;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;


@DataJpaTest
public class RecipeTableTest {

    @Autowired
    RecipeTable recipeTable;
    @Autowired
    UserTable userTable;

    @Test
    void testSaveRecipe() {
        Recipe recipe = new Recipe();

        Recipe savedRecipe = recipeTable.save(recipe);

        assertThat(savedRecipe).isNotNull();
        assertThat(savedRecipe).isEqualTo(recipe);
    }

    @Test
    void testFindById() {
        Recipe recipe = new Recipe();
        Recipe recipe2 = new Recipe();

        recipeTable.save(recipe);
        Recipe savedRecipe = recipeTable.findById(recipe.getId()).get();

        // test existent recipe
        assertThat(savedRecipe).isNotNull();
        assertThat(savedRecipe).isEqualTo(recipe);

        // test invalid id
        assertThatException().isThrownBy(() -> {
            recipeTable.findById(recipe2.getId());
        });
    }

    @Test
    void testFindAll() {
        Recipe recipe = new Recipe();
        Recipe recipe2 = new Recipe();

        recipeTable.save(recipe);
        recipeTable.save(recipe2);

        List<Recipe> recipes = recipeTable.findAll();

        assertThat(recipes).isNotNull();
        assertThat(recipes.size()).isEqualTo(2);
        assertThat(recipes.get(0)).isEqualTo(recipe);
        assertThat(recipes.get(1)).isEqualTo(recipe2);
    }

    @Test
    void testFindRandom() {
        Recipe recipe = new Recipe();
        Recipe recipe2 = new Recipe();
        recipeTable.save(recipe);
        recipeTable.save(recipe2);

        List<Recipe> recipes = recipeTable.findRandomEntries(5);

        assertThat(recipes).isNotNull();
        assertThat(recipes.size()).isEqualTo(2);
    }

    @Test
    void testFilterTime() {
        Recipe recipe = Recipe.builder().time(10).build();
        Recipe recipe2 = Recipe.builder().time(30).build();

        recipeTable.save(recipe);
        recipeTable.save(recipe2);

        List<Recipe> recipes = recipeTable.findByTimeLessThan(20);

        assertThat(recipes).isNotNull();
        assertThat(recipes).contains(recipe);
        assertThat(recipes).doesNotContain(recipe2);
    }

    @Test
    void testFilterPrice(){
        Recipe recipe = Recipe.builder().price(5f).build();
        Recipe recipe2 = Recipe.builder().price(7f).build();

        recipeTable.save(recipe);
        recipeTable.save(recipe2);

        List<Recipe> recipes = recipeTable.findByPriceLessThan(6);

        assertThat(recipes).isNotNull();
        assertThat(recipes).contains(recipe);
        assertThat(recipes).doesNotContain(recipe2);
    }

    @Test
    void testFilterCategory(){
        Recipe recipe = Recipe.builder().categories(Arrays.asList("snack", "dessert")).build();
        Recipe recipe2 = Recipe.builder().categories(Arrays.asList("soup", "vegetarian")).build();

        recipeTable.save(recipe);
        recipeTable.save(recipe2);

        List<Recipe> recipes = recipeTable.findByCategoriesContaining("snack");
        List<Recipe> recipes2 = recipeTable.findByCategoriesContaining("soup");

        assertThat(recipes).isNotNull();
        assertThat(recipes2).isNotNull();
        assertThat(recipes).contains(recipe);
        assertThat(recipes2).contains(recipe2);
        assertThat(recipes).doesNotContain(recipe2);
        assertThat(recipes2).doesNotContain(recipe);
    }

    @Test
    void testFindFavorites() {
        // insert example users and favorite recipes
        Recipe recipe = new Recipe();
        Recipe recipe2 = new Recipe();

        User user = new User("anton", "jirul");
        user.addFavorite(recipe);
        User user2 = new User("Usher", "komil");

        userTable.save(user2);
        userTable.save(user);
        recipeTable.save(recipe);
        recipeTable.save(recipe2);

        // find favorites for each user
        List<Recipe> recipes = recipeTable.findByFavorites_id(user.getId());
        List<Recipe> recipes2 = recipeTable.findByFavorites_id(user2.getId());

        // test containing and non-containing favorite recipes for each user
        assertThat(recipes).isNotNull();
        assertThat(recipes.size()).isEqualTo(1);
        assertThat(recipes).contains(recipe);
        assertThat(recipes).doesNotContain(recipe2);

        assertThat(recipes2).isNotNull();
        assertThat(recipes2.size()).isEqualTo(0);
        assertThat(recipes2).doesNotContain(recipe);
        assertThat(recipes2).doesNotContain(recipe2);
    }
}