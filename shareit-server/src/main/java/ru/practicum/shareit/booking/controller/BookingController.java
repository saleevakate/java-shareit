package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestBody BookingRequestDto request,
                                     @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("POST /bookings - создание бронирования: {}, userId={}", request, userId);
        return bookingService.create(request, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@PathVariable int bookingId,
                                      @RequestParam boolean approved,
                                      @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("PATCH /bookings/{}?approved={} - подтверждение/отклонение, userId={}",
                bookingId, approved, userId);
        return bookingService.approve(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@PathVariable int bookingId,
                                      @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /bookings/{} - просмотр бронирования, userId={}", bookingId, userId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getByBooker(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /bookings?state={} - список бронирований пользователя, userId={}", state, userId);
        return bookingService.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getByOwner(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /bookings/owner?state={} - список бронирований владельца, userId={}", state, userId);
        return bookingService.getByOwner(userId, state);
    }
}