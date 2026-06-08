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
    private final String baseUrl;

    public ItemRequestClient(@Value("${shareit-server.url:http://localhost:9090}") String serverUrl,
                             RestTemplateBuilder builder) {
        super(builder.build());
        this.baseUrl = serverUrl + API_PREFIX;
        System.out.println("DEBUG: ItemRequestClient using baseUrl = " + baseUrl);
    }

    public ResponseEntity<Object> create(ItemRequestCreateDto requestDto, int userId) {
        return post(baseUrl, (long) userId, requestDto);
    }

    public ResponseEntity<Object> getByUser(int userId) {
        return get(baseUrl, (long) userId);
    }

    public ResponseEntity<Object> getAllOther(int userId) {
        return get(baseUrl + "/all", (long) userId);
    }

    public ResponseEntity<Object> getById(int requestId, int userId) {
        return get(baseUrl + "/" + requestId, (long) userId);
    }
}