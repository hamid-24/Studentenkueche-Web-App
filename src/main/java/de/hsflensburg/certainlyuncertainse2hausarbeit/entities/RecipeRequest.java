package de.hsflensburg.certainlyuncertainse2hausarbeit.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Getter
@Setter
public class RecipeRequest {
    private String name;
    private MultipartFile image;
    private int time;
    private String difficulty;
    private int calories;
    private float price;
    private List<String> ingredients;
    private List<String> categories;
    private String preparation;
    private Integer userId;
}
