package ru.practicum.shareit.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    void testUserDtoDeserializationWithNullFields() throws Exception {
        String json = "{\"id\":null,\"name\":null,\"email\":null}";
        UserDto dto = objectMapper.readValue(json, UserDto.class);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isNull();
    }

    @Test
    void testItemDtoSerialization() throws Exception {
        ItemDto dto = new ItemDto(1, "Drill", "Powerful drill", true, null);
        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Drill\"");
        assertThat(json).contains("\"description\":\"Powerful drill\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":null");
    }

    @Test
    void testItemDtoDeserialization() throws Exception {
        String json = "{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful drill\",\"available\":true,\"requestId\":5}";
        ItemDto dto = objectMapper.readValue(json, ItemDto.class);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getDescription()).isEqualTo("Powerful drill");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(5);
    }

    @Test
    void testItemDtoWithNullFields() throws Exception {
        ItemDto dto = new ItemDto(null, null, null, null, null);
        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":null");
        assertThat(json).contains("\"name\":null");
        assertThat(json).contains("\"description\":null");
        assertThat(json).contains("\"available\":null");
    }

    @Test
    void testItemWithBookingsDtoSerialization() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemWithBookingsDto dto = new ItemWithBookingsDto(new ItemDto(1, "Drill", "Powerful drill", true, null));
        dto.setComments(List.of(new CommentDto(1, "Great!", "User", now)));
        dto.setLastBooking(new BookingShortDto(1, 2, now.minusDays(1), now));
        dto.setNextBooking(new BookingShortDto(2, 3, now.plusDays(1), now.plusDays(2)));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Drill\"");
        assertThat(json).contains("\"comments\"");
        assertThat(json).contains("\"lastBooking\"");
        assertThat(json).contains("\"nextBooking\"");
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
    void testBookingRequestDtoDeserialization() throws Exception {
        String json = "{\"itemId\":1,\"start\":\"2025-01-01T10:00:00\",\"end\":\"2025-01-02T18:00:00\"}";
        BookingRequestDto dto = objectMapper.readValue(json, BookingRequestDto.class);

        assertThat(dto.getItemId()).isEqualTo(1);
        assertThat(dto.getStart()).isNotNull();
        assertThat(dto.getEnd()).isNotNull();
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
    void testBookingResponseDtoDeserialization() throws Exception {
        String json = "{\"id\":1,\"start\":\"2025-01-01T10:00:00\",\"end\":\"2025-01-02T18:00:00\","
                + "\"item\":{\"id\":1,\"name\":\"Drill\"},\"booker\":{\"id\":2,\"name\":\"John\"},"
                + "\"status\":\"WAITING\"}";

        BookingResponseDto dto = objectMapper.readValue(json, BookingResponseDto.class);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getStatus()).isEqualTo(StatusBooking.WAITING);
        assertThat(dto.getItem().getId()).isEqualTo(1);
        assertThat(dto.getBooker().getName()).isEqualTo("John");
    }

    @Test
    void testBookingShortDtoSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        BookingShortDto dto = new BookingShortDto(1, 2, start, end);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"bookerId\":2");
        assertThat(json).contains("\"start\"");
        assertThat(json).contains("\"end\"");
    }

    @Test
    void testBookingShortDtoDeserialization() throws Exception {
        String json = "{\"id\":1,\"bookerId\":2,\"start\":\"2025-01-01T10:00:00\",\"end\":\"2025-01-02T18:00:00\"}";
        BookingShortDto dto = objectMapper.readValue(json, BookingShortDto.class);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getBookerId()).isEqualTo(2);
        assertThat(dto.getStart()).isNotNull();
        assertThat(dto.getEnd()).isNotNull();
    }

    @Test
    void testCommentCreateDtoSerialization() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great item!");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"text\":\"Great item!\"");
    }

    @Test
    void testCommentCreateDtoDeserialization() throws Exception {
        String json = "{\"text\":\"Very nice!\"}";
        CommentCreateDto dto = objectMapper.readValue(json, CommentCreateDto.class);

        assertThat(dto.getText()).isEqualTo("Very nice!");
    }

    @Test
    void testCommentDtoSerialization() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        CommentDto dto = new CommentDto(1, "Great item!", "John", created);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Great item!\"");
        assertThat(json).contains("\"authorName\":\"John\"");
        assertThat(json).contains("\"created\"");
    }

    @Test
    void testCommentDtoDeserialization() throws Exception {
        String json = "{\"id\":1,\"text\":\"Nice!\",\"authorName\":\"Jane\",\"created\":\"2025-01-01T10:00:00\"}";
        CommentDto dto = objectMapper.readValue(json, CommentDto.class);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getText()).isEqualTo("Nice!");
        assertThat(dto.getAuthorName()).isEqualTo("Jane");
        assertThat(dto.getCreated()).isNotNull();
    }

    @Test
    void testItemRequestCreateDtoSerialization() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"description\":\"Need a drill\"");
    }

    @Test
    void testItemRequestCreateDtoDeserialization() throws Exception {
        String json = "{\"description\":\"Need a hammer\"}";
        ItemRequestCreateDto dto = objectMapper.readValue(json, ItemRequestCreateDto.class);

        assertThat(dto.getDescription()).isEqualTo("Need a hammer");
    }

    @Test
    void testItemRequestDtoSerialization() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        List<ItemResponseDto> items = List.of(new ItemResponseDto(1, "Drill", 2));
        ItemRequestDto dto = new ItemRequestDto(1, "Need a drill", created, items);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Need a drill\"");
        assertThat(json).contains("\"created\"");
        assertThat(json).contains("\"items\"");
    }

    @Test
    void testItemRequestDtoDeserialization() throws Exception {
        String json = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2025-01-01T10:00:00\",\"items\":[]}";
        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getCreated()).isNotNull();
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void testItemResponseDtoSerialization() throws Exception {
        ItemResponseDto dto = new ItemResponseDto(1, "Drill", 2);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Drill\"");
        assertThat(json).contains("\"ownerId\":2");
    }

    @Test
    void testItemResponseDtoDeserialization() throws Exception {
        String json = "{\"id\":1,\"name\":\"Hammer\",\"ownerId\":3}";
        ItemResponseDto dto = objectMapper.readValue(json, ItemResponseDto.class);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Hammer");
        assertThat(dto.getOwnerId()).isEqualTo(3);
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

    @Test
    void testBookingResponseDtoAllStatuses() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        for (StatusBooking status : StatusBooking.values()) {
            BookingResponseDto dto = new BookingResponseDto(
                    1, now, now.plusDays(1),
                    new BookingResponseDto.ItemInfo(1, "Drill"),
                    new BookingResponseDto.BookerInfo(1, "Booker"),
                    status
            );

            String json = objectMapper.writeValueAsString(dto);
            assertThat(json).contains("\"status\":\"" + status.name() + "\"");
        }
    }

    @Test
    void testBookingResponseDtoWithNullFields() throws Exception {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(1);
        dto.setStatus(StatusBooking.WAITING);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"status\":\"WAITING\"");
    }
}
