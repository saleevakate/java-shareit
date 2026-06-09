package ru.practicum.shareit.json.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.within;

@JsonTest
class BookingRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookingRequestDto> json;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1);
        dto.setStart(start);
        dto.setEnd(end);

        JsonContent<BookingRequestDto> result = json.write(dto);

        assertThat(result)
                .hasJsonPath("$.itemId")
                .hasJsonPath("$.start")
                .hasJsonPath("$.end")
                .hasJsonPathValue("$.start")
                .hasJsonPathValue("$.end")
                .extractingJsonPathNumberValue("$.itemId")
                .satisfies(itemId -> assertThat(itemId).isEqualTo(dto.getItemId()));
    }

    @Test
    void testDeserialize() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        String jsonContent = String.format(
                "{\"itemId\":1,\"start\":\"%s\",\"end\":\"%s\"}",
                start.format(formatter),
                end.format(formatter)
        );

        BookingRequestDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getItemId()).isEqualTo(1);
        assertThat(dto.getStart()).isCloseTo(start, within(1, java.time.temporal.ChronoUnit.SECONDS));
        assertThat(dto.getEnd()).isCloseTo(end, within(1, java.time.temporal.ChronoUnit.SECONDS));
    }
}
