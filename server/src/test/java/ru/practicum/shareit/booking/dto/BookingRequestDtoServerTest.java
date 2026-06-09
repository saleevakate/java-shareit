package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingRequestDtoServerTest {

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        BookingRequestDto dto = new BookingRequestDto();

        assertThat(dto.getItemId()).isNull();
        assertThat(dto.getStart()).isNull();
        assertThat(dto.getEnd()).isNull();
    }

    @Test
    void allArgsConstructor_shouldSetFields() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1);
        dto.setStart(start);
        dto.setEnd(end);

        assertThat(dto.getItemId()).isEqualTo(1);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
    }

    @Test
    void setters_shouldUpdateFields() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1);
        dto.setStart(start);
        dto.setEnd(end);

        assertThat(dto.getItemId()).isEqualTo(1);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
    }

    @Test
    void toString_shouldContainFields() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1);
        dto.setStart(start);
        dto.setEnd(end);

        String result = dto.toString();

        assertThat(result).contains("itemId=1");
        assertThat(result).contains("start=");
        assertThat(result).contains("end=");
    }
}
