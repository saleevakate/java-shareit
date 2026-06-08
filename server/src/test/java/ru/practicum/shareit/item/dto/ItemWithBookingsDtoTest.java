package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemWithBookingsDtoTest {

    private ItemDto itemDto;
    private ItemWithBookingsDto dto;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);

        LocalDateTime now = LocalDateTime.now();
        lastBooking = new BookingShortDto(1, 2, now.minusDays(2), now.minusDays(1));
        nextBooking = new BookingShortDto(2, 3, now.plusDays(1), now.plusDays(2));

        CommentDto comment = new CommentDto(1, "Great item!", "User", now);
        comments = new ArrayList<>();
        comments.add(comment);

        dto = new ItemWithBookingsDto(itemDto);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments);
    }

    @Test
    void constructor_shouldCreateFromItemDto() {
        ItemWithBookingsDto result = new ItemWithBookingsDto(itemDto);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getDescription()).isEqualTo("Powerful drill");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isNull();
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).isNull();
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        ItemWithBookingsDto emptyDto = new ItemWithBookingsDto();

        assertThat(emptyDto.getId()).isNull();
        assertThat(emptyDto.getName()).isNull();
        assertThat(emptyDto.getDescription()).isNull();
        assertThat(emptyDto.getAvailable()).isNull();
        assertThat(emptyDto.getRequestId()).isNull();
        assertThat(emptyDto.getLastBooking()).isNull();
        assertThat(emptyDto.getNextBooking()).isNull();
        assertThat(emptyDto.getComments()).isNull();
    }

    @Test
    void setters_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        BookingShortDto newLastBooking = new BookingShortDto(3, 4, now.minusDays(5), now.minusDays(4));
        BookingShortDto newNextBooking = new BookingShortDto(4, 5, now.plusDays(3), now.plusDays(4));
        List<CommentDto> newComments = new ArrayList<>();

        dto.setLastBooking(newLastBooking);
        dto.setNextBooking(newNextBooking);
        dto.setComments(newComments);

        assertThat(dto.getLastBooking()).isEqualTo(newLastBooking);
        assertThat(dto.getNextBooking()).isEqualTo(newNextBooking);
        assertThat(dto.getComments()).isEqualTo(newComments);
    }

    @Test
    void getters_shouldReturnCorrectValues() {
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getDescription()).isEqualTo("Powerful drill");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isNull();
        assertThat(dto.getLastBooking()).isEqualTo(lastBooking);
        assertThat(dto.getNextBooking()).isEqualTo(nextBooking);
        assertThat(dto.getComments()).isEqualTo(comments);
    }

    @Test
    void equals_shouldReturnTrue_whenAllFieldsSame() {
        ItemWithBookingsDto dto2 = new ItemWithBookingsDto(itemDto);
        dto2.setLastBooking(lastBooking);
        dto2.setNextBooking(nextBooking);
        dto2.setComments(comments);

        assertThat(dto).isEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        ItemDto differentItemDto = new ItemDto();
        differentItemDto.setId(2);
        differentItemDto.setName("Drill");
        differentItemDto.setDescription("Powerful drill");
        differentItemDto.setAvailable(true);

        ItemWithBookingsDto dto2 = new ItemWithBookingsDto(differentItemDto);
        dto2.setLastBooking(lastBooking);
        dto2.setNextBooking(nextBooking);
        dto2.setComments(comments);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentLastBooking() {
        LocalDateTime now = LocalDateTime.now();
        BookingShortDto differentLastBooking = new BookingShortDto(5, 6, now.minusDays(3), now.minusDays(2));

        ItemWithBookingsDto dto2 = new ItemWithBookingsDto(itemDto);
        dto2.setLastBooking(differentLastBooking);
        dto2.setNextBooking(nextBooking);
        dto2.setComments(comments);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentNextBooking() {
        LocalDateTime now = LocalDateTime.now();
        BookingShortDto differentNextBooking = new BookingShortDto(6, 7, now.plusDays(2), now.plusDays(3));

        ItemWithBookingsDto dto2 = new ItemWithBookingsDto(itemDto);
        dto2.setLastBooking(lastBooking);
        dto2.setNextBooking(differentNextBooking);
        dto2.setComments(comments);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentComments() {
        ItemWithBookingsDto dto2 = new ItemWithBookingsDto(itemDto);
        dto2.setLastBooking(lastBooking);
        dto2.setNextBooking(nextBooking);
        dto2.setComments(new ArrayList<>());

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void hashCode_shouldBeSame_whenAllFieldsSame() {
        ItemWithBookingsDto dto2 = new ItemWithBookingsDto(itemDto);
        dto2.setLastBooking(lastBooking);
        dto2.setNextBooking(nextBooking);
        dto2.setComments(comments);

        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentId() {
        ItemDto differentItemDto = new ItemDto();
        differentItemDto.setId(2);
        differentItemDto.setName("Drill");
        differentItemDto.setDescription("Powerful drill");
        differentItemDto.setAvailable(true);

        ItemWithBookingsDto dto2 = new ItemWithBookingsDto(differentItemDto);
        dto2.setLastBooking(lastBooking);
        dto2.setNextBooking(nextBooking);
        dto2.setComments(comments);

        assertThat(dto.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void toString_shouldContainBookingAndCommentInfo() {
        String result = dto.toString();

        assertThat(result).contains("lastBooking=BookingShortDto");
        assertThat(result).contains("nextBooking=BookingShortDto");
        assertThat(result).contains("comments=[CommentDto");
        assertThat(result).contains("text=Great item!");
        assertThat(result).contains("authorName=User");
    }

    @Test
    void canHandleNullBookings() {
        dto.setLastBooking(null);
        dto.setNextBooking(null);

        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNull();
    }

    @Test
    void canHandleEmptyComments() {
        dto.setComments(new ArrayList<>());

        assertThat(dto.getComments()).isEmpty();
    }
}
