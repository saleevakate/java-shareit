package ru.practicum.shareit.user.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";
    private final String baseUrl;

    public UserClient(@Value("${shareit-server.url:http://localhost:9090}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(builder.build());
        this.baseUrl = serverUrl + API_PREFIX;
        System.out.println("DEBUG: UserClient using baseUrl = " + baseUrl);
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post(baseUrl, null, userDto);
    }

    public ResponseEntity<Object> update(int id, UserDto userDto) {
        return patch(baseUrl + "/" + id, (long) id, userDto);
    }

    public ResponseEntity<Object> getById(int id) {
        return get(baseUrl + "/" + id, (long) id);
    }

    public ResponseEntity<Object> getAll() {
        return get(baseUrl, null);
    }

    public ResponseEntity<Object> delete(int id) {
        return delete(baseUrl + "/" + id, (long) id);
    }
}