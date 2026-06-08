package ru.practicum.shareit.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class DtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void testUserDtoSerialization() throws Exception {
        UserDto dto = new UserDto(1, "Test User", "test@example.com");
        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test User\"");
        assertThat(json).contains("\"email\":\"test@example.com\"");
    }

    @Test
    void testUserDtoDeserialization() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@example.com\"}";
        UserDto dto = objectMapper.readValue(json, UserDto.class);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testItemDtoSerialization() throws Exception {
        ItemDto dto = new ItemDto(1, "Drill", "Powerful drill", true, null);
        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Drill\"");
        assertThat(json).contains("\"available\":true");
    }

    @Test
    void testBookingRequestDtoSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1);
        dto.setStart(start);
        dto.setEnd(end);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"start\":\"" + start.format(formatter) + "\"");
        assertThat(json).contains("\"end\":\"" + end.format(formatter) + "\"");
    }

    @Test
    void testBookingResponseDtoSerialization() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingResponseDto dto = new BookingResponseDto(
                1, now, now.plusDays(1),
                new BookingResponseDto.ItemInfo(1, "Drill"),
                new BookingResponseDto.BookerInfo(1, "Booker"),
                StatusBooking.APPROVED
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"status\":\"APPROVED\"");
        assertThat(json).contains("\"item\"");
        assertThat(json).contains("\"booker\"");
    }

    @Test
    void testBookingRequestDtoWithDatesInPast_shouldSerializeCorrectly() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 12, 25, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 26, 18, 0, 0);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1);
        dto.setStart(start);
        dto.setEnd(end);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"start\":\"2024-12-25T10:00:00\"");
        assertThat(json).contains("\"end\":\"2024-12-26T18:00:00\"");
    }
}