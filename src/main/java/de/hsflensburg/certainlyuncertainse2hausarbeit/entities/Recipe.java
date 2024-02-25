package de.hsflensburg.certainlyuncertainse2hausarbeit.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.hsflensburg.certainlyuncertainse2hausarbeit.attributeConverters.StringListConverter;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.*;


@Entity
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private Integer time;

    private String difficulty;

    private Integer calories;

    @JsonIgnore
    private String imagePath;

    private Float price;

    private int views;

    @Lob
    @Column(length = 1000)
    private String preparation;

    @Getter
    @Convert(converter = StringListConverter.class)
    private List<String> ingredients;

    // foreign key from user table
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToMany(mappedBy = "favoriteRecipes")
    private Set<User> favorites  = new HashSet<>();

    @Convert(converter = StringListConverter.class)
    private List<String> categories;

    // deletes all children entries upon parent deletion
    @JsonIgnore
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rating> ratings = new HashSet<>();

    // Override the equals and hashCode Methods to make for better comparison of objects
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe recipe)) return false;
        return Objects.equals(getId(), recipe.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getClass());
    }
}