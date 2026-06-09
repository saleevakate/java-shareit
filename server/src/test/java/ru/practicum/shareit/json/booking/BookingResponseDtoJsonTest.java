package ru.practicum.shareit.json.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseDtoJsonTest {

    @Autowired
    private JacksonTester<BookingResponseDto> json;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingResponseDto dto = new BookingResponseDto(
                1,
                start,
                end,
                new BookingResponseDto.ItemInfo(1, "Drill"),
                new BookingResponseDto.BookerInfo(2, "Booker"),
                StatusBooking.WAITING
        );

        JsonContent<BookingResponseDto> result = json.write(dto);

        assertThat(result)
                .hasJsonPath("$.id")
                .hasJsonPath("$.start")
                .hasJsonPath("$.end")
                .hasJsonPath("$.status")
                .hasJsonPath("$.item")
                .hasJsonPath("$.booker")
                .hasJsonPath("$.item.id")
                .hasJsonPath("$.item.name")
                .hasJsonPath("$.booker.id")
                .hasJsonPath("$.booker.name")
                .extractingJsonPathNumberValue("$.id")
                .satisfies(id -> assertThat(id).isEqualTo(1));
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\"id\":1,\"start\":\"2025-01-01T10:00:00\",\"end\":\"2025-01-02T18:00:00\","
                + "\"item\":{\"id\":1,\"name\":\"Drill\"},"
                + "\"booker\":{\"id\":2,\"name\":\"Booker\"},"
                + "\"status\":\"WAITING\"}";

        BookingResponseDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getStatus()).isEqualTo(StatusBooking.WAITING);
        assertThat(dto.getItem().getId()).isEqualTo(1);
        assertThat(dto.getItem().getName()).isEqualTo("Drill");
        assertThat(dto.getBooker().getId()).isEqualTo(2);
        assertThat(dto.getBooker().getName()).isEqualTo("Booker");
    }
}
