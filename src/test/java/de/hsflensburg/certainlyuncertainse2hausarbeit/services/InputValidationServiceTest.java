package de.hsflensburg.certainlyuncertainse2hausarbeit.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class InputValidationServiceTest {

    @InjectMocks
    InputValidationService inputValidationService;

    @Test
    void testPassword() {
        List<String> validPasswords = Arrays.asList("asd455", "asdasdasf6", "asdfgg", "asd985");
        List<String> invalidPasswords = Arrays.asList("abcd", "1234", "as23", "");

        validPasswords.forEach(s -> {
            assertThat(inputValidationService.validatePassword(s)).isTrue();
        });
        invalidPasswords.forEach(s -> {
            assertThat(inputValidationService.validatePassword(s)).isFalse();
        });
    }

    @Test
    void testUsername() {
        List<String> validUsernames = Arrays.asList("tom", "tom1", "tomhubo", "tumhubo1234");
        List<String> invalidUsernames = Arrays.asList("to", "123456", "12", "");

        validUsernames.forEach(s -> {
            assertThat(inputValidationService.validateUsername(s)).isTrue();
        });
        invalidUsernames.forEach(s -> {
            assertThat(inputValidationService.validateUsername(s)).isFalse();
        });
    }
}
