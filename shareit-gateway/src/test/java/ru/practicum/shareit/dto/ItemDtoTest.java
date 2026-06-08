package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ItemDtoTest {

    private Validator validator;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);
    }

    @Test
    void shouldBeValid_whenAllRequiredFieldsCorrect() {
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldBeValid_whenRequestIdIsNull() {
        itemDto.setRequestId(null);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldBeValid_whenRequestIdIsPresent() {
        itemDto.setRequestId(5);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFail_whenNameIsNull() {
        itemDto.setName(null);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Название не может быть пустым");
    }

    @Test
    void shouldFail_whenNameIsBlank() {
        itemDto.setName("");
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldFail_whenDescriptionIsNull() {
        itemDto.setDescription(null);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Описание не может быть пустым");
    }

    @Test
    void shouldFail_whenDescriptionIsBlank() {
        itemDto.setDescription("");
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldFail_whenAvailableIsNull() {
        itemDto.setAvailable(null);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Статус доступности должен быть указан");
    }

    @Test
    void shouldPass_whenAvailableIsFalse() {
        itemDto.setAvailable(false);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).isEmpty();
    }
}