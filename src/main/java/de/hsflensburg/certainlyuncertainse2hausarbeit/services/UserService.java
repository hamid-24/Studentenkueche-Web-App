package de.hsflensburg.certainlyuncertainse2hausarbeit.services;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Recipe;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.UpdateUserRequest;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import de.hsflensburg.certainlyuncertainse2hausarbeit.tables.UserTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@Service
public class UserService {

    @Autowired private UserTable userTable;
    public User findByUsername(String username){
        return userTable.findByUsername(username);
    }

    public User findById(Integer id){
        return userTable.findById(id).orElse(null);
    }

    public User saveUser(User user) {
        return userTable.save(user);
    }

    // check if the recipe is already contained within the favorite recipes and toggle it accordingly.
    // makes use of the overwritten equals method in the recipe entity
    public User toggleFavorite(User user, Recipe recipe) {
        user = userTable.findById(user.getId()).get();
        if (user.getFavoriteRecipes().contains(recipe)) {
            user.removeFavorite(recipe);
        } else {
            user.addFavorite(recipe);
        }
        return userTable.save(user);
    }

    // removes the given recipe from the favorite list of all users
    // and updates the user records in the database
    public void removeFromFavoritesOfAllUsers(Recipe recipe) {
        for (User user : userTable.findAll()) {
            user.removeFavorite(recipe);
            userTable.save(user);
        }
    }

    // overwrites the old user information with the new ones
    public void updateUserDetails(User user, UpdateUserRequest request){
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setDescription(request.getDescription());
        userTable.save(user);
    }

    // overwrites the old user image with the new one
    public void updateUserImage(User user, MultipartFile image){
        Path path = saveUserImage(image);

        if(user.getImagePath() != null){
            deleteUserImage(user.getImagePath());
        }

        user.setImagePath(path.toString());
    }

    // saves the new user image in a folder
    public Path saveUserImage(MultipartFile image) {
        String dir = "upload/userImages";
        Path path = Path.of(dir, UUID.randomUUID().toString());
        try {
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return path;
        } catch (IOException e) {
            return null;
        }
    }

    // deletes the image corresponding to the given imagePath
    public void deleteUserImage(String imagePath) {
        try {
            Path path = Path.of(imagePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Error deleting image file: " + e.getMessage());
        }
    }
}


