package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .rootUri(serverUrl + API_PREFIX)
                .build());
    }

    public ResponseEntity<Object> create(ItemRequestCreateDto requestDto, int userId) {
        return post("", (long) userId, requestDto);
    }

    public ResponseEntity<Object> getByUser(int userId) {
        return get("", (long) userId);
    }

    public ResponseEntity<Object> getAllOther(int userId) {
        return get("/all", (long) userId);
    }

    public ResponseEntity<Object> getById(int requestId, int userId) {
        return get("/" + requestId, (long) userId);
    }
}