package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.StatusBooking;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingResponseDtoTest {

    private LocalDateTime start;
    private LocalDateTime end;
    private BookingResponseDto.ItemInfo itemInfo;
    private BookingResponseDto.BookerInfo bookerInfo;
    private BookingResponseDto dto;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now();
        end = start.plusDays(1);
        itemInfo = new BookingResponseDto.ItemInfo(1, "Drill");
        bookerInfo = new BookingResponseDto.BookerInfo(2, "Booker");
        dto = new BookingResponseDto(1, start, end, itemInfo, bookerInfo, StatusBooking.WAITING);
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyObject() {
        BookingResponseDto emptyDto = new BookingResponseDto();

        assertThat(emptyDto.getId()).isNull();
        assertThat(emptyDto.getStart()).isNull();
        assertThat(emptyDto.getEnd()).isNull();
        assertThat(emptyDto.getItem()).isNull();
        assertThat(emptyDto.getBooker()).isNull();
        assertThat(emptyDto.getStatus()).isNull();
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
        assertThat(dto.getItem()).isEqualTo(itemInfo);
        assertThat(dto.getBooker()).isEqualTo(bookerInfo);
        assertThat(dto.getStatus()).isEqualTo(StatusBooking.WAITING);
    }

    @Test
    void setters_shouldUpdateFields() {
        LocalDateTime newStart = LocalDateTime.now().plusDays(2);
        LocalDateTime newEnd = newStart.plusDays(1);
        BookingResponseDto.ItemInfo newItemInfo = new BookingResponseDto.ItemInfo(3, "Hammer");
        BookingResponseDto.BookerInfo newBookerInfo = new BookingResponseDto.BookerInfo(4, "New Booker");

        dto.setId(5);
        dto.setStart(newStart);
        dto.setEnd(newEnd);
        dto.setItem(newItemInfo);
        dto.setBooker(newBookerInfo);
        dto.setStatus(StatusBooking.APPROVED);

        assertThat(dto.getId()).isEqualTo(5);
        assertThat(dto.getStart()).isEqualTo(newStart);
        assertThat(dto.getEnd()).isEqualTo(newEnd);
        assertThat(dto.getItem()).isEqualTo(newItemInfo);
        assertThat(dto.getBooker()).isEqualTo(newBookerInfo);
        assertThat(dto.getStatus()).isEqualTo(StatusBooking.APPROVED);
    }

    @Test
    void itemInfo_shouldWorkCorrectly() {
        assertThat(itemInfo.getId()).isEqualTo(1);
        assertThat(itemInfo.getName()).isEqualTo("Drill");
    }

    @Test
    void itemInfo_setters_shouldUpdateFields() {
        BookingResponseDto.ItemInfo newItemInfo = new BookingResponseDto.ItemInfo(0, "");
        newItemInfo.setId(3);
        newItemInfo.setName("Hammer");

        assertThat(newItemInfo.getId()).isEqualTo(3);
        assertThat(newItemInfo.getName()).isEqualTo("Hammer");
    }

    @Test
    void bookerInfo_shouldWorkCorrectly() {
        assertThat(bookerInfo.getId()).isEqualTo(2);
        assertThat(bookerInfo.getName()).isEqualTo("Booker");
    }

    @Test
    void bookerInfo_setters_shouldUpdateFields() {
        BookingResponseDto.BookerInfo newBookerInfo = new BookingResponseDto.BookerInfo(0, "");
        newBookerInfo.setId(4);
        newBookerInfo.setName("New Booker");

        assertThat(newBookerInfo.getId()).isEqualTo(4);
        assertThat(newBookerInfo.getName()).isEqualTo("New Booker");
    }

    @Test
    void equals_shouldReturnTrue_whenSameIdAndSameFields() {
        BookingResponseDto dto2 = new BookingResponseDto(1, start, end, itemInfo, bookerInfo, StatusBooking.WAITING);

        assertThat(dto).isEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        BookingResponseDto dto2 = new BookingResponseDto(2, start, end, itemInfo, bookerInfo, StatusBooking.WAITING);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentStart() {
        BookingResponseDto dto2 = new BookingResponseDto(1, start.plusDays(1), end, itemInfo, bookerInfo, StatusBooking.WAITING);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentEnd() {
        BookingResponseDto dto2 = new BookingResponseDto(1, start, end.plusDays(1), itemInfo, bookerInfo, StatusBooking.WAITING);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentStatus() {
        BookingResponseDto dto2 = new BookingResponseDto(1, start, end, itemInfo, bookerInfo, StatusBooking.APPROVED);

        assertThat(dto).isNotEqualTo(dto2);
    }

    @Test
    void hashCode_shouldBeSame_whenSameFields() {
        BookingResponseDto dto2 = new BookingResponseDto(1, start, end, itemInfo, bookerInfo, StatusBooking.WAITING);

        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_shouldBeDifferent_whenDifferentFields() {
        BookingResponseDto dto2 = new BookingResponseDto(2, start, end, itemInfo, bookerInfo, StatusBooking.WAITING);

        assertThat(dto.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void toString_shouldContainFields() {
        String result = dto.toString();

        assertThat(result).contains("id=1");
        assertThat(result).contains("status=WAITING");
        assertThat(result).contains("item");
        assertThat(result).contains("booker");
    }

    @Test
    void itemInfo_equals_shouldReturnTrue_whenSameFields() {
        BookingResponseDto.ItemInfo itemInfo1 = new BookingResponseDto.ItemInfo(1, "Drill");
        BookingResponseDto.ItemInfo itemInfo2 = new BookingResponseDto.ItemInfo(1, "Drill");
        BookingResponseDto.ItemInfo itemInfo3 = new BookingResponseDto.ItemInfo(2, "Hammer");

        assertThat(itemInfo1).isEqualTo(itemInfo2);
        assertThat(itemInfo1).isNotEqualTo(itemInfo3);
    }

    @Test
    void bookerInfo_equals_shouldReturnTrue_whenSameFields() {
        BookingResponseDto.BookerInfo bookerInfo1 = new BookingResponseDto.BookerInfo(2, "Booker");
        BookingResponseDto.BookerInfo bookerInfo2 = new BookingResponseDto.BookerInfo(2, "Booker");
        BookingResponseDto.BookerInfo bookerInfo3 = new BookingResponseDto.BookerInfo(3, "Other");

        assertThat(bookerInfo1).isEqualTo(bookerInfo2);
        assertThat(bookerInfo1).isNotEqualTo(bookerInfo3);
    }
}
