package de.hsflensburg.certainlyuncertainse2hausarbeit.tables;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Rating;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Recipe;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingTable extends JpaRepository<Rating, Integer> {
    Optional<Rating> findByUserAndRecipe(User user, Recipe recipe);
    List<Rating> findByRecipe(Recipe recipe);
}
