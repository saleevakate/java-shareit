package ru.practicum.shareit.booking.service;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    void create_shouldThrowException_whenItemNotFound() {
        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(requestDto, 2))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь не найдена");
    }

    @Test
    void create_shouldThrowException_whenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(requestDto, 2))
                .isInstanceOf(ru.practicum.shareit.exception.ItemUnavailableException.class);
    }

    @Test
    void create_shouldThrowException_whenEndBeforeStart() {
        requestDto.setStart(LocalDateTime.now().plusDays(2));
        requestDto.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(requestDto, 2))
                .isInstanceOf(ru.practicum.shareit.exception.ValidationException.class);
    }

    @Test
    void create_shouldThrowException_whenEndEqualsStart() {
        LocalDateTime now = LocalDateTime.now().plusDays(1);
        requestDto.setStart(now);
        requestDto.setEnd(now);

        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(requestDto, 2))
                .isInstanceOf(ru.practicum.shareit.exception.ValidationException.class);
    }

    @Test
    void create_shouldThrowException_whenStartInPast() {
        requestDto.setStart(LocalDateTime.now().minusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(requestDto, 2))
                .isInstanceOf(ru.practicum.shareit.exception.ValidationException.class);
    }

    @Test
    void approve_shouldRejectBooking_whenOwnerRejects() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.approve(1, false, 1);

        assertThat(result.getStatus()).isEqualTo(StatusBooking.REJECTED);
    }

    @Test
    void approve_shouldThrowException_whenNotOwner() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approve(1, true, 99))
                .isInstanceOf(ru.practicum.shareit.exception.ItemUnavailableException.class);
    }

    @Test
    void approve_shouldThrowException_whenAlreadyApproved() {
        booking.setStatus(StatusBooking.APPROVED);
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approve(1, true, 1))
                .isInstanceOf(ru.practicum.shareit.exception.ValidationException.class);
    }

    @Test
    void approve_shouldThrowException_whenAlreadyRejected() {
        booking.setStatus(StatusBooking.REJECTED);
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approve(1, true, 1))
                .isInstanceOf(ru.practicum.shareit.exception.ValidationException.class);
    }

    @Test
    void getById_shouldThrowException_whenNoAccess() {
        when(bookingRepository.findByIdAndUser(1, 99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getById(1, 99))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByBooker_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getByBooker(99, "ALL"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByBooker_shouldHandleCurrentState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(bookingRepository.findCurrentByBooker(eq(1), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByBooker(1, "CURRENT");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByBooker_shouldHandleFutureState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(bookingRepository.findFutureByBooker(eq(1), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByBooker(1, "FUTURE");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByBooker_shouldHandlePastState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(bookingRepository.findPastByBooker(eq(1), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByBooker(1, "PAST");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByBooker_shouldHandleWaitingState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatus(eq(1), eq(StatusBooking.WAITING), any(Sort.class)))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByBooker(1, "WAITING");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByBooker_shouldHandleRejectedState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatus(eq(1), eq(StatusBooking.REJECTED), any(Sort.class)))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByBooker(1, "REJECTED");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByBooker_shouldThrowException_whenInvalidState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));

        assertThatThrownBy(() -> bookingService.getByBooker(1, "INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getByOwner_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getByOwner(99, "ALL"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByOwner_shouldReturnEmptyList_whenUserHasNoItems() {
        when(userRepository.findById(99)).thenReturn(Optional.of(booker));
        when(itemRepository.findByOwnerId(99)).thenReturn(List.of());

        List<BookingResponseDto> result = bookingService.getByOwner(99, "ALL");

        assertThat(result).isEmpty();
    }

    @Test
    void getByOwner_shouldHandleCurrentState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1)).thenReturn(List.of(item));
        when(bookingRepository.findCurrentByOwner(eq(1), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByOwner(1, "CURRENT");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByOwner_shouldHandleFutureState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1)).thenReturn(List.of(item));
        when(bookingRepository.findFutureByOwner(eq(1), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByOwner(1, "FUTURE");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByOwner_shouldHandlePastState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1)).thenReturn(List.of(item));
        when(bookingRepository.findPastByOwner(eq(1), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByOwner(1, "PAST");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByOwner_shouldHandleWaitingState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1)).thenReturn(List.of(item));
        when(bookingRepository.findByItemOwnerIdAndStatus(eq(1), eq(StatusBooking.WAITING), any(Sort.class)))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByOwner(1, "WAITING");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByOwner_shouldHandleRejectedState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1)).thenReturn(List.of(item));
        when(bookingRepository.findByItemOwnerIdAndStatus(eq(1), eq(StatusBooking.REJECTED), any(Sort.class)))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByOwner(1, "REJECTED");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByOwner_shouldThrowException_whenInvalidState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1)).thenReturn(List.of(item));

        assertThatThrownBy(() -> bookingService.getByOwner(1, "INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
