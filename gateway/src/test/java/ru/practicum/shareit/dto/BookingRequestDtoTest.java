package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BookingRequestDtoTest {

    private Validator validator;
    private BookingRequestDto requestDto;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        requestDto = new BookingRequestDto();
        requestDto.setItemId(1);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void shouldBeValid_whenAllFieldsCorrect() {
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(requestDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFail_whenItemIdIsNull() {
        requestDto.setItemId(null);
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(requestDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("ID вещи обязателен");
    }

    @Test
    void shouldFail_whenStartIsNull() {
        requestDto.setStart(null);
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(requestDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Дата начала обязательна");
    }

    @Test
    void shouldFail_whenEndIsNull() {
        requestDto.setEnd(null);
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(requestDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Дата окончания обязательна");
    }

    @Test
    void shouldFail_whenStartIsInPast() {
        requestDto.setStart(LocalDateTime.now().minusDays(1));
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(requestDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Дата начала не может быть в прошлом");
    }

    @Test
    void shouldFail_whenEndIsInPast() {
        requestDto.setEnd(LocalDateTime.now().minusDays(1));
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(requestDto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Дата окончания должна быть в будущем");
    }

    @Test
    void shouldPass_whenStartIsNow() {
        // @FutureOrPresent допускает текущий момент
        requestDto.setStart(LocalDateTime.now().plusSeconds(1));
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(requestDto);
        assertThat(violations).isEmpty();
    }
}
