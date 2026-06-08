package ru.practicum.shareit.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class DtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

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
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"itemId\":1");
    }

    @Test
    void testItemRequestCreateDtoSerialization() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"description\":\"Need a drill\"");
    }

    @Test
    void testCommentCreateDtoSerialization() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great item!");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"text\":\"Great item!\"");
    }
}
