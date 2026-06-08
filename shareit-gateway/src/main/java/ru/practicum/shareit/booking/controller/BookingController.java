package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookingRequestDto request,
                                         @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("POST /bookings - создание бронирования: {}, userId={}", request, userId);
        return bookingClient.create(request, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable int bookingId,
                                          @RequestParam boolean approved,
                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("PATCH /bookings/{}?approved={} - подтверждение/отклонение, userId={}",
                bookingId, approved, userId);
        return bookingClient.approve(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@PathVariable int bookingId,
                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /bookings/{} - просмотр бронирования, userId={}", bookingId, userId);
        return bookingClient.getById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByBooker(@RequestParam(defaultValue = "ALL") String state,
                                              @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /bookings?state={} - список бронирований пользователя, userId={}", state, userId);
        return bookingClient.getByBooker(state, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestParam(defaultValue = "ALL") String state,
                                             @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("GET /bookings/owner?state={} - список бронирований владельца, userId={}", state, userId);
        return bookingClient.getByOwner(state, userId);
    }
}
