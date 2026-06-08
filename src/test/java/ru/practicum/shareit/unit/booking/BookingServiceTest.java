package ru.practicum.shareit.unit.booking;

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
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
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
class BookingServiceTest {

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
    void create_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(requestDto, 99))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void create_shouldThrowNotFoundException_whenItemNotFound() {
        // ✅ ИСПРАВЛЕНО: мокаем findById с тем же id, который будет в запросе
        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(requestDto, 2))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь не найдена");
    }

    @Test
    void create_shouldThrowNotFoundException_whenBookingOwnItem() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(requestDto, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Нельзя бронировать свою вещь");
    }

    @Test
    void create_shouldThrowItemUnavailableException_whenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(requestDto, 2))
                .isInstanceOf(ItemUnavailableException.class)
                .hasMessageContaining("Вещь недоступна для бронирования");
    }

    @Test
    void create_shouldThrowValidationException_whenStartInPast() {
        requestDto.setStart(LocalDateTime.now().minusDays(1));
        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(requestDto, 2))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Дата начала не может быть в прошлом");
    }

    @Test
    void create_shouldThrowValidationException_whenEndBeforeStart() {
        requestDto.setEnd(LocalDateTime.now().plusHours(1));
        requestDto.setStart(LocalDateTime.now().plusHours(2));
        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(requestDto, 2))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Дата окончания должна быть позже даты начала");
    }

    @Test
    void approve_shouldApproveBooking_whenOwnerApproves() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.approve(1, true, 1);

        assertThat(result.getStatus()).isEqualTo(StatusBooking.APPROVED);
    }

    @Test
    void approve_shouldRejectBooking_whenOwnerRejects() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.approve(1, false, 1);

        assertThat(result.getStatus()).isEqualTo(StatusBooking.REJECTED);
    }

    @Test
    void approve_shouldThrowItemUnavailableException_whenNotOwner() {
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approve(1, true, 99))
                .isInstanceOf(ItemUnavailableException.class)
                .hasMessageContaining("Подтвердить бронирование может только владелец вещи");
    }

    @Test
    void approve_shouldThrowValidationException_whenBookingAlreadyProcessed() {
        booking.setStatus(StatusBooking.APPROVED);
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approve(1, true, 1))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Бронирование уже обработано");
    }

    @Test
    void getById_shouldReturnBooking_whenUserIsBooker() {
        when(bookingRepository.findByIdAndUser(1, 2)).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.getById(1, 2);

        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void getById_shouldReturnBooking_whenUserIsOwner() {
        when(bookingRepository.findByIdAndUser(1, 1)).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.getById(1, 1);

        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void getById_shouldThrowNotFoundException_whenUserHasNoAccess() {
        when(bookingRepository.findByIdAndUser(1, 99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getById(1, 99))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByBooker_shouldReturnBookings_whenStateAll() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerId(eq(1), any(Sort.class))).thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByBooker(1, "ALL");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByBooker_shouldReturnCurrentBookings() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));
        when(bookingRepository.findCurrentByBooker(eq(1), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByBooker(1, "CURRENT");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByOwner_shouldReturnBookings_whenUserHasItems() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1)).thenReturn(List.of(item));
        when(bookingRepository.findByItemOwnerId(eq(1), any(Sort.class))).thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getByOwner(1, "ALL");

        assertThat(result).hasSize(1);
    }

    @Test
    void getByOwner_shouldReturnEmptyList_whenUserHasNoItems() {
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(1)).thenReturn(List.of());

        List<BookingResponseDto> result = bookingService.getByOwner(1, "ALL");

        assertThat(result).isEmpty();
    }

    @Test
    void getByBooker_shouldThrowIllegalArgumentException_whenUnknownState() {
        when(userRepository.findById(1)).thenReturn(Optional.of(booker));

        assertThatThrownBy(() -> bookingService.getByBooker(1, "UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}