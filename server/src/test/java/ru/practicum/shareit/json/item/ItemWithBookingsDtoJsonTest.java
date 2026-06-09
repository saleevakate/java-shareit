package ru.practicum.shareit.json.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemWithBookingsDtoJsonTest {

    @Autowired
    private JacksonTester<ItemWithBookingsDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemDto itemDto = new ItemDto(1, "Drill", "Powerful drill", true, null);

        ItemWithBookingsDto dto = new ItemWithBookingsDto(itemDto);
        dto.setLastBooking(new BookingShortDto(1, 2, now.minusDays(2), now.minusDays(1)));
        dto.setNextBooking(new BookingShortDto(2, 3, now.plusDays(1), now.plusDays(2)));
        dto.setComments(List.of(new CommentDto(1, "Great item!", "User", now)));

        JsonContent<ItemWithBookingsDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Great item!");
    }

    @Test
    void testSerializeWithoutBookingsAndComments() throws Exception {
        ItemDto itemDto = new ItemDto(1, "Drill", "Powerful drill", true, null);
        ItemWithBookingsDto dto = new ItemWithBookingsDto(itemDto);

        JsonContent<ItemWithBookingsDto> result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.lastBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.nextBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.comments").isNull();
    }

    @Test
    void testSerializeWithEmptyComments() throws Exception {
        ItemDto itemDto = new ItemDto(1, "Drill", "Powerful drill", true, null);
        ItemWithBookingsDto dto = new ItemWithBookingsDto(itemDto);
        dto.setComments(List.of());

        JsonContent<ItemWithBookingsDto> result = json.write(dto);

        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{"
                + "\"id\":1,"
                + "\"name\":\"Drill\","
                + "\"description\":\"Powerful drill\","
                + "\"available\":true,"
                + "\"requestId\":null,"
                + "\"lastBooking\":{\"id\":1,\"bookerId\":2,\"start\":\"2025-01-01T10:00:00\",\"end\":\"2025-01-02T10:00:00\"},"
                + "\"nextBooking\":{\"id\":2,\"bookerId\":3,\"start\":\"2025-01-03T10:00:00\",\"end\":\"2025-01-04T10:00:00\"},"
                + "\"comments\":[{\"id\":1,\"text\":\"Great item!\",\"authorName\":\"User\",\"created\":\"2025-01-01T10:00:00\"}]"
                + "}";

        ItemWithBookingsDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getDescription()).isEqualTo("Powerful drill");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isNull();

        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getLastBooking().getId()).isEqualTo(1);
        assertThat(dto.getNextBooking()).isNotNull();
        assertThat(dto.getNextBooking().getId()).isEqualTo(2);
        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().get(0).getText()).isEqualTo("Great item!");
    }

    @Test
    void testDeserializeWithoutBookings() throws Exception {
        String jsonContent = "{"
                + "\"id\":1,"
                + "\"name\":\"Drill\","
                + "\"description\":\"Powerful drill\","
                + "\"available\":true,"
                + "\"comments\":[]"
                + "}";

        ItemWithBookingsDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNull();
        assertThat(dto.getComments()).isEmpty();
    }

    @Test
    void testRoundTrip() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemDto itemDto = new ItemDto(1, "Drill", "Powerful drill", true, null);
        ItemWithBookingsDto original = new ItemWithBookingsDto(itemDto);
        original.setLastBooking(new BookingShortDto(1, 2, now.minusDays(2), now.minusDays(1)));
        original.setNextBooking(new BookingShortDto(2, 3, now.plusDays(1), now.plusDays(2)));
        original.setComments(List.of(new CommentDto(1, "Great item!", "User", now)));

        String jsonString = json.write(original).getJson();
        ItemWithBookingsDto deserialized = json.parse(jsonString).getObject();

        assertThat(deserialized.getId()).isEqualTo(original.getId());
        assertThat(deserialized.getName()).isEqualTo(original.getName());
        assertThat(deserialized.getLastBooking().getId()).isEqualTo(original.getLastBooking().getId());
        assertThat(deserialized.getNextBooking().getId()).isEqualTo(original.getNextBooking().getId());
        assertThat(deserialized.getComments()).hasSize(1);
    }
}
