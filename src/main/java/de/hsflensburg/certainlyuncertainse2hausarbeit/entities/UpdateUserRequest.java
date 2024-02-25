package de.hsflensburg.certainlyuncertainse2hausarbeit.entities;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String description;
}