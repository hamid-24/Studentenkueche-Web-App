package de.hsflensburg.certainlyuncertainse2hausarbeit.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @JsonIgnore
    private String imagePath;

    private String firstName;

    private String lastName;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Column(length = 1000)
    private String description;

    // deletes all children entries upon parent deletion
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rating> ratings = new HashSet<>();

    // creates is_favorite table with two foreign keys
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "is_favorite",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private Set<Recipe> favoriteRecipes = new HashSet<>();

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void removeFavorite(Recipe recipe) {
        favoriteRecipes.remove(recipe);
        recipe.getFavorites().remove(this);
    }

    public void addFavorite(Recipe recipe) {
        favoriteRecipes.add(recipe);
        recipe.getFavorites().add(this);
    }

    // Override the equals and hashCode Methods to make for better comparison of objects
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getClass());
    }
}