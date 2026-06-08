package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .rootUri(serverUrl + API_PREFIX)
                .build());
    }

    public ResponseEntity<Object> create(BookingRequestDto request, int userId) {
        return post("", (long) userId, request);
    }

    public ResponseEntity<Object> approve(int bookingId, boolean approved, int userId) {
        return patch("/" + bookingId + "?approved=" + approved, (long) userId, null);
    }

    public ResponseEntity<Object> getById(int bookingId, int userId) {
        return get("/" + bookingId, (long) userId);
    }

    public ResponseEntity<Object> getByBooker(String state, int userId) {
        return get("?state=" + state, (long) userId);
    }

    public ResponseEntity<Object> getByOwner(String state, int userId) {
        return get("/owner?state=" + state, (long) userId);
    }
}