package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentCreateDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CommentCreateDtoTest {

    private Validator validator;
    private CommentCreateDto commentDto;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        commentDto = new CommentCreateDto();
        commentDto.setText("Great item!");
    }

    @Test
    void shouldBeValid_whenTextIsCorrect() {
        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(commentDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFail_whenTextIsNull() {
        commentDto.setText(null);
        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(commentDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Текст комментария не может быть пустым");
    }

    @Test
    void shouldFail_whenTextIsBlank() {
        commentDto.setText("");
        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(commentDto);
        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldFail_whenTextIsEmpty() {
        commentDto.setText("   ");
        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(commentDto);
        assertThat(violations).hasSize(1);
    }

    @Test
    void shouldPass_whenTextHasMinimumLength() {
        commentDto.setText("a");
        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(commentDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPass_whenTextHasMaximumLength() {
        String longText = "a".repeat(1000);
        commentDto.setText(longText);
        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(commentDto);
        assertThat(violations).isEmpty();
    }
}
