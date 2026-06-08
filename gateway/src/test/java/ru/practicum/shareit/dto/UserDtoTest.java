package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTest {

    private Validator validator;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
    }

    @Test
    void shouldBeValid_whenAllFieldsCorrect() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFail_whenNameIsNull() {
        userDto.setName(null);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Имя не может быть пустым");
    }

    @Test
    void shouldFail_whenNameIsBlank() {
        userDto.setName("");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldFail_whenEmailIsNull() {
        userDto.setEmail(null);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        // @NotBlank сработает, @Email не проверяется при null
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Email не может быть пустым");
    }

    @Test
    void shouldFail_whenEmailIsBlank() {
        userDto.setEmail("");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        // @NotBlank сработает, @Email не проверяется при пустой строке
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Email не может быть пустым");
    }

    @Test
    void shouldFail_whenEmailIsInvalid() {
        userDto.setEmail("invalid-email");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        // @NotBlank проходит, @Email не проходит
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Неверный формат email");
    }

    @Test
    void shouldPass_whenEmailHasCorrectFormat() {
        userDto.setEmail("valid@example.com");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPass_whenNameIsUpdated() {
        userDto.setName("Updated Name");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isEmpty();
    }
}
