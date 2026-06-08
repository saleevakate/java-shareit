package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    private Booking booking;
    private BookingRequestDto requestDto;
    private User booker;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        owner = new User();
        owner.setId(2);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);

        requestDto = new BookingRequestDto();
        requestDto.setItemId(1);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusBooking.WAITING);
    }

    @Test
    void toBooking_shouldMapCorrectly() {
        Booking result = BookingMapper.toBooking(requestDto, item, booker);

        assertThat(result).isNotNull();
        assertThat(result.getStart()).isEqualTo(requestDto.getStart());
        assertThat(result.getEnd()).isEqualTo(requestDto.getEnd());
        assertThat(result.getItem()).isEqualTo(item);
        assertThat(result.getBooker()).isEqualTo(booker);
        assertThat(result.getStatus()).isEqualTo(StatusBooking.WAITING);
    }

    @Test
    void toResponseDto_shouldMapCorrectly() {
        BookingResponseDto result = BookingMapper.toResponseDto(booking);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getStart()).isEqualTo(booking.getStart());
        assertThat(result.getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.getStatus()).isEqualTo(StatusBooking.WAITING);
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(1);
        assertThat(result.getItem().getName()).isEqualTo("Drill");
        assertThat(result.getBooker()).isNotNull();
        assertThat(result.getBooker().getId()).isEqualTo(1);
        assertThat(result.getBooker().getName()).isEqualTo("Booker");
    }

    @Test
    void toResponseDto_shouldReturnNull_whenBookingIsNull() {
        BookingResponseDto result = BookingMapper.toResponseDto(null);
        assertThat(result).isNull();
    }

}