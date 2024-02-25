package de.hsflensburg.certainlyuncertainse2hausarbeit.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    private int userRating;

    public Rating(User user, Recipe recipe, int userRating) {
        this.user = user;
        this.recipe = recipe;
        this.userRating = userRating;
    }
}
