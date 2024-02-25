package de.hsflensburg.certainlyuncertainse2hausarbeit.services;

import org.springframework.stereotype.Service;
import java.util.regex.*;

@Service
public class InputValidationService {


    // check if the first 3 characters are letters of the alphabet,
    // the following characters can either be a letter or a number,
    // the whole username must be 3 characters or longer
    public boolean validateUsername(String username) {
        return Pattern.compile("^(?=[a-zA-Z]{3})[a-zA-Z\\d]{3,}$").matcher(username).matches();
    }

    // check if the password is at least 5 characters long
    public boolean validatePassword(String password) {
        return Pattern.compile("^.{5,}$").matcher(password).matches();
    }
}
