package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestCreateDtoTest {

    private Validator validator;
    private ItemRequestCreateDto createDto;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");
    }

    @Test
    void shouldBeValid_whenDescriptionIsCorrect() {
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(createDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFail_whenDescriptionIsNull() {
        createDto.setDescription(null);
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(createDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Описание запроса не может быть пустым");
    }

    @Test
    void shouldFail_whenDescriptionIsBlank() {
        createDto.setDescription("");
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(createDto);
        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldFail_whenDescriptionIsEmpty() {
        createDto.setDescription("   ");
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(createDto);
        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldPass_whenDescriptionHasMinimumLength() {
        createDto.setDescription("a");
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(createDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPass_whenDescriptionHasMaximumLength() {
        String longDescription = "a".repeat(500);
        createDto.setDescription(longDescription);
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(createDto);
        assertThat(violations).isEmpty();
    }
}
