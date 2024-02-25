package de.hsflensburg.certainlyuncertainse2hausarbeit.tables;

import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.Recipe;
import de.hsflensburg.certainlyuncertainse2hausarbeit.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RecipeTable extends JpaRepository<Recipe, Integer> {
    List<Recipe> findByTimeLessThan(int t);
    List<Recipe> findByPriceLessThan(float p);
    @Query(value = "SELECT * FROM recipe WHERE categories like concat('%', :category, '%')", nativeQuery = true)
    List<Recipe> findByCategoriesContaining(String category);
    List<Recipe> findByFavorites_id(Integer userId);
    List<Recipe> findByUser(User user);
    @Query(value = "SELECT * FROM recipe ORDER BY RAND() LIMIT :n", nativeQuery = true)
    List<Recipe> findRandomEntries(int n);
}
