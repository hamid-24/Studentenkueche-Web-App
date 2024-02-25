package de.hsflensburg.certainlyuncertainse2hausarbeit.services;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Recipe;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.UpdateUserRequest;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import de.hsflensburg.certainlyuncertainse2hausarbeit.tables.UserTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserTable userTable;

    @InjectMocks
    private UserService userService;

    @Test
    void testToggleFavorite() {
        // mock 2 example users and recipes
        User user = User.builder().id(1).favoriteRecipes(new HashSet<>()).build();
        User user2 = User.builder().id(2).favoriteRecipes(new HashSet<>()).build();
        Recipe recipe = Recipe.builder().id(1).favorites(new HashSet<>()).build();
        Recipe recipe2 = Recipe.builder().id(2).favorites(new HashSet<>()).build();

        when(userTable.save(user)).thenReturn(user);
        when(userTable.save(user2)).thenReturn(user2);
        when(userTable.findById(1)).thenReturn(Optional.of(user));
        when(userTable.findById(2)).thenReturn(Optional.of(user2));

        // test adding favorites
        userService.toggleFavorite(user, recipe);
        userService.toggleFavorite(user2, recipe2);

        assertThat(user.getFavoriteRecipes()).contains(recipe);
        assertThat(user2.getFavoriteRecipes()).contains(recipe2);
        assertThat(user.getFavoriteRecipes()).doesNotContain(recipe2);
        assertThat(user2.getFavoriteRecipes()).doesNotContain(recipe);

        // test toggling off recipes
        userService.toggleFavorite(user, recipe);
        userService.toggleFavorite(user, recipe2);
        assertThat(user.getFavoriteRecipes()).doesNotContain(recipe);
        assertThat(user.getFavoriteRecipes()).contains(recipe2);
    }

    @Test
    void testRemoveFromFavoritesOfAllUsers() {
        // Mock data
        User user1 = User.builder().id(1).favoriteRecipes(new HashSet<>()).build();
        User user2 = User.builder().id(2).favoriteRecipes(new HashSet<>()).build();
        Recipe recipe = Recipe.builder().id(1).favorites(new HashSet<>()).build();

        // Mock findAll method
        when(userTable.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Save the recipe as a favorite for both users
        user1.addFavorite(recipe);
        user2.addFavorite(recipe);

        assertThat(user1.getFavoriteRecipes()).contains(recipe);
        assertThat(user2.getFavoriteRecipes()).contains(recipe);

        // Test removeFromFavoritesOfAllUsers method
        userService.removeFromFavoritesOfAllUsers(recipe);

        // Verify that save method is called for both users
        verify(userTable, times(2)).save(any(User.class));
        assertThat(user1.getFavoriteRecipes()).doesNotContain(recipe);
        assertThat(user2.getFavoriteRecipes()).doesNotContain(recipe);
    }

    @Test
    void testSaveUser() {
        // mock user
        User user = User.builder().id(1).build();
        when(userTable.save(user)).thenReturn(user);

        // test saveUser method
        User savedUser = userService.saveUser(user);

        assertThat(savedUser).isEqualTo(user);
    }

    @Test
    void testUpdateUserDetails() {
        // mock user and request
        User user = User.builder().id(1).build();
        UpdateUserRequest request = new UpdateUserRequest();

        // test updateUserDetails method
        userService.updateUserDetails(user, request);

        assertThat(user.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(user.getLastName()).isEqualTo(request.getLastName());
        assertThat(user.getDateOfBirth()).isEqualTo(request.getDateOfBirth());
        assertThat(user.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    void testSaveUserImage() throws IOException {
        // mock image with empty bytes
        MockMultipartFile image =
                new MockMultipartFile("image.png", "image.png", "image", new byte[0]);
        MockMultipartFile nonExistentImage = null;

        // test valid image and delete
        Path path = userService.saveUserImage(image);

        assertThat(Files.exists(path)).isTrue();
        assertThat(image.getBytes()).isEqualTo(Files.readAllBytes(path));
        if (Files.exists(path)) Files.delete(path);

        // test non existent image
        assertThatException().isThrownBy(() -> {
            userService.saveUserImage(nonExistentImage);
        });
    }

}
