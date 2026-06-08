package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingShortDtoTest {

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        BookingShortDto dto = new BookingShortDto();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getBookerId()).isNull();
        assertThat(dto.getStart()).isNull();
        assertThat(dto.getEnd()).isNull();
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingShortDto dto = new BookingShortDto(1, 2, start, end);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getBookerId()).isEqualTo(2);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
    }

    @Test
    void setters_shouldUpdateFields() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingShortDto dto = new BookingShortDto();
        dto.setId(1);
        dto.setBookerId(2);
        dto.setStart(start);
        dto.setEnd(end);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getBookerId()).isEqualTo(2);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
    }

    @Test
    void equals_shouldReturnTrue_whenAllFieldsSame() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingShortDto dto1 = new BookingShortDto(1, 2, start, end);
        BookingShortDto dto2 = new BookingShortDto(1, 2, start, end);

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingShortDto dto1 = new BookingShortDto(1, 2, start, end);
        BookingShortDto dto2 = new BookingShortDto(2, 2, start, end);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentBookerId() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingShortDto dto1 = new BookingShortDto(1, 2, start, end);
        BookingShortDto dto2 = new BookingShortDto(1, 3, start, end);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentStart() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingShortDto dto1 = new BookingShortDto(1, 2, start, end);
        BookingShortDto dto2 = new BookingShortDto(1, 2, start.plusDays(1), end);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentEnd() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingShortDto dto1 = new BookingShortDto(1, 2, start, end);
        BookingShortDto dto2 = new BookingShortDto(1, 2, start, end.plusDays(1));

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void hashCode_shouldBeSame_whenAllFieldsSame() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingShortDto dto1 = new BookingShortDto(1, 2, start, end);
        BookingShortDto dto2 = new BookingShortDto(1, 2, start, end);

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentId() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingShortDto dto1 = new BookingShortDto(1, 2, start, end);
        BookingShortDto dto2 = new BookingShortDto(2, 2, start, end);

        assertThat(dto1.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void toString_shouldContainFields() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingShortDto dto = new BookingShortDto(1, 2, start, end);
        String result = dto.toString();

        assertThat(result).contains("id=1");
        assertThat(result).contains("bookerId=2");
    }
}