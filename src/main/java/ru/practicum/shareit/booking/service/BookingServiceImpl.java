package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto create(BookingRequestDto request, Integer bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (item.getOwner().getId()== bookerId) {
            throw new NotFoundException("Нельзя бронировать свою вещь");
        }

        if (!item.isAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (request.getEnd().isBefore(request.getStart()) || request.getEnd().equals(request.getStart())) {
            throw new ValidationException("Дата окончания должна быть позже даты начала");
        }

        if (request.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала не может быть в прошлом");
        }

        Booking booking = new Booking();
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(StatusBooking.WAITING);

        booking = bookingRepository.save(booking);
        return toResponseDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto approve(Integer bookingId, boolean approved, Integer ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!(booking.getItem().getOwner().getId() == ownerId)) {
            throw new NotFoundException("Подтвердить бронирование может только владелец вещи");
        }

        if (booking.getStatus() != StatusBooking.WAITING) {
            throw new ValidationException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? StatusBooking.APPROVED : StatusBooking.REJECTED);
        booking = bookingRepository.save(booking);
        return toResponseDto(booking);
    }

    @Override
    public BookingResponseDto getById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findByIdAndUser(bookingId, userId)
                .orElseThrow(() -> new NotFoundException(
                        "Бронирование не найдено или у вас нет прав на его просмотр"));
        return toResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getByBooker(Integer userId, String stateParam) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        BookingState state = BookingState.from(stateParam);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerId(userId, Sort.by(Sort.Direction.DESC, "start"));
            case CURRENT -> bookingRepository.findCurrentByBooker(userId, now);
            case FUTURE -> bookingRepository.findFutureByBooker(userId, now);
            case PAST -> bookingRepository.findPastByBooker(userId, now);
            case WAITING -> bookingRepository.findByBookerId(userId, Sort.by(Sort.Direction.DESC, "start"))
                    .stream()
                    .filter(b -> b.getStatus() == StatusBooking.WAITING)
                    .collect(Collectors.toList());
            case REJECTED -> bookingRepository.findByBookerId(userId, Sort.by(Sort.Direction.DESC, "start"))
                    .stream()
                    .filter(b -> b.getStatus() == StatusBooking.REJECTED)
                    .collect(Collectors.toList());
            default -> throw new IllegalArgumentException("Unknown state: " + stateParam);
        };

        return bookings.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getByOwner(Integer userId, String stateParam) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (itemRepository.findByOwnerId(userId).isEmpty()) {
            return List.of();
        }

        BookingState state = BookingState.from(stateParam);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItemOwnerId(userId, Sort.by(Sort.Direction.DESC, "start"));
            case CURRENT -> bookingRepository.findCurrentByOwner(userId, now);
            case FUTURE -> bookingRepository.findFutureByOwner(userId, now);
            case PAST -> bookingRepository.findPastByOwner(userId, now);
            case WAITING -> bookingRepository.findByItemOwnerId(userId, Sort.by(Sort.Direction.DESC, "start"))
                    .stream()
                    .filter(b -> b.getStatus() == StatusBooking.WAITING)
                    .collect(Collectors.toList());
            case REJECTED -> bookingRepository.findByItemOwnerId(userId, Sort.by(Sort.Direction.DESC, "start"))
                    .stream()
                    .filter(b -> b.getStatus() == StatusBooking.REJECTED)
                    .collect(Collectors.toList());
            default -> throw new IllegalArgumentException("Unknown state: " + stateParam);
        };

        return bookings.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private BookingResponseDto toResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getItem().getName(),
                booking.getBooker().getId(),
                booking.getBooker().getName(),
                booking.getStatus()
        );
    }
}
