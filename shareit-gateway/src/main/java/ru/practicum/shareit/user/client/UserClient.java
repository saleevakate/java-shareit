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

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .rootUri(serverUrl + API_PREFIX)
                .build());
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post("", null, userDto);
    }

    public ResponseEntity<Object> update(int id, UserDto userDto) {
        return patch("/" + id, (long) id, userDto);
    }

    public ResponseEntity<Object> getById(int id) {
        return get("/" + id, (long) id);
    }

    public ResponseEntity<Object> getAll() {
        return get("", null);
    }

    public ResponseEntity<Object> delete(int id) {
        return delete("/" + id, (long) id);
    }
}