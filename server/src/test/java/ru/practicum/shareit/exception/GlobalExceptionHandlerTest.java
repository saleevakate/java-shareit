package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void notFoundExceptionHandler_shouldReturn404() {
        NotFoundException exception = new NotFoundException("User not found");
        ErrorResponse response = handler.notFoundExceptionHandler(exception);

        assertThat(response.getCode()).isEqualTo(404);
        assertThat(response.getMessage()).isEqualTo("User not found");
    }

    @Test
    void validationExceptionHandler_shouldReturn409() {
        ValidationException exception = new ValidationException("Email already exists");
        ErrorResponse response = handler.validationExceptionHandler(exception);

        assertThat(response.getCode()).isEqualTo(409);
        assertThat(response.getMessage()).isEqualTo("Email already exists");
    }

    @Test
    void itemUnavailableException_shouldReturn400() {
        ItemUnavailableException exception = new ItemUnavailableException("Item not available");
        ErrorResponse response = handler.itemUnavailableException(exception);

        assertThat(response.getCode()).isEqualTo(400);
        assertThat(response.getMessage()).isEqualTo("Item not available");
    }

    @Test
    void handleThrowable_shouldReturn500() {
        Throwable exception = new RuntimeException("Internal error");
        ErrorResponse response = handler.handleThrowable(exception);

        assertThat(response.getCode()).isEqualTo(500);
        assertThat(response.getMessage()).contains("Internal error");
    }
}
