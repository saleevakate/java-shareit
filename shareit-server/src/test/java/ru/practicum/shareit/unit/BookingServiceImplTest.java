package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingRequestDto requestDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setName("Owner");

        booker = new User();
        booker.setId(2);
        booker.setName("Booker");

        item = new Item();
        item.setId(1);
        item.setName("Drill");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusBooking.WAITING);

        requestDto = new BookingRequestDto();
        requestDto.setItemId(1);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void create_shouldSaveBooking_whenValid() {
        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.create(requestDto, 2);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(StatusBooking.WAITING);
    }

    @Test
    void create_shouldThrowException_whenBookingOwnItem() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(requestDto, 1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void approve_shouldApproveBooking_whenOwnerApproves() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.approve(1, true, 1);

        assertThat(result.getStatus()).isEqualTo(StatusBooking.APPROVED);
    }

    @Test
    void getById_shouldReturnBooking() {
        when(bookingRepository.findByIdAndUser(1, 1)).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.getById(1, 1);

        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void getByBooker_shouldReturnBookings() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerId(eq(1), any(Sort.class))).thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByBooker(1, "ALL");

        assertThat(result).hasSize(1);
    }
}