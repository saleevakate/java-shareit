package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import java.util.List;

public interface BookingService {

    BookingResponseDto create(BookingRequestDto request, Integer bookerId);

    BookingResponseDto approve(Integer bookingId, boolean approved, Integer ownerId);

    BookingResponseDto getById(Integer bookingId, Integer userId);

    List<BookingResponseDto> getByBooker(Integer userId, String state);

    List<BookingResponseDto> getByOwner(Integer userId, String state);
}
